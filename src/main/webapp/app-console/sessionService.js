
function sessionService($log, $http, $q, $rootScope, wydNotifyService) {
    var basePathS = 'sessions', basePathC = 'console';

    var service = {
        context: {},
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

    service.context.appName = 'Smart Exchange';
    service.context.appDescription = 'Exchange Management Application';
    service.context.appUrls = [
        {name: 'App Mobile', url: '/index-small.html'},
        {name: 'App Desktop', url: '/index.html'}
    ];

    function processProps(props) {
        $log.debug('processing session properties started...');
        _.assign(service.context, props);
        if (props.sessionDto) {
            $rootScope.xUserId = props.sessionDto.userId;
            // $log.info('Session User Id = ' + $rootScope.xUserId);
        }
        $log.debug('processing session properties finished...');
    }

    service.properties = function () {
        var path = basePathS + '/properties';

        var deferred = $q.defer();
        $http.get(path).success(function (response) {
            //$log.debug(response);
            if (response.type === 0) {
                $log.debug(response.data);
                processProps(response.data);
                $rootScope.$broadcast('session:properties', 'Session properties updated...');
                deferred.resolve(response);
            }
        }).error(function () {
            deferred.reject("unable fetch properties...");
        });

        return deferred.promise;
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

    service.getBranchs = function() {
        var path = basePathC + '/branchs';

        var deferred = $q.defer();
        $http.get(path).success(function(response) {
            if (response.type === 0) {
                processBranchs(response.data);
                deferred.resolve(response);
            }
            // $log.info(response);
        })

        return deferred.promise;
    };

    service.resetBranch = function(id) {
        var path = basePathC + '/branchs/branch/' + id + '/reset'

        var deferred = $q.defer();
        $http.get(path).success(function(response) {
            // $log.debug(response);
            if (response.type === 0) {
                wydNotifyService.success(response.message, true);
                service.getBranchs();
                deferred.resolve(response);
            } else {
                wydNotifyService.error(response.message, true);
                $log.error('reset branch failed...');
            }
        });

        return deferred.promise;
    };

    service.deleteBranch = function(id) {
        var path = basePathC + '/branchs/branch/' + id

        var deferred = $q.defer();
        $http['delete'](path).success(function(response) {
            // $log.debug(response);
            if (response.type === 0) {
                wydNotifyService.success(response.message, true);
                service.getBranchs();
                deferred.resolve(response);
            } else {
                wydNotifyService.error(response.message, true);
                $log.error('deleting branch failed...');
            }
        });

        return deferred.promise;
    };

    return service;
}
appServices.factory('sessionService', sessionService);