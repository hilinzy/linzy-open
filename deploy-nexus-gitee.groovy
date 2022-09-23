node {
    stage('git chekout') {
        git branch: "master", url: 'https://github.com/hilinzy/linzy-open.git'
    }
    stage('mvn deploy') {
        sh 'sh build.sh publish_plugin'
    }
}