function signInController($log, $rootScope, $scope, wydNotifyService, wydFocusService, $http, $window) {
    var cmpId = 'signInController', cmpName = 'Sign In';
    $log.debug(cmpId + '...');
    $rootScope.viewName = cmpName;

    var vm = this;
    vm.uiState = {isReady: false, isBlocked: false};

    vm.signIn = function () {
        wydNotifyService.hide();

        vm.message = null;

        vm.uiState.isBlocked = true;
        var path = 'sessions/sign-in';
        $http.post(path, vm.user).success(function (response) {
            vm.uiState.isBlocked = false;
            $log.info(response);
            if (response.type === 0) {
                if (response.data) {
                    $window.location = 'home.html#' + response.data;
                }
                else {
                    $window.location = 'home.html';
                }
            } else {
                vm.user.password = '';
                vm.message = response.message;
                wydNotifyService.showError(vm.message);
                wydFocusService('signInUserUserId');
            }
        });
    };

    vm.processKeyUp = function (event) {
        if (event.keyCode === 13) {
            vm.signIn();
        }
    };

    function init() {
        vm.message = null

        vm.user = {
            userId: '',
            password: ''
        };

        wydFocusService('signInUserUserId');

        vm.uiState.isReady = true;
    }

    init();

}
appControllers.controller('signInController', signInController);