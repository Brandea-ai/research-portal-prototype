-- ============================================================
-- Research Portal: Demo-Daten (Schweizer Wertschriften)
-- Profil: local (H2 In-Memory)
-- ============================================================

-- ────────────────────────────────────────────────
-- ANALYSTEN
-- ────────────────────────────────────────────────

INSERT INTO analysts (id, name, title, department, email, coverage_universe, star_rating, accuracy_12m)
VALUES (1, 'Dr. Lukas Meier', 'Senior Equity Analyst', 'Equity Research', 'lukas.meier@researchportal.ch', 'NESN,NOVN,ROG,LONN', 5, 87.3);

INSERT INTO analysts (id, name, title, department, email, coverage_universe, star_rating, accuracy_12m)
VALUES (2, 'Sarah Brunner', 'Lead Financial Analyst', 'Financial Research', 'sarah.brunner@researchportal.ch', 'UBSG,ZURN,SREN', 4, 82.1);

INSERT INTO analysts (id, name, title, department, email, coverage_universe, star_rating, accuracy_12m)
VALUES (3, 'Marc Keller', 'Senior Industrials Analyst', 'Sector Research', 'marc.keller@researchportal.ch', 'ABBN,SIKA,GIVN', 4, 79.5);

INSERT INTO analysts (id, name, title, department, email, coverage_universe, star_rating, accuracy_12m)
VALUES (4, 'Dr. Anna Widmer', 'Head of Credit Research', 'Fixed Income', 'anna.widmer@researchportal.ch', 'UBSG,SREN,ZURN', 5, 91.2);

INSERT INTO analysts (id, name, title, department, email, coverage_universe, star_rating, accuracy_12m)
VALUES (5, 'Thomas Gerber', 'Junior Equity Analyst', 'Equity Research', 'thomas.gerber@researchportal.ch', 'NESN,GIVN,LONN', 3, 74.8);

-- ────────────────────────────────────────────────
-- WERTSCHRIFTEN (Schweizer Blue Chips, SIX Swiss Exchange)
-- ────────────────────────────────────────────────

INSERT INTO securities (id, ticker, isin, name, asset_class, sector, industry, exchange_name, currency, market_cap)
VALUES (1, 'NESN', 'CH0038863350', 'Nestlé SA', 'EQUITY', 'Consumer Staples', 'Food Products', 'SIX', 'CHF', 245000000000.00);

INSERT INTO securities (id, ticker, isin, name, asset_class, sector, industry, exchange_name, currency, market_cap)
VALUES (2, 'NOVN', 'CH0012005267', 'Novartis AG', 'EQUITY', 'Healthcare', 'Pharmaceuticals', 'SIX', 'CHF', 198000000000.00);

INSERT INTO securities (id, ticker, isin, name, asset_class, sector, industry, exchange_name, currency, market_cap)
VALUES (3, 'ROG', 'CH0012032048', 'Roche Holding AG', 'EQUITY', 'Healthcare', 'Pharmaceuticals', 'SIX', 'CHF', 210000000000.00);

INSERT INTO securities (id, ticker, isin, name, asset_class, sector, industry, exchange_name, currency, market_cap)
VALUES (4, 'UBSG', 'CH0244767585', 'UBS Group AG', 'EQUITY', 'Financials', 'Banking', 'SIX', 'CHF', 95000000000.00);

INSERT INTO securities (id, ticker, isin, name, asset_class, sector, industry, exchange_name, currency, market_cap)
VALUES (5, 'ZURN', 'CH0011075394', 'Zurich Insurance Group', 'EQUITY', 'Financials', 'Insurance', 'SIX', 'CHF', 78000000000.00);

INSERT INTO securities (id, ticker, isin, name, asset_class, sector, industry, exchange_name, currency, market_cap)
VALUES (6, 'SREN', 'CH0126881561', 'Swiss Re AG', 'EQUITY', 'Financials', 'Reinsurance', 'SIX', 'CHF', 35000000000.00);

INSERT INTO securities (id, ticker, isin, name, asset_class, sector, industry, exchange_name, currency, market_cap)
VALUES (7, 'ABBN', 'CH0012221716', 'ABB Ltd', 'EQUITY', 'Industrials', 'Electrical Equipment', 'SIX', 'CHF', 88000000000.00);

