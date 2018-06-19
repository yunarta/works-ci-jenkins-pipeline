import com.mobilesolutionworks.jenkinsci.pipeline.bintray.BinTrayArguments
import com.mobilesolutionworks.jenkinsci.pipeline.bintray.BinTrayDownloadMatches

def call(Map config) {
    def arguments = BinTrayArguments.parse(config)
    BinTrayDownloadMatches.execute(
            pipeline: this,
            arguments: arguments
    )
}