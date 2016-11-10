function dialogController($log, $rootScope, $scope, $mdDialog, arguments) {
    var cmpId = 'dialogController', cmpName = 'Dialog';
    $log.debug(cmpId + '...');

    $scope.arguments = arguments;

    $scope.hide = function () {
        $mdDialog.hide();
    };

    $scope.cancel = function () {
        $mdDialog.cancel();
    };
}
appControllers.controller('dialogController', dialogController);

function messageController($log, $rootScope, $scope, sessionService, $location) {
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

function termsController($log, $rootScope, $scope) {
    $log.debug('termsController...');
    $rootScope.viewName = 'Terms';

    var vm = this;
    vm.uiState = {isReady: false};
}
appControllers.controller('termsController', termsController);

function aboutController($log, $rootScope, $scope) {
    $log.debug('aboutController...');
    $rootScope.viewName = 'About';

    var vm = this;
    vm.uiState = {isReady: false};
}
appControllers.controller('aboutController', aboutController);

function testController($log, $rootScope, $scope) {
    $log.debug('testController...');
    $rootScope.viewName = 'Test';

    var vm = this;
    vm.uiState = {isReady: false};

    vm.url = "https://www.youtube.com/watch?v=DM6dLcg0618";
    $scope.url = vm.url;
}
appControllers.controller('testController', testController);

