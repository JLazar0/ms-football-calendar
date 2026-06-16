package es.jlazaro.ms_football_calendar.services;

import es.jlazaro.ms_football_calendar.services.dtos.DayServiceDTO;
import es.jlazaro.ms_football_calendar.services.dtos.MatchServiceDTO;
import es.jlazaro.ms_football_calendar.services.dtos.TeamServiceDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScraperService {

    private final HttpClient httpClient;

    public List<DayServiceDTO> scrape(String url) {

        try {

            final HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0")
                    .GET()
                    .build();

            final String html = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            ).body();

            final List<DayServiceDTO> dias = new ArrayList<>();

            final Document doc = Jsoup.parse(html);
            final Elements diasEl = doc.getElementsByClass("matchday");
            diasEl.forEach(diaEl -> {

                final Element fechaEl = diaEl.getElementsByClass("matchdayHeader").first();
                final String fecha = fechaEl.text();
                log.debug("FECHA: "+fecha);
                final Elements partidosEl = diaEl.getElementsByTag("article");

                final List<MatchServiceDTO> partidosDia = new ArrayList<>();

                partidosEl.forEach(partidoEl -> {
                    final Element fechaPartidoEl = partidoEl.getElementsByAttributeValue("itemprop","startDate").first();
                    final LocalDateTime fechaPartidoUTC = LocalDateTime.parse(fechaPartidoEl.attr("content"));
                    final ZonedDateTime utcTime = fechaPartidoUTC.atZone(ZoneId.of("UTC"));
                    final ZonedDateTime spainTime = utcTime.withZoneSameInstant(ZoneId.of("Europe/Madrid"));
                    log.debug("FECHA PARTIDO: "+spainTime);

                    final Element partidoEquiposEl = partidoEl.getElementsByClass("match_teams").first();
                    final Element equipoAEl = partidoEquiposEl.getElementsByAttributeValueContaining("class","match_teamA").first();
                    final String descEquipoA = equipoAEl.text();
                    final Element equipoBEl = partidoEquiposEl.getElementsByAttributeValueContaining("class","match_teamB").first();
                    final String descEquipoB = equipoBEl.text();
                    final Element partidoLogosEl = partidoEquiposEl.getElementsByClass("match_logos").first();
                    final String imgEquipoA = partidoLogosEl.getElementsByTag("img").first().attr("src");
                    final String imgEquipoB = partidoLogosEl.getElementsByTag("img").last().attr("src");
                    log.debug("EQUIPO A: "+descEquipoA);
                    log.debug("EQUIPO B: "+descEquipoB);
                    log.debug("LOGO A: "+imgEquipoA);
                    log.debug("LOGO B: "+imgEquipoB);
                    TeamServiceDTO equipoA = new TeamServiceDTO(descEquipoA,imgEquipoA);
                    TeamServiceDTO equipoB = new TeamServiceDTO(descEquipoB,imgEquipoB);

                    final Element partidoInfoEl = partidoEl.getElementsByClass("match_info").first();
                    final Element competicionEl = partidoInfoEl.getElementsByClass("match_comp").first();
                    final String competicion = competicionEl.text();
                    log.debug("COMPETICION: "+competicion);

                    final Element faseEl = partidoInfoEl.getElementsByClass("match_phase").first();
                    String fase = "";
                    if (faseEl != null) {
                        fase = faseEl.text();
                        log.debug("FASE: "+faseEl.text());
                    }

                    final Elements canalesEl = partidoEl.getElementsByClass("match_channels").first()
                                                    .getElementsByAttributeValueContaining("class","channelLnk");
                    final List<String> canales = new ArrayList<>();
                    canalesEl.forEach(canaleEl -> {
                        canales.add(canaleEl.text());
                    });
                    log.debug("CANALES: "+canales);
                    partidosDia.add(new MatchServiceDTO(spainTime.toLocalDateTime(),competicion,fase,equipoA,equipoB,canales));
                });

                final List<LocalDate> fechasPartidos = partidosDia.stream()
                        .map(MatchServiceDTO::fecha)
                        .map(LocalDateTime::toLocalDate)
                        .distinct().toList();

                if (fechasPartidos.size() != 1){
                    throw new RuntimeException("Error al scrapear los partidos");
                }

                dias.add(new DayServiceDTO(fecha,partidosDia.stream()
                        .map(MatchServiceDTO::fecha).findFirst().get(),partidosDia));

            });

            return dias;

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error leyendo " + url,
                    e
            );
        }
    }
}
