package io.wybis.smartexchange.service;

import io.wybis.smartexchange.dto.SessionDto
import io.wybis.smartexchange.model.Branch
import io.wybis.smartexchange.service.exceptions.ModelAlreadyExistException

interface BranchService {

    void create(SessionDto sessionUser, Branch branch) throws ModelAlreadyExistException
}
