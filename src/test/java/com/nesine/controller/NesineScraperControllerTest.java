package com.nesine.controller;

import com.nesine.model.MatchDetail;
import com.nesine.service.NesineScraper;
import com.nesine.service.NesineScraper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NesineScraperController.class)
class NesineScraperControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NesineScraper nesineScraper;

    @Test
    void getMatches_returnsMockData() throws Exception {
        List<MatchDetail> mockMatches = List.of(
            new MatchDetail("Team A", "Team B", LocalDate.of(2025, 4, 20), "Super League", Map.of("home", 1.8, "draw", 3.2, "away", 2.1), new java.util.HashMap<>()),
            new MatchDetail("Team C", "Team D", LocalDate.of(2025, 4, 21), "Premier League", Map.of("home", 2.0, "draw", 3.0, "away", 2.5), new java.util.HashMap<>())
        );
        when(nesineScraper.getMatchesBetween(any(LocalDate.class), any(LocalDate.class))).thenReturn(mockMatches);
        mockMvc.perform(get("/api/matches?start=2025-04-20&end=2025-04-21"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].homeTeam").value("Team A"))
                .andExpect(jsonPath("$[1].awayTeam").value("Team D"))
                .andExpect(jsonPath("$[0].odds.home").value(1.8));
    }
}
