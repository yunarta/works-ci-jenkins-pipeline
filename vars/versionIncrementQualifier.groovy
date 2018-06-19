import com.mobilesolutionworks.jenkinsci.pipeline.version.VersionQualifier
import org.gradle.util.VersionNumber

def call() {
    if (fileExists(".jenkins/bintray/dlv.yaml")) {
        def yaml = readYaml(file: ".jenkins/bintray/dlv.yaml")

        VersionNumber version = VersionNumber.parse(yaml.version)
        def incremented = VersionQualifier.increment(version.qualifier)
        if (incremented != null) {
            def newVersion = VersionNumber.create(version.major, version.minor, version.micro, incremented)
            echo "Version qualifier incremented to ${newVersion.toString()}"
            return newVersion.toString()
        } else {
            echo "Unable to increment version qualifier part of ${yaml.version}"
        }
    }

    return null
}