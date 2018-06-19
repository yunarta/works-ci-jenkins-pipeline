def call() {
    if (BRANCH_NAME.startsWith("feature/"))
        return false

    return true
}