package io.wybis.smartexchange.service.impl

import groovyx.gaelyk.GaelykBindings
import groovyx.gaelyk.logging.GroovyLogger
import io.wybis.smartexchange.dto.SessionDto
import io.wybis.smartexchange.model.*
import io.wybis.smartexchange.model.constants.OrderStatus
import io.wybis.smartexchange.model.constants.OrderType
import io.wybis.smartexchange.model.constants.TransactionType
import io.wybis.smartexchange.service.AccountService
import io.wybis.smartexchange.service.OrderService
import io.wybis.smartexchange.service.exceptions.OrderException

@GaelykBindings
class DefaultOrderService extends AbstractService implements OrderService {

    GroovyLogger log = new GroovyLogger(DefaultOrderService.class.getName())

    AccountService accountService

    @Override
    public OrderReceipt findByOrderReceiptId(long orderReceiptId) {
        OrderReceipt receipt = OrderReceipt.get(orderReceiptId)

        def entitys = datastore.execute {
            from Order.class.simpleName
            and receiptId == orderReceiptId
        }

        List<Order> orders = []
        entitys.each { entity ->
            Order model = entity as Order
            model.computeAmount()
            orders << model
        }
        receipt.orders = orders;

        return receipt;
    }

    @Override
    public void add(SessionDto sessionUser, OrderReceipt receipt) throws OrderException {

        Date now = new Date()
        receipt.id = autoNumberService.getNextNumber(sessionUser, OrderReceipt.ID_KEY)
        receipt.date = now
        receipt.status = OrderStatus.PENDING
        receipt.byUserId = sessionUser.branchVirtualEmployeeId
        receipt.branchId = sessionUser.branchId

        List<Order> orders = receipt.orders
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i)

            order.receiptId = receipt.id
            order.date = receipt.date
            order.forUserId = receipt.forUserId
            order.byUserId = receipt.byUserId
            order.branchId = receipt.branchId

            this.addOrder(sessionUser, order)
        }

        receipt.prePersist(sessionUser.id)
        receipt.save()
    }

    private void addOrder(SessionDto sessionUser, Order order) throws OrderException {

        Account account = Account.get(order.accountId)
        order.account = account

        Product product = Product.get(account.productId)
        account.product = product

        //order.product = product
        order.productCode = product.code
        order.productId = product.id
        order.baseUnit = product.baseUnit
        order.computeAmount()

        account = accountService.findByUserIdAndProductId(order.byUserId, product.id)
        account.product = product
        order.accountId = account.id
        order.account = account

        if (order.type == OrderType.BUY) {
            product.computeVirtualStockAverage(order.unit, order.rate)

            account.depositVirtualStockBuy(order.unit)
        } else {
            account.depositVirtualStockSell(order.unit)
        }
        order.averageRate = product.virtualStockAverage
        order.status = OrderStatus.PENDING

        order.id = autoNumberService.getNextNumber(sessionUser, Order.ID_KEY)

        order.prePersist(sessionUser.id)
        order.save()

        account.computeAvailableStock();
        account.preUpdate(sessionUser.id)
        account.save()

        product.computeAvailableStockAverage(order.rate)
        product.preUpdate(sessionUser.id)
        product.save()
    }

    @Override
    public void onTransaction(SessionDto sessionUser, Tran tran) {

        Account account = tran.account
        if (tran.type == TransactionType.BUY) {
            account.withdrawVirtualStockBuy(tran.unit)
        } else {
            account.withdrawVirtualStockSell(tran.unit)
        }

        Order order = tran.order
        if (order.unit == tran.unit) {
            order.status = OrderStatus.COMPLETE
        } else {
            order.unit -= tran.unit
            order.computeAmount()
        }

        order.preUpdate(sessionUser.id)
        order.save()

        def keys = datastore.execute {
            select keys from Order.class.getSimpleName()
            where receiptId == order.receiptId
            and status == OrderStatus.PENDING
        }
        if (keys.size() == 0) {
            OrderReceipt receipt = OrderReceipt.get(order.receiptId)

            receipt.status = OrderStatus.COMPLETE

            receipt.preUpdate(sessionUser.id)
            receipt.save()
        }
    }
}