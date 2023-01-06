package com.github.jvalentino.jenkins.util

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response

/**
 * A general utility for making HTTP rest magic more simple
 * @author john.valentino
 */
@CompileDynamic
@Slf4j
@SuppressWarnings(['DuplicateStringLiteral', 'UnnecessaryGString'])
class HttpUtil {

    static Response postMedia(String url, String username, String password, String content) {
        log.info(url)
        OkHttpClient client = new OkHttpClient().newBuilder().build()
        String auth = "${username}:${password}".bytes.encodeBase64()

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded")
        RequestBody body = RequestBody.create(mediaType, content)
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Authorization", "Basic ${auth}")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build()
        Response response = client.newCall(request).execute()
        response
    }

    static Object postMediaJson(String url, String username, String password, String content, File file=null) {
        Response response = postMedia(url, username, password, content)
        String text = response.body().string()
        log.info(text)
        Object result = new JsonSlurper().parseText(text)
        if (file != null) {
            file.text = new JsonBuilder(result).toPrettyString()
        }
        result
    }

    static Response post(String url, String key, String json) {
        OkHttpClient client = new OkHttpClient().newBuilder().build()
        MediaType mediaType = MediaType.parse("application/json")
        RequestBody body = RequestBody.create(mediaType, json)
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-Insert-Key", key)
                .build()
        Response response = client.newCall(request).execute()
        response
    }

}
