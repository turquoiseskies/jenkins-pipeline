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
                       sh 'mvn sonar:sonar -Dsonar.host.url=http:172.17.0.3:9000 -Dsonar.login=c6eea290ca2ef147929dc78f402182570438a39f'
                   }
               }
          }
      }
 }
