package io.wybis.smartexchange.web.session

import io.wybis.smartexchange.dto.ResponseDto
import io.wybis.smartexchange.dto.SessionDto
import io.wybis.smartexchange.dto.UserDto
import io.wybis.smartexchange.service.SessionService
import io.wybis.smartexchange.util.Helper

ResponseDto responseDto = new ResponseDto(type: 0, message: 'Successfully saved...')

SessionDto sessionDto = session[SessionService.SESSION_USER_KEY]

UserDto userDto = jsonCategory.parseJson(request, UserDto.class)
try {
    sessionService.changeDetails(sessionDto, userDto)
}
catch (Throwable t) {
    t.printStackTrace()
    responseDto.type = ResponseDto.UNKNOWN
    responseDto.message = 'Saving user details failed...';
    responseDto.data = Helper.getStackTraceAsString(t)
    log.warning(responseDto.message)
}

jsonCategory.respondWithJson(response, responseDto)