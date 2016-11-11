package io.wybis.smartexchange.model

import groovy.transform.Canonical
import groovy.transform.ToString
import groovyx.gaelyk.datastore.Entity
import groovyx.gaelyk.datastore.Ignore

@Entity(unindexed = false)
@Canonical
@ToString(includeNames = true)
public class TranReceipt extends AbstractModel {

    static final String ID_KEY = "tranReceiptId"

    String category

    Date date

    @Ignore
    double totalAmount

    @Ignore
    double totalSaleAmount

    double amount

    @Ignore
    double balanceAmount

    String description

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

    @Ignore
    List<Tran> trans

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
}
