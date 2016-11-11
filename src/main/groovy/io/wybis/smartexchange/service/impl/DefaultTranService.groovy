package io.wybis.smartexchange.service.impl

import groovyx.gaelyk.GaelykBindings
import groovyx.gaelyk.logging.GroovyLogger
import io.wybis.smartexchange.dto.SessionDto
import io.wybis.smartexchange.model.*
import io.wybis.smartexchange.model.constants.AccountType
import io.wybis.smartexchange.model.constants.OrderStatus
import io.wybis.smartexchange.model.constants.TransactionStatus
import io.wybis.smartexchange.model.constants.TransactionType
import io.wybis.smartexchange.service.OrderService
import io.wybis.smartexchange.service.TranService
import io.wybis.smartexchange.service.exceptions.ModelNotFoundException
import io.wybis.smartexchange.service.exceptions.TransactionException

@GaelykBindings
class DefaultTranService extends AbstractService implements TranService {

    GroovyLogger log = new GroovyLogger(DefaultTranService.class.getName())

    OrderService orderService;

    @Override
    TranReceipt findByTranReceiptId(long tranReceiptId) {
        TranReceipt receipt = TranReceipt.get(tranReceiptId)

        def entitys = datastore.execute {
            from Tran.class.simpleName
            where receiptId == tranReceiptId
        }

        List<Tran> trans = []
        entitys.each { entity ->
            Tran model = entity as Tran
            model.computeAmount()
            trans << model
        }
        receipt.trans = trans;

        return receipt;
    }

    @Override
    TranReceipt accept(SessionDto sessionUser, long tranReceiptId) throws ModelNotFoundException, TransactionException {
        TranReceipt receipt = TranReceipt.get(tranReceiptId)

        if (!receipt) {
            throw new ModelNotFoundException('Transaction doesn\'t exists...')
        }
        if (receipt.status == TransactionStatus.DELETED) {
            String s = 'Transaction is already accepted...'
            throw new TransactionException(s)
        }
        if (receipt.status != TransactionStatus.PENDING) {
            String s = 'Transaction is not in acceptable state...'
            throw new TransactionException(s)
        }

        def entitys = datastore.execute {
            from Tran.class.simpleName
            where receiptId == receipt.id
        }

        List<Tran> trans = []
        entitys.each { entity ->
            Tran model = entity as Tran
            model.account = Account.get(model.accountId)
            model.account.product = Product.get(model.account.productId)
            trans << model
        }
        receipt.trans = trans;

        Tran frTran = receipt.trans[0]
        Account frAccount = frTran.account

        frTran.status = TransactionStatus.COMPLETE
        frTran.preUpdate(sessionUser.id)
        frTran.save()

        frAccount.withdrawHandStockMove(frTran.unit)
        frAccount.computeAvailableStock()
        frAccount.preUpdate(sessionUser.id)
        frAccount.save()

        frAccount.product.computeAvailableStockAverage(frTran.rate)
        frAccount.product.preUpdate(sessionUser.id)
        frAccount.product.save()

        Tran toTran = receipt.trans[1]
        Account toAccount = toTran.account

        toAccount.depositHandStock(toTran.unit)
        toAccount.computeAvailableStock()
        toAccount.preUpdate(sessionUser.id)
        toAccount.save()

        toAccount.product.computeAvailableStockAverage(toTran.rate)
        toAccount.product.preUpdate(sessionUser.id)
        toAccount.product.save()

        toTran.status = TransactionStatus.COMPLETE
        toTran.preUpdate(sessionUser.id)
        toTran.save()

        receipt.status = TransactionStatus.COMPLETE
        receipt.preUpdate(sessionUser.id)
        receipt.save()

        return receipt
    }

