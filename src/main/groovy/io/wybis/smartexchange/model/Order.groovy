package io.wybis.smartexchange.model

import groovy.transform.Canonical
import groovy.transform.ToString
import groovyx.gaelyk.datastore.Entity
import groovyx.gaelyk.datastore.Ignore

@Entity(unindexed = false)
@Canonical
@ToString(includeNames = true)
public class Order extends AbstractModel {

    static final String ID_KEY = "orderId"

    long receiptId

    @Ignore
    OrderReceipt orderReceipt

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

    double rate

    double averageRate

    double amount

    Date date

    String status

    long forUserId

    @Ignore
    User forUser

    long byUserId

    @Ignore
    User byUser

    long branchId

    @Ignore
    Branch branch

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
        double value = (this.rate / this.baseUnit);
        value = this.unit * value;
        this.amount = value;
    }
}
