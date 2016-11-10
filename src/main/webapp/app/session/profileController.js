function profileController($log, $rootScope, $scope, sessionService, wydNotifyService, wydFocusService, $timeout) {
    $log.debug('profileController...');
    $rootScope.viewName = 'Profile';

    var vm = this;
    vm.uiState = {isReady: false, isBlocked: false};

    vm.onGeneralSelected = function () {
        wydFocusService('userFirstName');
    };

    vm.onChangePasswordSelected = function () {
        wydFocusService('userCurrentPassword');
    };

    vm.changeDetails = function () {
        if (!$scope.userDetail.$valid) {
            wydNotifyService.showError('Please fix the error fields...');

            var error = $scope.userDetail.userFirstName.$error;
            if (Object.keys(error).length > 0) {
                wydFocusService('userFirstName');
                return;
            }
            error = $scope.userDetail.userLastName.$error;
            if (Object.keys(error).length > 0) {
                wydFocusService('userLastName');
                return;
            }

            return;
        }

        vm.uiState.isBlocked = true;
        sessionService.changeDetails(vm.user).then(function (response) {
            vm.uiState.isBlocked = false;
        });
    };

    vm.changePassword = function () {
        if (!$scope.userSecret.$valid) {
            wydNotifyService.showError('Please fix the error fields...');

            //vm.user.currentPassword = '';
            vm.user.newPassword = '';
            vm.user.retypeNewPassword = '';

            var error = $scope.userSecret.userCurrentPassword.$error;
            if (Object.keys(error).length > 0) {
                wydFocusService('userCurrentPassword');
                return;
            }
            error = $scope.userSecret.userNewPassword.$error;
            if (Object.keys(error).length > 0) {
                wydFocusService('userPassword');
                return;
            }
            error = $scope.userSecret.userRetypeNewPassword.$error;
            if (Object.keys(error).length > 0) {
                wydFocusService('userRetypePassword');
                return;
            }
            return;
        }

        if (vm.user.currentPassword == vm.user.newPassword) {
            wydNotifyService.showError('Current password and New password should not be same...');
            wydFocusService('userPassword');
            return;
        }

        vm.uiState.isBlocked = true;
        sessionService.changePassword(vm.user).then(function (response) {
            vm.uiState.isBlocked = false;
            if (response.type !== 0) {
                if (response.message.startsWith('Invalid')) {
                    wydFocusService('userCurrentPassword');
                }
                else {
                    wydFocusService('userPassword');
                }
            }
            vm.user.currentPassword = '';
            vm.user.newPassword = '';
            vm.user.retypeNewPassword = '';
        });
    }

    $scope.$on('session:properties', function (event, data) {
        init();
    });

    function init() {
        if (sessionService.context.sessionDto) {
            var user = {}, userDto = sessionService.context.sessionDto;

            user.id = userDto.id;
            user.userId = userDto.userId;
            user.emailId = userDto.emailId;
            user.firstName = _.capitalize(userDto.firstName);
            user.lastName = _.capitalize(userDto.lastName);
            user.currentPassword = '';
            user.newPassword = '';
            user.retypeNewPassword = '';
            vm.user = user;

            vm.uiState.isReady = true;
        }
    }

    init();
}
appControllers.controller('profileController', profileController);
