package io.wybis.smartexchange.service

import io.wybis.smartexchange.dto.SessionDto
import io.wybis.smartexchange.model.Account
import io.wybis.smartexchange.model.Branch
import io.wybis.smartexchange.model.Product
import io.wybis.smartexchange.model.User
import io.wybis.smartexchange.service.exceptions.ModelAlreadyExistException

interface AccountService {

    List<Account> findByUserId(long userId)

    List<Account> findByUserIdAndType(long userId, String accountType)

    List<Account> findByUserIdAndTypes(long userId, List<String> accountTypes)

    Account findByUserIdAndProductId(long userId, long productId)

    List<Account> findByBranchIdAndTypes(long branchId, List<String> accountTypes)

    void create(SessionDto sessionUser, Account model) throws ModelAlreadyExistException

    void onBranchCreate(SessionDto sessionUser, Branch branch);

    void onProductCreate(SessionDto sessionUser, Product product)

    void onEmployeeCreate(SessionDto sessionUser, User employee)

    void onCustomerCreate(SessionDto sessionUser, User customer)

}
