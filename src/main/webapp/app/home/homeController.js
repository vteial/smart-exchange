function homeController($rootScope, $log) {
    $log.debug('homeController...');
    $rootScope.viewName = 'Home';

    var vm = this;
    vm.uiState = {isReady: false};
}
appControllers.controller('homeController', homeController);


