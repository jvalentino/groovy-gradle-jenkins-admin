package com.github.jvalentino.jenkins.util

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import okhttp3.Response
import spock.lang.Specification

class HttpUtilTest extends Specification {

    def "test postMedia"() {
        when:
        Map result = HttpUtil.postMediaJson(
                "https://httpbin.org/post",
                "username",
                "password",
                "script=blahblah")

        then:
        result.form.script == 'blahblah'
        result.headers.Authorization == 'Basic dXNlcm5hbWU6cGFzc3dvcmQ='
        result.headers['Content-Type'] == 'application/x-www-form-urlencoded; charset=utf-8'
    }

    def "test post"() {
        when:
        Response result = HttpUtil.post("https://httpbin.org/post", "charlie", '{"alpha":"bravo"}')

        then:
        String text = result.body().string()
        Map map = new JsonSlurper().parseText(text)

        and:
        map.headers['X-Insert-Key'] == 'charlie'
        map.json.alpha == 'bravo'

    }

}
