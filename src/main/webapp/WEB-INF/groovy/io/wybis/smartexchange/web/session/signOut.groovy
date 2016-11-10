package io.wybis.smartexchange.web.session;

sessionService.signOut(session)

session.removeAttribute('sessionSurvey')
session.removeAttribute('sessionSurveyResultUniqueId')
session.invalidate()

forward '/index'