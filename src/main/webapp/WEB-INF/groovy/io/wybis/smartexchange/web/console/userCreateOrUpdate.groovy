package io.wybis.smartexchange.web.console

import io.wybis.smartexchange.dto.ResponseDto
import io.wybis.smartexchange.dto.SessionDto
import io.wybis.smartexchange.model.User
import io.wybis.smartexchange.service.exceptions.ModelAlreadyExistException
import io.wybis.smartexchange.service.exceptions.ModelNotFoundException
import io.wybis.smartexchange.util.Helper

ResponseDto responseDto = new ResponseDto(type: 0, message: 'Successfully created/updated...')

try {
    SessionDto sessionUserDto = new SessionDto()

    User iuser = jsonCategory.parseJson(request, User.class), ouser = null

    if (iuser.id == 0) {

        ouser = userService.create(sessionUserDto, iuser)
        responseDto.message = "User created successfully..."

    } else {

        ouser = userService.update(sessionUserDto, iuser)
        responseDto.message = "User updated successfully..."

    }

    responseDto.data = ouser
}
catch (ModelNotFoundException e) {
    responseDto.type = ResponseDto.ERROR
    responseDto.message = "User ${iuser.id} doesn't exist..."
}
catch (ModelAlreadyExistException e) {
    responseDto.type = ResponseDto.ERROR
    responseDto.message = "User ${iuser.id} already exist..."
}
catch (Throwable t) {
    if (localMode) {
        t.printStackTrace()
    }
    responseDto.type = ResponseDto.UNKNOWN
    responseDto.message = 'User create/update failed...';
    responseDto.data = Helper.getStackTraceAsString(t)
    log.severe(responseDto.data)
}

jsonCategory.respondWithJson(response, responseDto)