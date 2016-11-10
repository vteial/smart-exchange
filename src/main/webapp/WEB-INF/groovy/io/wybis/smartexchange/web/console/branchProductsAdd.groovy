package io.wybis.smartexchange.web.console

import io.wybis.smartexchange.dto.ResponseDto
import io.wybis.smartexchange.dto.SessionDto
import io.wybis.smartexchange.model.Branch
import io.wybis.smartexchange.model.Product
import io.wybis.smartexchange.util.Helper

ResponseDto responseDto = new ResponseDto(type: 0, message: 'Successfully created/updated...')

try {
    List<Product> products = jsonCategory.parseJson(request, List.class, Product.class)

    log.info("creating branch products started...")

    SessionDto sessionUser = new SessionDto()

    long branchId = params.branchId as Long

    Branch branch = Branch.get(branchId)

    sessionUser.with { code = branch.code }

    products.each { t ->
        t.branchId = branch.id
        productService.create(sessionUser, t)
    }

    log.info("creating branch products finished...")

    responseDto.message = "Branch products successfully created..."
    responseDto.data = products

}
catch (Throwable t) {
    if (localMode) {
        t.printStackTrace()
    }
    responseDto.type = ResponseDto.UNKNOWN
    responseDto.message = 'Branch products create failed...';
    responseDto.data = Helper.getStackTraceAsString(t)
    log.severe(responseDto.data)
}
jsonCategory.respondWithJson(response, responseDto)
