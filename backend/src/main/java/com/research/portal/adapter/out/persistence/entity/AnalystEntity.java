package com.research.portal.adapter.out.persistence.entity;

import jakarta.persistence.*;

/**
 * JPA Entity für Analyst.
 * Diese Klasse gehört zur ADAPTER-Schicht, nicht zur Domain.
 * Sie enthält JPA-Annotationen (@Entity, @Column, etc.)
 * die in der Domain-Schicht verboten sind.
 */
@Entity
@Table(name = "analysts")
public class AnalystEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String title;

    private String department;

    @Column(unique = true)
    private String email;

    @Column(name = "coverage_universe")
    private String coverageUniverse;

    @Column(name = "star_rating")
    private int starRating;

    @Column(name = "accuracy_12m")
    private double accuracy12m;

    public AnalystEntity() {}

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

    public String getCoverageUniverse() { return coverageUniverse; }
    public void setCoverageUniverse(String coverageUniverse) { this.coverageUniverse = coverageUniverse; }

    public int getStarRating() { return starRating; }
    public void setStarRating(int starRating) { this.starRating = starRating; }

    public double getAccuracy12m() { return accuracy12m; }
    public void setAccuracy12m(double accuracy12m) { this.accuracy12m = accuracy12m; }
}
