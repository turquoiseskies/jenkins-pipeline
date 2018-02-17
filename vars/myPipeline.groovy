def call() {
      pipeline {
          agent any
          stages {
              stage('build') {
                  steps {
                      sh 'mvn clean package'
                  }
              }
               stage('code-quality-check') {
                   steps {                         
                     withSonarQubeEnv('localSonar') {
                       sh 'mvn sonar:sonar -Dsonar.host.url=http://sonarqube:9000 -Dsonar.login=8465fafd1be292f86c73376249213d0ce313f359'
                     } 
                   }
               }
                
              stage("Quality Gate"){
                    steps {   
                      timeout(time: 2, unit: 'MINUTES') {
                      
                            script {
                                  def qg = waitForQualityGate()
                                  if (qg.status != 'OK') {
                                     error "Pipeline aborted due to quality gate failure: ${qg.status}"
                                  }
                            }
                       }

                    }
             }

              stage("bake image") {

                  steps {
                      sh "whoami"
                      script {
                          docker.withRegistry('http://localhost:5000') {

                              def customImage = docker.build("my-image:${env.BUILD_ID}")

                              /* Push the container to the custom Registry */
                              customImage.push()
                          }
                      }
                  }
              }
          }

          post {
              always {
                  logstashSend failBuild: false, maxLines: 20000
              }

          }
      }
 }
