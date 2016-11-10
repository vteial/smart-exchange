package io.wybis.smartexchange.web.session

import io.wybis.smartexchange.dto.ResponseDto
import io.wybis.smartexchange.dto.SessionDto
import io.wybis.smartexchange.dto.UserDto
import io.wybis.smartexchange.service.SessionService
import io.wybis.smartexchange.service.exceptions.GeneralException
import io.wybis.smartexchange.service.exceptions.ModelNotFoundException
import io.wybis.smartexchange.util.Helper

ResponseDto responseDto = new ResponseDto(type: 0, message: 'Successfully saved...')

try {
    SessionDto sessionDto = session[SessionService.SESSION_USER_KEY]

    UserDto userDto = jsonCategory.parseJson(request, UserDto.class)

    sessionService.changePassword(sessionDto, userDto)
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
    t.printStackTrace()
    responseDto.type = ResponseDto.UNKNOWN
    responseDto.message = 'Reset password failed...';
    responseDto.data = Helper.getStackTraceAsString(t)
    log.severe(responseDto.data)
}

jsonCategory.respondWithJson(response, responseDto)