package com.mobilesolutionworks.jenkinsci.pipeline.java

class PackageInfo implements Serializable {

    /**
     * Package group
     */
    String group

    /**
     * Package name
     */
    String name

    /**
     * Package version
     */
    String version

    PackageInfo(config) {
        group = config.group
        name = config.artifact
        version = config.version
    }


    @Override
    String toString() {
        return "PackageInfo{${group}:${name}:${version}}"
    }
}
