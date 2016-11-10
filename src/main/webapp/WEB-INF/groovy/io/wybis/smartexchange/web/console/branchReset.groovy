package io.wybis.smartexchange.web.console

import io.wybis.smartexchange.dto.ResponseDto
import io.wybis.smartexchange.model.Account
import io.wybis.smartexchange.model.Product
import io.wybis.smartexchange.util.Helper

ResponseDto responseDto = new ResponseDto(type: 0, message: 'success...')

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

//    deleteEntitys(Transfer.class.simpleName, ibranchId)
//
//    deleteEntitys(TransferReceipt.class.simpleName, ibranchId)
//
//    deleteEntitys(Order.class.simpleName, ibranchId)
//
//    deleteEntitys(OrderReceipt.class.simpleName, ibranchId)
//
//    deleteEntitys(Tran.class.simpleName, ibranchId)
//
//    deleteEntitys(TranReceipt.class.simpleName, ibranchId)

    def entitys = datastore.execute {
        from Account.class.simpleName
        where branchId == ibranchId
    }
    entitys.each { entity ->
        Account account = entity as Account
        account.with {
            amount = 0
            handStock = 0
            handStockMove = 0
            virtualStockBuy = 0
            virtualStockSell = 0
            availableStock = 0
        }
        account.save()
    }

    entitys = datastore.execute {
        from Product.class.simpleName
        where branchId == ibranchId
    }
    entitys.each { entity ->
        Product product = entity as Product
        product.with {
            amount = 0
            handStock = 0
            handStockAverage = 0
            virtualStockBuy = 0
            virtualStockSell = 0
            virtualStockAverage = 0
            availableStock = 0
            availableStockAverage = 0
        }
        product.save()
    }

    responseDto.message = "Branch '${ibranchId}' reseted...";
}
catch (Throwable t) {
    if (localMode) {
        t.printStackTrace()
    }
    responseDto.type = ResponseDto.UNKNOWN
    responseDto.message = 'Branch reset failed...'
    responseDto.data = Helper.getStackTraceAsString(t)
    log.severe(responseDto.data)
}

jsonCategory.respondWithJson(response, responseDto)