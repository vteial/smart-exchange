package io.wybis.smartexchange.service.impl

import groovyx.gaelyk.GaelykBindings
import groovyx.gaelyk.logging.GroovyLogger
import io.wybis.smartexchange.dto.SessionDto
import io.wybis.smartexchange.model.Branch
import io.wybis.smartexchange.model.constants.BranchStatus
import io.wybis.smartexchange.service.AccountService
import io.wybis.smartexchange.service.BranchService
import io.wybis.smartexchange.service.CustomerService
import io.wybis.smartexchange.service.EmployeeService
import io.wybis.smartexchange.service.ProductService
import io.wybis.smartexchange.service.exceptions.ModelAlreadyExistException

@GaelykBindings
class DefaultBranchService extends AbstractService implements BranchService {

    GroovyLogger log = new GroovyLogger(DefaultBranchService.class.getName())

    AccountService accountService

    ProductService productService

    EmployeeService employeeService

    CustomerService customerService

    @Override
    public void create(SessionDto sessionUser, Branch model)
            throws ModelAlreadyExistException {

        model.id = autoNumberService.getNextNumber(sessionUser, Branch.ID_KEY)
        model.status = BranchStatus.ACTIVE

        model.prePersist(sessionUser.id)
        model.save()

        productService.onBranchCreate(sessionUser, model)
        employeeService.onBranchCreate(sessionUser, model)
        customerService.onBranchCreate(sessionUser, model)
    }
}