INSERT INTO securities (id, ticker, isin, name, asset_class, sector, industry, exchange_name, currency, market_cap)
VALUES (8, 'LONN', 'CH0013841017', 'Lonza Group AG', 'EQUITY', 'Healthcare', 'Life Sciences', 'SIX', 'CHF', 42000000000.00);

INSERT INTO securities (id, ticker, isin, name, asset_class, sector, industry, exchange_name, currency, market_cap)
VALUES (9, 'GIVN', 'CH0010645932', 'Givaudan SA', 'EQUITY', 'Materials', 'Specialty Chemicals', 'SIX', 'CHF', 38000000000.00);

INSERT INTO securities (id, ticker, isin, name, asset_class, sector, industry, exchange_name, currency, market_cap)
VALUES (10, 'SIKA', 'CH0418792922', 'Sika AG', 'EQUITY', 'Materials', 'Building Materials', 'SIX', 'CHF', 44000000000.00);

-- ────────────────────────────────────────────────
-- RESEARCH REPORTS
-- ────────────────────────────────────────────────

-- Report 1: Nestlé Initiation (Analyst: Dr. Lukas Meier)
INSERT INTO research_reports (id, analyst_id, security_id, published_at, report_type, title, executive_summary, full_text, rating, previous_rating, rating_changed, target_price, previous_target, current_price, implied_upside, risk_level, investment_catalysts, key_risks, tags)
VALUES (1, 1, 1, '2026-01-15 08:30:00', 'INITIATION',
    'Nestlé SA: Defensive Qualität in unsicheren Zeiten',
    'Nestlé bleibt ein erstklassiger defensiver Titel mit starker Preissetzungsmacht. Die laufende Portfolio-Optimierung und der Fokus auf wachstumsstarke Kategorien (Health Science, Coffee) stützen das organische Wachstum. Wir initiieren die Coverage mit BUY und einem Kursziel von CHF 98.',
    'Die strategische Neuausrichtung unter CEO Laurent Freixe zeigt erste Erfolge. Das organische Wachstum beschleunigt sich auf 4.2% im letzten Quartal, getrieben durch Preiserhöhungen und positive Mix-Effekte. Die EBIT-Marge stabilisiert sich bei 17.1%. Der Abbau von Randaktivitäten (Wassergeschäft, Tiefkühlkost) schärft das Profil. Die Free-Cashflow-Generierung bleibt beeindruckend mit CHF 11.2 Mrd. p.a., was eine Dividendenrendite von 3.1% und Aktienrückkäufe ermöglicht.',
    'BUY', NULL, false, 98.00, NULL, 84.50, 15.98, 'LOW',
    'Portfolio-Optimierung|Health Science Wachstum|Preissetzungsmacht|Emerging Markets Recovery',
    'Konsumentenschwäche in Europa|Rohstoffpreisvolatilität|Wechselkursrisiken CHF',
    'Initiation|Consumer Staples|Defensive|Dividende');

-- Report 2: UBS Group Update (Analyst: Sarah Brunner)
INSERT INTO research_reports (id, analyst_id, security_id, published_at, report_type, title, executive_summary, full_text, rating, previous_rating, rating_changed, target_price, previous_target, current_price, implied_upside, risk_level, investment_catalysts, key_risks, tags)
VALUES (2, 2, 4, '2026-01-22 07:15:00', 'UPDATE',
    'UBS Group: Credit Suisse Integration auf Kurs',
    'Die Integration der Credit Suisse verläuft planmässig. Die Kostensynergien übertreffen die Erwartungen mit USD 8.5 Mrd. realisiert (Ziel: USD 13 Mrd. bis 2027). Das Wealth Management zeigt starke Nettoneugelder von USD 27 Mrd. im Q4. Wir erhöhen unser Kursziel auf CHF 32.',
    'UBS demonstriert operative Exzellenz bei der grössten Bankenintegration Europas. Die CET1-Quote liegt komfortabel bei 14.3%, deutlich über dem regulatorischen Minimum. Das Investment Banking erholt sich mit einem Ertragswachstum von 18% YoY. Die Cost-Income-Ratio verbessert sich auf 74% (Ziel: unter 70% bis 2028). Wir sehen erhebliches Potenzial für Kapitalrückführungen ab 2027.',
    'BUY', 'HOLD', true, 32.00, 27.50, 28.40, 12.68, 'MEDIUM',
    'CS-Integration Synergien|Wealth Management Inflows|Kapitalrückführung ab 2027|Marktanteilsgewinne Asien',
    'Regulatorische Verschärfung TBTF|Litigation-Risiken CS-Altlasten|Zinsumfeld-Unsicherheit',
    'Upgrade|Financials|Integration|Wealth Management');

