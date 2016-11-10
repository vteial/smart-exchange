package io.wybis.smartexchange.web.console

import io.wybis.smartexchange.dto.ResponseDto
import io.wybis.smartexchange.model.User
import io.wybis.smartexchange.util.Helper

ResponseDto responseDto = new ResponseDto(type: 0, message: 'Successfully fetched...')

try {

    long iuserId = 0
    if (params.userId) {
        try {
            iuserId = Long.parseLong(params.userId)
        } catch (NumberFormatException nfe) {
        }
    }
    responseDto.data = User.get(iuserId)
    if (!responseDto.data) {
        responseDto.type = ResponseDto.ERROR
        responseDto.message = 'Invalid user id or user doesn\'t exist'
    }

}
catch (Throwable t) {
    if (localMode) {
        t.printStackTrace()
    }
    responseDto.type = ResponseDto.UNKNOWN
    responseDto.message = 'User fetching failed...'
    responseDto.data = Helper.getStackTraceAsString(t)
    log.severe(responseDto.data)
}

jsonCategory.respondWithJson(response, responseDto)