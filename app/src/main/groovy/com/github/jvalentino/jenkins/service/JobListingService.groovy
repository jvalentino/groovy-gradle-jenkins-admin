package com.github.jvalentino.jenkins.service

import com.github.jvalentino.jenkins.AppSettings
import com.github.jvalentino.jenkins.util.HttpUtil
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
class JobListingService {

    List fetch(AppSettings settings) {
        String content = new File("${settings.path}/scripts/list-jobs.groovy").text
        new File("${settings.path}/build").mkdir()
        File out = new File("${settings.path}/build/list-jobs.json")

        List results = HttpUtil.postMediaJson(
                "${settings.jenkinsUrl}/scriptText",
                settings.jenkinsUsername,
                settings.jenkinsToken,
                "script=${content}",
                out)

        return results
    }

    void submitToNewRelic(AppSettings settings, List jobs) {
        if (settings.newRelicKey == null) {
            log.info("No new relic key given, skipping submitting to New Relic")
            return
        }

        String url = settings.deriveNewRelicUrl()
        String eventType = "JenkinsJob"

        for (Map job : jobs) {
            job.eventType = eventType
        }

        String json = new JsonBuilder(jobs).toString()

        Response response = HttpUtil.post(url, settings.newRelicKey, json)
        log.info(response.headers().toString())
        log.info(response.successful.toString())
        log.info(response.code().toString())
        log.info(response.body().string())
    }

}
