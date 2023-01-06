package com.github.jvalentino.jenkins.service

import com.github.jvalentino.jenkins.AppSettings
import com.github.jvalentino.jenkins.util.HttpUtil
import com.github.jvalentino.jenkins.util.TextUtil
import groovy.json.JsonBuilder
import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j
import okhttp3.Response

/**
 * A service used to list job stats from Jenkins using a script
 * @author john.valentino
 */
@CompileDynamic
@Slf4j
@SuppressWarnings('JavaIoPackageAccess')
class JobListingService {

    List fetch(AppSettings settings) {
        File contentFile = new File("${settings.path}/scripts/list-jobs.groovy")
        String content = TextUtil.extract(contentFile)
        new File("${settings.path}/build").mkdir()
        File out = new File("${settings.path}/build/list-jobs.json")

        List results = HttpUtil.postMediaJson(
                "${settings.jenkinsUrl}/scriptText",
                settings.jenkinsUsername,
                settings.jenkinsToken,
                "script=${content}",
                out)

        results
    }

    String submitToNewRelic(AppSettings settings, List jobs) {
        if (settings.newRelicKey == null) {
            log.info('No new relic key given, skipping submitting to New Relic')
            return
        }

        String url = settings.deriveNewRelicUrl()
        String eventType = 'JenkinsJob'

        for (Map job : jobs) {
            job.eventType = eventType
        }

        String json = new JsonBuilder(jobs)
        log.info(json)

        Response response = HttpUtil.post(url, settings.newRelicKey, json)
        String body = response.body().string()
        log.info(body)

        body
    }

}
