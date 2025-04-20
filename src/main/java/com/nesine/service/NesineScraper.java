package com.nesine.service;

import java.time.LocalDate;
import com.nesine.model.MatchDetail;
import java.util.List;
import com.nesine.model.MatchDetail;

public interface NesineScraper {
    /**
     * Returns a list of match details between the given date range (inclusive).
     * @param start Start date (inclusive)
     * @param end End date (inclusive)
     * @return List of match details
     */
    List<MatchDetail> getMatchesBetween(LocalDate start, LocalDate end);
}
