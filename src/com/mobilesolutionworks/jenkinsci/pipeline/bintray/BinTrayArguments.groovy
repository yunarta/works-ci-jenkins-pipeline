package com.mobilesolutionworks.jenkinsci.pipeline.bintray

import com.mobilesolutionworks.jenkinsci.pipeline.java.PackageInfo

class BinTrayArguments implements Serializable {

    /**
     * Bintray repository name
     */
    String repository

    /**
     * Bintray organization name
     */
    String organization

    /**
     * Path where the artifact can be found
     */
    String path

    /**
     * Credential for Bintray
     */
    String credential

    /**
     * Package info
     */
    PackageInfo packageInfo

    def getBinTrayStore() {
        String prefix = repository
        if (organization != null) {
            prefix = organization + "/" + repository
        }

        return prefix
    }

    def getPackageID() {
        return packageInfo.group + ":" + packageInfo.name
    }

    def getPackagePath() {
        return packageInfo.group + "/" + packageInfo.name
    }


    @Override
    String toString() {
        return """BinTrayArguments{
                 |  repository='$repository, 
                 |  organization=$organization, 
                 |  path=$path, 
                 |  credential=$credential, 
                 |  packageInfo=$packageInfo
                 |}""".stripMargin()
    }

    static BinTrayArguments parse(Map config) {
        BinTrayArguments arguments = new BinTrayArguments()
        arguments.repository = config.repository
        arguments.credential = config.credential
        arguments.organization = config.organization
        arguments.path = config.path

        arguments.packageInfo = new PackageInfo(config.packageInfo)

        return arguments
    }
}