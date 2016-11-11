
function dataService($log, $rootScope) {
    var basePathS = 'sessions', basePathC = 'console';

    var service = {
        branchs : [],
        branchsMap : {},
        accounts : [],
        accountsMap : {},
        products : [],
        productsMap : {},
        employees : [],
        employeesMap : {},
        customers : [],
        customersMap : {},
        users : [],
        usersMap : {}
    };

    function addOrUpdateCacheY(propName, objectx) {
        var objectsLst = service[propName]
        var objectsMap = service[propName + 'Map'];
        var object = objectsMap[objectx.id];
        if (object) {
            _.assign(object, objectx);
        } else {
            objectsLst.push(objectx);
            objectsMap[objectx.id] = objectx;
        }
    }

    function processAccounts(items) {
        $log.debug('processing accounts started...')
        _.forEach(items, function(objectx) {
            addOrUpdateCacheY('accounts', objectx);
            objectx.product = service.productsMap[objectx.productId];
        });
        $log.debug('processing accounts finished...')
    }

    function processCustomers(items) {
        $log.debug('processing customers started...')
        _.forEach(items, function(objectx) {
            addOrUpdateCacheY('customers', objectx);
            addOrUpdateCacheY('users', objectx);
        });
        $log.debug('processing customers finished...')
    }

    function processEmployees(items) {
        $log.debug('processing employees started...')
        _.forEach(items, function(objectx) {
            addOrUpdateCacheY('employees', objectx);
            addOrUpdateCacheY('users', objectx);
        });
        $log.debug('processing employees finished...')
    }

    function processProducts(items) {
        $log.debug('processing products started...')
        _.forEach(items, function(objectx) {
            addOrUpdateCacheY('products', objectx);
        });
        $log.debug('processing products finished...')
    }

    function processBranchs(items) {
        $log.debug('processing branchs started...')
        _.forEach(items, function(objectx) {
            addOrUpdateCacheY('branchs', objectx);
            processProducts(objectx.products);
            processEmployees(objectx.employees);
            processCustomers(objectx.customers);
            processAccounts(objectx.accounts);
        });
        $log.debug('processing branchs finished...')
    }
    service.processBranchs = processBranchs;

    return service;
}
appServices.factory('dataService', dataService);