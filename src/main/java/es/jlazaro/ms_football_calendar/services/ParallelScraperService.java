package es.jlazaro.ms_football_calendar.services;

import es.jlazaro.ms_football_calendar.services.dtos.DayServiceDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
public class ParallelScraperService {

    private final ScraperService scraperService;

    public List<DayServiceDTO> scrapeAll(){

        List<String> urls = List.of(
                "https://futboltv.info/"
        );

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {

            List<CompletableFuture<List<DayServiceDTO>>> futures =
                    urls.stream()
                            .map(url -> CompletableFuture.supplyAsync(
                                    () -> scraperService.scrape(url),
                                    executor
                            ))
                            .toList();

            CompletableFuture.allOf(
                    futures.toArray(CompletableFuture[]::new)
            ).join();

            return futures.stream()
                    .flatMap(f -> f.join().stream())
                    .toList();
        }
    }
}