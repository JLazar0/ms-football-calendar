package es.jlazaro.ms_football_calendar.services.dtos;

import java.time.LocalDateTime;
import java.util.List;

public record MatchServiceDTO(LocalDateTime fecha,String competicion,String fase,
    TeamServiceDTO equipoA, TeamServiceDTO equipoB, List<String> canales) {
}
