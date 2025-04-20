package com.nesine.model;

import java.time.LocalDate;
import java.util.Map;

public class MatchDetail {
    private String homeTeam;
    private String awayTeam;
    private LocalDate matchDate;
    private String league;
    private Map<String, Double> odds; // e.g. {"home": 1.85, "draw": 3.20, "away": 2.10}
    private Map<String, Map<String, Double>> allOdds; // e.g. {"1X2": {"1": 1.85, "X": 3.20, "2": 2.10}, "OU2.5": {"Over": 1.80, "Under": 2.00}, ...}

    public MatchDetail(String homeTeam, String awayTeam, LocalDate matchDate, String league, Map<String, Double> odds, Map<String, Map<String, Double>> allOdds) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.matchDate = matchDate;
        this.league = league;
        this.odds = odds;
        this.allOdds = allOdds;
    }

    public String getHomeTeam() { return homeTeam; }
    public String getAwayTeam() { return awayTeam; }
    public LocalDate getMatchDate() { return matchDate; }
    public String getLeague() { return league; }
    public Map<String, Double> getOdds() { return odds; }
    public Map<String, Map<String, Double>> getAllOdds() { return allOdds; }

    public void setHomeTeam(String homeTeam) { this.homeTeam = homeTeam; }
    public void setAwayTeam(String awayTeam) { this.awayTeam = awayTeam; }
    public void setMatchDate(LocalDate matchDate) { this.matchDate = matchDate; }
    public void setLeague(String league) { this.league = league; }
    public void setOdds(Map<String, Double> odds) { this.odds = odds; }
    public void setAllOdds(Map<String, Map<String, Double>> allOdds) { this.allOdds = allOdds; }

    @Override
    public String toString() {
        return String.format("%s vs %s (%s) [%s] Odds: %s, AllOdds: %s", homeTeam, awayTeam, matchDate, league, odds, allOdds);
    }
}
