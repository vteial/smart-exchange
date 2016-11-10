package io.wybis.smartexchange.model

import groovyx.gaelyk.datastore.Ignore

public abstract class AbstractModelOne implements Model {

    private static final long serialVersionUID = 1L;

    long createBy

    long updateBy

    Date createTime

    Date updateTime

    @Ignore
    String errorMessage
}
