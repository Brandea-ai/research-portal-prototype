package com.research.portal.domain.model;

import java.util.List;

/**
 * Ein Research-Analyst der Bank.
 * Beobachtet eine Liste von Wertschriften (Coverage Universe)
 * und publiziert Reports mit Empfehlungen.
 *
 * Keine Spring/JPA Annotationen: reine Domain-Klasse.
 */
public class Analyst {

    private Long id;
    private String name;
    private String title;
    private String department;
    private String email;
    private List<String> coverageUniverse;
    private int starRating;
    private double accuracy12m;

    public Analyst() {}

    public Analyst(Long id, String name, String title, String department,
                   String email, List<String> coverageUniverse,
                   int starRating, double accuracy12m) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.department = department;
        this.email = email;
        this.coverageUniverse = coverageUniverse;
        this.starRating = starRating;
        this.accuracy12m = accuracy12m;
    }

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
