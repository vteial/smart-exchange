function sessionService($rootScope, $log, $http, $q, $filter, wydNotifyService) {
    var basePath = 'sessions';

    var service = {
        context: {},
        surveysMap: {},
        questionsMap: {}
    };

    function addOrUpdateCache(propName, objectx) {
        //var objectsLst = service[propName]
        var objectsMap = service[propName + 'Map'];
        var object = objectsMap[objectx.id];
        if (object) {
            _.assign(object, objectx);
        } else {
            //objectsLst.push(objectx);
            objectsMap[objectx.id] = objectx;
        }
    }

    function processProps(props) {
        $log.debug('processing session properties started...');
        _.assign(service.context, props);
        if (props.sessionDto) {
            $rootScope.xUserId = props.sessionDto.userId;
            // $log.info('Session User Id = ' + $rootScope.xUserId);
        }
        $log.debug('processing session properties finished...');
    }

    function processModel(model) {
        $log.debug('processing session model started...');
        service.processEvents(model.events);
        $log.debug('processing session model finished...');
    }

    service.properties = function () {
        var path = basePath + '/properties';

        var deferred = $q.defer();
        $http.get(path).success(function (response) {
            $log.debug(response);
            if (response.type === 0) {
                processProps(response.data);
                if (response.model) {
                    processModel(response.model);
                }
                $rootScope.$broadcast('session:properties', 'Session properties updated...');
                deferred.resolve(response);
            }
        }).error(function () {
            deferred.reject("unable to authenticate...");
        });

        return deferred.promise;
    };

    service.changeDetails = function (user) {
        var path = basePath + '/changeDetails';

        var userDetail = {
            firstName: user.firstName,
            lastName: user.lastName
        };
        //$log.info(userDetail);

        var deferred = $q.defer();
        $http.post(path, userDetail).success(function (response) {
            $log.debug(response);
            if (response.type === 0) {
                service.context.sessionDto.firstName = userDetail.firstName;
                service.context.sessionDto.lastName = userDetail.lastName;
                wydNotifyService.showSuccess(response.message);
            }
            else {
                wydNotifyService.showError(response.message);
            }
            deferred.resolve(response);
        }).error(function () {
            deferred.reject("unable to change details...");
        });

        return deferred.promise;
    };

    service.changePassword = function (user) {
        var path = basePath + '/changePassword';

        var userSecret = {
            currentPassword: user.currentPassword,
            newPassword: user.newPassword,
            retypeNewPassword: user.retypeNewPassword
        };
        //$log.info(userSecret);

        var deferred = $q.defer();
        $http.post(path, userSecret).success(function (response) {
            $log.debug(response);
            if (response.type === 0) {
                wydNotifyService.showSuccess(response.message);
            }
            else {
                wydNotifyService.showError(response.message);
            }
            deferred.resolve(response);
        }).error(function () {
            deferred.reject("unable to change password...");
        });

        return deferred.promise;
    };

    service.processSurvey = function (objectx) {
        if (objectx.date) {
            objectx.date = new Date(objectx.date);
            objectx.dateString = $filter('date')(objectx.date, 'dd / MM / yyyy');
        }
        addOrUpdateCache('surveys', objectx);
    };

    function processSurveys(objects) {
        $log.debug('processing surveys started...')
        _.forEach(objects, function (objectx) {
            service.processSurvey(objectx);
        });
        $log.debug('processing surveys finished...')
    }

    service.getSurvey = function(id) {
        var path = basePath + '/surveys/survey/' + id;

        var deferred = $q.defer();
        $http.get(path).success(function (response) {
            // $log.debug(response);
            if (response.type === 0) {
                service.processSurvey(response.data);
                $rootScope.$broadcast('session:survey', 'Survey updated...');
                deferred.resolve(response);
            }
        });

        return deferred.promise;
    };

    service.saveSurvey = function (model) {
        var path = basePath + '/surveys/survey';

        var reqModel = {
            id: model.id,
            name: model.name,
            title: model.title,
            description: model.description
        };
        //$log.info(reqModel);

        var deferred = $q.defer();
        $http.post(path, reqModel).success(function (response) {
            $log.debug(response);
            if (response.type === 0) {
                var model = response.data;
                service.processSurvey(model);
            }
            deferred.resolve(response);
        }).error(function () {
            deferred.reject("unable to update survey...");
        });

        return deferred.promise;
    };

    service.processTran = function (objectx) {
        addOrUpdateCache('trans', objectx);
        if (objectx.type == 'buy') {
            objectx.type = 'Credit'
        }
        if (objectx.type == 'sell') {
            objectx.type = 'Debit'
        }
        if (objectx.event) {
            service.processEvent(objectx.event);
        }
    };

    service.processTrans = function (trans) {
        $log.debug('processing trans started...')
        _.forEach(events, function (objectx) {
            service.processTran(objectx);
        });
        $log.debug('processing trans finished...')
    };

    service.contributeToEvent = function (event, order) {
        var path = basePath + '/order';

        var reqReceipt = {
            description: order.description,
            orders: [{
                eventId: event.id,
                description: order.description,
                accountId: event.accountId,
                type: 'buy',
                amount: order.amountRaw
            }]
        };
        $log.info(reqReceipt);

        var deferred = $q.defer();
        $http.post(path, reqReceipt).success(function (response) {
            $log.debug(response);
            deferred.resolve(response);
        }).error(function () {
            deferred.reject("unable to contribute event...");
        });

        return deferred.promise;
    };

    return service;
}
appServices.factory('sessionService', sessionService);