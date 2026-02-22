package com.research.portal.adapter.in.web.controller;

import com.research.portal.adapter.in.web.dto.ReportDto;
import com.research.portal.adapter.in.web.dto.XmlImportResponse;
import com.research.portal.adapter.in.web.dto.XmlValidationResponse;
import com.research.portal.adapter.in.web.mapper.ReportApiMapper;
import com.research.portal.application.service.XmlReportParserService;
import com.research.portal.domain.model.ResearchReport;
import com.research.portal.domain.port.in.ManageReportUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * REST-Controller für den XML-Import von Research Reports.
 *
 * Bietet Endpunkte zum Importieren, Validieren und Anzeigen
 * von Reports im XML-Format. Verwendet intern den
 * {@link XmlReportParserService} mit XPath-basiertem Parsing.
 *
 * Endpunkte:
 * <ul>
 *   <li>{@code POST /api/import/xml} - Importiert Reports aus hochgeladenem XML</li>
 *   <li>{@code GET /api/import/sample} - Gibt die Beispiel-XML zurück</li>
 *   <li>{@code POST /api/import/xml/validate} - Validiert XML gegen XSD</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/import")
public class XmlImportController {

    private static final Logger log = LoggerFactory.getLogger(XmlImportController.class);
    private static final String SAMPLE_XML_PATH = "sample-reports/research-reports.xml";
    private static final String SCHEMA_XSD_PATH = "sample-reports/research-report.xsd";

    private final XmlReportParserService xmlParserService;
    private final ManageReportUseCase manageReport;
    private final ReportApiMapper reportMapper;

    public XmlImportController(XmlReportParserService xmlParserService,
                               ManageReportUseCase manageReport,
                               ReportApiMapper reportMapper) {
        this.xmlParserService = xmlParserService;
        this.manageReport = manageReport;
        this.reportMapper = reportMapper;
    }

    /**
     * Importiert Research Reports aus einer hochgeladenen XML-Datei.
     *
     * Die XML-Datei wird mittels XPath geparst und die enthaltenen Reports
     * werden in der Datenbank gespeichert. Optional kann ein Ticker-Filter
     * angegeben werden, um nur bestimmte Wertschriften zu importieren.
     *
     * @param file die hochgeladene XML-Datei (multipart/form-data)
     * @param ticker optionaler Filter: nur Reports für diesen Ticker importieren
     * @return Importergebnis mit Anzahl und Liste der importierten Reports
     */
    @PostMapping(value = "/xml", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<XmlImportResponse> importFromXml(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "ticker", required = false) String ticker) {

        try {
            List<ResearchReport> parsedReports;

            if (ticker != null && !ticker.isBlank()) {
                parsedReports = xmlParserService
                        .parseReportByTicker(file.getInputStream(), ticker)
                        .map(List::of)
                        .orElse(List.of());
            } else {
                parsedReports = xmlParserService.parseReportsFromXml(file.getInputStream());
            }

            // Reports in der Datenbank speichern
            List<ReportDto> savedReports = parsedReports.stream()
                    .map(manageReport::createReport)
                    .map(reportMapper::toDto)
                    .toList();

            String statusMsg = String.format(
                    "%d Report(s) erfolgreich importiert", savedReports.size());

            log.info("XML-Import abgeschlossen: {}", statusMsg);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new XmlImportResponse(savedReports.size(), statusMsg, savedReports));

        } catch (XmlReportParserService.XmlParseException e) {
            log.error("XML-Parse-Fehler beim Import", e);
            return ResponseEntity.badRequest()
                    .body(new XmlImportResponse(0, "XML-Parse-Fehler: " + e.getMessage(), List.of()));
        } catch (IOException e) {
            log.error("I/O-Fehler beim XML-Import", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new XmlImportResponse(0, "Datei konnte nicht gelesen werden", List.of()));
        }
    }

    /**
     * Gibt die mitgelieferte Beispiel-XML-Datei zurück.
     *
     * Dient als Referenz für das erwartete XML-Format und enthält
     * 5 Schweizer Research Reports (NESN, NOVN, UBSG, ROG, ZURN).
     *
     * @return XML-Inhalt der Beispieldatei
     */
    @GetMapping(value = "/sample", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getSampleXml() {
        try {
            ClassPathResource resource = new ClassPathResource(SAMPLE_XML_PATH);
            String xmlContent = new String(
                    resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            return ResponseEntity.ok(xmlContent);
        } catch (IOException e) {
            log.error("Beispiel-XML konnte nicht geladen werden", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Validiert eine hochgeladene XML-Datei gegen das XSD-Schema.
     *
     * Prüft ob die Struktur und Datentypen des XML dem erwarteten
     * Research-Report-Schema entsprechen, ohne einen Import durchzuführen.
     *
     * @param file die zu validierende XML-Datei
     * @return Validierungsergebnis mit Status und Nachricht
     */
    @PostMapping(value = "/xml/validate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<XmlValidationResponse> validateXml(
            @RequestParam("file") MultipartFile file) {

        try {
            ClassPathResource xsdResource = new ClassPathResource(SCHEMA_XSD_PATH);
            InputStream xsdStream = xsdResource.getInputStream();
            InputStream xmlStream = file.getInputStream();

            boolean isValid = xmlParserService.validateXml(xmlStream, xsdStream);

            if (isValid) {
                return ResponseEntity.ok(
                        new XmlValidationResponse(true,
                                "XML ist valide und entspricht dem Schema"));
            } else {
                return ResponseEntity.ok(
                        new XmlValidationResponse(false,
                                "XML entspricht nicht dem erwarteten Schema"));
            }

        } catch (IOException e) {
            log.error("Fehler bei XML-Validierung", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new XmlValidationResponse(false,
                            "Validierung konnte nicht durchgeführt werden"));
        }
    }
}
