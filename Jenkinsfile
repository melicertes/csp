pipeline {
    agent { label 'prod' }

    tools {
        maven 'M3'
        jdk 'jdk8u152'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '2'))
        disableConcurrentBuilds()
    }

    stages {
        stage('Initialize') {
            steps {
                sh '''
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                '''
                sh "mvn -version"
            }
        }
    }

    post {
        always {
            notifyBuild(currentBuild.result)
            deleteDir()
        }
    }
}


def maven_build(lifecycle) {
    configFileProvider(
            [configFile(fileId: 'sparks-setting.xml', variable: 'MAVEN_SETTINGS')]) {
        sh """mvn -s $MAVEN_SETTINGS ${lifecycle}"""
    }
}

def notifyBuild(String buildStatus = 'STARTED') {
    // build status of null means successful
    buildStatus = buildStatus ?: 'SUCCESSFUL'

    // Default values
    def colorName = 'RED'
    def colorCode = '#FF0000'
    def subject = "'${env.JOB_NAME} - #${env.BUILD_NUMBER}' ${buildStatus} "
    def summary = "${subject} (<${env.RUN_DISPLAY_URL}|Open>)"
    def details = """<p>STARTED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
    <p>Check console output at &QUOT;<a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a>&QUOT;</p>"""

    // Override default values based on build status
    if (buildStatus == 'STARTED') {
        color = 'YELLOW'
        colorCode = '#FFFF00'
    } else if (buildStatus == 'SUCCESSFUL') {
        color = 'GREEN'
        colorCode = '#00FF00'
    } else {
        color = 'RED'
        colorCode = '#FF0000'
    }

    // Send notifications
    slackSend(color: colorCode, message: summary)

//    emailext(
//            subject: subject,
//            body: details,
//            recipientProviders: [[$class: 'DevelopersRecipientProvider']]
//    )
}