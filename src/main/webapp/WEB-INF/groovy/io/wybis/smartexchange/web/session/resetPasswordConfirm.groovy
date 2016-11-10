package io.wybis.smartexchange.web.session

import io.wybis.smartexchange.model.User
import io.wybis.smartexchange.service.SessionService

String path = '/index.html'
if (params.userId == null || params.userToken == null) {

    path += '#/message?errorMessage=Missing parameters. Please use the correct confirmation link.'

} else {

    long userId = params.userId as Long
    User auser = User.get(userId)
    String userToken = params.userToken

    if (auser != null && auser.token == userToken) {

        auser.token = null
        auser.save()
        session[SessionService.SESSION_USER_ID_KEY] = new Long(auser.id)
        session[SessionService.SESSION_USER_PASSWORD_KEY] = auser.password

        path += '#/reset-password';

    } else {

        path += '#/message?errorMessage=Invalid id or token. Please use the correct reset link.';

    }
}

redirect path
