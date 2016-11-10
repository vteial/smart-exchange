package io.wybis.smartexchange.web.session

import io.wybis.smartexchange.dto.ResponseDto
import io.wybis.smartexchange.dto.SessionDto
import io.wybis.smartexchange.service.SessionService
import io.wybis.smartexchange.util.Helper

ResponseDto responseDto = request.responseDto

def props = sessionService.properties(session), model = [:]
props.domainPrefix = Helper.getDomainPrefix(request, app)

if(users.userLoggedIn) {
    props.adminLogoutUrl = users.createLogoutURL('/console.html')
} else {
    props.adminLoginUrl = users.createLoginURL('/console.html')
}

def message = session.message
if(message) {
    props.message = message
    session.removeAttribute('message')
}


if (responseDto) {
    responseDto.data = props
} else {
    responseDto = new ResponseDto(data: props)
}

SessionDto sessionDto = session[SessionService.SESSION_USER_KEY]

//if (sessionDto) {
//
//    model['products'] = sessionService.products(sessionDto)
//
//    responseDto.model = model;
//}

jsonCategory.respondWithJson(response, responseDto)

