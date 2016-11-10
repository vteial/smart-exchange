function messageController($log, $rootScope, $scope, $location) {
    $log.debug('messageController...');
    $rootScope.viewName = 'Message';

    var vm = this;
    vm.uiState = {isReady: false};

    vm.params = $location.search();
    if (vm.params.errorMessage) {
        vm.hasErrorMessage = true;
    }
    else {
        vm.hasErrorMessage = false;
    }
    //$log.debug(vm.params);
}
appControllers.controller('messageController', messageController);

