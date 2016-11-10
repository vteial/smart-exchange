package io.wybis.smartexchange.web.session

import io.wybis.smartexchange.dto.ResponseDto
import io.wybis.smartexchange.dto.SessionDto
import io.wybis.smartexchange.dto.UserDto
import io.wybis.smartexchange.service.SessionService
import io.wybis.smartexchange.service.exceptions.GeneralException
import io.wybis.smartexchange.service.exceptions.ModelNotFoundException
import io.wybis.smartexchange.util.Helper

ResponseDto responseDto = new ResponseDto(type: 0, message: 'Success...')

try {
    Long userId = session[SessionService.SESSION_USER_ID_KEY]

    UserDto userDto = jsonCategory.parseJson(request, UserDto.class)

    if (userId) {

        SessionDto sessionDto = new SessionDto(id: userId.longValue())

        userDto.id = sessionDto.id
        userDto.currentPassword = session[SessionService.SESSION_USER_PASSWORD_KEY]
        sessionService.changePassword(sessionDto, userDto)
        session[SessionService.SESSION_USER_ID_KEY] = null
        session[SessionService.SESSION_USER_PASSWORD_KEY] = null

        responseDto.message = 'Your password has been changed successfully. Please sign-in to proceed.'

    } else {

        responseDto.type = ResponseDto.ERROR
        responseDto.message = 'Invalid account for reset password...'
    }
}
catch (GeneralException e) {
    responseDto.type = ResponseDto.ERROR
    responseDto.message = e.message
}
catch (ModelNotFoundException e) {
    responseDto.type = ResponseDto.ERROR
    responseDto.message = 'Account doesn\'t exists...'
}
catch (Throwable t) {
    responseDto.type = ResponseDto.UNKNOWN
    responseDto.message = 'Reset password failed...';
    responseDto.data = Helper.getStackTraceAsString(t)
    log.severe(responseDto.data)
}

jsonCategory.respondWithJson(response, responseDto)