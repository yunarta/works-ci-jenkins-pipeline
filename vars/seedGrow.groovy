def call(String... names) {
    dir(".jenkins") {
        def seeds = readJSON file: "seeds.json"

        names.each {
            def key = String.valueOf(it)
            def stepCount = seeds[key] ?: 0
            seeds[key] = stepCount + 1
        }

        def builder = new groovy.json.JsonBuilder(seeds)
        def json = builder.content
        builder = null

        writeJSON file: "seeds.json", json: json, pretty: 4

        return seeds
    }
}