-- Report 3: Novartis Quarterly (Analyst: Dr. Lukas Meier)
INSERT INTO research_reports (id, analyst_id, security_id, published_at, report_type, title, executive_summary, full_text, rating, previous_rating, rating_changed, target_price, previous_target, current_price, implied_upside, risk_level, investment_catalysts, key_risks, tags)
VALUES (3, 1, 2, '2026-02-01 06:45:00', 'QUARTERLY',
    'Novartis Q4 2025: Pipeline liefert, Entresto treibt',
    'Novartis übertrifft die Konsenserwartungen im Q4 deutlich. Der Umsatz wächst 9% (cc) auf USD 13.8 Mrd., getrieben durch Entresto (+32%) und Kisqali (+68%). Die Core Operating Margin erreicht 39.8%. Wir bestätigen BUY mit erhöhtem Kursziel CHF 108.',
    'Die fokussierte Pharma-Strategie nach der Sandoz-Abspaltung trägt Früchte. Die Pipeline zeigt bemerkenswerte Tiefe mit 8 potenziellen Blockbustern in Phase III. Entresto bleibt der Wachstumstreiber mit einem Peak-Sales-Potenzial von USD 9 Mrd. Kisqali gewinnt Marktanteile im Brustkrebs-Segment. Die Radioligand-Therapie-Plattform (Pluvicto) öffnet ein neues Kapitel in der Onkologie. Der Free Cashflow von USD 14.2 Mrd. ermöglicht aggressive Aktienrückkäufe.',
    'BUY', 'BUY', false, 108.00, 102.00, 95.30, 13.33, 'LOW',
    'Entresto Peak Sales|Kisqali Marktanteilsgewinne|Radioligand-Plattform|Pipeline-Katalysatoren 2026',
    'Patentabläufe ab 2028|Pricing-Druck USA|Pipeline-Rückschläge',
    'Quarterly|Healthcare|Pipeline|Blockbuster');

-- Report 4: ABB Flash Note (Analyst: Marc Keller)
INSERT INTO research_reports (id, analyst_id, security_id, published_at, report_type, title, executive_summary, full_text, rating, previous_rating, rating_changed, target_price, previous_target, current_price, implied_upside, risk_level, investment_catalysts, key_risks, tags)
VALUES (4, 3, 7, '2026-02-05 09:00:00', 'FLASH',
    'ABB: Elektrifizierungs-Boom bestätigt, Auftragseingang stark',
    'ABBs Auftragseingang im Q4 übertrifft mit USD 9.1 Mrd. die Erwartungen deutlich (+14% YoY). Besonders die Division Electrification glänzt mit +22% organischem Wachstum. Der Megatrend Elektrifizierung und Datacenter-Ausbau stützt die Visibilität. Wir bestätigen STRONG_BUY.',
    'ABB profitiert wie kaum ein anderes Industrieunternehmen von den Megatrends Elektrifizierung, Automatisierung und Energieeffizienz. Die Datacenter-Nachfrage explodiert mit einem Auftragsplus von 45% in diesem Segment. Die Marge der Division Electrification erreicht Rekordniveau bei 23.4%. Das Management bestätigt die mittelfristigen Ziele einer operativen Marge von 19-20% auf Konzernebene.',
    'STRONG_BUY', 'STRONG_BUY', false, 58.00, 54.00, 48.20, 20.33, 'LOW',
    'Datacenter-Investitionszyklus|Elektrifizierungs-Megatrend|Margin Expansion|Portfoliobereinigung',
    'Konjunkturabschwächung China|Auftragsrückgang kurzzyklisches Geschäft|Wettbewerb Siemens/Schneider',
    'Flash|Industrials|Elektrifizierung|Datacenter');

