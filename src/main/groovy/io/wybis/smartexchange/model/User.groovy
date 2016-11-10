package io.wybis.smartexchange.model;

import groovy.transform.Canonical
import groovy.transform.ToString
import groovyx.gaelyk.datastore.Entity
import groovyx.gaelyk.datastore.Ignore
import groovyx.gaelyk.datastore.Unindexed

@Entity(unindexed = false)
@Canonical
@ToString(includeNames = true)
public class User extends AbstractModel {

    static final String ID_KEY = "userId"

    String userId

    @Unindexed
    String password

    @Ignore
    String retypePassword

    //String identificationNumber

    String emailId

    String firstName

    String lastName

    String handPhoneNumber

    String landPhoneNumber

    long addressId

    @Ignore
    Address address

    String token

    String type

    String status

    String roleId

    @Ignore
    Role role

    long branchId

    @Ignore
    Branch branch

    long cashAccountId

    @Ignore
    Account cashAccount

    long profitAccountId

    @Ignore
    Account profitAccount

    @Ignore
    List<Long> accountIds

    @Ignore
    List<Account> accounts

    // persistance operations

    void preUpdate(long updateBy) {
        this.correctData()
        this.updateBy = updateBy
        this.updateTime = new Date()
    }

    void prePersist(long createAndUpdateBy) {
        this.correctData()
        this.createBy = createAndUpdateBy
        this.updateBy = createAndUpdateBy
        Date now = new Date()
        this.createTime = now;
        this.updateTime = now;
    }

    // domain operations
    void correctData() {
        if (userId) {
            userId = userId.toLowerCase()
        }
        if (emailId) {
            emailId = emailId.toLowerCase()
        }
        if (firstName) {
            firstName = firstName.toLowerCase()
        }
        if (lastName) {
            lastName = lastName.toLowerCase()
        }
    }

    boolean isVirtual() {
        return userId == null ? false : userId.startsWith("${this.branchId}@")
    }
}