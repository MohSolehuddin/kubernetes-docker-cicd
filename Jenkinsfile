pipeline {
    agent any

    environment {
        DOCKERHUB_USERNAME = 'mohsolehuddin'
        IMAGE_NAME = 'kubernetes-docker-cicd'
        IMAGE_TAG = "${env.BUILD_NUMBER}"
        KUBERNETES_MANIFESTS_PATH = 'kubernetes'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        disableConcurrentBuilds()
        timeout(time: 1, unit: 'HOURS')
    }

    stages {
        stage('Clone github project') {
            steps {
                checkout scm
            }
        }

        stage('Build Spring Boot App') {
            steps {
                script {
                    docker.image('maven:3.9.9-sapmachine-24').inside {
                        sh 'mvn clean package -DskipTests'
                    }
                }
            }
        }

        stage('Build & Push Docker Image') {
            steps {
                script {
                    def fullImageName = "${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${IMAGE_TAG}"
                    def dockerImage = docker.build(fullImageName, '.')

                    withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                        sh "docker login -u ${DOCKER_USERNAME} -p ${DOCKER_PASSWORD}"
                        dockerImage.push()
                    }
                }
            }
        }

        stage('Deploy to Minikube') {
                    steps {
                        script {
                            sh 'kubectl config use-context minikube'

                            sh "sed -i 's|image: ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:.*|image: ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${IMAGE_TAG}|g' ${KUBERNETES_MANIFESTS_PATH}/deployment.yml"

                            sh "kubectl apply -f ${KUBERNETES_MANIFESTS_PATH}/deployment.yaml"
                            sh "kubectl apply -f ${KUBERNETES_MANIFESTS_PATH}/service.yaml"
                            sh "kubectl apply -f ${KUBERNETES_MANIFESTS_PATH}/ingress.yaml"

                            sh "kubectl rollout status deployment/${IMAGE_NAME}-deployment --timeout=5m"
                            try {
                                def ingressUrl = sh(returnStdout: true, script: 'minikube service kubernetes-docker-cicd-service --url | head -n 1').trim()
                                echo "Aplikasi dapat diakses melalui Ingress di URL: ${ingressUrl}/hello"
                            } catch (err) {
                                echo "Tidak dapat mengambil URL Ingress secara otomatis."
                            }
                        }
                    }
                }
            }


    post {
        always {
            echo 'Pipeline completed'
        }
        success {
            echo 'Successfully'
        }
        failure {
            echo 'Failed, check details in log'
        }
    }
}