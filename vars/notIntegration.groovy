def call() {
    if (BRANCH_NAME.startsWith("integrate/"))
        return false

    return true
}