package com.research.portal.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Research Portal API",
                description = "REST API für das Banking Research Portal — Equity & Fixed-Income Analysen. "
                        + "Verwaltet Research Reports, Wertschriften und Analysten-Daten "
                        + "für eine Schweizer Grossbank.",
                version = "1.0.0",
                contact = @Contact(
                        name = "Armend Amerllahu",
                        email = "armend.amerllahu@brandea.ch"
                )
        ),
        tags = {
                @Tag(name = "Reports", description = "Research Report Verwaltung: Erstellen, Bearbeiten, Suchen und Löschen von Analysen"),
                @Tag(name = "Securities", description = "Wertschriften-Stammdaten: Aktien, Obligationen und Derivate im Coverage-Universum"),
                @Tag(name = "Analysts", description = "Analysten-Verwaltung: Profile, Coverage-Zuordnung und Performance-Kennzahlen")
        }
)
public class OpenApiConfig {
}
