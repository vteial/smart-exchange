package io.wybis.smartexchange.dto

import groovy.transform.Canonical
import groovy.transform.ToString

@Canonical
@ToString(includeNames = true)
class SessionDto implements Serializable {

    long id

    String userId

    String emailId

    String firstName

    String lastName

    String type

    String roleId

    long cashAccountId

    long profitAccountId

    long branchId

    String branchCode

    String branchName

    long branchVirtualEmployeeId

}
