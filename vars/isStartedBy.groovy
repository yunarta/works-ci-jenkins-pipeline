def call(expected) {
    causeMap = [
            'hudson.model.Cause$UserIdCause'                       : 'user',
            'hudson.model.Cause$UpstreamCause'                     : 'upstream',
            'hudson.model.Cause$RemoteCause'                       : 'remote',
            'org.jenkinsci.plugins.workflow.cps.replay.ReplayCause': 'replay',
            'timer'                                                : 'timer'
    ]

    return currentBuild.rawBuild.causes.collect {
        String name = it.class.name
        causeMap[name] ?: name
    }.contains(expected)
}
