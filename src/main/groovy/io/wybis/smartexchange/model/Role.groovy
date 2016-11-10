package io.wybis.smartexchange.model

import groovy.transform.Canonical
import groovy.transform.ToString
import groovyx.gaelyk.datastore.Entity

@Entity(unindexed = false)
@Canonical
@ToString(includeNames = true)
public class Role extends AbstractModel {

    static final String ID_MANAGER = 'manager'

    static final String ID_EMPLOYEE = 'employee'

    static final String ID_CUSTOMER = 'customer'

    static final List<String> ROLES = [
            ID_MANAGER,
            ID_EMPLOYEE,
            ID_CUSTOMER
    ]

    String name

    // persistance operations

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
