package io.wybis.smartexchange.web.session

import io.wybis.smartexchange.dto.ResponseDto
import io.wybis.smartexchange.dto.UserDto
import io.wybis.smartexchange.service.exceptions.GeneralException
import io.wybis.smartexchange.service.exceptions.ModelAlreadyExistException
import io.wybis.smartexchange.util.Helper

ResponseDto responseDto = new ResponseDto(type: 0, message: 'Success...')

try {
    UserDto userDto = jsonCategory.parseJson(request, UserDto.class)

    String confirmUrl = sessionService.signUp(userDto, Helper.getDomainPrefix(request, app))
    //if (localMode) {
    responseDto.data = confirmUrl
    //}
    responseDto.message = 'Successfully signed up... An email has been sent to your email id, Please confirm your email id using the link in the email.'

}
catch (GeneralException e) {
    responseDto.type = ResponseDto.ERROR
    responseDto.message = e.message
}
catch (ModelAlreadyExistException e) {
    responseDto.type = ResponseDto.ERROR
    responseDto.message = 'User Id or Email Id already exists...'
}
catch (Throwable t) {
    responseDto.type = ResponseDto.UNKNOWN
    responseDto.message = 'Sign Up failed...';
    responseDto.data = Helper.getStackTraceAsString(t)
    log.severe(responseDto.data)
}

jsonCategory.respondWithJson(response, responseDto)