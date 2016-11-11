package io.wybis.smartexchange.web.session

import io.wybis.smartexchange.dto.ResponseDto
import io.wybis.smartexchange.dto.SessionDto
import io.wybis.smartexchange.model.Tran
import io.wybis.smartexchange.model.TranReceipt
import io.wybis.smartexchange.model.User
import io.wybis.smartexchange.model.constants.TransactionType
import io.wybis.smartexchange.service.SessionService
import io.wybis.smartexchange.service.exceptions.TransactionException
import io.wybis.smartexchange.util.Helper

ResponseDto responseDto = new ResponseDto(type : 0, message : 'success...')

log.info('buyAndTransfer started...')
try {
    SessionDto sessionUserDto = session[SessionService.SESSION_USER_KEY]

    TranReceipt tranReceipt = jsonCategory.parseJson(request, TranReceipt.class)

    double totalAmount = 0, absTotalAmount = 0, amount = 0
    tranReceipt.trans.each { Tran tran ->
        amount = tran.unit * tran.rate
        if(tran.type == TransactionType.BUY) {
            amount *= -1
        }
        totalAmount += amount
    }
    absTotalAmount = totalAmount < 0 ? totalAmount * -1 : totalAmount

    if(tranReceipt.amount != 0) {

        User emp = User.get(sessionUserDto.id);
        //emp.cashAccount = Account.get(emp.cashAccountId);

        Tran tran = new Tran()
        tran.accountId = emp.cashAccountId
        tran.unit = absTotalAmount
        tran.rate = 1

        if(totalAmount < 0) {
            tran.type = TransactionType.SELL
        } else {
            tran.type = TransactionType.BUY
        }

        tranReceipt.trans << tran

    } else {

        User cus = User.get(tranReceipt.forUserId);
        //cus.cashAccount = Account.get(cus.cashAccountId);

        Tran tran = new Tran()
        tran.accountId = cus.cashAccountId
        tran.unit = absTotalAmount
        tran.rate = 1

        if(totalAmount < 0) {
            tran.type = TransactionType.BUY
        }
        else {
            tran.type = TransactionType.SELL
        }

        tranReceipt.trans << tran
    }

    tranService.create(sessionUserDto, tranReceipt)

    responseDto.data = tranReceipt;
    responseDto.message = 'Purchase and transfer executed successfully...'
}
catch(TransactionException e) {
    responseDto.type = ResponseDto.ERROR
    responseDto.message = tranReceipt.errorMessage;
}
catch(Throwable t) {
    if(localMode) {
        t.printStackTrace()
    }
    responseDto.type = ResponseDto.UNKNOWN
    responseDto.message = 'Buy and Transfer failed...'
    responseDto.data = Helper.getStackTraceAsString(t)
    log.severe(responseDto.data)
}
log.info('buyAndTransfer finished...')

jsonCategory.respondWithJson(response, responseDto)