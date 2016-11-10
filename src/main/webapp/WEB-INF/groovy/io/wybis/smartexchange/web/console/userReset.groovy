package io.wybis.smartexchange.web.console

import io.wybis.smartexchange.dto.ResponseDto
import io.wybis.smartexchange.dto.SessionDto
import io.wybis.smartexchange.service.exceptions.ModelNotFoundException
import io.wybis.smartexchange.util.Helper

ResponseDto responseDto = new ResponseDto(type: 0, message: 'success...')

try {
    if (params.userId) {
        try {
            long iuserId = Long.parseLong(params.userId)
            try {
                userService.reset(new SessionDto(), iuserId)
                responseDto.message = "User id '${iuserId}' reseted successfully...";
            } catch (ModelNotFoundException mfe) {
                responseDto.message = "User id '${iuserId}' doesn't exists...";
            }
        } catch (NumberFormatException nfe) {
            responseDto.message = "Invalid user id...";
        }
    } else {
        responseDto.message = "Invalid user id...";
    }
}
catch (Throwable t) {
    if (localMode) {
        t.printStackTrace()
    }
    responseDto.type = ResponseDto.UNKNOWN
    responseDto.message = 'User reset failed...'
    responseDto.data = Helper.getStackTraceAsString(t)
    log.severe(responseDto.data)
}

jsonCategory.respondWithJson(response, responseDto)