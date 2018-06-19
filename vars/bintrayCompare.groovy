import com.mobilesolutionworks.jenkinsci.pipeline.bintray.BinTrayArguments
import com.mobilesolutionworks.jenkinsci.pipeline.bintray.BinTrayCompare

def call(Map config) {
    def arguments = BinTrayArguments.parse(config)
    println("using arguments ${arguments}")

    BinTrayCompare.execute(
            pipeline: this,
            arguments: arguments
    )
}