def call(expression) {
    if (!expression) {
        currentBuild.result = 'FAILURE'
    }
}