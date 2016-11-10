function indexController($rootScope, $log) {
    var cmpId = 'indexController', cmpName = 'Home';
    $log.debug(cmpId + '...');
    $rootScope.viewName = cmpName;

    var vm = this;
    vm.uiState = {isReady: false};
}
appControllers.controller('indexController', indexController);


