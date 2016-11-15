package io.wybis.smartexchange.service.impl

import groovyx.gaelyk.GaelykBindings
import io.wybis.smartexchange.dto.SessionDto
import io.wybis.smartexchange.model.Account
import io.wybis.smartexchange.model.Product
import io.wybis.smartexchange.model.Transfer
import io.wybis.smartexchange.model.TransferReceipt
import io.wybis.smartexchange.model.constants.TransferStatus
import io.wybis.smartexchange.model.constants.TransferType
import io.wybis.smartexchange.service.TransferService
import io.wybis.smartexchange.service.exceptions.ModelNotFoundException
import io.wybis.smartexchange.service.exceptions.TransactionException
import io.wybis.smartexchange.service.exceptions.TransferException

@GaelykBindings
class DefaultTransferService extends AbstractService implements TransferService {

    @Override
    TransferReceipt accept(SessionDto sessionUser, long tranReceiptId) throws ModelNotFoundException, TransferException {
        TransferReceipt receipt = TransferReceipt.get(tranReceiptId)

        if (!receipt) {
            throw new ModelNotFoundException('Transfer doesn\'t exists...')
        }
        if (receipt.status == TransferStatus.ACCEPTED) {
            String s = 'Transfer is already accepted...'
            throw new TransactionException(s)
        }
        if (receipt.status != TransferStatus.PENDING) {
            String s = 'Transfer is not in acceptable state...'
            throw new TransactionException(s)
        }

        def entitys = datastore.execute {
            from Transfer.class.simpleName
            where receiptId == receipt.id
        }

        List<Transfer> trans = []
        entitys.each { entity ->
            Transfer model = entity as Transfer
            model.account = Account.get(model.accountId)
            model.account.product = Product.get(model.account.productId)
            trans << model
        }
        receipt.trans = trans;

        Transfer frTran = receipt.trans[0]
        Account frAccount = frTran.account

        frTran.status = TransferStatus.ACCEPTED
        frTran.preUpdate(sessionUser.id)
        frTran.save()

        frAccount.withdrawHandStockMove(frTran.unit)
        frAccount.computeAvailableStock()
        frAccount.preUpdate(sessionUser.id)
        frAccount.save()

        frAccount.product.preUpdate(sessionUser.id)
        frAccount.product.save()

        Transfer toTran = receipt.trans[1]
        Account toAccount = toTran.account

        toAccount.depositHandStock(toTran.unit)
        toAccount.computeAvailableStock()
        toAccount.preUpdate(sessionUser.id)
        toAccount.save()

        toAccount.product.preUpdate(sessionUser.id)
        toAccount.product.save()

        toTran.status = TransferStatus.ACCEPTED
        toTran.preUpdate(sessionUser.id)
        toTran.save()

        receipt.status = TransferStatus.ACCEPTED
        receipt.preUpdate(sessionUser.id)
        receipt.save()

        return receipt
    }

    @Override
    TransferReceipt cancel(SessionDto sessionUser, long tranReceiptId) throws ModelNotFoundException, TransferException {
        TransferReceipt receipt = TransferReceipt.get(tranReceiptId)

        if (!receipt) {
            throw new ModelNotFoundException('Transfer doesn\'t exists...')
        }
        if (receipt.status == TransferStatus.CANCELED) {
            String s = 'Transfer is already canceled...'
            throw new TransactionException(s)
        }
        if (receipt.status != TransferStatus.PENDING) {
            String s = 'Transfer is not in canceling state...'
            throw new TransactionException(s)
        }

        def entitys = datastore.execute {
            from Transfer.class.simpleName
            and receiptId == receipt.id
        }

        List<Transfer> trans = []
        entitys.each { entity ->
            Transfer model = entity as Transfer
            model.account = Account.get(model.accountId)
            model.account.product = Product.get(model.account.productId)
            trans << model
        }
        receipt.trans = trans;

        Transfer frTran = receipt.trans[0]
        Account frAccount = frTran.account

        frTran.status = TransferStatus.CANCELED
        frTran.preUpdate(sessionUser.id)
        frTran.save()

        frAccount.withdrawHandStockMove(frTran.unit)
        frAccount.depositHandStock(frTran.unit)
        frAccount.computeAvailableStock()
        frAccount.preUpdate(sessionUser.id)
        frAccount.save()

        frAccount.product.preUpdate(sessionUser.id)
        frAccount.product.save()

        Transfer toTran = receipt.trans[1]
//        Account toAccount = toTran.account

        toTran.status = TransferStatus.CANCELED
        toTran.preUpdate(sessionUser.id)
        toTran.save()

        receipt.status = TransferStatus.CANCELED
        receipt.preUpdate(sessionUser.id)
        receipt.save()

        return receipt
    }

    @Override
    void create(SessionDto sessionUser, TransferReceipt receipt) throws TransactionException {
        Date now = new Date()
        receipt.id = autoNumberService.getNextNumber(sessionUser, TransferReceipt.ID_KEY)
        receipt.date = now
        receipt.byUserId = sessionUser.id
        receipt.branchId = sessionUser.branchId

        // from transaction
        Transfer tran = receipt.trans[0]
        tran.receiptId = receipt.id
        tran.category = receipt.category
        tran.date = receipt.date
        tran.status = receipt.status

        Account account = tran.account
        if(!account) {
            account = Account.get(tran.accountId)
            tran.account = account
        }
        Product product = Product.get(account.productId)
        account.product = product
        //tran.product = product

        tran.productCode = product.code
        tran.productId = product.id

        account.withdrawHandStock(tran.unit)
        account.depositHandStockMove(tran.unit)

        if (!tran.byUserId) {
            tran.byUserId = sessionUser.id
        }
        tran.branchId = sessionUser.branchId

        tran.id = autoNumberService.getNextNumber(sessionUser, Transfer.ID_KEY)

        tran.prePersist(sessionUser.id)
        tran.save()

        account.computeAvailableStock()
        account.preUpdate(sessionUser.id)
        account.save()

        product.preUpdate(sessionUser.id)
        product.save()

        // to transaction
        tran = receipt.trans[1]
        tran.receiptId = receipt.id
        tran.category = receipt.category
        tran.date = receipt.date
        tran.status = receipt.status

        account = tran.account
        if(!account) {
            account = Account.get(tran.accountId)
            tran.account = account
        }
        product = Product.get(account.productId)
        account.product = product
        //tran.product = product

        tran.productCode = product.code
        tran.productId = product.id

        //account.withdrawHandStock(tran.unit)
        //account.depositHandStockMove(tran.unit)
        //tran.averageRate = product.handStockAverage

        tran.byUserId = account.userId
        tran.branchId = sessionUser.branchId

        tran.id = autoNumberService.getNextNumber(sessionUser, Transfer.ID_KEY)

        tran.prePersist(sessionUser.id)
        tran.save()

        account.computeAvailableStock()
        account.preUpdate(sessionUser.id)
        account.save()

        product.preUpdate(sessionUser.id)
        product.save()

        receipt.prePersist(sessionUser.id)
        receipt.save()
    }
}
