function homeController($rootScope, $log) {
    var cmpId = 'homeController', cmpName = 'Home';
    $log.debug(cmpId + '...');
    $rootScope.viewName = cmpName;

    var vm = this;
    vm.uiState = {isReady: false};
}
appControllers.controller('homeController', homeController);


