pipeline {
    agent any

    tools {
        maven 'Maven3'
    }

    environment {
        RENDER_DEPLOY_HOOK_DEV  = credentials('render-hook-dev')
        RENDER_DEPLOY_HOOK_MAIN = credentials('render-hook-main')
        TELEGRAM_BOT_TOKEN      = credentials('telegram-bot-token')
        TELEGRAM_CHAT_ID        = credentials('telegram-chat-id')
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
            steps {
                timeout(time: 30, unit: 'MINUTES') {
                    input message: 'Deploy to production?', ok: 'Yes, deploy!'
                }
                sh 'curl -X POST "$RENDER_DEPLOY_HOOK_MAIN"'
                echo 'Deployed to production on Render'
            }
        }
    }

    post {
        success {
            script {
                def committer = sh(script: "git log -1 --pretty=format:'%ae'", returnStdout: true).trim()
                def commitId  = sh(script: "git log -1 --pretty=format:'%H'", returnStdout: true).trim()
                def commitMsg = sh(script: "git log -1 --pretty=format:'%s'", returnStdout: true).trim()
                def buildTime = new Date().format("EEE MMM dd HH:mm:ss yyyy", TimeZone.getTimeZone('Asia/Phnom_Penh'))
                def environment = env.BRANCH_NAME == 'main' ? 'production' : 'dev'
                def jenkinsUrl = "http://localhost:8080/job/hotelbooking-pipeline/job/${env.BRANCH_NAME}/${env.BUILD_NUMBER}/"

                sh """
                    curl -s -X POST "https://api.telegram.org/bot\${TELEGRAM_BOT_TOKEN}/sendMessage" \\
                    -d chat_id="\${TELEGRAM_CHAT_ID}" \\
                    -d parse_mode="HTML" \\
                    -d text="🚀 <b>Pipeline Notification</b>%0A<b>Status:</b> SUCCESS ✅%0A%0A<b>Application:</b> hotelbookingwebsite%0A<b>Environment:</b> ${environment}%0A<b>Branch:</b> ${env.BRANCH_NAME}%0A<b>Build:</b> #${env.BUILD_NUMBER}%0A<b>Last Deployed:</b> ${buildTime}%0A<b>Release Status:</b> deployed%0A<b>Committer:</b> ${committer}%0A<b>Commit ID:</b> ${commitId}%0A<b>Commit Message:</b> ${commitMsg}%0A%0A<a href='${jenkinsUrl}'>🔗 View Build Details</a>"
                """
            }
        }
        failure {
            script {
                def committer = sh(script: "git log -1 --pretty=format:'%ae'", returnStdout: true).trim()
                def commitId  = sh(script: "git log -1 --pretty=format:'%H'", returnStdout: true).trim()
                def commitMsg = sh(script: "git log -1 --pretty=format:'%s'", returnStdout: true).trim()
                def buildTime = new Date().format("EEE MMM dd HH:mm:ss yyyy", TimeZone.getTimeZone('Asia/Phnom_Penh'))
                def environment = env.BRANCH_NAME == 'main' ? 'production' : 'dev'
                def jenkinsUrl = "http://localhost:8080/job/hotelbooking-pipeline/job/${env.BRANCH_NAME}/${env.BUILD_NUMBER}/"

                sh """
                    curl -s -X POST "https://api.telegram.org/bot\${TELEGRAM_BOT_TOKEN}/sendMessage" \\
                    -d chat_id="\${TELEGRAM_CHAT_ID}" \\
                    -d parse_mode="HTML" \\
                    -d text="🚀 <b>Pipeline Notification</b>%0A<b>Status:</b> FAILED ❌%0A%0A<b>Application:</b> hotelbookingwebsite%0A<b>Environment:</b> ${environment}%0A<b>Branch:</b> ${env.BRANCH_NAME}%0A<b>Build:</b> #${env.BUILD_NUMBER}%0A<b>Last Deployed:</b> ${buildTime}%0A<b>Release Status:</b> failed%0A<b>Committer:</b> ${committer}%0A<b>Commit ID:</b> ${commitId}%0A<b>Commit Message:</b> ${commitMsg}%0A%0A<a href='${jenkinsUrl}'>🔗 View Build Details</a>"
                """
            }
        }
    }
}