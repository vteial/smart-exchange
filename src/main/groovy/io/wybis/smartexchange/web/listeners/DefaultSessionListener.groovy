package io.wybis.smartexchange.web.listeners

import groovyx.gaelyk.GaelykBindings
import groovyx.gaelyk.logging.GroovyLogger

import javax.servlet.http.HttpSession
import javax.servlet.http.HttpSessionEvent
import javax.servlet.http.HttpSessionListener

@GaelykBindings
class DefaultSessionListener implements HttpSessionListener {

    GroovyLogger log = new GroovyLogger(DefaultSessionListener.class.getName())

//    @Override
//    void onLogin(HttpSession session) {
//        SessionDto sessionDto = session.getAttribute(SessionService.SESSION_USER_KEY)
//        log.info("session ${session.id} logged in by ${sessionDto.id}")
//    }
//
//    @Override
//    void onLogout(HttpSession session) {
//        SessionDto sessionDto = session.getAttribute(SessionService.SESSION_USER_KEY)
//        log.info("session ${session.id} logged out by ${sessionDto.id}")
//    }

    @Override
    void sessionCreated(HttpSessionEvent sessionEvent) {
        HttpSession session = sessionEvent.session

        log.info("session ${session.id} created with timeout ${session.maxInactiveInterval} minutes...")
    }

    @Override
    void sessionDestroyed(HttpSessionEvent sessionEvent) {
        HttpSession session = sessionEvent.session

        log.info("session ${session.id} destroyed with timeout ${session.maxInactiveInterval} minutes...")
    }
}
