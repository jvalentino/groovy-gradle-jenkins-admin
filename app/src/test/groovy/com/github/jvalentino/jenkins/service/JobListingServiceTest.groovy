package com.github.jvalentino.jenkins.service

import com.github.jvalentino.jenkins.AppSettings
import com.github.jvalentino.jenkins.util.HttpUtil
import com.github.jvalentino.jenkins.util.TextUtil
import okhttp3.Response
import okhttp3.ResponseBody
import org.codehaus.groovy.ant.Groovy
import spock.lang.Specification

class JobListingServiceTest extends Specification {

    JobListingService subject = null

    def setup() {
        GroovyMock(HttpUtil, global:true)
        GroovyMock(TextUtil, global:true)
        subject = new JobListingService()
    }

    def "test fetch"() {
        given:
        AppSettings settings = new AppSettings()
        settings.with {
            jenkinsUrl: 'alpha'
            jenkinsUsername: 'bravo'
            jenkinsToken: 'delta'
            path: '/foo'
        }

        and:
        List jobs = [[:]]

        when:
        List results = subject.fetch(settings)

        then:
        1 * TextUtil.extract(_) >> 'content'
        1 * HttpUtil.postMediaJson(
                "${settings.jenkinsUrl}/scriptText",
                settings.jenkinsUsername,
                settings.jenkinsToken,
                "script=content",
                _) >> jobs

        and:
        results.size() == 1
    }

    def "test submitToNewRelic when no key"() {
        given:
        AppSettings settings = new AppSettings()
        List jobs = []

        when:
        subject.submitToNewRelic(settings, jobs)

        then:
        true
    }

    def "test submitToNewRelic"() {
        given:
        AppSettings settings = new AppSettings()
        settings.with {
            newRelicAccountId = "123"
            newRelicKey = '456'
        }

        and:
        List jobs = [
                [
                        'foo':'bar'
                ]
        ]

        and:
        Response response = GroovyMock()
        ResponseBody body = GroovyMock()

        when:
        String result = subject.submitToNewRelic(settings, jobs)

        then:
        1 * HttpUtil.post(
                'https://insights-collector.newrelic.com/v1/accounts/123/events',
                settings.newRelicKey,
                '[{"foo":"bar","eventType":"JenkinsJob"}]') >> response
        1 * response.body() >> body
        1 * body.string() >> 'hi'

        and:
        result == 'hi'
    }

}
