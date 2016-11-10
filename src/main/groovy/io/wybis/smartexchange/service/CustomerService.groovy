package io.wybis.smartexchange.service

import io.wybis.smartexchange.dto.SessionDto
import io.wybis.smartexchange.model.Branch

interface CustomerService {

    //void create(SessionDto sessionUser, User customer) throws ModelAlreadyExistException

    void onBranchCreate(SessionDto sessionUser, Branch branch)
}
