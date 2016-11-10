function signOutController($rootScope, $log, $http, $window) {
    $log.debug('signOutController...');
    $rootScope.viewName = 'SignOut';

    var vm = this;
    vm.uiState = {isReady: false};

    var path = 'sessions/sign-out';
    $http.get(path).success(function (response) {
        $window.location = 'index.html';
        // $log.info(response);
    }).error(function () {
        $window.location = 'index.html';
    });
}
appControllers.controller('signOutController', signOutController);