package io.wybis.smartexchange

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.CollectionType
import io.wybis.smartexchange.util.Helper

import javax.servlet.ServletContext
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JacksonCategory extends Helper {

    static ObjectMapper jsonObjectMapper = new ObjectMapper()

    static Object parseJson(HttpServletRequest hreq, Class clazz) {
        Object object = jsonObjectMapper.readValue(hreq.reader.text, clazz)
        ServletContext context = hreq.getSession(true).servletContext
        if (context.getAttribute(Helper.DUMP_REQUEST_RESPONSE_KEY)) {
            System.out.println object
        }
        return object;
    }

    static Object parseJson(HttpServletRequest hreq, Class clazz, Class subClazz) {
        final CollectionType finalCalzz = jsonObjectMapper.getTypeFactory().constructCollectionType(clazz, subClazz);
        Object object = jsonObjectMapper.readValue(hreq.reader.text, finalCalzz)
        ServletContext context = hreq.getSession(true).servletContext
        if (context.getAttribute(Helper.DUMP_REQUEST_RESPONSE_KEY)) {
            System.out.println object
        }
        return object
    }

    static respondWithJson(HttpServletResponse hres, def object) {
        hres.contentType = 'application/json'
        jsonObjectMapper.writeValue(hres.getWriter(), object)
    }
}
