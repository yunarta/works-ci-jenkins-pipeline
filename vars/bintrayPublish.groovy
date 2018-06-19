def call(Map config) {
    // config.org    - organization
    // config.repo   - repo
    // config.target - target
    String prefix = String.valueOf(config.repo)
    if (config.containsKey("org")) {
        prefix = "${config.org}/prefix"
    }

    def module = config.pkg
    def packaging = "${module.group}:${module.artifact}/${module.version}"
    def path = "${module.group.replace(".", "/")}/${module.artifact}/${module.version}"

    withCredentials([usernamePassword(credentialsId: config.credential, usernameVariable: 'JFROG_USER', passwordVariable: 'JFROG_API')]) {
        sh """jfrog bt u "${config.src}/(*)" ${prefix}/${packaging} ${path}/{1} \
            --publish --override \
            --user=${JFROG_USER} --key=${JFROG_API}"""
    }
}