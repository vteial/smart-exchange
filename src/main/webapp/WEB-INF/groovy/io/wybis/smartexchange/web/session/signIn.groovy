package io.wybis.smartexchange.web.session

import io.wybis.smartexchange.dto.ResponseDto
import io.wybis.smartexchange.dto.UserDto
import io.wybis.smartexchange.service.SessionService
import io.wybis.smartexchange.service.exceptions.InvalidCredentialException
import io.wybis.smartexchange.service.exceptions.UnAuthorizedException
import io.wybis.smartexchange.util.Helper

ResponseDto responseDto = new ResponseDto(type: 0, message: 'Successfully signed in...')
request.responseDto = responseDto

UserDto userDto = jsonCategory.parseJson(request, UserDto.class)
try {
    if (userDto.userId) {
        userDto.userId = userDto.userId.toLowerCase()
    }

    sessionService.signIn(session, userDto)

    String loginResponseRedirectURI = session[SessionService.SESSION_LOGIN_REDIRECT_KEY]
    if (loginResponseRedirectURI) {
        loginResponseRedirectURI = loginResponseRedirectURI.substring(10)
        responseDto.data = loginResponseRedirectURI
    }
    log.info(responseDto.data.toString())
}
catch (InvalidCredentialException e) {
    responseDto.type = ResponseDto.ERROR
    responseDto.message = 'Invalid User Id or Password...'
}
catch (UnAuthorizedException e) {
    responseDto.type = ResponseDto.ERROR
    responseDto.message = 'Invalid access. Your account may be disabled or not confirmed.'
}
catch (Throwable t) {
    responseDto.type = ResponseDto.UNKNOWN
    responseDto.message = 'Sign In failed...';
    responseDto.data = Helper.getStackTraceAsString(t)
    t.printStackTrace()
    //logger.warning(responseDto.message)
}

jsonCategory.respondWithJson(response, responseDto)