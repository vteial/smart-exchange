package io.wybis.smartexchange.dto

import groovy.transform.Canonical

@Canonical
class ResponseDto {

    static final int UNKNOWN = -2

    static final int FORBIDDEN = -1

    static final int SUCCESS = 0

    static final int ERROR = 1

    static final int WARNING = 2

    int type

    String message;

    Object data

    Object model
}
