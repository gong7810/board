def mainDir="."
def ecrLoginHelper="docker-credential-ecr-login"
def region="ap-northeast-1"
def ecrUrl="598552988151.dkr.ecr.ap-northeast-1.amazonaws.com"
def repository="board"
def deployHost="54.168.148.170"

pipeline {
    agent any

    stages {
        stage('Pull Codes from Github'){
            steps{
                checkout scm
            }
        }
        stage('Build Codes by Gradle') {
            steps {
              sh "./gradlew clean build"
            }
        }
                stage('Build Docker Image by Jib & Push to AWS ECR Repository') {
                    steps {
                        withAWS(region:"${region}", credentials:"aws-key") {
                            ecrLogin()
                            sh """
                                curl -O https://amazon-ecr-credential-helper-releases.s3.us-east-2.amazonaws.com/0.4.0/linux-amd64/${ecrLoginHelper}

                                cd ${mainDir}
                                ./gradlew jib -Djib.to.image=${ecrUrl}/${repository}:${currentBuild.number} -Djib.console='plain'
                            """
                        }
                    }
                }
                 stage('Deploy to AWS EC2 VM'){
                            steps{
                                sshagent(credentials : ["deploy-key"]) {
                                    sh "ssh -o StrictHostKeyChecking=no ubuntu@${deployHost} \
                                     'aws ecr get-login-password --region ${region} | docker login --username AWS --password-stdin ${ecrUrl}/${repository}; \
                                      docker run -d -p 80:8888 -t ${ecrUrl}/${repository}:${currentBuild.number};'"
                                }
                            }
                        }

    }
}
