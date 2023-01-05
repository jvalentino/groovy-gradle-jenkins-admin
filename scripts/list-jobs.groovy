import groovy.json.JsonBuilder

List jobs = [];
Jenkins.instance.getAllItems(Job.class).each {
    def buildHealth = it.buildHealth
    def lastBuild = it.lastBuild
    Map job = [
            fullName: it.fullName,
            absoluteUrl: it.absoluteUrl,
            description: it.description,
            displayName: it.displayName,
            name: it.name,
            clazz: it.class.getName(),
            pronoun: it.pronoun,
            searchName: it.searchName,
            searchUrl: it.searchUrl,
            url: it.url,
            buildHealthDescription: buildHealth.description,
            buildIcon: buildHealth.iconClassName,
            buildScore: buildHealth.score,
            iconColor: it.iconColor.toString(),
            working: it.iconColor.toString().startsWith('blue') ? 1 : 0,
            lastBuildTime: lastBuild?.getTime(),
            lastBuildDurationInMillis: lastBuild?.duration,
    ]
    jobs.add(job)
}

println new JsonBuilder(jobs).toString()