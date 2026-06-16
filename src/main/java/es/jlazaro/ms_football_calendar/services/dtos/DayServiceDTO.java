package es.jlazaro.ms_football_calendar.services.dtos;

import java.time.LocalDateTime;
import java.util.List;

public record DayServiceDTO(String desc, LocalDateTime fecha, List<MatchServiceDTO> partidos){
}
