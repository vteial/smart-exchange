package io.wybis.smartexchange.model

import groovy.transform.Canonical
import groovy.transform.ToString
import groovyx.gaelyk.datastore.Entity
import groovyx.gaelyk.datastore.Ignore

@Entity(unindexed = false)
@Canonical
@ToString(includeNames = true)
public class OrderReceipt extends AbstractModel {

    static final String ID_KEY = "orderReceiptId"

    String category

    Date date

    @Ignore
    double totalAmount

    @Ignore
    double totalSaleAmount

    String description

    String status

//	long transferId
//
//	@Ignore
//	Transfer transfer

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
    List<Order> orders

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
