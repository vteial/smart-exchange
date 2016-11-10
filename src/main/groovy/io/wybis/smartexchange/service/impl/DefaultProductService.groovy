package io.wybis.smartexchange.service.impl

import groovyx.gaelyk.GaelykBindings
import groovyx.gaelyk.logging.GroovyLogger
import io.wybis.smartexchange.dto.SessionDto
import io.wybis.smartexchange.model.Branch
import io.wybis.smartexchange.model.Product
import io.wybis.smartexchange.model.constants.ProductStatus
import io.wybis.smartexchange.model.constants.ProductType
import io.wybis.smartexchange.service.AccountService
import io.wybis.smartexchange.service.ProductService
import io.wybis.smartexchange.service.exceptions.ModelAlreadyExistException

@GaelykBindings
class DefaultProductService extends AbstractService implements ProductService {

    GroovyLogger log = new GroovyLogger(DefaultProductService.class.getName())

    AccountService accountService

    @Override
    public Product findByCodeAndBranchId(String prdCode, long prdBranchId) {
        Product product = null

        def entitys = datastore.execute {
            from Product.class.simpleName
            where code == prdCode
            and branchId == prdBranchId
            limit 1
        }

        entitys.each { entity ->
            product = entity as Product
        }

        return product
    }

    @Override
    public void create(SessionDto sessionUser, Product model)
            throws ModelAlreadyExistException {

        if (model.type == null) {
            model.type = ProductType.PRODUCT
        }
        model.status = ProductStatus.ACTIVE

        model.id = autoNumberService.getNextNumber(sessionUser, Product.ID_KEY)

        model.prePersist(sessionUser.id)
        model.save()

        accountService.onProductCreate(sessionUser, model)
    }

    @Override
    public void onBranchCreate(SessionDto sessionUser, Branch branch) {
        Product model = new Product()

        model.with {
            type = ProductType.CASH_CAPITAL
            code = 'CPT'
            name = 'CASH IN CAPITAL'
            baseUnit = 1
            denominator = 1
            buyRate = 1
            buyPercent = 1
            sellRate = 1
            sellPercent = 1
            handStockAverage = 1
            virtualStockAverage = 1
            branchId = branch.id
        }
        this.create(sessionUser, model)

        model = new Product()

        model.with {
            type = ProductType.CASH_EMPLOYEE
            code = 'CIE'
            name = 'CASH IN EMPLOYEE'
            baseUnit = 1
            denominator = 1
            buyRate = 1
            buyPercent = 1
            sellRate = 1
            sellPercent = 1
            handStockAverage = 1
            virtualStockAverage = 1
            branchId = branch.id
        }
        this.create(sessionUser, model)

        model.with {
            type = ProductType.PROFIT_EMPLOYEE
            code = 'PIE'
            name = 'PROFIT IN EMPLOYEE'
            baseUnit = 1
            denominator = 1
            buyRate = 1
            buyPercent = 1
            sellRate = 1
            sellPercent = 1
            handStockAverage = 1
            virtualStockAverage = 1
            branchId = branch.id
        }
        this.create(sessionUser, model)

        model.with {
            type = ProductType.CASH_CUSTOMER
            code = 'CIC'
            name = 'CASH IN CUSTOMER'
            baseUnit = 1
            denominator = 1
            buyRate = 1
            buyPercent = 1
            sellRate = 1
            sellPercent = 1
            handStockAverage = 1
            virtualStockAverage = 1
            branchId = branch.id
        }
        this.create(sessionUser, model)
    }
}
