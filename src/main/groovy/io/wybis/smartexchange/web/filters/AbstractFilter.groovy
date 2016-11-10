package io.wybis.smartexchange.web.filters

import groovyx.gaelyk.logging.GroovyLogger

import javax.servlet.Filter
import javax.servlet.FilterConfig

abstract class AbstractFilter implements Filter {

    protected GroovyLogger log = new GroovyLogger(DumpFilter.class.getName())

    public void init(FilterConfig filterConfig) {
    }

    public void destroy() {
    }
}