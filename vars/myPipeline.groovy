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
                       sh 'mvn sonar:sonar -Dsonar.host.url=http:172.17.0.2:9000 -Dsonar.login=0854e56d829cf1b936ed8b21113d475decef2034'
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
          }
      }
 }
