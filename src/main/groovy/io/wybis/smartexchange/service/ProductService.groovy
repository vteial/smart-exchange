package io.wybis.smartexchange.service

import io.wybis.smartexchange.dto.SessionDto
import io.wybis.smartexchange.model.Branch
import io.wybis.smartexchange.model.Product
import io.wybis.smartexchange.service.exceptions.ModelAlreadyExistException;

interface ProductService {

    Product findByCodeAndBranchId(String code, long branchId)

    void create(SessionDto sessionUser, Product model) throws ModelAlreadyExistException

    void onBranchCreate(SessionDto sessionUser, Branch product)
}
