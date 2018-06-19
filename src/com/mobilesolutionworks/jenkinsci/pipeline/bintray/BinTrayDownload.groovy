package com.mobilesolutionworks.jenkinsci.pipeline.bintray

import com.mobilesolutionworks.jenkinsci.pipeline.java.PackageInfo
@Grab('commons-io:commons-io:2.6')
import org.apache.commons.io.FilenameUtils
import org.gradle.util.VersionNumber

class BinTrayDownload implements Serializable {

    static def execute(Map config) {
        def pipeline = config.pipeline
        BinTrayArguments arguments = config.arguments as BinTrayArguments

        pipeline.echo("execute with ${arguments}")

        pipeline.withCredentials([
                pipeline.usernamePassword(
                        credentialsId: arguments.credential,
                        usernameVariable: 'JFROG_USER',
                        passwordVariable: 'JFROG_API'
                )
        ]) {
            pipeline.dir(FilenameUtils.separatorsToSystem(".jenkins/bintray")) {
                pipeline.echo(arguments)

                def response = pipeline.sh(script: """jfrog bt ps ${arguments.binTrayStore}/${arguments.packageID} \
                --user=${pipeline.JFROG_USER} --key=${pipeline.JFROG_API}""", returnStdout: true)

                pipeline.echo(response)

                def match = acquireMatchingVersion(pipeline: pipeline, response: response, arguments: arguments)
                pipeline.sh("rm dlv.yaml | cat")
                if (match != null) {
                    pipeline.echo("found version: ${match}")

                    pipeline.sh(script: """jfrog bt dlv ${arguments.binTrayStore}/${arguments.packageID}/${match} \
                    --user=${pipeline.JFROG_USER} --key=${pipeline.JFROG_API}""", returnStdout: true)

                    def path = arguments.packagePath.replace(".", "/") + "/" + match
                    def data = [
                            version: match,
                            path   : FilenameUtils.separatorsToSystem(".jenkins/bintray/" + path)
                    ]

                    pipeline.writeYaml file: 'dlv.yaml', data: data
                    return data
                } else {
                    pipeline.echo("no version matches")
                }
            }
        }

        return null
    }

    static def acquireMatchingVersion(Map config) {
        BinTrayArguments arguments = config.arguments as BinTrayArguments

        def ps = config.pipeline.readJSON(text: config.response)
        println("ps = ${ps}")
        if (ps?.versions != null) {
            def requested = VersionNumber.parse(arguments.packageInfo.version)

            def matches = []
            for (version in ps.versions) {
                def test = VersionNumber.parse(String.valueOf(version))
                println("matching ${test}")
                if (requested.numberMatch(test)) {
                    matches.add(test)
                }
            }

            println("${matches}")
            if (!matches.empty) {
                matches.sort()
                return String.valueOf(matches.last())
            }
        }

        return null
    }

    static BinTrayArguments parse(Map config) {
        BinTrayArguments arguments = new BinTrayArguments()
        arguments.repository = config.repository
        arguments.credential = config.credential
        arguments.organization = config.organization

        arguments.packageInfo = new PackageInfo(config.packageInfo)

        return arguments
    }
}