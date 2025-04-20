package com.nesine.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;


import com.nesine.service.NesineScraper;
import com.nesine.model.MatchDetail;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class NesineScraperController {
    private final NesineScraper nesineScraper;

    @Autowired
    public NesineScraperController(NesineScraper nesineScraper) {
        this.nesineScraper = nesineScraper;
    }

    @GetMapping
    public List<MatchDetail> getMatches(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        return nesineScraper.getMatchesBetween(start, end);
    }
}
