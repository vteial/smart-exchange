package io.wybis.smartexchange.model

import groovy.transform.Canonical
import groovy.transform.ToString
import groovyx.gaelyk.datastore.Entity

@Entity(unindexed = false)
@Canonical
@ToString(includeNames = true)
public class Address extends AbstractModel {

    final String ID_KEY = "addressId"

    String address

    String cityOrTown

    String postalCode

    String stateCode

    String countryCode

    long createBy

    long updateBy

    Date createTime

    Date updateTime

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
}