-- Report 5: Swiss Re Deep Dive (Analyst: Sarah Brunner)
INSERT INTO research_reports (id, analyst_id, security_id, published_at, report_type, title, executive_summary, full_text, rating, previous_rating, rating_changed, target_price, previous_target, current_price, implied_upside, risk_level, investment_catalysts, key_risks, tags)
VALUES (5, 2, 6, '2026-02-08 08:00:00', 'DEEP_DIVE',
    'Swiss Re: Rückversicherungs-Zyklus intakt, Pricing Power hält',
    'Unsere Tiefenanalyse des globalen Rückversicherungsmarkts bestätigt die attraktive Positionierung von Swiss Re. Die Combined Ratio verbessert sich auf 89.2%, die Prämien wachsen 8% organisch. Die Kapitalstärke (SST-Ratio: 275%) ermöglicht Sonderdividenden. Wir stufen auf BUY hoch.',
    'Der Rückversicherungszyklus bleibt im Aufwärtstrend. Steigende Naturkatastrophen-Schäden erhöhen die Eintrittsbarrieren und stützen die Preissetzungsmacht. Swiss Re profitiert von der Diversifikation über Life, P&C und Corporate Solutions. Das Reservepolster ist komfortabel mit einer Reserveüberdeckung von geschätzt CHF 2.8 Mrd. Die iptiQ-Plattform zeigt Fortschritte im B2B2C-Segment. Die Dividendenrendite von 5.8% ist im aktuellen Zinsumfeld weiterhin attraktiv.',
    'BUY', 'HOLD', true, 128.00, 112.00, 109.50, 16.89, 'MEDIUM',
    'Harter Rückversicherungsmarkt|Sonderdividenden-Potenzial|iptiQ Skalierung|Reserveauflösungen',
    'Grossschaden-Ereignisse (NatCat)|Reserveentwicklung|Zinsrisiko Anlagenportfolio',
    'Deep Dive|Financials|Rückversicherung|Upgrade');

-- Report 6: Roche Update (Analyst: Dr. Lukas Meier)
INSERT INTO research_reports (id, analyst_id, security_id, published_at, report_type, title, executive_summary, full_text, rating, previous_rating, rating_changed, target_price, previous_target, current_price, implied_upside, risk_level, investment_catalysts, key_risks, tags)
VALUES (6, 1, 3, '2026-02-10 07:30:00', 'UPDATE',
    'Roche: Obesity-Pipeline als Game Changer',
    'Roches Einstieg in den Obesity-Markt mit CT-996 könnte den Konzern transformieren. Phase-I-Daten zeigen 7.3% Gewichtsverlust in 4 Wochen, kompetitiv mit Novo Nordisk. Der Diagnostics-Bereich stabilisiert sich nach dem COVID-Rückgang. Wir erhöhen auf HOLD von SELL.',
    'Roche durchlebt eine Transformation. Die Post-COVID-Normalisierung im Diagnostics-Geschäft ist weitgehend abgeschlossen. Im Pharma-Segment kompensieren neue Produkte (Vabysmo, Phesgo, Polivy) zunehmend die Biosimilar-Erosion bei Avastin und Herceptin. Der eigentliche Kurstreiber ist die GLP-1-Pipeline: CT-996 (oral, einmal täglich) zeigt vielversprechende frühe Daten. Bei Erfolg könnte der adressierbare Markt über USD 100 Mrd. erreichen. Wir sehen Aufwärtspotenzial, aber die Bewertung preist bereits Teilerfolge ein.',
    'HOLD', 'SELL', true, 278.00, 245.00, 262.00, 6.11, 'MEDIUM',
    'CT-996 Obesity Pipeline|Vabysmo Blockbuster-Potenzial|Diagnostics Stabilisierung|Neue Onkologie-Assets',
    'GLP-1 Wettbewerb (Novo, Lilly)|Biosimilar-Erosion|Pipeline-Misserfolge',
    'Upgrade|Healthcare|Obesity|Pipeline');

