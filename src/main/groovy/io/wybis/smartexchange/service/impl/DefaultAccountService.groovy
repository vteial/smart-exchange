package io.wybis.smartexchange.service.impl

import groovyx.gaelyk.GaelykBindings
import groovyx.gaelyk.logging.GroovyLogger
import io.wybis.smartexchange.dto.SessionDto
import io.wybis.smartexchange.model.Account
import io.wybis.smartexchange.model.Branch
import io.wybis.smartexchange.model.Product
import io.wybis.smartexchange.model.User
import io.wybis.smartexchange.model.constants.AccountStatus
import io.wybis.smartexchange.model.constants.ProductType
import io.wybis.smartexchange.model.constants.UserType
import io.wybis.smartexchange.service.AccountService
import io.wybis.smartexchange.service.exceptions.ModelAlreadyExistException
import io.wybis.smartexchange.util.Helper

@GaelykBindings
class DefaultAccountService extends AbstractService implements AccountService {

    GroovyLogger log = new GroovyLogger(DefaultAccountService.class.getName())

    @Override
    public List<Account> findByUserId(long auserId) {
        List<Account> models = []

        def entitys = datastore.execute {
            from Account.class.simpleName
            where userId == auserId
        }

        entitys.each { entity ->
            Account model = entity as Account
            if (model.productId > 0) {
                model.product = Product.get(model.productId)
            }
            models << model
        }

        return models;
    }

    @Override
    public List<Account> findByUserIdAndType(long auserId, String accountType) {
        List<Account> models = []

        def entitys = datastore.execute {
            from Account.class.simpleName
            where userId == auserId
            and type == accountType
        }

        entitys.each { entity ->
            Account model = entity as Account
            if (model.productId > 0) {
                model.product = Product.get(model.productId)
            }
            models << model
        }

        return models;
    }

    @Override
    public List<Account> findByUserIdAndTypes(long aUserId, List<String> accountTypes) {
        List<Account> models = []

        def entitys = datastore.execute {
            from Account.class.simpleName
            where userId == aUserId
            and type in accountTypes
        }

        entitys.each { entity ->
            Account model = entity as Account
            if (model.productId > 0) {
                model.product = Product.get(model.productId)
            }
            models << model
        }

        return models;
    }

    @Override
    public Account findByUserIdAndProductId(long accUserId, long accProductId) {
        Account account = null

        def entitys = datastore.execute {
            from Account.class.simpleName
            where userId == accUserId
            and productId == accProductId
            limit 1
        }

        entitys.each { entity ->
            account = entity as Account
        }

        return account
    }

    @Override
    public List<Account> findByBranchIdAndTypes(long abranchId, List<String> accountTypes) {
        List<Account> models = []

        def entitys = datastore.execute {
            from Account.class.simpleName
            where branchId == abranchId
            and type in accountTypes
        }

        entitys.each { entity ->
            Account model = entity as Account
            if (model.productId > 0) {
                model.product = Product.get(model.productId)
            }
            models << model
        }

        return models;
    }

    @Override
    public void create(SessionDto sessionUser, Account model)
            throws ModelAlreadyExistException {

        model.status = AccountStatus.ACTIVE
        model.id = autoNumberService.getNextNumber(sessionUser, Account.ID_KEY)

        model.prePersist(sessionUser.id)
        model.save()
    }

    @Override
    public void onBranchCreate(SessionDto sessionUser, Branch branch) {
    }

    @Override
    public void onProductCreate(SessionDto sessionUser, Product product) {
        def entitys = datastore.execute {
            from User.class.getSimpleName()
            where branchId == product.branchId
            and type == UserType.EMPLOYEE
        }

        entitys.each { entity ->
            User user = entity as User
            Account account = new Account()
            account.with {
                aliasName = product.code
                type = product.type
                productId = product.id
                userId = user.id
                branchId = product.branchId
            }
            account.name = product.code + '-' + Helper.capitalize(user.firstName)
            if (user.lastName) {
                account.name = account.name + ' ' + Helper.capitalize(user.lastName)
            }
            this.create(sessionUser, account)
        }
    }

