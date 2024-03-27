pipeline {
    agent any
    environment {
            TIME_ZONE = 'Asia/Seoul'
            PROFILE = 'local'
            AWS_CREDENTIAL_NAME = 'aws-key'     //aws-key id
            DEPLOY_CREDENTIAL_NAME = 'deploy-key'   // ec2 연결하는 키
            REGION="us-east-2"  // 현재 위치
            ECR_PATH = '211125372242.dkr.ecr.us-east-2.amazonaws.com'   // ecr repository 푸시옵션
            IMAGE_NAME = '211125372242.dkr.ecr.us-east-2.amazonaws.com/board'
            DEPLOY_Host="3.134.95.73"  // ec2 인스턴스 ip4
        }
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
        stage('dockerizing project by dockerfile') {
             steps {
                sh '''
                   docker build -t $IMAGE_NAME:$BUILD_NUMBER .
                   docker tag $IMAGE_NAME:$BUILD_NUMBER $IMAGE_NAME:latest
                   '''
             }
             post {
                   success {
                        echo 'success dockerizing project'
                   }
                   failure {
                        error 'fail dockerizing project' // exit pipeline
                   }
             }
        }
        stage('upload aws ECR') {
                    steps {
                        script{

                            docker.withRegistry("https://$ECR_PATH", "ecr:$REGION:$AWS_CREDENTIAL_NAME") {
                              docker.image("$IMAGE_NAME:$BUILD_NUMBER").push()
                              docker.image("$IMAGE_NAME:latest").push()
                            }

                        }
                    }
                    post {
                        success {
                            echo 'success upload image'
                        }
                        failure {
                            error 'fail upload image' // exit pipeline
                        }
                    }
                }
        stage('Deploy to AWS EC2 VM'){
             steps{
                sshagent(credentials : ['deploy-key']) {        // pem 키 id
                    sh "ssh -o StrictHostKeyChecking=no ubuntu@$DEPLOY_Host \
                     'aws ecr get-login-password --region us-east-2 | docker login --username AWS --password-stdin $ECR_PATH; \
                    docker run -d -p 80:8080 -t $IMAGE_NAME:${BUILD_NUMBER};'"
                }
             }
        }

    }
}
