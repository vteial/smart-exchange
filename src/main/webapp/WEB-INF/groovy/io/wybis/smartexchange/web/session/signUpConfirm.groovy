package io.wybis.smartexchange.web.session

import io.wybis.smartexchange.dto.MessageDto
import io.wybis.smartexchange.model.User
import io.wybis.smartexchange.model.constants.UserStatus

MessageDto message = new MessageDto()
if (params.userId == null || params.userToken == null) {
    message.redirectPage = 'index'
    message.type = 'error'
    message.text = 'Missing parameters. Please use the correct confirmation link'

} else {

    long userId = params.userId as Long
    User auser = User.get(userId)
    String userToken = params.userToken

    if (auser != null && auser.status == UserStatus.ACTIVE) {

        message.redirectPage = 'sign-in'
        message.type = 'warning'
        message.text = 'Your account is already confirmed. Please sign in to proceed...'

    } else if (auser != null && auser.token == userToken) {

        auser.token = null
        auser.status = UserStatus.ACTIVE
        auser.save()

        message.redirectPage = 'sign-in'
        message.type = 'success'
        message.text = 'Your account has been confirmed. Please sign in to proceed..."'

    } else {

        message.redirectPage = 'index'
        message.type = 'error'
        message.text = 'Invalid id or token. Please use the correct confirmation link'

    }
}
session.message = message

redirect '/index.html'
