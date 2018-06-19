def call(String name) {
    dir(".jenkins") {
        def seeds = [:]
        if (fileExists(file: "seeds.json")) {
            seeds = readJSON file: "seeds.json"
        }

        def key = String.valueOf(it)
        def stepCount = seeds[key] ?: 0

        return stepCount
    }
}