-- Report 7: Sika Quarterly (Analyst: Marc Keller)
INSERT INTO research_reports (id, analyst_id, security_id, published_at, report_type, title, executive_summary, full_text, rating, previous_rating, rating_changed, target_price, previous_target, current_price, implied_upside, risk_level, investment_catalysts, key_risks, tags)
VALUES (7, 3, 10, '2026-02-12 06:30:00', 'QUARTERLY',
    'Sika Q4: MBCC-Integration liefert, Margen steigen',
    'Sika meldet ein solides Q4 mit 6.8% organischem Wachstum und einer EBITDA-Marge von 19.8% (+120 Bp. YoY). Die MBCC-Integration verläuft über Plan mit CHF 180 Mio. realisierten Synergien. Die geografische Diversifikation zahlt sich aus. Wir bestätigen BUY.',
    'Sika bleibt ein Qualitäts-Compounder im Baustoffsektor. Das organische Wachstum wird von der Infrastruktur-Nachfrage (55% des Umsatzes) und Renovierungen getragen. Die MBCC-Akquisition transformiert Sika zum unangefochtenen Marktführer in Bauzusatzstoffen. Die Integration läuft über Plan: Synergien von CHF 180 Mio. übertreffen das Ziel von CHF 160 Mio. Der asiatische Markt wächst zweistellig (+13% organisch). Wir erwarten eine weitere Margenexpansion Richtung 21% EBITDA-Marge bis 2027.',
    'BUY', 'BUY', false, 295.00, 280.00, 258.00, 14.34, 'LOW',
    'MBCC Synergien über Plan|Infrastruktur-Megatrend|Emerging Markets Wachstum|Margenexpansion',
    'Bau-Zyklus Abschwächung Europa|Rohstoffkosten|Integration-Execution-Risiko',
    'Quarterly|Materials|Integration|Infrastruktur');

-- Report 8: Zurich Insurance Update (Analyst: Dr. Anna Widmer)
INSERT INTO research_reports (id, analyst_id, security_id, published_at, report_type, title, executive_summary, full_text, rating, previous_rating, rating_changed, target_price, previous_target, current_price, implied_upside, risk_level, investment_catalysts, key_risks, tags)
VALUES (8, 4, 5, '2026-02-15 07:00:00', 'UPDATE',
    'Zurich Insurance: Solide Execution, attraktive Rendite',
    'Zurich Insurance liefert verlässlich. Der Business Operating Profit steigt 7% auf USD 7.4 Mrd. Die Combined Ratio im P&C-Geschäft verbessert sich auf 93.1%. Die Dividendenrendite von 5.2% bei einer Ausschüttungsquote von 75% ist nachhaltig. Wir bestätigen HOLD, Bewertung fair.',
    'Zurich Insurance ist ein Muster an operativer Disziplin. Alle drei Segmente (P&C, Life, Farmers) liefern stabile Beiträge. Die Prämienentwicklung im P&C-Geschäft bleibt mit +5% positiv. Das Life-Geschäft profitiert vom höheren Zinsniveau mit einer verbesserten Neugeschäftsmarge von 6.2%. Farmers Exchanges zeigt beschleunigtes Prämienwachstum. Die Solvenzquote (SST) von 267% bietet Spielraum für Aktienrückkäufe. Die Bewertung mit einem KGV von 13.5x ist angemessen, aber nicht günstig.',
    'HOLD', 'HOLD', false, 520.00, 510.00, 498.00, 4.42, 'LOW',
    'Stabile Dividende 5%+|P&C Pricing Power|Farmers Turnaround|Solvenzstärke',
    'NatCat-Grossereignis|Zinsrückgang belastet Life|Regulierung Solvency II',
    'Update|Financials|Insurance|Dividende');

