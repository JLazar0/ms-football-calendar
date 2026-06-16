package es.jlazaro.ms_football_calendar.mappers;

import es.jlazaro.ms_football_calendar.controllers.dtos.DayOut;
import es.jlazaro.ms_football_calendar.services.dtos.DayServiceDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MatchMapper {
    public abstract DayOut toDayOut(DayServiceDTO dayServiceDTO);
}
