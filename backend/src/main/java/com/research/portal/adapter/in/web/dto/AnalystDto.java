package com.research.portal.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Analysten-Profil mit Coverage-Universum und Performance-Kennzahlen")
public class AnalystDto {

    @Schema(description = "Eindeutige Analysten-ID", example = "1")
    private Long id;

    @Schema(description = "Vollständiger Name des Analysten", example = "Dr. Thomas Meier")
    private String name;

    @Schema(description = "Berufsbezeichnung", example = "Senior Equity Analyst")
    private String title;

    @Schema(description = "Abteilung", example = "Equity Research")
    private String department;

    @Schema(description = "Geschäftliche E-Mail-Adresse", example = "thomas.meier@zkb.ch")
    private String email;

    @Schema(description = "Liste der abgedeckten Wertschrift-Ticker",
            example = "[\"NESN\", \"NOVN\", \"ROG\", \"LONN\"]")
    private List<String> coverageUniverse;

    @Schema(description = "Analysten-Bewertung (1-5 Sterne)", example = "4", minimum = "1", maximum = "5")
    private int starRating;

    @Schema(description = "Trefferquote der Empfehlungen über 12 Monate in Prozent", example = "78.5")
    private double accuracy12m;

    public AnalystDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<String> getCoverageUniverse() { return coverageUniverse; }
    public void setCoverageUniverse(List<String> coverageUniverse) { this.coverageUniverse = coverageUniverse; }

    public int getStarRating() { return starRating; }
    public void setStarRating(int starRating) { this.starRating = starRating; }

    public double getAccuracy12m() { return accuracy12m; }
    public void setAccuracy12m(double accuracy12m) { this.accuracy12m = accuracy12m; }
}
