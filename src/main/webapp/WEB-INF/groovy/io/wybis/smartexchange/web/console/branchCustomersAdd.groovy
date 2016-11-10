package io.wybis.smartexchange.web.console

import io.wybis.smartexchange.dto.ResponseDto
import io.wybis.smartexchange.dto.SessionDto
import io.wybis.smartexchange.model.Branch
import io.wybis.smartexchange.model.User
import io.wybis.smartexchange.util.Helper

ResponseDto responseDto = new ResponseDto(type: 0, message: 'Successfully created/updated...')

try {

    List<User> customers = jsonCategory.parseJson(request, List.class, User.class)

    log.info("creating branch customers started...")

    SessionDto sessionUser = new SessionDto()

    long branchId = params.branchId as Long

    Branch branch = Branch.get(branchId)

    sessionUser.with { code = branch.code }

    customers.each { t ->
        t.branchId = branch.id
        customerService.create(sessionUser, t)
    }

    log.info("creating branch customers finished...")

    responseDto.message = "Branch customers successfully created..."
    responseDto.data = customers

}
catch (Throwable t) {
    if (localMode) {
        t.printStackTrace()
    }
    responseDto.type = ResponseDto.UNKNOWN
    responseDto.message = 'Branch customers create failed...';
    responseDto.data = Helper.getStackTraceAsString(t)
    log.severe(responseDto.data)
}
jsonCategory.respondWithJson(response, responseDto)