package io.wybis.smartexchange.web.session

import io.wybis.smartexchange.dto.ResponseDto
import io.wybis.smartexchange.dto.SessionDto
import io.wybis.smartexchange.model.Account
import io.wybis.smartexchange.model.Branch
import io.wybis.smartexchange.model.Product
import io.wybis.smartexchange.model.User
import io.wybis.smartexchange.model.constants.UserType
import io.wybis.smartexchange.service.SessionService
import io.wybis.smartexchange.util.Helper

ResponseDto responseDto = request.responseDto

def props = sessionService.properties(session)
props.domainPrefix = Helper.getDomainPrefix(request, app)

if (users.userLoggedIn) {
    props.adminLogoutUrl = users.createLogoutURL('/console.html')
} else {
    props.adminLoginUrl = users.createLoginURL('/console.html')
}

def message = session.message
if (message) {
    props.message = message
    session.removeAttribute('message')
}


if (responseDto) {
    responseDto.data = props
} else {
    responseDto = new ResponseDto(data: props)
}

SessionDto sessionDto = session[SessionService.SESSION_USER_KEY]

if (sessionDto) {
    Branch branch = Branch.get(sessionDto.branchId)
    def entitys = datastore.execute {
        from Account.class.getSimpleName()
        where branchId == branch.id
        and userId == sessionDto.id
    }
    def models = []
    entitys.each { entity ->
        Account model = entity as Account
        models << model
    }
    branch.accounts = models

    entitys = datastore.execute {
        from Product.class.getSimpleName()
        where branchId == branch.id
    }
    models = []
    entitys.each { entity ->
        Product model = entity as Product
        models << model
    }
    branch.products = models

    entitys = datastore.execute {
        from User.class.getSimpleName()
        where branchId == branch.id
        and type == UserType.EMPLOYEE
    }
    models = []
    entitys.each { entity ->
        User model = entity as User
        models << model
    }
    branch.employees = models

    entitys = datastore.execute {
        from User.class.getSimpleName()
        where branchId == branch.id
        and type == UserType.CUSTOMER
    }
    models = []
    entitys.each { entity ->
        User model = entity as User
        models << model
    }
    branch.customers = models

    responseDto.model = branch;
}

jsonCategory.respondWithJson(response, responseDto)

