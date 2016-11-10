package io.wybis.smartexchange.util

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class Helper {

    static String DUMP_REQUEST_RESPONSE_KEY = 'dumpRequestResponse'

    static String capitalize(String s) {
        if (!s || s == '') {
            return s
        }
        if (s.indexOf(' ') == -1) {
            return s.capitalize()
        }
        s = s.tokenize().collect { it.capitalize() }.join(' ')
        return s
    }

    private static String domainPrefix = null

    static String getDomainPrefix(def request, def app) {
        if (domainPrefix) {
            return domainPrefix
        }

        String appEnvName = app.env.name

        String s = 'http://'
        if (appEnvName.equals('Production')) {
            s += app.id + '.appspot.com'
        } else {
            s += request.localAddr + ':' + request.localPort
        }
        domainPrefix = s

        return domainPrefix
    }

    static String getStackTraceAsString(Throwable t) {
        StringWriter sw = new StringWriter()
        PrintWriter pw = new PrintWriter(sw)
        t.printStackTrace(pw)
        return sw.toString()
    }

    static void dumpRequest(HttpServletRequest hreq) {
        String s = '-', schar = '-'; int noOfChars = 80;
        System.out.println s.padRight(noOfChars, schar)

        try {
            System.out.println "Request URI                 = ${hreq.requestURI}"
            System.out.println "Request URL                 = ${hreq.requestURL}"
            System.out.println "Path Info                   = ${hreq.pathInfo}"
            System.out.println "Http Method                 = ${hreq.method}"
            System.out.println "Query String                = ${hreq.queryString}"
            hreq.headerNames.each { headerKey ->
                System.out.println "${headerKey} = " + hreq.getHeader(headerKey)
            }
            //System.out.println s.padRight(noOfChars, schar)
            Enumeration<String> keys = hreq.getParameterNames()
            while (keys.hasMoreElements()) {
                String key = keys.nextElement()
                Object val = hreq.getParameter(key)
                System.out.println "$key = $val"
            }
//            System.out.println("Request Text")
//            System.out.println(hreq.reader.text)
        }
        catch (Throwable t) {
            t.printStackTrace(System.out)
        }
    }

    static void dumpResponse(HttpServletResponse hres) {
        String s = '-', schar = '-'; int noOfChars = 80;
        System.out.println s.padRight(noOfChars, schar)

        try {
            System.out.println "Response Status             = ${hres.status}"
            System.out.println "Response ContentType        = ${hres.contentType}"
            System.out.println "Response Character Encoding = ${hres.characterEncoding}"
            System.out.println "Response Locale             = ${hres.locale}"
            hres.headers.each { headerKey, headerVal ->
                System.out.println "${headerKey} = ${headerVal}"
            }
        }
        catch (Throwable t) {
            t.printStackTrace(System.out)
        }
    }

}
