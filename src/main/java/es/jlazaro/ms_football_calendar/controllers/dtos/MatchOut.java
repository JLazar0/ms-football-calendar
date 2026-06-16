package es.jlazaro.ms_football_calendar.controllers.dtos;

import java.time.LocalDateTime;
import java.util.List;

public record MatchOut(LocalDateTime fecha, String competicion, String fase,
                       TeamOut equipoA, TeamOut equipoB, List<String> canales) {
}
