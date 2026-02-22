package com.research.portal.application.service;

import com.research.portal.domain.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.*;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service zum Parsen von Research Reports aus XML-Dateien mittels XPath.
 *
 * Demonstriert verschiedene XPath-Expressions:
 * <ul>
 *   <li>{@code //report} - Alle Reports selektieren</li>
 *   <li>{@code //report[security/ticker='NESN']} - Filter per Ticker-Symbol</li>
 *   <li>{@code //report[rating='BUY']} - Filter per Rating</li>
 *   <li>{@code //report[number(targetPrice) > 100]} - Numerischer Vergleich</li>
 *   <li>{@code //report/catalysts/catalyst} - Verschachtelte Elemente</li>
 * </ul>
 *
 * @see ResearchReport
 */
@Service
public class XmlReportParserService {

    private static final Logger log = LoggerFactory.getLogger(XmlReportParserService.class);

    /** XPath: Selektiert alle Report-Elemente */
    private static final String XPATH_ALL_REPORTS = "//report";

    /** XPath: Selektiert Reports per Ticker-Symbol */
    private static final String XPATH_BY_TICKER = "//report[security/ticker='%s']";

    /** XPath: Selektiert Reports per Rating */
    private static final String XPATH_BY_RATING = "//report[rating='%s']";

    /** XPath: Selektiert Reports mit Kursziel über einem Schwellenwert */
    private static final String XPATH_TARGET_ABOVE = "//report[number(targetPrice) > %s]";

    /** XPath: Selektiert alle Katalysatoren eines Reports */
    private static final String XPATH_CATALYSTS = "catalysts/catalyst";

    /** XPath: Selektiert alle Risiken eines Reports */
    private static final String XPATH_RISKS = "risks/risk";

    /** XPath: Selektiert alle Tags eines Reports */
    private static final String XPATH_TAGS = "tags/tag";

    private final XPathFactory xPathFactory;
    private final DocumentBuilderFactory documentBuilderFactory;

    public XmlReportParserService() {
        this.xPathFactory = XPathFactory.newInstance();
        this.documentBuilderFactory = createSecureDocumentBuilderFactory();
    }

    /**
     * Parst alle Research Reports aus einem XML-InputStream.
     *
     * Verwendet den XPath-Ausdruck {@code //report}, um alle Report-Elemente
     * im Dokument zu finden und in Domain-Objekte zu konvertieren.
     *
     * @param xml InputStream mit XML-Daten im erwarteten Schema
     * @return Liste aller geparsten ResearchReport-Objekte
     * @throws XmlParseException wenn das XML nicht gelesen oder geparst werden kann
     */
    public List<ResearchReport> parseReportsFromXml(InputStream xml) {
        try {
            Document document = parseDocument(xml);
            XPath xpath = xPathFactory.newXPath();

            NodeList reportNodes = (NodeList) xpath.evaluate(
                    XPATH_ALL_REPORTS, document, XPathConstants.NODESET);

            List<ResearchReport> reports = new ArrayList<>();
            for (int i = 0; i < reportNodes.getLength(); i++) {
                Element reportElement = (Element) reportNodes.item(i);
                reports.add(mapElementToReport(reportElement, xpath));
            }

            log.info("{} Reports aus XML geparst", reports.size());
            return reports;

        } catch (XPathExpressionException | IOException | SAXException |
                 ParserConfigurationException e) {
            throw new XmlParseException("Fehler beim Parsen der XML-Daten", e);
        }
    }

    /**
     * Sucht einen spezifischen Report anhand des Ticker-Symbols der Wertschrift.
     *
     * Verwendet den XPath-Ausdruck {@code //report[security/ticker='NESN']},
     * um gezielt nach einem Ticker zu filtern.
     *
     * @param xml InputStream mit XML-Daten
     * @param ticker Ticker-Symbol der gesuchten Wertschrift (z.B. "NESN", "NOVN")
     * @return Optional mit dem gefundenen Report, oder leer wenn nicht vorhanden
     * @throws XmlParseException wenn das XML nicht gelesen oder geparst werden kann
     */
    public Optional<ResearchReport> parseReportByTicker(InputStream xml, String ticker) {
        try {
            Document document = parseDocument(xml);
            XPath xpath = xPathFactory.newXPath();

            String expression = String.format(XPATH_BY_TICKER, ticker);
            NodeList reportNodes = (NodeList) xpath.evaluate(
                    expression, document, XPathConstants.NODESET);

            if (reportNodes.getLength() == 0) {
                log.debug("Kein Report gefunden für Ticker: {}", ticker);
                return Optional.empty();
            }

            Element reportElement = (Element) reportNodes.item(0);
            ResearchReport report = mapElementToReport(reportElement, xpath);
            log.info("Report für Ticker {} gefunden: {}", ticker, report.getTitle());
            return Optional.of(report);

        } catch (XPathExpressionException | IOException | SAXException |
                 ParserConfigurationException e) {
            throw new XmlParseException(
                    "Fehler beim Suchen des Reports für Ticker: " + ticker, e);
        }
    }

    /**
     * Filtert Reports nach Rating (Analystenempfehlung).
     *
     * Verwendet den XPath-Ausdruck {@code //report[rating='BUY']},
     * um nach einer bestimmten Empfehlung zu filtern.
     *
     * @param xml InputStream mit XML-Daten
     * @param rating die gewünschte Analystenempfehlung
     * @return Liste der Reports mit dem angegebenen Rating
     * @throws XmlParseException wenn das XML nicht gelesen oder geparst werden kann
     */
    public List<ResearchReport> parseReportsByRating(InputStream xml, Rating rating) {
        try {
            Document document = parseDocument(xml);
            XPath xpath = xPathFactory.newXPath();

            String expression = String.format(XPATH_BY_RATING, rating.name());
            NodeList reportNodes = (NodeList) xpath.evaluate(
                    expression, document, XPathConstants.NODESET);

            List<ResearchReport> reports = new ArrayList<>();
            for (int i = 0; i < reportNodes.getLength(); i++) {
                Element reportElement = (Element) reportNodes.item(i);
                reports.add(mapElementToReport(reportElement, xpath));
            }

            log.info("{} Reports mit Rating {} gefunden", reports.size(), rating);
            return reports;

        } catch (XPathExpressionException | IOException | SAXException |
                 ParserConfigurationException e) {
            throw new XmlParseException(
                    "Fehler beim Filtern nach Rating: " + rating, e);
        }
    }

    /**
     * Filtert Reports nach Kursziel über einem Schwellenwert.
     *
     * Verwendet den XPath-Ausdruck {@code //report[number(targetPrice) > 100]},
     * um numerische Vergleiche direkt in XPath durchzuführen.
     *
     * @param xml InputStream mit XML-Daten
     * @param minTargetPrice Mindestkursziel als Schwellenwert
     * @return Liste der Reports mit Kursziel über dem Schwellenwert
     * @throws XmlParseException wenn das XML nicht gelesen oder geparst werden kann
     */
    public List<ResearchReport> parseReportsWithTargetAbove(InputStream xml,
                                                             BigDecimal minTargetPrice) {
        try {
            Document document = parseDocument(xml);
            XPath xpath = xPathFactory.newXPath();

            String expression = String.format(XPATH_TARGET_ABOVE,
                    minTargetPrice.toPlainString());
            NodeList reportNodes = (NodeList) xpath.evaluate(
                    expression, document, XPathConstants.NODESET);

            List<ResearchReport> reports = new ArrayList<>();
            for (int i = 0; i < reportNodes.getLength(); i++) {
                Element reportElement = (Element) reportNodes.item(i);
                reports.add(mapElementToReport(reportElement, xpath));
            }

            log.info("{} Reports mit Kursziel > {} gefunden",
                    reports.size(), minTargetPrice);
            return reports;

        } catch (XPathExpressionException | IOException | SAXException |
                 ParserConfigurationException e) {
            throw new XmlParseException(
                    "Fehler beim Filtern nach Kursziel > " + minTargetPrice, e);
        }
    }

    /**
     * Validiert ein XML-Dokument gegen ein XSD-Schema.
     *
     * Prüft ob die Struktur und Datentypen des XML dem erwarteten Schema
     * entsprechen, bevor ein Import durchgeführt wird.
     *
     * @param xml InputStream mit den zu validierenden XML-Daten
     * @param xsd InputStream mit dem XSD-Validierungsschema
     * @return true wenn das XML valide ist, false bei Validierungsfehlern
     */
    public boolean validateXml(InputStream xml, InputStream xsd) {
        try {
            SchemaFactory schemaFactory = SchemaFactory.newInstance(
                    XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(new StreamSource(xsd));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(xml));

            log.info("XML-Validierung erfolgreich");
            return true;

        } catch (SAXException e) {
            log.warn("XML-Validierung fehlgeschlagen: {}", e.getMessage());
            return false;
        } catch (IOException e) {
            log.error("I/O-Fehler bei XML-Validierung", e);
            return false;
        }
    }

    // ==================== Private Hilfsmethoden ====================

    /**
     * Erstellt eine sichere DocumentBuilderFactory mit deaktivierten
     * externen Entities (XXE-Schutz).
     */
    private DocumentBuilderFactory createSecureDocumentBuilderFactory() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            // XXE-Schutz: Externe Entities und DTDs deaktivieren
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setFeature(
                    "http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature(
                    "http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature(
                    "http://xml.org/sax/features/external-parameter-entities", false);
        } catch (ParserConfigurationException e) {
            log.warn("Nicht alle XML-Sicherheitsfeatures konnten aktiviert werden", e);
        }
        return factory;
    }

    /**
     * Parst einen InputStream zu einem DOM-Document.
     */
    private Document parseDocument(InputStream xml)
            throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
        return builder.parse(xml);
    }

    /**
     * Konvertiert ein XML-Report-Element in ein ResearchReport Domain-Objekt.
     *
     * Verwendet relative XPath-Expressions, um die einzelnen Felder
     * aus dem Element zu extrahieren, einschliesslich verschachtelter
     * Strukturen wie Katalysatoren, Risiken und Tags.
     */
    private ResearchReport mapElementToReport(Element reportElement, XPath xpath)
            throws XPathExpressionException {

        ResearchReport report = new ResearchReport();

        // Einfache Text-Felder
        report.setTitle(getTextContent(reportElement, "title"));
        report.setExecutiveSummary(
                getTextContent(reportElement, "executiveSummary").trim());

        // Analyst-Referenz (verschachtelt)
        String analystIdStr = (String) xpath.evaluate(
                "analyst/analystId", reportElement, XPathConstants.STRING);
        if (!analystIdStr.isEmpty()) {
            report.setAnalystId(Long.parseLong(analystIdStr));
        }

        // Wertschrift-Referenz (verschachtelt)
        String securityIdStr = (String) xpath.evaluate(
                "security/securityId", reportElement, XPathConstants.STRING);
        if (!securityIdStr.isEmpty()) {
            report.setSecurityId(Long.parseLong(securityIdStr));
        }

        // Zeitstempel
        String publishedAtStr = getTextContent(reportElement, "publishedAt");
        if (!publishedAtStr.isEmpty()) {
            report.setPublishedAt(LocalDateTime.parse(publishedAtStr));
        }

        // Enums
        String reportTypeStr = getTextContent(reportElement, "reportType");
        if (!reportTypeStr.isEmpty()) {
            report.setReportType(ReportType.valueOf(reportTypeStr));
        }

        String ratingStr = getTextContent(reportElement, "rating");
        if (!ratingStr.isEmpty()) {
            report.setRating(Rating.valueOf(ratingStr));
        }

        String previousRatingStr = getTextContent(reportElement, "previousRating");
        if (!previousRatingStr.isEmpty()) {
            report.setPreviousRating(Rating.valueOf(previousRatingStr));
            report.setRatingChanged(!ratingStr.equals(previousRatingStr));
        }

        String riskLevelStr = getTextContent(reportElement, "riskLevel");
        if (!riskLevelStr.isEmpty()) {
            report.setRiskLevel(RiskLevel.valueOf(riskLevelStr));
        }

        // Finanzkennzahlen
        String targetPriceStr = getTextContent(reportElement, "targetPrice");
        if (!targetPriceStr.isEmpty()) {
            report.setTargetPrice(new BigDecimal(targetPriceStr));
        }

        String previousTargetStr = getTextContent(reportElement, "previousTarget");
        if (!previousTargetStr.isEmpty()) {
            report.setPreviousTarget(new BigDecimal(previousTargetStr));
        }

        String currentPriceStr = getTextContent(reportElement, "currentPrice");
        if (!currentPriceStr.isEmpty()) {
            report.setCurrentPrice(new BigDecimal(currentPriceStr));
        }

        // Implied Upside berechnen
        if (report.getCurrentPrice() != null && report.getTargetPrice() != null
                && report.getCurrentPrice().signum() > 0) {
            BigDecimal upside = report.getTargetPrice()
                    .subtract(report.getCurrentPrice())
                    .multiply(BigDecimal.valueOf(100))
                    .divide(report.getCurrentPrice(), 2, RoundingMode.HALF_UP);
            report.setImpliedUpside(upside);
        }

        // Verschachtelte Listen per XPath: //report/catalysts/catalyst
        report.setInvestmentCatalysts(
                extractStringList(reportElement, xpath, XPATH_CATALYSTS));

        // Verschachtelte Listen per XPath: //report/risks/risk
        report.setKeyRisks(
                extractStringList(reportElement, xpath, XPATH_RISKS));

        // Verschachtelte Listen per XPath: //report/tags/tag
        report.setTags(
                extractStringList(reportElement, xpath, XPATH_TAGS));

        return report;
    }

    /**
     * Extrahiert den Textinhalt eines direkten Kind-Elements.
     */
    private String getTextContent(Element parent, String childName) {
        NodeList children = parent.getElementsByTagName(childName);
        if (children.getLength() > 0) {
            return children.item(0).getTextContent();
        }
        return "";
    }

    /**
     * Extrahiert eine Liste von String-Werten mittels XPath-Expression.
     * Wird für verschachtelte Elemente wie Katalysatoren, Risiken und Tags verwendet.
     */
    private List<String> extractStringList(Element context, XPath xpath,
                                           String expression)
            throws XPathExpressionException {

        NodeList nodes = (NodeList) xpath.evaluate(
                expression, context, XPathConstants.NODESET);

        List<String> result = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            String text = nodes.item(i).getTextContent();
            if (text != null && !text.trim().isEmpty()) {
                result.add(text.trim());
            }
        }
        return result;
    }

    /**
     * Exception für XML-Parse-Fehler.
     * Wird geworfen wenn die XML-Verarbeitung fehlschlägt.
     */
    public static class XmlParseException extends RuntimeException {
        public XmlParseException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
