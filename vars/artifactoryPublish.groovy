// vars/artifactoryPublish.groovy
def call(Map config) {
    def group = config.group
    def artifact = config.artifact
    def version = config.version

    def path = config.root

    def server = Artifactory.server "REPO"
    def result = server.upload spec: """{
                          "files": [
                             {
                              "pattern": "${path}/build/libs/(${artifact}-${version}*)",
                              "target": "${repo}/${group}/${artifact}/${version}/{1}"
                             }
                          ]
                        }"""
    server.publishBuildInfo result
}