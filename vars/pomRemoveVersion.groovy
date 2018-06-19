def call(Map config) {
    def source = config.source
    def destination = config.destination

    def sourceXml = readFile(file: source)

    def pomXml = new XmlSlurper(false, false).parseText(sourceXml)
    pomXml.version.replaceBody("-")
    def destinationXml = groovy.xml.XmlUtil.serialize(pomXml)

    writeFile(file: destination, text: destinationXml)
}