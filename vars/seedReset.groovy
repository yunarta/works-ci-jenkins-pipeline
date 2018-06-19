def call() {
    dir(".jenkins") {
        writeFile file: "seeds.json", text: "{}"
    }
}