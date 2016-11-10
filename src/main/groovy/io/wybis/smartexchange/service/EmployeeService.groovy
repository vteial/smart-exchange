package io.wybis.smartexchange.service

import io.wybis.smartexchange.dto.SessionDto
import io.wybis.smartexchange.model.Branch
import io.wybis.smartexchange.model.User
import io.wybis.smartexchange.service.exceptions.ModelAlreadyExistException;

interface EmployeeService {

    void add(SessionDto sessionUser, User employee) throws ModelAlreadyExistException

    void onBranchCreate(SessionDto sessionUser, Branch branch)
}
