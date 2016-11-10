package io.wybis.smartexchange.service.impl

import groovyx.gaelyk.GaelykBindings
import groovyx.gaelyk.logging.GroovyLogger
import io.wybis.smartexchange.dto.SessionDto
import io.wybis.smartexchange.model.Account
import io.wybis.smartexchange.model.Product
import io.wybis.smartexchange.model.User
import io.wybis.smartexchange.model.constants.UserStatus
import io.wybis.smartexchange.service.UserService
import io.wybis.smartexchange.service.exceptions.ModelAlreadyExistException
import io.wybis.smartexchange.service.exceptions.ModelNotFoundException

@GaelykBindings
class DefaultUserService extends AbstractService implements UserService {

    GroovyLogger log = new GroovyLogger(DefaultUserService.class.getName())

    @Override
    User findByUserId(String iuserId) {
        User user = null

        def entitys = datastore.execute {
            from User.class.simpleName
            where userId == iuserId
        }

        if (entitys.size() > 0) {
            user = entitys[0] as User
        }

        return user
    }

    @Override
    public List<User> findByBranchIdAndType(long userBranchId, String customerType) {
        List<User> models = []

        def entitys = datastore.execute {
            from User.class.simpleName
            where branchId == userBranchId
            and type == customerType
        }

        entitys.each { entity ->
            User model = entity as User
            model.cashAccount = Account.get(model.cashAccountId)
            model.cashAccount.product = Product.get(model.cashAccount.productId)
            models <<  model
        }

        return models;
    }

    @Override
    public User create(SessionDto sessionUser, User model)
            throws ModelAlreadyExistException {
        model.userId = model.userId.toLowerCase()

        if (this.findByUserId(model.userId)) {
            throw new ModelAlreadyExistException()
        }

        if (model.password == null) {
            model.password = 'wybis123'
        }
        model.token = UUID.randomUUID().toString()
        model.status = UserStatus.ACTIVE
        model.id = autoNumberService.getNextNumber(sessionUser, User.ID_KEY)

        if (sessionUser.id == 0) {
            sessionUser.id = model.id
        }
        model.prePersist(sessionUser.id)
        model.save()

        return model
    }

    @Override
    public User update(SessionDto sessionUser, User nmodel)
            throws ModelNotFoundException {
        User emodel = User.get(nmodel.id)
        if (emodel == null) {
            throw new ModelNotFoundException();
        }

        if (nmodel.password) {
            emodel.password = nmodel.password
        }
        if (nmodel.emailId) {
            emodel.password = nmodel.emailId
        }
        if (nmodel.firstName) {
            emodel.firstName = nmodel.firstName
        }
        if (nmodel.lastName) {
            emodel.lastName = nmodel.lastName
        }
        if (nmodel.status) {
            emodel.status = nmodel.status
        }

        emodel.preUpdate(sessionUser.id)
        emodel.save()

        return emodel
    }

    @Override
    void reset(SessionDto sessionUser, long iuserId) throws ModelNotFoundException {

    }

    @Override
    void delete(SessionDto sessionUser, long userId) throws ModelNotFoundException {
        User user = User.get(userId)
        if (user) {
            user.delete()
        } else {
            throw new ModelNotFoundException()
        }
    }

    @Override
    void delete(SessionDto sessionUser, String userId) throws ModelNotFoundException {

    }
}
