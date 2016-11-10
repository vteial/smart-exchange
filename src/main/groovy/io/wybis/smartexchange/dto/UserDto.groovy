package io.wybis.smartexchange.dto

import groovy.transform.Canonical
import groovy.transform.ToString

@Canonical
@ToString(includeNames = true)
class UserDto implements Serializable {

    long id

    String recaptchaValue

    String userId

    String password

    String retypePassword

    String emailId

    String firstName

    String lastName

    String currentPassword

    String newPassword

    String retypeNewPassword

}
