package com.nesine.service;

import com.nesine.model.MatchDetail;
import com.nesine.service.NesineScraperImpl;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class NesineScraperImplTest {
    @Test
    void getMatchesBetween_returnsMockData() {
        NesineScraperImpl scraper = new NesineScraperImpl() {
            @Override
            public List<MatchDetail> getMatchesBetween(LocalDate start, LocalDate end) {
                return List.of(
                    new MatchDetail("Team A", "Team B", LocalDate.of(2025, 4, 20), "Super League", Map.of("home", 1.8, "draw", 3.2, "away", 2.1), new java.util.HashMap<>()),
                    new MatchDetail("Team C", "Team D", LocalDate.of(2025, 4, 21), "Premier League", Map.of("home", 2.0, "draw", 3.0, "away", 2.5), new java.util.HashMap<>())
                );
            }
        };
        List<MatchDetail> matches = scraper.getMatchesBetween(LocalDate.of(2025, 4, 20), LocalDate.of(2025, 4, 21));
        assertEquals(2, matches.size());
        assertEquals("Team A", matches.get(0).getHomeTeam());
        assertEquals("Super League", matches.get(0).getLeague());
        assertEquals(1.8, matches.get(0).getOdds().get("home"));
    }
}
