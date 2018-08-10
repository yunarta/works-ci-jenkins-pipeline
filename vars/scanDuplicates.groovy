def call(Map config) {
    def minTokens = config.minTokens ?: "50"

    def files = config.files
    def filelist = config.filelist ?: ""

    def input = "--files $files"
    if (!filelist.isEmpty()) {
        input = "--filelist $filelist"
    }

    if (!fileExists("reports")) {
        sh "mkdir reports"
    } else {
        sh "rm reports/cpd.xml | cat"
    }

    sh """
        $PMD_HOME/run.sh cpd --failOnViolation false --format xml --minimum-tokens ${minTokens} $input > reports/cpd.xml
    """
}