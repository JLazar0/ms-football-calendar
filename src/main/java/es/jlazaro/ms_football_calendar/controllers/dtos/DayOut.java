package es.jlazaro.ms_football_calendar.controllers.dtos;

import java.time.LocalDateTime;
import java.util.List;

public record DayOut(String desc, LocalDateTime fecha, List<MatchOut> partidos){
}