    @Override
    TranReceipt cancel(SessionDto sessionUser, long tranReceiptId) throws ModelNotFoundException, TransactionException {
        TranReceipt receipt = TranReceipt.get(tranReceiptId)

        if (!receipt) {
            throw new ModelNotFoundException('Transaction doesn\'t exists...')
        }
        if (receipt.status == TransactionStatus.DELETED) {
            String s = 'Transaction is already canceled...'
            throw new TransactionException(s)
        }
        if (receipt.status != TransactionStatus.PENDING) {
            String s = 'Transaction is not in canceling state...'
            throw new TransactionException(s)
        }

        def entitys = datastore.execute {
            from Tran.class.simpleName
            and receiptId == receipt.id
        }

        List<Tran> trans = []
        entitys.each { entity ->
            Tran model = entity as Tran
            model.account = Account.get(model.accountId)
            model.account.product = Product.get(model.account.productId)
            trans << model
        }
        receipt.trans = trans;

        Tran frTran = receipt.trans[0], toTran = receipt.trans[1]
        Account frAccount = frTran.account, toAccount = toTran.account

        frTran.status = TransactionStatus.DELETED
        frTran.preUpdate(sessionUser.id)
        frTran.save()

        frAccount.withdrawHandStockMove(frTran.unit)
        frAccount.depositHandStock(frTran.unit)
        frAccount.computeAvailableStock()
        frAccount.preUpdate(sessionUser.id)
        frAccount.save()

        frAccount.product.computeAvailableStockAverage(frTran.rate)
        frAccount.product.preUpdate(sessionUser.id)
        frAccount.product.save()

        toTran.status = TransactionStatus.DELETED
        toTran.preUpdate(sessionUser.id)
        toTran.save()

        receipt.status = TransactionStatus.DELETED
        receipt.preUpdate(sessionUser.id)
        receipt.save()

        return receipt
    }

    @Override
    void create(SessionDto sessionUser, TranReceipt receipt) throws TransactionException {
        if (receipt.status == TransactionStatus.PENDING) {
            this.pending(sessionUser, receipt)
        } else {
            this.complete(sessionUser, receipt)
        }
    }

    private void pending(SessionDto sessionUser, TranReceipt receipt) throws TransactionException {
        Date now = new Date()
        receipt.id = autoNumberService.getNextNumber(sessionUser, TranReceipt.ID_KEY)
        receipt.date = now
        receipt.byUserId = sessionUser.id
        receipt.branchId = sessionUser.branchId

        // from transaction
        Tran tran = receipt.trans[0]
        tran.receiptId = receipt.id
        tran.category = receipt.category
        tran.date = receipt.date
        tran.forUserId = receipt.forUserId
        tran.status = receipt.status

        Account account = tran.account
        if (!account) {
            account = Account.get(tran.accountId)
            tran.account = account
        }
        Product product = Product.get(account.productId)
        account.product = product
        //tran.product = product

        tran.productCode = product.code
        tran.productId = product.id
        tran.baseUnit = product.baseUnit
        tran.computeAmount()

        account.withdrawHandStock(tran.unit)
        account.depositHandStockMove(tran.unit)
        tran.averageRate = product.handStockAverage

        if (!tran.byUserId) {
            tran.byUserId = sessionUser.id
        }
        tran.branchId = sessionUser.branchId

        tran.id = autoNumberService.getNextNumber(sessionUser, Tran.ID_KEY)

        tran.prePersist(sessionUser.id)
        tran.save()

        account.computeAvailableStock()
        account.preUpdate(sessionUser.id)
        account.save()

        product.computeAvailableStockAverage(tran.rate)
        product.preUpdate(sessionUser.id)
        product.save()

        // to transaction
        tran = receipt.trans[1]
        tran.receiptId = receipt.id
        tran.category = receipt.category
        tran.date = receipt.date
        tran.forUserId = receipt.forUserId
        tran.status = receipt.status

        account = tran.account
        if (!account) {
            account = Account.get(tran.accountId)
            tran.account = account
        }
        product = Product.get(account.productId)
        account.product = product
        //tran.product = product

        tran.productCode = product.code
        tran.productId = product.id
        tran.baseUnit = product.baseUnit
        tran.computeAmount()

        //account.withdrawHandStock(tran.unit)
        //account.depositHandStockMove(tran.unit)
        //tran.averageRate = product.handStockAverage

        if (!tran.byUserId) {
            tran.byUserId = sessionUser.id
        }
        tran.branchId = sessionUser.branchId

        tran.id = autoNumberService.getNextNumber(sessionUser, Tran.ID_KEY)

        tran.prePersist(sessionUser.id)
        tran.save()

        account.computeAvailableStock()
        account.preUpdate(sessionUser.id)
        account.save()

        product.computeAvailableStockAverage(tran.rate)
        product.preUpdate(sessionUser.id)
        product.save()

        receipt.prePersist(sessionUser.id)
        receipt.save()
    }

