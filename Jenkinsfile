pipeline {
    agent any

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
                sh 'mvn clean verify'
            }
        }

        stage('Deploy to Render (dev)') {
            when {
                branch 'dev'
            }
            steps {
                sh 'curl -X POST "$RENDER_DEPLOY_HOOK_DEV"'
                echo 'Deployed to dev environment on Render'
            }
        }

        stage('Deploy to Render (main)') {
            when {
                branch 'main'
            }
            input {
                message 'Deploy to production (main)?'
                ok 'Yes, deploy!'
            }
            steps {
                sh 'curl -X POST "$RENDER_DEPLOY_HOOK_MAIN"'
                echo 'Deployed to production on Render'
            }
        }
    }

    post {
        failure {
            echo 'Build failed! Check logs above.'
        }
    }
}