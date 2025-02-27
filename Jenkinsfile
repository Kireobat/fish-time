pipeline {
    agent any
    tools {
        jdk 'temurin-jdk21'
        maven 'maven3'
    }
    stages {
        stage('Checkout') {
            steps {
                script {
                    // Checkout the code from the repository
                    git branch: 'master', url: 'https://github.com/kireobat/fish-time.git'
                }
            }
        }
        stage('Build') {
            agent {
                docker {
                    image 'maven:3.9.9-eclipse-temurin-21-alpine'
                }
            }
            steps {
                script {
                    // Build the project to generate the JAR file in the target directory
                    sh 'mvn -Dmaven.repo.local=/tmp/.m2/repository clean package'
                }
            }
        }
        stage('Build and Push Docker Image') {
            agent {
                docker {
                    image 'docker:20.10'
                }
            }
            steps {
                script {
                    // Build and push the Docker image
                    docker.withRegistry('https://index.docker.io/v1/', 'dockerhub') {
                        def app = docker.build("kireobat/fish-time")
                        app.push("${env.BUILD_NUMBER}")
                        app.push("latest")
                    }
                }
            }
        }
        stage('Deploy to Portainer') {
            steps {
                withCredentials(
                    [
                        usernamePassword(
                            credentialsId: 'docker',
                            passwordVariable: 'PORTAINER_PASSWORD',
                            usernameVariable: 'PORTAINER_USERNAME'
                        ),
                        usernamePassword(
                            credentialsId: 'fish_time_postgres',
                            passwordVariable: 'POSTGRES_PASSWORD',
                            usernameVariable: 'POSTGRES_USERNAME'
                        ),
                        usernamePassword(
                            credentialsId: 'fish_time_github_oauth_app',
                            passwordVariable: 'GITHUB_SECRET',
                            usernameVariable: 'GITHUB_CLIENT_ID'
                        ),
                        string(
                            credentialsId: 'postgres_url',
                            variable: 'POSTGRES_URL'
                        )
                    ]) {
                    script {
                        // =============================================
                        // Define functions as closure variables FIRST
                        // =============================================
                        def getPortainerToken = { username, password ->
                            def response = httpRequest(
                                url: 'https://docker.kireobat.eu/api/auth',
                                httpMode: 'POST',
                                contentType: 'APPLICATION_JSON',
                                requestBody: """{"username": "${username}", "password": "${password}"}"""
                            )
                            return readJSON(text: response.content).jwt
                        }

                        def findContainerByName = { token, name ->
                            def response = httpRequest(
                                url: "https://docker.kireobat.eu/api/endpoints/2/docker/containers/json",
                                httpMode: 'GET',
                                contentType: 'APPLICATION_JSON',
                                customHeaders: [[name: 'Authorization', value: "Bearer ${token}"]]
                            )
                            def containers = readJSON(text: response.content)
                            containers.find { container ->
                                container.Names.any { it == "/${name}" }
                            }
                        }

                        def extractHostPort = { container ->
                            def portBindings = container.HostConfig?.PortBindings?.'8080/tcp'
                            portBindings ? portBindings[0].HostPort : null
                        }

                        def stopAndRemoveContainer = { token, containerId ->
                            httpRequest(
                                url: "https://docker.kireobat.eu/api/endpoints/2/docker/containers/${containerId}/stop",
                                httpMode: 'POST',
                                contentType: 'APPLICATION_JSON',
                                customHeaders: [[name: 'Authorization', value: "Bearer ${token}"]]
                            )
                            httpRequest(
                                url: "https://docker.kireobat.eu/api/endpoints/2/docker/containers/${containerId}",
                                httpMode: 'DELETE',
                                contentType: 'APPLICATION_JSON',
                                customHeaders: [[name: 'Authorization', value: "Bearer ${token}"]],
                                requestBody: '{"force": true}'
                            )
                        }

                        def createContainer = { token, name, hostPort, postgresUrl, postgresUser, postgresPass, githubClient, githubSecret ->
                            def encodedName = URLEncoder.encode(name, 'UTF-8')
                            def deployResponse = httpRequest(
                                url: "https://docker.kireobat.eu/api/endpoints/2/docker/containers/create?name=${encodedName}",
                                httpMode: 'POST',
                                contentType: 'APPLICATION_JSON',
                                customHeaders: [[name: 'Authorization', value: "Bearer ${token}"]],
                                requestBody: """{
                                    "Image": "kireobat/fish-time:latest",
                                    "Env": [
                                        "SPRING_DATASOURCE_URL=${postgresUrl}/${postgresUser}",
                                        "SPRING_DATASOURCE_USER=${postgresUser}",
                                        "SPRING_DATASOURCE_PASSWORD=${postgresPass}",
                                        "GITHUB_CLIENT_ID=${githubClient}",
                                        "GITHUB_SECRET=${githubSecret}"
                                    ],
                                    "HostConfig": {
                                        "PortBindings": {
                                            "8080/tcp": [${hostPort ? '{"HostPort": "' + hostPort + '"}' : '{}'}]
                                        }
                                    }
                                }"""
                            )
                            readJSON(text: deployResponse.content).Id
                        }

                        def startContainer = { token, containerId ->
                            httpRequest(
                                url: "https://docker.kireobat.eu/api/endpoints/2/docker/containers/${containerId}/start",
                                httpMode: 'POST',
                                contentType: 'APPLICATION_JSON',
                                customHeaders: [[name: 'Authorization', value: "Bearer ${token}"]]
                            )
                        }

                        // =============================================
                        // Main execution logic AFTER function definitions
                        // =============================================
                        def token = getPortainerToken(PORTAINER_USERNAME, PORTAINER_PASSWORD) // Fix credentials here
                        def containerName = "fish-time"
                        def hostPort = null

                        def existingContainer = findContainerByName(token, containerName)
                        if (existingContainer) {
                            hostPort = extractHostPort(existingContainer)
                            stopAndRemoveContainer(token, existingContainer.Id)
                        }

                        def containerId = createContainer(
                            token,
                            containerName,
                            hostPort,
                            POSTGRES_URL,
                            POSTGRES_USERNAME,
                            POSTGRES_PASSWORD,
                            GITHUB_CLIENT_ID,
                            GITHUB_SECRET
                        )

                        startContainer(token, containerId)
                    }
                }
            }
        }
    }
    post {
        always {
            // Clean up any resources or perform actions regardless of success or failure
            echo "Pipeline completed."
        }
    }
}

