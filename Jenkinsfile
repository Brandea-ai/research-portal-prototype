/**
 * ============================================================================
 * Research Portal - Jenkins Declarative Pipeline
 * ============================================================================
 *
 * Banking Research Portal für Schweizer Bank (ZKB)
 * Stack: Spring Boot 3.5 (Java 17) + Angular 21 + Docker + Kubernetes
 *
 * Pipeline-Stages:
 *   1. Checkout
 *   2. Build Backend (Maven)
 *   3. Test Backend (JUnit 5 + JaCoCo)
 *   4. Build Frontend (Angular CLI)
 *   5. Test Frontend (Karma + Cypress)
 *   6. Quality Gate (SonarQube)
 *   7. Security Scan (OWASP Dependency-Check)
 *   8. Docker Build & Push
 *   9. Deploy (Staging / Production)
 *
 * Voraussetzungen:
 *   - Jenkins mit Docker/Kubernetes Agent
 *   - SonarQube-Server konfiguriert
 *   - Docker Registry Credentials hinterlegt
 *   - Kubernetes Kubeconfig als Secret
 *
 * Autor: DevOps-Team Research
 * ============================================================================
 */

pipeline {
    // Kubernetes-basierter Agent für skalierbare Builds
    agent {
        kubernetes {
            yaml '''
                apiVersion: v1
                kind: Pod
                metadata:
                  labels:
                    jenkins-build: research-portal
                spec:
                  containers:
                  - name: maven
                    image: maven:3.9-eclipse-temurin-17-alpine
                    command: ['cat']
                    tty: true
                    volumeMounts:
                    - name: maven-cache
                      mountPath: /root/.m2/repository
                  - name: node
                    image: node:22-alpine
                    command: ['cat']
                    tty: true
                    volumeMounts:
                    - name: npm-cache
                      mountPath: /root/.npm
                  - name: docker
                    image: docker:24-dind
                    privileged: true
                    volumeMounts:
                    - name: docker-sock
                      mountPath: /var/run/docker.sock
                  - name: kubectl
                    image: bitnami/kubectl:1.28
                    command: ['cat']
                    tty: true
                  volumes:
                  - name: maven-cache
                    persistentVolumeClaim:
                      claimName: maven-repo-cache
                  - name: npm-cache
                    persistentVolumeClaim:
                      claimName: npm-cache
                  - name: docker-sock
                    hostPath:
                      path: /var/run/docker.sock
            '''
        }
    }

    // Umgebungsvariablen für die gesamte Pipeline
    environment {
        // Java/Maven Konfiguration
        JAVA_HOME         = '/opt/java/openjdk'
        MAVEN_OPTS        = '-Xmx1024m -XX:+UseG1GC'

        // Node.js Konfiguration
        NODE_VERSION       = '22'
        NPM_CONFIG_CACHE   = '/root/.npm'

        // Docker Registry (Schweizer Bank, private Registry)
        DOCKER_REGISTRY    = credentials('docker-registry-url')
        DOCKER_CREDENTIALS = credentials('docker-registry-credentials')
        IMAGE_BACKEND      = "${DOCKER_REGISTRY}/research-portal/backend"
        IMAGE_FRONTEND     = "${DOCKER_REGISTRY}/research-portal/frontend"
        IMAGE_TAG          = "${env.BUILD_NUMBER}-${env.GIT_COMMIT?.take(8) ?: 'unknown'}"

        // SonarQube
        SONAR_HOST_URL     = credentials('sonarqube-url')
        SONAR_TOKEN        = credentials('sonarqube-token')

        // Kubernetes
        K8S_NAMESPACE      = 'research-portal'
        K8S_KUBECONFIG     = credentials('k8s-kubeconfig')

        // Quality Gate Schwellenwerte (Banking-Standard)
        COVERAGE_THRESHOLD = '70'
        QUALITY_GATE_TIMEOUT = '300' // Sekunden
    }

    // Pipeline-Optionen
    options {
        buildDiscarder(logRotator(numToKeepStr: '20'))
        timeout(time: 45, unit: 'MINUTES')
        timestamps()
        disableConcurrentBuilds()
        ansiColor('xterm')
    }

    // Trigger: Webhook oder periodisch
    triggers {
        pollSCM('H/5 * * * *') // Fallback, falls kein Webhook konfiguriert
    }

    // Parameter für manuelle Builds
    parameters {
        choice(
            name: 'DEPLOY_ENV',
            choices: ['staging', 'production'],
            description: 'Zielumgebung für das Deployment'
        )
        booleanParam(
            name: 'SKIP_TESTS',
            defaultValue: false,
            description: 'Tests überspringen (nur für Notfall-Deployments)'
        )
        booleanParam(
            name: 'FORCE_DEPLOY',
            defaultValue: false,
            description: 'Deployment auch bei fehlgeschlagenem Quality Gate erzwingen'
        )
    }

    stages {
        // =====================================================================
        // Stage 1: Checkout und Vorbereitung
        // =====================================================================
        stage('Checkout') {
            steps {
                checkout scm

                script {
                    // Branch-Info und Commit-Hash für spätere Verwendung
                    env.GIT_BRANCH_NAME = env.GIT_BRANCH?.replaceAll('origin/', '') ?: 'unknown'
                    env.GIT_SHORT_HASH  = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                    env.GIT_AUTHOR      = sh(script: 'git log -1 --format=%an', returnStdout: true).trim()

                    echo "Branch: ${env.GIT_BRANCH_NAME}"
                    echo "Commit: ${env.GIT_SHORT_HASH}"
                    echo "Autor:  ${env.GIT_AUTHOR}"
                }
            }
        }

        // =====================================================================
        // Stage 2: Backend Build (Maven)
        // =====================================================================
        stage('Build Backend') {
            steps {
                container('maven') {
                    dir('backend') {
                        echo 'Maven Dependencies laden und Backend kompilieren...'
                        sh '''
                            mvn clean compile \
                                -B \
                                -DskipTests \
                                -Dmaven.javadoc.skip=true \
                                -T 2C
                        '''
                    }
                }
            }
        }

        // =====================================================================
        // Stage 3: Backend Tests (JUnit 5 + JaCoCo Coverage)
        // =====================================================================
        stage('Test Backend') {
            when {
                expression { !params.SKIP_TESTS }
            }
            steps {
                container('maven') {
                    dir('backend') {
                        echo 'Unit- und Integrationstests mit Code-Coverage ausführen...'
                        sh '''
                            mvn verify \
                                -B \
                                -Dspring.profiles.active=test \
                                -Djacoco.output=file \
                                -T 2C
                        '''
                    }
                }
            }
            post {
                always {
                    // JUnit Test-Ergebnisse publizieren
                    junit(
                        testResults: 'backend/target/surefire-reports/*.xml',
                        allowEmptyResults: true,
                        skipPublishingChecks: false
                    )

                    // JaCoCo Coverage-Report publizieren
                    jacoco(
                        execPattern: 'backend/target/jacoco.exec',
                        classPattern: 'backend/target/classes',
                        sourcePattern: 'backend/src/main/java',
                        exclusionPattern: '**/dto/**,**/config/**,**/entity/**',
                        minimumLineCoverage: "${COVERAGE_THRESHOLD}",
                        changeBuildStatus: true
                    )
                }
            }
        }

        // =====================================================================
        // Stage 4+5: Frontend Build und Test (parallel für schnellere Pipeline)
        // =====================================================================
        stage('Frontend') {
            parallel {
                // Angular Build
                stage('Build Frontend') {
                    steps {
                        container('node') {
                            dir('frontend') {
                                echo 'npm Dependencies installieren und Angular-App bauen...'
                                sh '''
                                    npm ci --prefer-offline --no-audit
                                    npx ng build --configuration=production
                                '''
                            }
                        }
                    }
                    post {
                        success {
                            // Build-Artefakte archivieren
                            archiveArtifacts(
                                artifacts: 'frontend/dist/**/*',
                                fingerprint: true,
                                allowEmptyArchive: false
                            )
                        }
                    }
                }

                // Frontend Unit-Tests
                stage('Test Frontend') {
                    when {
                        expression { !params.SKIP_TESTS }
                    }
                    steps {
                        container('node') {
                            dir('frontend') {
                                echo 'Angular Unit-Tests mit Karma ausführen...'
                                sh '''
                                    npm ci --prefer-offline --no-audit
                                    npx ng test --watch=false \
                                        --browsers=ChromeHeadless \
                                        --code-coverage
                                '''
                            }
                        }
                    }
                    post {
                        always {
                            // Karma Test-Reports publizieren
                            junit(
                                testResults: 'frontend/test-results/*.xml',
                                allowEmptyResults: true
                            )
                        }
                    }
                }
            }
        }

        // =====================================================================
        // Stage 6: Quality Gate (SonarQube Analyse)
        // =====================================================================
        stage('Quality Gate') {
            when {
                expression { !params.SKIP_TESTS }
            }
            steps {
                container('maven') {
                    echo 'SonarQube-Analyse für Backend und Frontend starten...'

                    // Backend SonarQube Analyse
                    dir('backend') {
                        sh """
                            mvn sonar:sonar \
                                -B \
                                -Dsonar.host.url=${SONAR_HOST_URL} \
                                -Dsonar.login=${SONAR_TOKEN} \
                                -Dsonar.projectKey=research-portal-backend \
                                -Dsonar.projectName='Research Portal Backend' \
                                -Dsonar.java.coveragePlugin=jacoco \
                                -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \
                                -Dsonar.exclusions='**/dto/**,**/config/**'
                        """
                    }
                }

                // Quality Gate Ergebnis abwarten
                timeout(time: "${QUALITY_GATE_TIMEOUT}", unit: 'SECONDS') {
                    waitForQualityGate abortPipeline: !params.FORCE_DEPLOY
                }
            }
        }

        // =====================================================================
        // Stage 7: Security Scan (OWASP Dependency-Check)
        // =====================================================================
        stage('Security Scan') {
            when {
                anyOf {
                    branch 'main'
                    branch 'develop'
                    branch 'release/*'
                }
            }
            steps {
                container('maven') {
                    dir('backend') {
                        echo 'OWASP Dependency-Check für bekannte Schwachstellen...'
                        sh '''
                            mvn org.owasp:dependency-check-maven:check \
                                -B \
                                -DfailBuildOnCVSS=7 \
                                -DsuppressionFile=../owasp-suppressions.xml || true
                        '''
                    }
                }
            }
            post {
                always {
                    // OWASP Report publizieren
                    dependencyCheckPublisher(
                        pattern: 'backend/target/dependency-check-report.xml',
                        failedTotalCritical: 0,
                        failedTotalHigh: 2
                    )
                }
            }
        }

        // =====================================================================
        // Stage 8: Docker Build & Push
        // =====================================================================
        stage('Docker Build & Push') {
            when {
                anyOf {
                    branch 'main'
                    branch 'develop'
                    branch 'release/*'
                }
            }
            steps {
                container('docker') {
                    script {
                        echo 'Docker Images für Backend und Frontend bauen und pushen...'

                        // Docker Login
                        sh """
                            echo '${DOCKER_CREDENTIALS_PSW}' | \
                            docker login ${DOCKER_REGISTRY} \
                                -u '${DOCKER_CREDENTIALS_USR}' \
                                --password-stdin
                        """

                        // Backend Image (Multi-Stage Build aus Dockerfile)
                        sh """
                            docker build \
                                --cache-from ${IMAGE_BACKEND}:latest \
                                -t ${IMAGE_BACKEND}:${IMAGE_TAG} \
                                -t ${IMAGE_BACKEND}:latest \
                                -f backend/Dockerfile \
                                backend/
                        """

                        // Frontend Image (Multi-Stage Build aus Dockerfile)
                        sh """
                            docker build \
                                --cache-from ${IMAGE_FRONTEND}:latest \
                                -t ${IMAGE_FRONTEND}:${IMAGE_TAG} \
                                -t ${IMAGE_FRONTEND}:latest \
                                -f frontend/Dockerfile \
                                frontend/
                        """

                        // Push beider Images
                        sh """
                            docker push ${IMAGE_BACKEND}:${IMAGE_TAG}
                            docker push ${IMAGE_BACKEND}:latest
                            docker push ${IMAGE_FRONTEND}:${IMAGE_TAG}
                            docker push ${IMAGE_FRONTEND}:latest
                        """
                    }
                }
            }
        }

        // =====================================================================
        // Stage 9: Deploy nach Staging
        // =====================================================================
        stage('Deploy Staging') {
            when {
                anyOf {
                    branch 'develop'
                    allOf {
                        branch 'main'
                        expression { params.DEPLOY_ENV == 'staging' }
                    }
                }
            }
            steps {
                container('kubectl') {
                    echo 'Deployment nach Staging-Umgebung...'
                    sh """
                        export KUBECONFIG=${K8S_KUBECONFIG}

                        # Kubernetes Manifeste mit aktuellem Image-Tag aktualisieren
                        sed -i 's|IMAGE_BACKEND|${IMAGE_BACKEND}:${IMAGE_TAG}|g' k8s/staging/*.yaml
                        sed -i 's|IMAGE_FRONTEND|${IMAGE_FRONTEND}:${IMAGE_TAG}|g' k8s/staging/*.yaml

                        # Apply mit Rolling Update Strategie
                        kubectl apply -f k8s/staging/ -n ${K8S_NAMESPACE}-staging

                        # Rollout-Status prüfen (Timeout: 5 Minuten)
                        kubectl rollout status deployment/research-portal-backend \
                            -n ${K8S_NAMESPACE}-staging \
                            --timeout=300s

                        kubectl rollout status deployment/research-portal-frontend \
                            -n ${K8S_NAMESPACE}-staging \
                            --timeout=300s
                    """
                }
            }
        }

        // =====================================================================
        // Stage 10: Deploy nach Production (nur main Branch, manuell)
        // =====================================================================
        stage('Deploy Production') {
            when {
                allOf {
                    branch 'main'
                    expression { params.DEPLOY_ENV == 'production' }
                }
            }
            // Manuelle Freigabe vor Production-Deployment (Banking-Compliance)
            input {
                message 'Production-Deployment freigeben?'
                ok 'Deployment starten'
                submitter 'devops-leads,release-managers'
                parameters {
                    string(
                        name: 'APPROVAL_TICKET',
                        description: 'Change-Management Ticket-Nummer (z.B. CHG-2024-001)'
                    )
                }
            }
            steps {
                container('kubectl') {
                    echo "Production-Deployment genehmigt (Ticket: ${APPROVAL_TICKET})..."
                    sh """
                        export KUBECONFIG=${K8S_KUBECONFIG}

                        # Kubernetes Manifeste aktualisieren
                        sed -i 's|IMAGE_BACKEND|${IMAGE_BACKEND}:${IMAGE_TAG}|g' k8s/production/*.yaml
                        sed -i 's|IMAGE_FRONTEND|${IMAGE_FRONTEND}:${IMAGE_TAG}|g' k8s/production/*.yaml

                        # Apply mit Rolling Update (Zero-Downtime)
                        kubectl apply -f k8s/production/ -n ${K8S_NAMESPACE}

                        # Rollout-Status prüfen
                        kubectl rollout status deployment/research-portal-backend \
                            -n ${K8S_NAMESPACE} \
                            --timeout=600s

                        kubectl rollout status deployment/research-portal-frontend \
                            -n ${K8S_NAMESPACE} \
                            --timeout=600s
                    """
                }
            }
        }
    }

    // =========================================================================
    // Post-Actions: Benachrichtigungen und Aufräumarbeiten
    // =========================================================================
    post {
        always {
            echo 'Pipeline abgeschlossen. Artefakte und Workspace bereinigen...'

            // Test-Reports immer publizieren (auch bei Fehler)
            junit(
                testResults: '**/target/surefire-reports/*.xml, **/test-results/*.xml',
                allowEmptyResults: true
            )

            // Docker Images lokal aufräumen
            sh 'docker system prune -f || true'
        }

        success {
            echo "Build ${env.BUILD_NUMBER} erfolgreich (Branch: ${env.GIT_BRANCH_NAME})"

            // Erfolgs-Benachrichtigung an Team-Channel
            // slackSend(
            //     channel: '#research-portal-builds',
            //     color: 'good',
            //     message: """
            //         :white_check_mark: *Research Portal Build #${env.BUILD_NUMBER}*
            //         Branch: `${env.GIT_BRANCH_NAME}` | Commit: `${env.GIT_SHORT_HASH}`
            //         Autor: ${env.GIT_AUTHOR}
            //         Duration: ${currentBuild.durationString}
            //     """
            // )
        }

        failure {
            echo "Build ${env.BUILD_NUMBER} fehlgeschlagen!"

            // Fehler-Benachrichtigung
            // slackSend(
            //     channel: '#research-portal-builds',
            //     color: 'danger',
            //     message: """
            //         :x: *Research Portal Build #${env.BUILD_NUMBER} FEHLGESCHLAGEN*
            //         Branch: `${env.GIT_BRANCH_NAME}` | Commit: `${env.GIT_SHORT_HASH}`
            //         Autor: ${env.GIT_AUTHOR}
            //         Log: ${env.BUILD_URL}console
            //     """
            // )

            // E-Mail an Entwickler bei Fehler
            // mail(
            //     to: env.GIT_AUTHOR_EMAIL,
            //     subject: "Jenkins Build fehlgeschlagen: Research Portal #${env.BUILD_NUMBER}",
            //     body: "Details: ${env.BUILD_URL}"
            // )
        }

        unstable {
            echo 'Build instabil: Quality Gate oder Tests nicht bestanden.'
        }

        cleanup {
            // Workspace immer aufräumen
            cleanWs(
                cleanWhenNotBuilt: false,
                deleteDirs: true,
                disableDeferredWipeout: true,
                notFailBuild: true
            )
        }
    }
}
