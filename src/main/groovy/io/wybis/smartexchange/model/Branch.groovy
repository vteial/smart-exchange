package io.wybis.smartexchange.model;

import groovy.transform.Canonical
import groovy.transform.ToString
import groovyx.gaelyk.datastore.Entity
import groovyx.gaelyk.datastore.Ignore

@Entity(unindexed = false)
@Canonical
@ToString(includeNames = true)
public class Branch extends AbstractModel {

    static final String ID_KEY = "branchId"

    String code

    String name

    String aliasName

    String licenceNumber

    String emailId

    String handPhoneNumber

    String landPhoneNumber

    String faxNumber;

    long addressId

    @Ignore
    Address address

    long virtualEmployeeId

    String status

    long parentId

    @Ignore
    List<Account> accounts;

    @Ignore
    List<Product> products

    @Ignore
    List<User> employees

    @Ignore
    List<User> customers

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