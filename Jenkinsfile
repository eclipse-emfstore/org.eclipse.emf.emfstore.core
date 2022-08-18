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
                        export EMFSTORE_TEST_SERVER_ROOT_DIR=$PWD
                        echo EMFSTORE_TEST_SERVER_ROOT_DIR = $EMFSTORE_TEST_SERVER_ROOT_DIR
                        mkdir -p $EMFSTORE_TEST_SERVER_ROOT_DIR/profiles/default_test/./conf/
                        cat <<EOT >> $EMFSTORE_TEST_SERVER_ROOT_DIR/profiles/default_test/./conf/es.properties
emfstore.startup.post.loadlistener=true
emfstore.keystore.certificate.type=SunX509
emfstore.accesscontrol.authentication.superuser.password.hash=f262c3daf9b9c8fd380bdf34a415ee01c38e423d956d4f9c732273cf51264783787415c3b6d8701f3416735a31b06b8330e0be0d17ae5361ef83de320f17ba84
emfstore.accesscontrol.authentication.superuser.password.salt=8a0InZ2yZMss65zHJrdhOXU6CqF8EeFncdv7V29ZO4qD565i5deWWXIi7aRkYwbY2BWamdOYQGqoZeZiJHZ0BH1mteMIhu3eIG5D2twfVQtjetvf8kiLOMwnYDsk4HPq
emfstore.keystore.alias=emfstoreServer
emfstore.accesscontrol.authentication.policy=spfv
emfstore.acceptedversions=any
emfstore.validation.level=7
emfstore.validation.exclude=
emfstore.connection.rmi.encryption=true
emfstore.startup.loadlistener=false
emfstore.keystore.cipher.algorithm=RSA
emfstore.keystore.password=123456
emfstore.connection.xmlrpc.port=8080
emfstore.validation=true
emfstore.persistence.version.projectstate.everyxversions=50
emfstore.accesscontrol.authentication.superuser=super
emfstore.accesscontrol.session.timeout=1800000
EOT
                        cat $EMFSTORE_TEST_SERVER_ROOT_DIR/profiles/default_test/./conf/es.properties
                        metacity --replace --sm-disable &
                        cd releng/emfstore-parent
                        mvn clean verify -Dcheckstyle.skip
                    '''
                }
            }
        }
    }
    post {
        always {
            junit '**/TEST-*.xml'
        }
    }
}