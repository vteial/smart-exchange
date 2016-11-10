package io.wybis.smartexchange.model;

import groovyx.gaelyk.datastore.Ignore
import groovyx.gaelyk.datastore.Key


public abstract class AbstractModel implements Model {

    private static final long serialVersionUID = 1L;

    @Key
    long id

    long createBy

    long updateBy

    Date createTime

    Date updateTime

    @Ignore
    String errorMessage
}