    @Override
    public void onEmployeeCreate(SessionDto sessionUser, User employee) {
        def entitys = null

        if (employee.isVirtual()) {
            entitys = datastore.execute {
                from Product.class.simpleName
                where branchId == employee.branchId
                and type == ProductType.CASH_CAPITAL
                limit 1
            }
            entitys.each { entity ->
                Product product = entity as Product
                Account account = new Account()
                account.with {
                    aliasName = product.code
                    type = product.type
                    isMinus = true
                    productId = product.id
                    userId = employee.id
                    branchId = employee.branchId
                }
                account.name = product.code + '-' + Helper.capitalize(employee.firstName)
                if (employee.lastName) {
                    account.name = account.name + ' ' + Helper.capitalize(employee.lastName)
                }
                this.create(sessionUser, account)
            }
        }

        entitys = datastore.execute {
            from Product.class.simpleName
            where branchId == employee.branchId
            and type == ProductType.CASH_EMPLOYEE
            limit 1
        }

        entitys.each { entity ->
            Product product = entity as Product
            Account account = new Account()
            account.with {
                aliasName = product.code
                type = product.type
                productId = product.id
                userId = employee.id
                branchId = employee.branchId
            }
            account.name = product.code + '-' + Helper.capitalize(employee.firstName)
            if (employee.lastName) {
                account.name = account.name + ' ' + Helper.capitalize(employee.lastName)
            }
            this.create(sessionUser, account)

            employee.cashAccount = account
            employee.cashAccountId = account.id
            employee.save()
        }

        entitys = datastore.execute {
            from Product.class.simpleName
            where branchId == employee.branchId
            and type == ProductType.PROFIT_EMPLOYEE
            limit 1
        }

        entitys.each { entity ->
            Product product = entity as Product
            Account account = new Account()
            account.with {
                aliasName = product.code
                type = product.type
                productId = product.id
                userId = employee.id
                branchId = employee.branchId
            }
            account.name = product.code + '-' + Helper.capitalize(employee.firstName)
            if (employee.lastName) {
                account.name = account.name + ' ' + Helper.capitalize(employee.lastName)
            }
            this.create(sessionUser, account)

            employee.profitAccount = account
            employee.profitAccountId = account.id
            employee.save()
        }

        entitys = datastore.execute {
            from Product.class.simpleName
            where branchId == employee.branchId
            and type == ProductType.PRODUCT
        }

        entitys.each { entity ->
            Product product = entity as Product
            Account account = new Account()
            account.with {
                aliasName = product.code
                type = product.type
                productId = product.id
                userId = employee.id
                branchId = employee.branchId
            }
            account.name = product.code + '-' + Helper.capitalize(employee.firstName)
            if (employee.lastName) {
                account.name = account.name + ' ' + Helper.capitalize(employee.lastName)
            }
            this.create(sessionUser, account)
        }
    }

    @Override
    public void onCustomerCreate(SessionDto sessionUser, User customer) {
        def entitys = datastore.execute {
            from Product.class.simpleName
            where branchId == customer.branchId
            and type == ProductType.CASH_CUSTOMER
            limit 1
        }

        entitys.each { entity ->
            Product product = entity as Product
            Account account = new Account()
            account.with {
                aliasName = product.code
                isMinus = true
                type = product.type
                productId = product.id
                userId = customer.id
                branchId = customer.branchId
            }
            account.name = product.code + '-' + Helper.capitalize(customer.firstName)
            if (customer.lastName) {
                account.name = account.name + ' ' + Helper.capitalize(customer.lastName)
            }
            this.create(sessionUser, account)

            customer.cashAccount = account
            customer.cashAccountId = account.id
            customer.save()
        }
    }
}
