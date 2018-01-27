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
                       sh 'mvn sonar:sonar -Dsonar.host.url=http://sonarqube:9000 -Dsonar.login=b661e2cfc2dcb8600a5bee2fe1195c9ae9be1c70'
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
