package io.wybis.smartexchange.model

import groovy.transform.Canonical
import groovy.transform.ToString
import groovyx.gaelyk.datastore.Entity

@Entity(unindexed = false)
@Canonical
@ToString(includeNames = true)
public class Country extends AbstractModel {

    String twoLetterCode

    String threeLetterCode

    int numericCode

    String name

    String toString() {
        StringBuilder sb = new StringBuilder(Country.class.getSimpleName())
        sb.append('[')

        sb.append("id:${this.id}, ")

        sb.append(']')
        return sb.toString()
    }

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
}
