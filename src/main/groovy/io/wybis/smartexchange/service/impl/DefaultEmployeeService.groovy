package io.wybis.smartexchange.service.impl

import groovyx.gaelyk.GaelykBindings
import groovyx.gaelyk.logging.GroovyLogger
import io.wybis.smartexchange.dto.SessionDto
import io.wybis.smartexchange.model.Branch
import io.wybis.smartexchange.model.Role
import io.wybis.smartexchange.model.User
import io.wybis.smartexchange.model.constants.UserType
import io.wybis.smartexchange.service.AccountService
import io.wybis.smartexchange.service.EmployeeService
import io.wybis.smartexchange.service.exceptions.ModelAlreadyExistException

@GaelykBindings
class DefaultEmployeeService extends DefaultUserService implements EmployeeService {

    GroovyLogger log = new GroovyLogger(DefaultEmployeeService.class.getName())

    AccountService accountService

    @Override
    public User create(SessionDto sessionUser, User model)
            throws ModelAlreadyExistException {

        model.type = UserType.EMPLOYEE
        //model.branchId = sessionUser.branchId

        model = super.create(sessionUser, model)

        accountService.onEmployeeCreate(sessionUser, model)

        return model;
    }

    @Override
    public void onBranchCreate(SessionDto sessionUser, Branch branch) {
        User model = new User()

        model.userId = branch.id + '@' + branch.code
        model.with {
            password = '123'
            firstName = branch.name
            lastName = branch.id as String
            roleId = Role.ID_MANAGER
            branchId = branch.id
        }

        model = this.create(sessionUser, model)

        branch.virtualEmployeeId = model.id

        model.prePersist(sessionUser.id)
        branch.save();
    }
}
