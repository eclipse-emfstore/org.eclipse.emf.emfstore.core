pipeline {
    agent {
        kubernetes {
            label 'centos-7'
        }
    }
    tools {
        maven 'apache-maven-latest'
        jdk 'adoptopenjdk-hotspot-jdk8-latest'
    }
    options {
        timeout(time: 30, unit: 'MINUTES')
    }
    stages {
        stage('Build') {
            steps {
                wrap([$class: 'Xvnc', takeScreenshot: false, useXauthority: true]) {
                    sh '''
                        metacity --replace --sm-disable &
                        cd releng/emfstore-parent
                        mvn clean verify -Dcheckstyle.skip
                    '''
                }
            }
        }
    }
}