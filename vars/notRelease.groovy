def call() {
    if (BRANCH_NAME.startsWith("release/"))
        return false

    return true
}