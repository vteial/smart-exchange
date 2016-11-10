package io.wybis.smartexchange.service.impl

import groovyx.gaelyk.GaelykBindings
import groovyx.gaelyk.logging.GroovyLogger
import io.wybis.smartexchange.dto.SessionDto
import io.wybis.smartexchange.model.Branch
import io.wybis.smartexchange.model.Role
import io.wybis.smartexchange.model.User
import io.wybis.smartexchange.model.constants.UserType
import io.wybis.smartexchange.service.AccountService
import io.wybis.smartexchange.service.CustomerService
import io.wybis.smartexchange.service.exceptions.ModelAlreadyExistException

@GaelykBindings
class DefaultCustomerService extends DefaultUserService implements CustomerService {

    GroovyLogger log = new GroovyLogger(DefaultCustomerService.class.getName())

    AccountService accountService

    @Override
    public User create(SessionDto sessionUser, User model)
            throws ModelAlreadyExistException {

        if (!model.userId) {
            Branch branch = Branch.get(model.branchId)
            model.branch = branch
            model.userId = "${model.firstName}-${model.lastName}@${branch.code}"
            model.userId = model.userId.toLowerCase()
        }
        model.type = UserType.CUSTOMER
        model.roleId = Role.ID_CUSTOMER
        //model.branchId = sessionUser.branchId

        model = super.create(sessionUser, model)

        accountService.onCustomerCreate(sessionUser, model)

        return model
    }

    @Override
    public void onBranchCreate(SessionDto sessionUser, Branch branch) {

        User model = new User()
        model.with {
            firstName = 'Guest'
            lastName = 'Customer'
            branchId = branch.id
        }

        model = this.create(sessionUser, model)
    }
}
