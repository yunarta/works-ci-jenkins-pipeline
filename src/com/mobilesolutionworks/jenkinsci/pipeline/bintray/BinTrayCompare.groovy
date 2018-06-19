package com.mobilesolutionworks.jenkinsci.pipeline.bintray

@Grab('commons-io:commons-io:2.6')
import com.mobilesolutionworks.jenkinsci.pipeline.java.PackageInfo

import static org.apache.commons.io.FilenameUtils.separatorsToSystem

class BinTrayCompare implements Serializable {

    static def execute(Map config) {
        BinTrayArguments arguments = config.arguments as BinTrayArguments
        def pipeline = config.pipeline

        if (!pipeline.fileExists(separatorsToSystem(".jenkins/bintray/dlv.yaml"))) {
            return false
        }

        pipeline.sh("cat .jenkins/bintray/dlv.yaml")
        def dlv = pipeline.readYaml(file: separatorsToSystem(".jenkins/bintray/dlv.yaml"))
        println(dlv)

        def name = arguments.packageInfo.name

        // compare pom
        def outputPom = separatorsToSystem("${dlv.path}/${name}-${dlv.version}.pom")
        def downloadPom = separatorsToSystem("${arguments.path}/${name}-${arguments.packageInfo.version}.pom")

        def pomDiff = false
        def outputExists = pipeline.fileExists(outputPom)
        def downloadExists = pipeline.fileExists(downloadPom)

        if (outputExists && downloadExists) {
            String out = pipeline.readFile(file: outputPom)
            String download = pipeline.readFile(file: downloadPom)

            String pomOut = removeVersion(out)
            String pomDownload = removeVersion(download)

            pomDiff = pomOut == pomDownload
            if (!pomDiff) {
                pipeline.echo("POM fingerprint is different")
            } else {
                pipeline.echo("POM fingerprint is identical")
            }
        } else {
            pipeline.echo("POM is different due to output ${outputPom}(${outputExists}) and download ${downloadPom}(${downloadExists})")
        }

        // compare artifact
        def outputArtifact = separatorsToSystem("${dlv.path}/${name}-${dlv.version}.md5")
        def downloadArtifact = separatorsToSystem("${arguments.path}/${name}-${arguments.packageInfo.version}.md5")

        def artifactDiff = false
        outputExists = pipeline.fileExists(outputArtifact)
        downloadExists = pipeline.fileExists(downloadArtifact)

        if (outputExists && downloadExists) {
            def out = pipeline.readFile(file: outputArtifact)
            def download = pipeline.readFile(file: downloadArtifact)

            artifactDiff = out.equals(download)
            if (!artifactDiff) {
                pipeline.echo("Artifact fingerprint is different")
            } else {
                pipeline.echo("Artifact fingerprint is identical")
            }
        } else {
            pipeline.echo("Artifact is different due to output ${outputArtifact}(${outputExists}) and download ${downloadArtifact}(${downloadExists})")
        }

        return pomDiff && artifactDiff
    }

    static def removeVersion(String sourceXml) {
        def pomXml = new XmlSlurper(false, false).parseText(sourceXml)
        pomXml.version.replaceBody("-")
        return groovy.xml.XmlUtil.serialize(pomXml).toString()
    }
}