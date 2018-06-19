package com.mobilesolutionworks.jenkinsci.pipeline.version


class VersionQualifier {

    static String increment(String qualifier) {
        def tokens = qualifier =~ /(.*[.\-_+]{1})(\d*)/
        if (tokens.matches()) {
            String prefix = tokens[0][1]
            Integer number = Integer.parseInt(tokens[0][2]) + 1

            return prefix + number
        }

        return null
    }
}
