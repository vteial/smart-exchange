package io.wybis.smartexchange.web.console

import io.wybis.smartexchange.dto.ResponseDto
import io.wybis.smartexchange.model.Account
import io.wybis.smartexchange.model.Branch
import io.wybis.smartexchange.model.Product
import io.wybis.smartexchange.model.User
import io.wybis.smartexchange.util.Helper

//import io.wybis.smartexchange.model.Order
//import io.wybis.smartexchange.model.OrderReceipt
//import io.wybis.smartexchange.model.Tran
//import io.wybis.smartexchange.model.TranReceipt
ResponseDto responseDto = new ResponseDto()

responseDto.type = ResponseDto.SUCCESS;

try {

    def deleteEntitys = { entityName, ibranchId ->
        def keys = datastore.execute {
            select keys from entityName
            where branchId == ibranchId
        }
        keys.each { key ->
            key.delete()
        }
    }

    long ibranchId = params.branchId as Long

    def entityNames = []
//	entityNames << Order.class.simpleName
//	entityNames << OrderReceipt.class.simpleName
//	entityNames << Tran.class.simpleName
//	entityNames << TranReceipt.class.simpleName
    entityNames << Account.class.simpleName
    entityNames << Product.class.simpleName
    entityNames << User.class.simpleName

    entityNames.each { entityName ->
        log.info("$entityName deletion started...")
        deleteEntitys(entityName, ibranchId)
        log.info("$entityName deletion finished...")
    }
    Branch branch = Branch.get(ibranchId)
    branch.delete()

    responseDto.message = "Branch '${ibranchId}' deleted...";
}
catch (Throwable t) {
    if (localMode) {
        t.printStackTrace()
    }
    responseDto.type = ResponseDto.UNKNOWN
    responseDto.message = t.message
    responseDto.data = Helper.getStackTraceAsString(t)
    log.severe(responseDto.data)
}

jsonCategory.respondWithJson(response, responseDto)