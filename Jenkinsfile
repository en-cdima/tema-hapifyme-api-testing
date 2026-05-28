pipeline {
    agent any

    environment {
        TEST_PASSWORD = credentials('TEST_PASSWORD')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Run API Tests') {
            steps {
                sh 'mvn clean test'
            }
        }
    }

    post {
        always {
            junit 'target/surefire-reports/*.xml'
        }
    }
}