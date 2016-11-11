function reportController($log, $rootScope, $scope, wydNotifyService, $window) {
    var cmpId = 'reportController', cmpName = 'Reports';
    $log.debug(cmpId + '...');
    $rootScope.viewName = cmpName;

    var vm = this;
    vm.uiState = {isReady: false};

    vm.currentState = function () {
        var path = '/sessions/current-state-as-report';
        $log.debug('Current State Report Path : ' + path);
        $window.open(path, '_blank');
    }

}
appControllers.controller('reportController', reportController);


