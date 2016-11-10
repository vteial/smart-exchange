function sessionService($log, $http, $q, $rootScope, wydNotifyService) {
    var basePath = 'sessions';

    var service = {
        context: {},
        model: {
            hasPrevious: false,
            hasNext: false
        }
    };

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
        var path = basePath + '/properties';

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

    return service;
}
appServices.factory('sessionService', sessionService);