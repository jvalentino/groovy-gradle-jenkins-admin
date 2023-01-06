package com.github.jvalentino.jenkins.util

import groovy.transform.CompileDynamic

/**
 * I made this because of how difficult it is to mock File.txt
 * @author john.valentino
 */
@CompileDynamic
class TextUtil {

    static String extract(File file) {
        file.text
    }

}
