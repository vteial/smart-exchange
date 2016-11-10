function counterController($rootScope, $log) {
    var cmpId = 'counterController', cmpName = 'Counter';
    $log.debug(cmpId + '...');
    $rootScope.viewName = cmpName;

    var vm = this;
    vm.uiState = {isReady: false};
}
appControllers.controller('counterController', counterController);


