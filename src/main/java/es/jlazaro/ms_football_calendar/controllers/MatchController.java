package es.jlazaro.ms_football_calendar.controllers;

import es.jlazaro.ms_football_calendar.controllers.dtos.DayOut;
import es.jlazaro.ms_football_calendar.controllers.transformers.MatchControllerTransformer;
import es.jlazaro.ms_football_calendar.services.ParallelScraperService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/match")
@RequiredArgsConstructor
public class MatchController {

    private final ParallelScraperService parallelScraperService;

    private final MatchControllerTransformer matchControllerTransformer;

    @GetMapping("/list")
    public List<DayOut> scrape() {
        return matchControllerTransformer.toDayOutList(parallelScraperService.scrapeAll());
    }
}
