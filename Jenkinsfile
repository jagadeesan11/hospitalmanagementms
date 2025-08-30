pipeline {
    agent any

    environment {
        AWS_REGION = 'eu-north-1'
        EKS_CLUSTER_NAME = 'dev1aw-cluster'
        ECR_REPOSITORY = '758888582296.dkr.ecr.eu-north-1.amazonaws.com/hospitalmanagement'
        IMAGE_TAG = "${BUILD_NUMBER}"
        KUBECONFIG = credentials('kubeconfig-hospital')
        AWS_CREDENTIALS = credentials('aws-credentials')
    }

    tools {
        maven 'Maven-3.9.4'
        jdk 'JDK-19'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                script {
                    env.GIT_COMMIT_SHORT = sh(
                        script: "git rev-parse --short HEAD",
                        returnStdout: true
                    ).trim()
                    env.IMAGE_TAG = "${BUILD_NUMBER}-${env.GIT_COMMIT_SHORT}"
                }
            }
        }

        stage('Build & Test') {
            steps {
                sh '''
                    echo "Building Hospital Management System..."
                    mvn clean compile
                    mvn test
                '''
            }
            post {
                always {
                    publishTestResults(
                        testResultsPattern: 'target/surefire-reports/*.xml',
                        allowEmptyResults: true
                    )
                }
            }
        }

        stage('Package') {
            steps {
                sh '''
                    echo "Packaging application..."
                    mvn clean package -DskipTests
                '''
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('SonarQube Analysis') {
            when {
                anyOf {
                    branch 'main'
                    branch 'develop'
                }
            }
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh '''
                        mvn sonar:sonar \
                        -Dsonar.projectKey=hospital-management \
                        -Dsonar.host.url=${SONAR_HOST_URL} \
                        -Dsonar.login=${SONAR_AUTH_TOKEN}
                    '''
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    sh '''
                        echo "Building Docker image..."
                        docker build -t hospitalmanagement:${IMAGE_TAG} .
                        docker tag hospitalmanagement:${IMAGE_TAG} hospitalmanagement:latest
                        docker tag hospitalmanagement:${IMAGE_TAG} ${ECR_REPOSITORY}:${IMAGE_TAG}
                        docker tag hospitalmanagement:${IMAGE_TAG} ${ECR_REPOSITORY}:latest
                    '''
                }
            }
        }

        stage('Security Scan') {
            parallel {
                stage('Trivy Scan') {
                    steps {
                        sh '''
                            echo "Running Trivy security scan..."
                            trivy image --exit-code 0 --severity HIGH,CRITICAL hospitalmanagement:${IMAGE_TAG}
                        '''
                    }
                }
                stage('Hadolint') {
                    steps {
                        sh '''
                            echo "Running Hadolint for Dockerfile..."
                            hadolint Dockerfile || true
                        '''
                    }
                }
            }
        }

        stage('Push to ECR') {
            when {
                anyOf {
                    branch 'main'
                    branch 'develop'
                }
            }
            steps {
                withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'aws-credentials']]) {
                    sh '''
                        echo "Logging into ECR..."
                        aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REPOSITORY}

                        echo "Pushing image to ECR..."
                        docker push ${ECR_REPOSITORY}:${IMAGE_TAG}
                        docker push ${ECR_REPOSITORY}:latest
                    '''
                }
            }
        }

        stage('Deploy to EKS') {
            when {
                branch 'main'
            }
            steps {
                withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'aws-credentials']]) {
                    sh '''
                        echo "Configuring kubectl for EKS..."
                        aws eks update-kubeconfig --region ${AWS_REGION} --name ${EKS_CLUSTER_NAME}

                        echo "Deploying to Kubernetes..."

                        # Apply namespace first
                        kubectl apply -f k8s/mysql.yaml

                        # Apply secrets and configmaps
                        kubectl apply -f k8s/secrets.yaml
                        kubectl apply -f k8s/configmap.yaml

                        # Wait for MySQL to be ready
                        kubectl wait --for=condition=ready pod -l app=mysql -n hospital-management --timeout=300s

                        # Update image tag in deployment
                        sed -i "s|hospitalmanagement:latest|${ECR_REPOSITORY}:${IMAGE_TAG}|g" k8s/deployment.yaml

                        # Apply application deployment
                        kubectl apply -f k8s/deployment.yaml

                        # Wait for deployment to be ready
                        kubectl rollout status deployment/hospital-app -n hospital-management --timeout=600s

                        echo "Deployment completed successfully!"

                        # Get service endpoint
                        kubectl get services -n hospital-management
                    '''
                }
            }
        }

        stage('Health Check') {
            when {
                branch 'main'
            }
            steps {
                sh '''
                    echo "Performing health check..."
                    sleep 30

                    # Get the load balancer URL
                    LB_URL=$(kubectl get svc hospital-service -n hospital-management -o jsonpath='{.status.loadBalancer.ingress[0].hostname}')

                    if [ ! -z "$LB_URL" ]; then
                        echo "Load Balancer URL: http://$LB_URL"

                        # Wait for health check to pass
                        for i in {1..10}; do
                            if curl -f "http://$LB_URL/actuator/health"; then
                                echo "Health check passed!"
                                break
                            else
                                echo "Health check failed, retrying in 30 seconds..."
                                sleep 30
                            fi
                        done
                    else
                        echo "Load Balancer URL not available yet"
                    fi
                '''
            }
        }
    }

    post {
        always {
            echo 'Cleaning up...'
            sh '''
                docker rmi hospitalmanagement:${IMAGE_TAG} || true
                docker rmi hospitalmanagement:latest || true
                docker rmi ${ECR_REPOSITORY}:${IMAGE_TAG} || true
                docker rmi ${ECR_REPOSITORY}:latest || true
                docker system prune -f
            '''
        }
        success {
            echo 'Pipeline executed successfully!'
            slackSend(
                channel: '#hospital-management',
                color: 'good',
                message: "✅ Hospital Management System deployed successfully!\nBuild: ${BUILD_NUMBER}\nCommit: ${env.GIT_COMMIT_SHORT}"
            )
        }
        failure {
            echo 'Pipeline failed!'
            slackSend(
                channel: '#hospital-management',
                color: 'danger',
                message: "❌ Hospital Management System deployment failed!\nBuild: ${BUILD_NUMBER}\nCommit: ${env.GIT_COMMIT_SHORT}"
            )
        }
    }
}