-- Report 9: Givaudan Update (Analyst: Thomas Gerber)
INSERT INTO research_reports (id, analyst_id, security_id, published_at, report_type, title, executive_summary, full_text, rating, previous_rating, rating_changed, target_price, previous_target, current_price, implied_upside, risk_level, investment_catalysts, key_risks, tags)
VALUES (9, 5, 9, '2026-02-18 08:15:00', 'UPDATE',
    'Givaudan: Premium-Bewertung gerechtfertigt',
    'Givaudan wächst organisch 5.4%, getrieben durch Volumenerholung und Innovation. Die EBITDA-Marge erreicht 22.1%. Das Geschäftsmodell bleibt hochattraktiv mit 80% wiederkehrenden Umsätzen. Kursziel CHF 4200 bestätigt. HOLD aufgrund voller Bewertung.',
    'Givaudan ist der weltweit grösste Anbieter von Aromen und Duftstoffen, ein Geschäft mit hohen Eintrittsbarrieren und starker Kundenbindung. Die Volumenerholung nach der Destocking-Phase beschleunigt sich. Innovation (naturnahe Inhaltsstoffe, Clean Label) treibt das Wachstum. Die geographische Expansion in Schwellenländern und die Übernahme von DDW und Ungerer stärken die Marktposition. Die Bewertung mit einem EV/EBITDA von 28x ist hoch, spiegelt aber die Qualität des Geschäftsmodells wider.',
    'HOLD', 'HOLD', false, 4200.00, 4100.00, 4050.00, 3.70, 'LOW',
    'Volumenerholung|Innovation Natural Ingredients|Emerging Markets Expansion|Margin Resilience',
    'Bewertungsrisiko bei Zinsanstieg|Rohstoffpreise|Regulierung (REACH, TSCA)',
    'Update|Materials|Specialty Chemicals|Quality Compounder');

-- Report 10: Lonza Flash (Analyst: Dr. Lukas Meier)
INSERT INTO research_reports (id, analyst_id, security_id, published_at, report_type, title, executive_summary, full_text, rating, previous_rating, rating_changed, target_price, previous_target, current_price, implied_upside, risk_level, investment_catalysts, key_risks, tags)
VALUES (10, 1, 8, '2026-02-20 09:30:00', 'FLASH',
    'Lonza: Kapazitätsausbau Visp signalisiert Zuversicht',
    'Lonza kündigt CHF 850 Mio. Investition in den Standort Visp (Biologics) an. Die Nachfrage nach CDMO-Kapazität für Biologika und ADCs bleibt robust. Das Management bestätigt die mittelfristigen Ziele. Wir stufen auf STRONG_BUY hoch.',
    'Lonzas Investitionsentscheidung unterstreicht das Vertrauen in die langfristige CDMO-Nachfrage. Der Biologics-Markt wächst jährlich 8-10%, getrieben durch GLP-1-Therapien und ADCs. Lonza ist als CDMO-Partner für 8 der 10 grössten Pharma-Konzerne positioniert. Die Capsules-Division stabilisiert sich nach dem COVID-bedingten Rückgang. Der neue CEO Wolfgang Wienand bringt frischen Fokus auf operative Exzellenz. Die Bewertung hat sich nach der Korrektur normalisiert.',
    'STRONG_BUY', 'BUY', true, 620.00, 540.00, 485.00, 27.84, 'MEDIUM',
    'CDMO-Nachfrageboom (GLP-1, ADC)|Visp Kapazitätsausbau|Neuer CEO-Fokus|Margin Recovery',
    'Kapazitätsüberangebot CDMO|Kundenkonzentration|Execution-Risiko Grossinvestition',
    'Upgrade|Healthcare|CDMO|Biologics');

-- ────────────────────────────────────────────────
-- AUDIT LOG (Demo-Daten für FINMA-Compliance-Nachweis)
-- ────────────────────────────────────────────────

INSERT INTO audit_log (id, timestamp, action, entity_type, entity_id, user_id, user_name, user_role, details, ip_address)
VALUES (1, '2026-01-15 08:30:00', 'CREATE', 'REPORT', 1, 'l.meier', 'Dr. Lukas Meier', 'ANALYST', 'Report ''Nestlé SA: Defensive Qualität in unsicheren Zeiten'' erstellt (Typ: INITIATION)', '10.0.1.42');

INSERT INTO audit_log (id, timestamp, action, entity_type, entity_id, user_id, user_name, user_role, details, ip_address)
VALUES (2, '2026-01-22 07:15:00', 'CREATE', 'REPORT', 2, 's.brunner', 'Sarah Brunner', 'ANALYST', 'Report ''UBS Group: Credit Suisse Integration auf Kurs'' erstellt (Typ: UPDATE)', '10.0.1.43');

