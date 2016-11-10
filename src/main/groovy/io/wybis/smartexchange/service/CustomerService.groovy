package io.wybis.smartexchange.service

import io.wybis.smartexchange.dto.SessionDto
import io.wybis.smartexchange.model.Branch
import io.wybis.smartexchange.model.User
import io.wybis.smartexchange.service.exceptions.ModelAlreadyExistException;

interface CustomerService {

    void add(SessionDto sessionUser, User customer) throws ModelAlreadyExistException

    void onBranchCreate(SessionDto sessionUser, Branch branch)
}
