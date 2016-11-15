package io.wybis.smartexchange.model

import groovy.transform.Canonical
import groovy.transform.ToString
import groovyx.gaelyk.datastore.Entity
import groovyx.gaelyk.datastore.Ignore

@Entity(unindexed = false)
@Canonical
@ToString(includeNames = true)
public class Tran extends AbstractModel {

    static final String ID_KEY = "tranId"

    long receiptId

    @Ignore
    TranReceipt tranReceipt

    String category

    String productCode

    long productId

    @Ignore
    Product product

    long accountId

    @Ignore
    Account account

    String type

    long baseUnit

    double unit

    double balanceUnit

    double rate

    double averageRate

    @Ignore
    double amount

    @Ignore
    double balanceAmount

    double profitRate

    @Ignore
    double profitAmount

    Date date

    String status

    long profitTranId

    @Ignore
    Tran profitTran

    long orderId

    @Ignore
    Order order

    long forUserId

    @Ignore
    User forUser

    long byUserId

    @Ignore
    User byUser

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
        this.createTime = now;
        this.updateTime = now;
    }

    // domain operations

    void computeAmount() {
        if (this.rate == this.baseUnit) {
            this.amount = this.unit;
        } else {
            double value = (this.rate / this.baseUnit);
            value = this.unit * value;
            this.amount = value;
        }
    }
}
