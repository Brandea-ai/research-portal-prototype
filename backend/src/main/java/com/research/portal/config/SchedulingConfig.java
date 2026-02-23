package com.research.portal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Konfiguration fuer Spring Scheduling.
 *
 * <p>Aktiviert die Verarbeitung von @Scheduled-Annotationen.
 * In einer eigenen Config-Klasse statt auf der Application-Klasse,
 * damit @WebMvcTest-Slices die Scheduled-Beans nicht laden.
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
    // @EnableScheduling aktiviert @Scheduled Annotationen
}
