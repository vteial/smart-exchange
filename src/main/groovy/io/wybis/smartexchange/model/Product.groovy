package io.wybis.smartexchange.model

import groovy.transform.Canonical
import groovy.transform.ToString
import groovyx.gaelyk.datastore.Entity
import groovyx.gaelyk.datastore.Ignore

@Entity(unindexed = false)
@Canonical
@ToString(includeNames = true)
class Product extends AbstractModel {

    static final String ID_KEY = "productId"

    String type

    String code

    String name

    int baseUnit

    int denominator

    double buyRate

    double buyPercent

    double sellRate

    double sellPercent

    double amount;

    double handStock

    double handStockAverage

    @Ignore
    double handStockValue

    double virtualStockBuy

    double virtualStockAverage

    double virtualStockSell

    double availableStock

    double availableStockAverage

    String status

    long branchId

    @Ignore
    Branch branch

    // persistance operations

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

    double getBuyPercentageRate() {
        double value = this.buyRate * (this.buyPercent / 100)
        value = this.buyRate + value
        return value
    }

    double getSellPercentageRate() {
        double value = this.sellRate * (this.sellPercent / 100)
        value = this.sellRate - value
        return value
    }

    boolean isRateIsAllowableForBuy(double rate) {
        double bpr = this.getBuyPercentageRate()
        return rate <= bpr
    }

    boolean isRateIsAllowableForSell(double rate) {
        double spr = this.getSellPercentageRate()
        return rate >= spr
    }

    //	boolean hasSufficientAmount(double amount) {
    //		return amount <= this.amount
    //	}
    //
    //	void withdrawAmount(double amount) {
    //		this.amount -= amount
    //	}
    //
    //	void depositAmount(double amount) {
    //		this.amount += amount
    //	}

    boolean hasSufficientHandStock(double unit) {
        return unit <= this.handStock
    }

    void withdrawHandStock(double unit) {
        this.handStock -= unit
        this.computeAmount();
    }

    void depositHandStock(double unit) {
        this.handStock += unit
        this.computeAmount();
    }

    void computeAmount() {
        double value = (this.handStockAverage / this.baseUnit);
        value = this.handStock * value;
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
    }

    void depositVirtualStockBuy(double unit) {
        this.virtualStockBuy += unit
    }

    void withdrawVirtualStockSell(double unit) {
        this.virtualStockSell -= unit
    }

    void depositVirtualStockSell(double unit) {
        this.virtualStockSell += unit
    }

    void computeHandStockValue() {
        if (this.handStockAverage > 0) {
            this.handStockValue = this.handStock * (this.handStockAverage / this.baseUnit)
        }
    }

    void computeAvailableStock() {
        this.availableStock = this.getVirtualStock() + this.handStock
    }

    void computeHandStockAverage(double unit, double rate) {
        // println("unit = " + unit)
        // println("rate = " + rate)
        // println("total = " + this.handStock)
        double value1 = this.handStock * (this.handStockAverage / this.baseUnit)
        // println("value1 = " + value1)
        double value2 = unit * (rate / this.baseUnit)
        // println("value2 = " + value2)
        double value3 = value1 + value2
        // println("value3 = " + value3)
        double value4 = this.handStock + unit
        // println("value4 = " + value4)
        double value5 = (value3 / value4) * this.baseUnit
        // println("value5 = " + value5)
        this.handStockAverage = value5
    }

    void revertHandStockAverage(double unit, double rate) {
        double hst = this.handStock - unit
        if (hst > 0) {
            // println("unit = " + unit)
            // println("rate = " + rate)
            // println("total = " + this.handStock)
            double value1 = this.handStock * (this.handStockAverage / this.baseUnit)
            // println("value1 = " + value1)
            double value2 = unit * (rate / this.baseUnit)
            // println("value2 = " + value2)
            double value3 = value1 - value2
            // println("value3 = " + value3)
            double value4 = this.getHandStock - unit
            // println("value4 = " + value4)
            double value5 = (value3 / value4) * this.baseUnit
            // println("value5 = " + value5)
            this.handStockAverage = value5
        }
    }

    void computeVirtualStockAverage(double unit, double rate) {
        // println("unit = " + unit)
        // println("rate = " + rate)
        // println("stockBuy = this.virtualStockBuy)
        double value1 = this.virtualStockBuy * (this.virtualStockAverage / this.baseUnit)
        // println("value1 = " + value1)
        double value2 = unit * (rate / this.baseUnit)
        // println("value2 = " + value3)
        double value3 = value1 + value2
        // println("value3 = " + value3)
        double value4 = this.virtualStockBuy + unit
        // println("value4 = " + value4)
        double value5 = (value3 / value4) * this.baseUnit
        // println("value5 = " + value5)
        this.virtualStockAverage = value5
    }

    void revertVirtualStockAverage(double unit, double rate) {
        double vsb = this.virtualStockBuy - unit
        if (vsb != 0) {
            // println("unit = " + unit)
            // println("rate = " + rate)
            // println("stockBuy = this.virtualStockBuy)
            double value1 = this.virtualStockBuy * (this.virtualStockAverage / this.baseUnit)
            // println("value1 = " + value1)
            double value2 = unit * (rate / this.baseUnit)
            // println("value2 = " + value2)
            double value3 = value1 - value2
            // println("value3 = " + value3)
            double value4 = this.virtualStockBuy - unit
            // println("value4 = " + value4)
            double value5 = (value3 / value4) * this.baseUnit
            // println("value5 = " + value5)
            this.virtualStockAverage = value5
        }
    }

    void computeAvailableStockAverage(double rate) {
        double value1 = this.virtualStockBuy * (this.virtualStockAverage / this.baseUnit)
        // println("value1 = " + value1)
        double value2 = this.handStock * (this.handStockAverage / this.baseUnit)
        // println("value2 = " + value2)
        double value3 = value1 + value2
        // println("value3 = " + value3)
        double value4 = this.virtualStockBuy + this.handStock
        // println("value4 = " + value4)
        double value5 = (value3 / value4) * this.baseUnit
        // println("value5 = " + value5)
        this.availableStockAverage = Double.isNaN(value5) ? 0 : value5
    }
}
