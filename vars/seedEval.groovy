def call(String name, Map eval) {
    dir(".jenkins") {
        def seeds = [:]
        if (fileExists(file: "seeds.json")) {
            seeds = readJSON file: "seeds.json"
        }

        def key = name
        def stepCount = seeds[key] ?: 0
        return eval[stepCount] ?: eval["else"]
    }
}