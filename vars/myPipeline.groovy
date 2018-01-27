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
                       sh 'mvn sonar:sonar -Dsonar.host.url=http:172.17.0.2:9000 -Dsonar.login=c6eea290ca2ef147929dc78f402182570438a39f'
                     } 
                      sh sleep 10
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
