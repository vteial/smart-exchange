package io.wybis.smartexchange.web.filters

import io.wybis.smartexchange.util.Helper

import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

public class DumpFilter extends AbstractFilter {

    public void doFilter(ServletRequest req, ServletResponse res,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest hreq = (HttpServletRequest) req
        ServletContext context = hreq.getSession(true).servletContext
        if (context.getAttribute(Helper.DUMP_REQUEST_RESPONSE_KEY)) {
            Helper.dumpRequest(hreq)
        }
        chain.doFilter(req, res)
        if (context.getAttribute(Helper.DUMP_REQUEST_RESPONSE_KEY)) {
            HttpServletResponse hres = (HttpServletResponse) res
            Helper.dumpResponse(hres)
        }
    }
}
