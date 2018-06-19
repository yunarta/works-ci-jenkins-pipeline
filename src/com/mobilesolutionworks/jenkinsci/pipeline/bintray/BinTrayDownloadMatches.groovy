package com.mobilesolutionworks.jenkinsci.pipeline.bintray

@Grab('commons-io:commons-io:2.6')
import org.apache.commons.io.FilenameUtils
import org.gradle.util.VersionNumber

class BinTrayDownloadMatches implements Serializable {

    static def execute(Map config) {
        def pipeline = config.pipeline
        BinTrayArguments arguments = config.arguments as BinTrayArguments
        pipeline.withCredentials([pipeline.usernamePassword(
                credentialsId: arguments.credential,
                usernameVariable: 'JFROG_USER',
                passwordVariable: 'JFROG_API'
        )]) {
            pipeline.dir(FilenameUtils.separatorsToSystem(".jenkins/bintray")) {
                pipeline.echo("Downloading package detail of ${arguments.binTrayStore} - ${arguments.packageID}")

                def response = pipeline.sh(script: """jfrog bt ps ${arguments.binTrayStore}/${arguments.packageID} \
                --user=${pipeline.JFROG_USER} --key=${pipeline.JFROG_API}""", returnStdout: true)
                pipeline.echo(response)

                def match = acquireMatchingVersion(pipeline: pipeline, response: response, arguments: arguments)

                pipeline.echo("Deleting download info")
                pipeline.sh("rm dlv.yaml | cat")
                if (match != null) {
                    pipeline.echo("Downloading content of ${arguments.packageID}:${match}")

                    pipeline.sh(script: """jfrog bt dlv ${arguments.binTrayStore}/${arguments.packageID}/${match} \
                    --user=${pipeline.JFROG_USER} --key=${pipeline.JFROG_API}""", returnStdout: true)

                    def path = arguments.packagePath.replace(".", "/") + "/" + match
                    def data = [
                            version: match,
                            path   : FilenameUtils.separatorsToSystem(".jenkins/bintray/" + path)
                    ]

                    pipeline.echo("Saving download info")
                    pipeline.writeYaml file: 'dlv.yaml', data: data
                    return data
                } else {
                    pipeline.echo("No matches was downloaded")
                }
            }
        }

        return null
    }

    static def acquireMatchingVersion(Map config) {
        def pipeline = config.pipeline
        BinTrayArguments arguments = config.arguments as BinTrayArguments

        def ps = config.pipeline.readJSON(text: config.response)
        if (ps?.versions != null) {
            def requested = VersionNumber.parse(arguments.packageInfo.version)

            def matches = []
            for (version in ps.versions) {
                def test = VersionNumber.parse(String.valueOf(version))
                if (requested.numberMatch(test)) {
                    matches.add(test)
                }
            }


            if (!matches.empty) {
                matches.sort()

                pipeline.echo("Related versions to ${String.valueOf(requested)}")
                for (match in matches) {
                    pipeline.echo(" - ${String.valueOf(match)}")
                }

                def target = matches.first()
                for (version in ps.versions) {
                    def test = VersionNumber.parse(String.valueOf(version))
                    if (test.toString() == target.toString()) {
                        return version
                    }
                }
//                return String.valueOf(matches.first())
            }
        }

        pipeline.echo("No related version found")
        return null
    }
}