def call() {
    if (BRANCH_NAME.startsWith("release/"))
        return true

    return false
}