INSERT INTO audit_log (id, timestamp, action, entity_type, entity_id, user_id, user_name, user_role, details, ip_address)
VALUES (3, '2026-01-22 07:16:00', 'UPDATE', 'REPORT', 2, 's.brunner', 'Sarah Brunner', 'ANALYST', 'Rating-Änderung: HOLD auf BUY (UBS Group AG)', '10.0.1.43');

INSERT INTO audit_log (id, timestamp, action, entity_type, entity_id, user_id, user_name, user_role, details, ip_address)
VALUES (4, '2026-02-01 06:45:00', 'CREATE', 'REPORT', 3, 'l.meier', 'Dr. Lukas Meier', 'ANALYST', 'Report ''Novartis Q4 2025: Pipeline liefert, Entresto treibt'' erstellt (Typ: QUARTERLY)', '10.0.1.42');

INSERT INTO audit_log (id, timestamp, action, entity_type, entity_id, user_id, user_name, user_role, details, ip_address)
VALUES (5, '2026-02-05 09:00:00', 'CREATE', 'REPORT', 4, 'm.keller', 'Marc Keller', 'ANALYST', 'Report ''ABB: Elektrifizierungs-Boom bestätigt'' erstellt (Typ: FLASH)', '10.0.1.44');

INSERT INTO audit_log (id, timestamp, action, entity_type, entity_id, user_id, user_name, user_role, details, ip_address)
VALUES (6, '2026-02-08 08:00:00', 'CREATE', 'REPORT', 5, 's.brunner', 'Sarah Brunner', 'ANALYST', 'Report ''Swiss Re: Rückversicherungs-Zyklus intakt'' erstellt (Typ: DEEP_DIVE)', '10.0.1.43');

INSERT INTO audit_log (id, timestamp, action, entity_type, entity_id, user_id, user_name, user_role, details, ip_address)
VALUES (7, '2026-02-10 07:30:00', 'CREATE', 'REPORT', 6, 'l.meier', 'Dr. Lukas Meier', 'ANALYST', 'Report ''Roche: Obesity-Pipeline als Game Changer'' erstellt (Typ: UPDATE)', '10.0.1.42');

INSERT INTO audit_log (id, timestamp, action, entity_type, entity_id, user_id, user_name, user_role, details, ip_address)
VALUES (8, '2026-02-10 07:31:00', 'UPDATE', 'REPORT', 6, 'l.meier', 'Dr. Lukas Meier', 'ANALYST', 'Rating-Änderung: SELL auf HOLD (Roche Holding AG)', '10.0.1.42');

INSERT INTO audit_log (id, timestamp, action, entity_type, entity_id, user_id, user_name, user_role, details, ip_address)
VALUES (9, '2026-02-15 11:00:00', 'VIEW', 'REPORT', 1, 'a.widmer', 'Dr. Anna Widmer', 'COMPLIANCE', 'Compliance-Review: Report ''Nestlé SA'' geprüft', '10.0.1.50');

INSERT INTO audit_log (id, timestamp, action, entity_type, entity_id, user_id, user_name, user_role, details, ip_address)
VALUES (10, '2026-02-20 09:30:00', 'CREATE', 'REPORT', 10, 'l.meier', 'Dr. Lukas Meier', 'ANALYST', 'Report ''Lonza: Kapazitätsausbau Visp signalisiert Zuversicht'' erstellt (Typ: FLASH)', '10.0.1.42');

INSERT INTO audit_log (id, timestamp, action, entity_type, entity_id, user_id, user_name, user_role, details, ip_address)
VALUES (11, '2026-02-20 09:31:00', 'UPDATE', 'REPORT', 10, 'l.meier', 'Dr. Lukas Meier', 'ANALYST', 'Rating-Änderung: BUY auf STRONG_BUY (Lonza Group AG)', '10.0.1.42');

INSERT INTO audit_log (id, timestamp, action, entity_type, entity_id, user_id, user_name, user_role, details, ip_address)
VALUES (12, '2026-02-21 14:00:00', 'EXPORT', 'REPORT', 3, 't.gerber', 'Thomas Gerber', 'ANALYST', 'PDF-Export: Report ''Novartis Q4 2025'' exportiert', '10.0.1.45');
