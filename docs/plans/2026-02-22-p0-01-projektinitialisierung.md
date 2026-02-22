# P0-01: Projektinitialisierung - Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Lauffähiges Angular + Spring Boot Projekt mit GitHub Repo, sodass `ng serve` und `mvn spring-boot:run` beide ohne Fehler starten.

**Architecture:** Monorepo mit `frontend/` (Angular 17+ Standalone) und `backend/` (Spring Boot 3 + Java 17 + Maven). Docker Compose verbindet beide. Design-System CSS wird in P0-02 aufgebaut.

**Tech Stack:** Angular 17+, TypeScript strict, Spring Boot 3.2, Java 17, Maven, H2, Docker

---

### Task 1: Prerequisites installieren

**Step 1: Java 17 via Homebrew installieren**
Run: `brew install openjdk@17`
Run: `sudo ln -sfn $(brew --prefix openjdk@17)/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk`
Run: `export JAVA_HOME=$(/usr/libexec/java_home -v 17)`
Expected: `java --version` zeigt OpenJDK 17

**Step 2: Maven installieren**
Run: `brew install maven`
Expected: `mvn --version` zeigt Maven 3.9+

**Step 3: Angular CLI installieren**
Run: `npm install -g @angular/cli`
Expected: `ng version` zeigt Angular CLI 17+

---

### Task 2: GitHub Repository erstellen

**Step 1: Git init + GitHub Repo**
Run: `cd /Users/brandea/Desktop/research-portal-prototype && git init`
Run: `gh repo create research-portal-prototype --public --source=. --remote=origin`

**Step 2: .gitignore erstellen**
Create: `.gitignore`

**Step 3: Initial Commit**
Run: `git add . && git commit -m "docs: Projektinfrastruktur und Wissensdatenbank"`
Run: `git push -u origin main`

---

### Task 3: Spring Boot Backend scaffolden

**Step 1: Spring Initializr via curl**
Generiere Spring Boot Projekt mit Web, JPA, H2, Validation, Flyway, DevTools.

**Step 2: Hexagonale Package-Struktur anlegen**
Erstelle: domain/model/, domain/port/in/, domain/port/out/, application/service/, adapter/in/rest/, adapter/out/persistence/, config/

**Step 3: application.yml konfigurieren**
H2 Console aktivieren, CORS für Angular, Profile (local, demo, production)

**Step 4: Build verifizieren**
Run: `cd backend && mvn clean package`
Expected: BUILD SUCCESS

**Step 5: Commit**

---

### Task 4: Angular Frontend scaffolden

**Step 1: Angular Projekt generieren**
Run: `ng new frontend --style=css --routing --standalone --skip-tests --directory=frontend`

**Step 2: tsconfig.json strict mode**
strict: true, noImplicitAny: true, strictNullChecks: true

**Step 3: PrimeNG installieren (nur Struktur)**
Run: `cd frontend && npm install primeng`
Kein Theme importieren, nur p-table Modul.

**Step 4: Build verifizieren**
Run: `cd frontend && ng build`
Expected: Build successful

**Step 5: Commit**

---

### Task 5: Docker + Konfiguration

**Step 1: docker-compose.yml**
Frontend (nginx) + Backend (JDK 17)

**Step 2: .editorconfig**
Einheitliche Formatierung

**Step 3: Final Commit + Push**
