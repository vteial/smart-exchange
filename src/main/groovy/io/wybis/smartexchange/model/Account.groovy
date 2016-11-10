package io.wybis.smartexchange.model

import groovy.transform.Canonical
import groovy.transform.ToString
import groovyx.gaelyk.datastore.Entity
import groovyx.gaelyk.datastore.Ignore
import groovyx.gaelyk.datastore.Unindexed

@Entity(unindexed = false)
@Canonical
@ToString(includeNames = true)
class Account extends AbstractModel {

    static final String ID_KEY = "accountId"

    String name;

    String aliasName;

    String type

    @Unindexed
    boolean isMinus;

    double amount;

    double amountVirtualBuy;

    double amountVirtualSell;

    String status;

    double handStock

    double handStockMove

    double virtualStockBuy

    double virtualStockSell

    double availableStock

    long eventId

    long productId

    @Ignore
    Product product

    long userId

    @Ignore
    User user

    long branchId

    @Ignore
    Branch branch

    // persistence operations

    void preUpdate(long updateBy) {
        this.updateBy = updateBy
        this.updateTime = new Date()
    }

    void prePersist(long createAndUpdateBy) {
        this.createBy = createAndUpdateBy
        this.updateBy = createAndUpdateBy
        Date now = new Date()
        this.createTime = now
        this.updateTime = now
    }

    // domain operations

    boolean hasSufficientBalance(double amount) {

        if (this.isMinus) {
            return true
        }

        return this.amount >= amount
    }

    void withdraw(double unit) {
        this.amount -= unit
    }

    void deposit(double unit) {
        this.amount += unit
    }

    boolean hasSufficientHandStock(double unit) {
        return this.isMinus ? true : unit <= this.handStock
    }

    void withdrawHandStock(double unit) {
        this.handStock -= unit
        this.computeAmount();
        this.product.withdrawHandStock(unit)
    }

    void depositHandStock(double unit) {
        this.handStock += unit
        this.computeAmount();
        this.product.depositHandStock(unit)
    }

    boolean hasSufficientHandStockMove(double unit) {
        return this.isMinus ? true : unit <= this.handStockMove
    }

    void withdrawHandStockMove(double unit) {
        this.handStockMove -= unit
        this.computeAmount();
        //this.product.withdrawHandStockMove(unit)
    }

    void depositHandStockMove(double unit) {
        this.handStockMove += unit
        this.computeAmount();
        //this.product.depositHandStockMove(unit)
    }

    void computeAmount() {
        double value = (this.product.handStockAverage / this.product.baseUnit);
        value = (this.handStock + this.handStockMove) * value;
        this.amount = value;
    }

    double getVirtualStock() {
        return this.virtualStockBuy - this.virtualStockSell
    }

    boolean hasSufficientVirtualStockBuy(double unit) {
        return unit <= this.virtualStockBuy
    }

    boolean hasSufficientVirtualStockSell(double unit) {
        return unit <= this.virtualStockSell
    }

    void withdrawVirtualStockBuy(double unit) {
        this.virtualStockBuy -= unit
        this.product.withdrawVirtualStockBuy(unit)
    }

    void depositVirtualStockBuy(double unit) {
        this.virtualStockBuy += unit
        this.product.depositVirtualStockBuy(unit)
    }

    void withdrawVirtualStockSell(double unit) {
        this.virtualStockSell -= unit
        this.product.withdrawVirtualStockSell(unit)
    }

    void depositVirtualStockSell(double unit) {
        this.virtualStockSell += unit
        this.product.depositVirtualStockSell(unit)
    }

    void computeAvailableStock() {
        this.availableStock = this.getVirtualStock() + (this.handStock + this.handStockMove)
        this.product.computeAvailableStock()
    }
}
