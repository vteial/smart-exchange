package io.wybis.smartexchange.web.session

import io.wybis.smartexchange.dto.BuyAndTransferReqDto
import io.wybis.smartexchange.dto.BuyAndTransferResDto
import io.wybis.smartexchange.dto.ResponseDto
import io.wybis.smartexchange.dto.SessionDto
import io.wybis.smartexchange.model.Tran
import io.wybis.smartexchange.model.TranReceipt
import io.wybis.smartexchange.model.Transfer
import io.wybis.smartexchange.model.TransferReceipt
import io.wybis.smartexchange.model.User
import io.wybis.smartexchange.model.constants.TransactionCategory
//import io.wybis.smartexchange.model.constants.TransferCategory
import io.wybis.smartexchange.model.constants.TransactionType
import io.wybis.smartexchange.model.constants.TransferType
import io.wybis.smartexchange.service.SessionService
import io.wybis.smartexchange.service.exceptions.TransactionException
import io.wybis.smartexchange.util.Helper

ResponseDto responseDto = new ResponseDto(type: 0, message: 'success...')

log.info('buyAndTransfer started...')
try {
    SessionDto sessionUserDto = session[SessionService.SESSION_USER_KEY]

    BuyAndTransferReqDto buyAndTransferReqDto = jsonCategory.parseJson(request, BuyAndTransferReqDto.class)

    BuyAndTransferResDto buyAndTransferResDto = new BuyAndTransferResDto()
    buyAndTransferResDto.tranReceipts = []
    TranReceipt tranReceipt = new TranReceipt()
    tranReceipt.category = TransactionCategory.CUSTOMER
    tranReceipt.trans = []

    Tran tran = new Tran()
    tran.accountId = buyAndTransferReqDto.tranAccountId
    tran.type = TransactionType.BUY
    tran.baseUnit = buyAndTransferReqDto.tranBaseUnit
    tran.unit = buyAndTransferReqDto.tranUnit
    tran.rate = buyAndTransferReqDto.tranRate
    tran.profitRate = buyAndTransferReqDto.tranProfitRate
    tran.computeAmount()
    tranReceipt.trans << tran

    double tranAmount = tran.amount
    User bvu = User.get(sessionUserDto.branchVirtualEmployeeId)

    tran = new Tran()
    tran.accountId = bvu.cashAccountId
    tran.type = TransactionType.BUY
    tran.baseUnit = 1
    tran.unit = tranAmount
    tran.rate = 1
    tran.computeAmount()
    tranReceipt.trans << tran

    buyAndTransferResDto.tranReceipts << tranReceipt

    buyAndTransferResDto.transferReceipts = []
    buyAndTransferReqDto.transfers.each { item ->
        TransferReceipt transferReceipt = new TransferReceipt()
        transferReceipt.category = 'customer'
        transferReceipt.trans = []

        Transfer transfer = new Transfer()
        transfer.accountId = bvu.cashAccountId
        transfer.type = TransferType.SELL
        transfer.unit = item.unit
        transferReceipt.trans << transfer

        transfer = new Transfer()
        transfer.accountId = item.accountId
        transfer.type = TransactionType.BUY
        transfer.unit = item.unit
        transferReceipt.trans << transfer

        buyAndTransferResDto.transferReceipts << transferReceipt
    }
    try {
        buyAndTransferResDto.id = autoNumberService.getNextNumber(sessionUserDto, 'buyAndTransferId')

        buyAndTransferResDto.tranReceipts.each { item ->
            tranService.create(sessionUserDto, item)
        }

        buyAndTransferResDto.transferReceipts.each { item ->
            transferService.create(sessionUserDto, item)
        }

        responseDto.data = buyAndTransferResDto;
        responseDto.message = 'Purchase and transfer executed successfully...'
    }
    catch (TransactionException e) {
        responseDto.type = ResponseDto.ERROR
        responseDto.message = tranReceipt.errorMessage;
    }

}
catch (Throwable t) {
    if (localMode) {
        t.printStackTrace()
    }
    responseDto.type = ResponseDto.UNKNOWN
    responseDto.message = 'Buy and Transfer failed...'
    responseDto.data = Helper.getStackTraceAsString(t)
    log.severe(responseDto.data)
}
log.info('buyAndTransfer finished...')

jsonCategory.respondWithJson(response, responseDto)