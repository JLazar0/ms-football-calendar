package es.jlazaro.ms_football_calendar.controllers.transformers;

import es.jlazaro.ms_football_calendar.controllers.dtos.DayOut;
import es.jlazaro.ms_football_calendar.mappers.MatchMapper;
import es.jlazaro.ms_football_calendar.services.dtos.DayServiceDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MatchControllerTransformer {

    private final MatchMapper matchMapper;

    public List<DayOut> toDayOutList(List<DayServiceDTO> dayServiceDTOList){
        return dayServiceDTOList.stream().map(matchMapper::toDayOut).toList();
    }
}
