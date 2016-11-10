package io.wybis.smartexchange.dto

import groovy.transform.Canonical
import groovy.transform.ToString

@Canonical
@ToString(includeNames = true)
class MessageDto implements Serializable {

    String type

    String text

    String redirectPage

}
