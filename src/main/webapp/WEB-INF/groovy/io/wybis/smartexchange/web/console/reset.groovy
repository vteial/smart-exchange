package io.wybis.smartexchange.web.console

import io.wybis.smartexchange.dto.ResponseDto
import io.wybis.smartexchange.model.Account
import io.wybis.smartexchange.model.Address
import io.wybis.smartexchange.model.AutoNumber
import io.wybis.smartexchange.model.Branch
import io.wybis.smartexchange.model.Country
import io.wybis.smartexchange.model.Product
import io.wybis.smartexchange.model.Role
import io.wybis.smartexchange.model.User
import io.wybis.smartexchange.util.Helper

ResponseDto responseDto = new ResponseDto()
responseDto.type = ResponseDto.SUCCESS;

log.info("reset started...")
try {
    StringWriter sw = new StringWriter()
    PrintWriter pw = new PrintWriter(sw);
    pw.println 'clear started...'

    if (localMode) {

        def entities = AutoNumber.findAll()
        entities.each { entity ->
            entity.delete()
        }
        pw.println entities.size() + ' autoNumbers deleted'

        entities = Country.findAll()
        entities.each { entity ->
            entity.delete()
        }
        pw.println entities.size() + ' countrys deleted'

        entities = Address.findAll()
        entities.each { entity ->
            entity.delete()
        }
        pw.println entities.size() + ' addresses deleted'

        entities = Role.findAll()
        entities.each { entity ->
            entity.delete()
        }
        pw.println entities.size() + ' roles deleted'

        entities = User.findAll()
        entities.each { entity ->
            entity.delete()
        }
        pw.println entities.size() + ' users deleted'

        entities = Product.findAll()
        entities.each { entity ->
            entity.delete()
        }
        pw.println entities.size() + ' products deleted'

        entities = Account.findAll()
        entities.each { entity ->
            entity.delete()
        }
        pw.println entities.size() + ' stocks deleted'

        entities = Branch.findAll()
        entities.each { entity ->
            entity.delete()
        }
        pw.println entities.size() + ' branchs deleted'

//        entities = TransferReceipt.findAll()
//        entities.each { entity ->
//            entity.delete()
//        }
//        pw.println entities.size() + ' transferReceipts deleted'
//
//        entities = Transfer.findAll()
//        entities.each { entity ->
//            entity.delete()
//        }
//        pw.println entities.size() + ' transfer deleted'
//
//        entities = OrderReceipt.findAll()
//        entities.each { entity ->
//            entity.delete()
//        }
//        pw.println entities.size() + ' orderReceipts deleted'
//
//        entities = Order.findAll()
//        entities.each { entity ->
//            entity.delete()
//        }
//        pw.println entities.size() + ' orders deleted'
//
//        entities = TranReceipt.findAll()
//        entities.each { entity ->
//            entity.delete()
//        }
//        pw.println entities.size() + ' tranReceipts deleted'
//
//        entities = Tran.findAll()
//        entities.each { entity ->
//            entity.delete()
//        }
//        pw.println entities.size() + ' trans deleted'
    } else {
        pw.println('reset is not allowed in production')
    }

    pw.println 'clear finished...'
    responseDto.data = sw.toString()
}
catch (Throwable t) {
    if (localMode) {
        t.printStackTrace()
    }
    responseDto.type = ResponseDto.UNKNOWN
    responseDto.message = t.message
    responseDto.data = Helper.getStackTraceAsString(t)
    log.warning(responseDto.data)
}
log.info("reset finished...")

jsonCategory.respondWithJson(response, responseDto)
