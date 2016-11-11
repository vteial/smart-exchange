function sessionService($rootScope, $log, $http, $q, $filter, wydNotifyService, dataService) {
    var basePath = 'sessions';

    var service = {
        context: {},
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
        dataService.processBranchs([model]);
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

    return service;
}
appServices.factory('sessionService', sessionService);