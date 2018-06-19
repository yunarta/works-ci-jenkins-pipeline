def call(expression) {
    if (expression) {
        currentBuild.result = 'NOT_BUILT'
    }
}