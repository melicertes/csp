pipeline {
    agent { label 'prod' }
    parameters {
        booleanParam(name: 'BUILD', defaultValue: true, description: '')
        booleanParam(name: 'TEST', defaultValue: true, description: '')
        booleanParam(name: 'MAVEN_DEPLOY', defaultValue: false, description: 'Deploy JAR artifacts to Maven')
    }

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

        stage('Build') {
            when {
                expression {
                    params.BUILD
                }
            }
            steps {
                dir("csp-apps") {
                    maven_build("-DskipTests clean package")
                }
            }
        }

        stage('Unit Tests') {
            when {
                expression {
                    params.TEST
                }
            }
            steps {
                dir("csp-apps") {
                    maven_build("clean test")
                }
            }

            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/TEST-*.xml'
                    jacoco(execPattern: '**/*.exec')
                }
            }
        }

        stage('Acceptance Tests') {
            when {
                expression {
                    params.TEST
                }
            }
            steps {
                dir("csp-apps") {
                    maven_build("clean verify")
                }
            }

            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/target/failsafe-reports/TEST-*.xml'
                }
            }
        }

        stage("Run SonarQube analysis") {
            when {
                expression {
                    params.TEST
                }
            }
            steps {
                dir("csp-apps") {
                    sh "mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent test"
                    sh "mvn sonar:sonar -Dsonar.host.url=http://sonar:9000 -Dsonar.login=acfa03872fbc02f58468cb05c21242ef4f8d5486"
                }
            }
        }

        stage('Deploy maven artifact') {
            when {
                expression {
                    params.MAVEN_DEPLOY
                }
            }
            steps {
                dir("csp-apps") {
                    maven_build("-DskipTests -DskipITs clean deploy")
                }
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
            [configFile(fileId: 'sastix-setting.xml', variable: 'MAVEN_SETTINGS')]) {
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