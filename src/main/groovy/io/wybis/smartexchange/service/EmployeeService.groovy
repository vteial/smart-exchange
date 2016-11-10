package io.wybis.smartexchange.service

import io.wybis.smartexchange.dto.SessionDto
import io.wybis.smartexchange.model.Branch

interface EmployeeService {

    //void add(SessionDto sessionUser, User employee) throws ModelAlreadyExistException

    void onBranchCreate(SessionDto sessionUser, Branch branch)
}
