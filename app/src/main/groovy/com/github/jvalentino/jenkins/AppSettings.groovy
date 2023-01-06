package com.github.jvalentino.jenkins

import groovy.transform.CompileDynamic

/**
 * General application settings for runtime
 * @author john.valentino
 */
@CompileDynamic
class AppSettings {

    String jenkinsUsername = null
    String jenkinsToken = null
    String jenkinsUrl = null
    String path = null
    String newRelicAccountId = null
    String newRelicKey = null

    String deriveNewRelicUrl() {
        "https://insights-collector.newrelic.com/v1/accounts/${newRelicAccountId}/events"
    }

}