    private void complete(SessionDto sessionUser, TranReceipt receipt) throws TransactionException {
        Date now = new Date()
        receipt.id = autoNumberService.getNextNumber(sessionUser, TranReceipt.ID_KEY)
        receipt.date = now
        if (!receipt.status) {
            receipt.status = TransactionStatus.COMPLETE
        }
        receipt.byUserId = sessionUser.id
        receipt.branchId = sessionUser.branchId

        List<Tran> trans = receipt.trans
        for (int i = 0; i < trans.size(); i++) {
            Tran tran = trans.get(i)
            tran.receiptId = receipt.id
            tran.category = receipt.category
            tran.date = receipt.date
            tran.forUserId = receipt.forUserId
            if (receipt.status) {
                tran.status = receipt.status
            }

            try {
                this.createTransaction(sessionUser, tran)
            }
            catch (TransactionException e) {
                receipt.errorMessage = tran.errorMessage
                throw e
            }
        }

        receipt.prePersist(sessionUser.id)
        receipt.save()
    }

    private void createTransaction(SessionDto sessionUser, Tran tran) throws TransactionException {

        if (tran.orderId > 0) {
            Order order = Order.get(tran.orderId)
            if (order.status == OrderStatus.COMPLETE) {
                String s = "Order $tran.orderId has already sold out"
                throw new TransactionException(s);
            }
            tran.order = order
        }

        Account account = tran.account
        if (account == null) {
            account = Account.get(tran.accountId)
            tran.account = account
        }

        if (tran.type == TransactionType.SELL && !account.hasSufficientHandStock(tran.unit)) {
            tran.errorMessage = 'Insufficient fund or stock...'
            throw new TransactionException();
        }

        Product product = Product.get(account.productId)
        account.product = product
        //tran.product = product

        tran.productCode = product.code
        tran.productId = product.id
        tran.baseUnit = product.baseUnit
        tran.computeAmount()
        if (tran.orderId > 0) {
            tran.order.baseUnit = product.baseUnit
            //tran.order.computeAmount()
        }

        if (tran.type == TransactionType.BUY) {
            product.computeHandStockAverage(tran.unit, tran.rate)

            account.depositHandStock(tran.unit)
        } else {

            if (tran.account.type == AccountType.PRODUCT) {
                Tran ptran = this.crateTransactionForProfit(sessionUser, tran)
                tran.profitTranId = ptran.id
                //tran.profitTran = tran
            }

            account.withdrawHandStock(tran.unit)
        }
        tran.averageRate = product.handStockAverage
        if (!tran.status) {
            tran.status = TransactionStatus.COMPLETE
        }

        tran.byUserId = sessionUser.id
        tran.branchId = sessionUser.branchId

        tran.id = autoNumberService.getNextNumber(sessionUser, Tran.ID_KEY)

        tran.prePersist(sessionUser.id)
        tran.save()

        if (tran.orderId > 0) {
            orderService.onTransaction(sessionUser, tran)
        }

        account.computeAvailableStock();
        account.preUpdate(sessionUser.id)
        account.save()

        product.computeAvailableStockAverage(tran.rate)
        product.preUpdate(sessionUser.id)
        product.save()
    }

    private Tran crateTransactionForProfit(SessionDto sessionUser, Tran tran) {

        Account account = Account.get(sessionUser.profitAccountId)
        Product product = Product.get(account.productId)
        account.product = product

        Tran ptran = new Tran()
        ptran.with {
            receiptId = tran.receiptId
            category = tran.category
            productCode = product.code
            accountId = account.id
            account = account
            type = TransactionType.BUY
            baseUnit = product.baseUnit
            rate = product.buyRate
            averageRate = product.handStockAverage
            date = tran.date
            status = TransactionStatus.COMPLETE
            forUserId = tran.forUserId
        }

        double avgAmount = tran.unit * (tran.account.product.handStockAverage / tran.account.product.baseUnit)
        ptran.unit = tran.amount - avgAmount
        ptran.computeAmount()
        if (ptran.unit >= 0) {
            ptran.type = TransactionType.SELL
            account.withdrawHandStock(ptran.unit)
        } else {
            ptran.type = TransactionType.BUY
            account.depositHandStock(ptran.unit)
        }

        ptran.byUserId = sessionUser.id
        ptran.branchId = sessionUser.branchId

        ptran.id = autoNumberService.getNextNumber(sessionUser, Tran.ID_KEY)

        ptran.prePersist(sessionUser.id)
        ptran.save()

        //account.computeAvailableStock();
        account.preUpdate(sessionUser.id)
        account.save()

        //product.computeAvailableStockAverage(ptran.rate)
        product.preUpdate(sessionUser.id)
        product.save()

        return ptran
    }
}
