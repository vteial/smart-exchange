package io.wybis.smartexchange.web.console

import io.wybis.smartexchange.dto.ResponseDto
import io.wybis.smartexchange.dto.SessionDto
import io.wybis.smartexchange.model.Branch
import io.wybis.smartexchange.util.Helper

ResponseDto responseDto = new ResponseDto(type: 0, message: 'Successfully created/updated...')

try {
    Branch branch = jsonCategory.parseJson(request, Branch.class)

    log.info("creating branch started...")

    SessionDto sessionUser = new SessionDto()

    branchService.create(sessionUser, branch)

    sessionUser.with { code = branch.code }

    branch.products.each { t ->
        t.branchId = branch.id
        productService.create(sessionUser, t)
    }

    branch.employees.each { t ->
        t.branchId = branch.id
        //t.userId = "${t.userId}@${branch.code}"
        employeeService.create(sessionUser, t)
    }

    branch.customers.each { t ->
        t.branchId = branch.id
        customerService.create(sessionUser, t)
    }

    log.info("creating branch finished...")

    responseDto.message = "Branch successfully created..."
    responseDto.data = branch
}
catch (Throwable t) {
    if (localMode) {
        t.printStackTrace()
    }
    responseDto.type = ResponseDto.UNKNOWN
    responseDto.message = 'Branch create failed...';
    responseDto.data = Helper.getStackTraceAsString(t)
    log.severe(responseDto.data)
}
jsonCategory.respondWithJson(response, responseDto)
