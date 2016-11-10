function homeController($rootScope, $log, pouchDB) {
    $log.debug('homeController...');
    $rootScope.viewName = 'Home';

    var vm = this, pouchdb = pouchDB;
    vm.uiState = {isReady: false};

}
appControllers.controller('homeController', homeController);


