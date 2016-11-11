function bnsController($log, $rootScope, $scope, wydNotifyService, bnsService) {
    var cmpId = 'bnsController', cmpName = 'Counter';
    $log.debug(cmpId + '...');
    $rootScope.viewName = cmpName;

    var vm = this;
    vm.uiState = {isReady: false};
}
appControllers.controller('bnsController', bnsController);


