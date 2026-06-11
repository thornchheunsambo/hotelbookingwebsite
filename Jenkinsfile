pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk 'JDK21'
    }

    environment {
        RENDER_DEPLOY_HOOK_DEV  = credentials('render-hook-dev')
        RENDER_DEPLOY_HOOK_MAIN = credentials('render-hook-main')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Deploy to Render (dev)') {
            when {
                branch 'dev'
            }
            steps {
                sh 'curl -X POST "$RENDER_DEPLOY_HOOK_DEV"'
                echo 'Deployed to dev on Render'
            }
        }

        stage('Deploy to Render (main)') {
            when {
                branch 'main'
            }
            input {
                message 'Deploy to production?'
                ok 'Yes, deploy!'
            }
            steps {
                sh 'curl -X POST "$RENDER_DEPLOY_HOOK_MAIN"'
                echo 'Deployed to production on Render'
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed! Check logs above.'
        }
    }
}