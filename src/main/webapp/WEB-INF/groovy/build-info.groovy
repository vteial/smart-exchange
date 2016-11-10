def buildInfo = application.getAttribute('buildInfo')

if(!buildInfo) {
    buildInfo = [:]

    Properties props = new Properties()
    String path = '/WEB-INF/classes/application.properties'
    props.load(application.getResourceAsStream(path))

    buildInfo['applicationId'] = props['buildInfo.applicationId']
    buildInfo['applicationName'] = props['buildInfo.applicationName']
    buildInfo['applicationVersion'] = props['buildInfo.applicationVersion']
    buildInfo['gitMilestone'] = props['buildInfo.gitMilestone']
    buildInfo['gitCommitId'] = props['buildInfo.gitCommitId']
    buildInfo['tag'] = props['buildInfo.tag']
    buildInfo['host'] = props['buildInfo.host']
    buildInfo['by'] = props['buildInfo.by']
    buildInfo['type'] = props['buildInfo.type']
    buildInfo['time'] = props['buildInfo.time']

    application['buildInfo'] = buildInfo
}

jsonCategory.respondWithJson(response, buildInfo)