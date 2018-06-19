def call() {
    if (BRANCH_NAME.startsWith("integrate/"))
        return true

    return false
}