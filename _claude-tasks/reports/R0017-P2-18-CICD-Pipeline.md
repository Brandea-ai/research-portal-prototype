# R0017: P2-18 CI/CD Pipeline (Jenkins, GitLab, Harness, SonarQube)

**Datum:** 22.02.2026
**Session:** 4 (Fortsetzung)
**Dauer:** ~6 Min (paralleler Sonnet 4.6 Agent)
**Status:** Abgeschlossen

---

## Erstellte Dateien (4)

### 1. Jenkinsfile (~22 KB)
- 10 Stages: Checkout → Build → Test → Quality → Security → Docker → Deploy
- Kubernetes Agent mit 4 Containern (maven, node, docker, kubectl)
- Quality Gate: Coverage > 70%, JaCoCo-Integration
- Banking-Compliance: Manuelles Approval für Production
- Post-Actions: JUnit Reports, Slack-Benachrichtigungen

### 2. .gitlab-ci.yml (~13 KB)
- 5 Stages: build, test, quality, package, deploy
- Parallele Jobs: maven + npm builds gleichzeitig
- Cache: Maven .m2, npm node_modules
- Artifacts: Test-Reports, Coverage, Docker Images
- Rules: Nur main/develop deployen, Production manuell

### 3. .harness/pipeline.yaml (~25 KB)
- 3 Stages: Build, Deploy Staging, Deploy Production
- 4-Augen-Prinzip: `disallowPipelineExecutor: true`
- Rolling Deployment mit automatischem Rollback
- Triggers: Push to main, Push to develop, Pull Requests

### 4. sonar-project.properties (~5 KB)
- Multi-Modul: backend (Java 17) + frontend (TypeScript)
- Coverage: JaCoCo XML + lcov.info
- Quality Gate: Coverage >= 70%, Duplicated Lines < 5%
- Banking-spezifisch: SQL-Injection, Hardcoded Credentials Regeln

## Skill-Nachweis

| Geforderter Skill | Nachweis |
|---|---|
| CI/CD | Jenkinsfile + .gitlab-ci.yml + Harness Pipeline |
| DevOps | Docker Multi-Stage, K8s Deployment, Rolling Updates |
| Quality Gates | SonarQube, JaCoCo, OWASP Dependency Check |
| Banking-Compliance | 4-Augen-Prinzip, Change-Management, Audit-Trail |

## Kompatibilität

- Keine bestehenden Dateien geändert
- Nur 4 neue Dateien im Projekt-Root / .harness/
- Pipeline-Dateien werden nicht ausgeführt (Konfigurationsnachweis)
