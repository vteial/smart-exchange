package io.wybis.smartexchange.web.session

import io.wybis.smartexchange.dto.ResponseDto
import io.wybis.smartexchange.dto.UserDto
import io.wybis.smartexchange.service.exceptions.GeneralException
import io.wybis.smartexchange.service.exceptions.ModelNotFoundException
import io.wybis.smartexchange.util.Helper

ResponseDto responseDto = new ResponseDto(type: 0, message: 'Success...')

try {
    UserDto userDto = jsonCategory.parseJson(request, UserDto.class)

    String resetUrl = sessionService.resetPasswordRequest(userDto, Helper.getDomainPrefix(request, app))
    //if (localMode) {
    responseDto.data = resetUrl
    //}
    responseDto.message = 'An email has been sent to your email id with instructions on how to reset your password.'
}
catch (GeneralException e) {
    responseDto.type = ResponseDto.ERROR
    responseDto.message = e.message
}
catch (ModelNotFoundException e) {
    responseDto.type = ResponseDto.ERROR
    responseDto.message = 'User Id or Email Id doesn\'t exists...'
}
catch (Throwable t) {
    responseDto.type = ResponseDto.UNKNOWN
    responseDto.message = 'Reset password request failed...';
    responseDto.data = Helper.getStackTraceAsString(t)
    log.severe(responseDto.data)
}

jsonCategory.respondWithJson(response, responseDto)