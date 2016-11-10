package io.wybis.smartexchange.service;

import io.wybis.smartexchange.dto.SessionDto
import io.wybis.smartexchange.model.User
import io.wybis.smartexchange.service.exceptions.ModelAlreadyExistException
import io.wybis.smartexchange.service.exceptions.ModelNotFoundException

interface UserService {

    User findByUserId(String userId)

    List<User> findByBranchIdAndType(long branchId, String type);

    User create(SessionDto sessionUser, User user) throws ModelAlreadyExistException

    User update(SessionDto sessionUser, User user) throws ModelNotFoundException

    void reset(SessionDto sesionUser, long userId) throws ModelNotFoundException

    void delete(SessionDto sesionUser, long userId) throws ModelNotFoundException

    void delete(SessionDto sesionUser, String userId) throws ModelNotFoundException

}
