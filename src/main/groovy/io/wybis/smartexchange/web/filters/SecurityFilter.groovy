package io.wybis.smartexchange.web.filters

import io.wybis.smartexchange.dto.SessionDto
import io.wybis.smartexchange.service.SessionService

import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

public class SecurityFilter extends AbstractFilter {

    Map<String, Boolean> pubExactPaths = [:]
    List<String> pubRegexPaths = [], pvtRegexPaths = []

    public void init(FilterConfig filterConfig) {
        pubExactPaths['/sessions/properties'] = true
        pubExactPaths['/sessions/sign-in'] = true
        pubExactPaths['/sessions/sign-out'] = true
        pubExactPaths['/sessions/sign-up'] = true
        pubExactPaths['/sessions/sign-up-confirm'] = true
        pubExactPaths['/sessions/reset-password-request'] = true
        pubExactPaths['/sessions/reset-password-confirm'] = true
        pubExactPaths['/sessions/reset-password'] = true

        pubRegexPaths << '^/sessions/sign-up-confirm/\\d+/.*'

        //pvtRegexPaths << '^/sessions/events/event/\\d+$'
    }

    public void doFilter(ServletRequest req, ServletResponse res,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req

        if (!request.requestURI.startsWith('/sessions/')) {
            chain.doFilter(req, res)
            return
        }
        //System.out.println("0 - ${request.requestURI}")

        //System.out.println("1 - ${request.requestURI}")
        if (pubExactPaths[request.requestURI] || this.isReqPathExistsIn(this.pubRegexPaths, request.requestURI)) {
            chain.doFilter(req, res)
            return
        }

        //System.out.println("2 - ${request.requestURI}")
        HttpServletResponse response = (HttpServletResponse) res
        HttpSession session = request.session
        SessionDto sessionUser = (SessionDto) session.getAttribute(SessionService.SESSION_USER_KEY)

        if (sessionUser == null && this.isReqPathExistsIn(this.pvtRegexPaths, request.requestURI)) {
            session.setAttribute(SessionService.SESSION_LOGIN_REDIRECT_KEY, request.requestURI)
            response.sendRedirect('/index.html#/sign-in')
            return;
        }

        if (sessionUser != null && this.isReqPathExistsIn(this.pvtRegexPaths, request.requestURI)) {
            session.setAttribute(SessionService.SESSION_LOGIN_REDIRECT_KEY, request.requestURI)
            response.sendRedirect('/home.html#/' + request.requestURI.substring(10))
            return;
        }

        //System.out.println("3 - ${request.requestURI}")
        if (sessionUser == null) {
            response.sendError(419);
            return;
        }

        //System.out.println("4 - ${request.requestURI}")
//        String arrivedUserId = request.getHeader('X-UserId')
//        if (arrivedUserId != 'null' && sessionUser.userId != arrivedUserId) {
//            response.sendError(419);
//            return;
//        }

        //System.out.println("5 - ${request.requestURI}")
        chain.doFilter(req, res)
    }

    private boolean isReqPathExistsIn(List<String> regexPaths, String reqPath) {
        boolean flag = false

        for (int i = 0; i < regexPaths.size(); i++) {
            String regexPath = regexPaths[i]
            flag = reqPath.matches(regexPath)
            if (flag) {
                i = regexPaths.size() + 1
            }
        }
        //System.out.println("${flag} - ${reqPath}")

        return flag;
    }
}
