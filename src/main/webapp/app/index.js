
function rootController($rootScope, $scope, $log, $window) {
    $log.debug('rootController...');

    $scope.lodash = _;

}
appControllers.controller('rootController', rootController);

function signInController($log, $rootScope, $scope, wydNotifyService, wydFocusService, $http, $window) {
    $log.debug('signInController...');
    $rootScope.viewName = 'SignIn';

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

/*
function signUpController($log, $rootScope, $scope, sessionService, wydNotifyService, wydFocusService, vcRecaptchaService, $location) {
    $log.debug('signUpController...');
    //$rootScope.viewName = 'Sign Up';

    var vm = this;
    vm.uiState = {isReady: false, isBlocked: false};

    vm.save = function () {

        //$log.info($scope.form.$valid)
        if (!$scope.form0.$valid) {
            wydNotifyService.showError('Please fix the error fields...');

            var error = $scope.form0.userUserId.$error;
            if (Object.keys(error).length > 0) {
                wydFocusService('userUserId');
                return;
            }
            error = $scope.form0.userPassword.$error;
            if (Object.keys(error).length > 0) {
                wydFocusService('userPassword');
                return;
            }
            error = $scope.form0.userRetypePassword.$error;
            if (Object.keys(error).length > 0) {
                wydFocusService('userRetypePassword');
                return;
            }
            error = $scope.form0.userFirstName.$error;
            if (Object.keys(error).length > 0) {
                wydFocusService('userFirstName');
                return;
            }
            error = $scope.form0.userLastName.$error;
            if (Object.keys(error).length > 0) {
                wydFocusService('userLastName');
                return;
            }
            error = $scope.form0.userIAgree.$error;
            if (Object.keys(error).length > 0) {
                wydNotifyService.showError('Must be agreed for the Terms and Conditions');
                wydFocusService('userIAgree');
                return;
            }

            return;
        }

        if (vm.user.password != vm.user.retypePassword) {
            wydNotifyService.showError('Password and Retype password should be matched...');
            return;
        }

        vm.uiState.isBlocked = true;
        sessionService.signUp(vm.user).then(function (response) {
            vm.uiState.isBlocked = false;
            vcRecaptchaService.reload(vm.widgetId);
            vm.user.password = '';
            vm.user.retypePassword = '';
            if (response.type !== 0) {
                wydNotifyService.showError(response.message);
            }
            else {
                vm.message = response.message;
                // var alert = $mdDialog.alert({
                //     title: 'Sign Up Acknowledgement!',
                //     content: response.message,
                //     ok: 'Sign In'
                // });
                // $mdDialog.show(alert).finally(function () {
                //     alert = undefined;
                //     $log.info('redirecting to sign in...');
                //     $location.path('/sign-in');
                // });
            }
        });
    };

    vm.setRecaptchaWidgetId = function (widgetId) {
        $log.debug('Created widget ID: %s', widgetId);
        vm.widgetId = widgetId;
    };

    vm.setRecaptchaResponse = function (response) {
        $log.debug('Response available');
        vm.user.recaptchaValue = response;
        vm.isSignUpDisabled = false;
    };

    vm.setRecaptchaExpiration = function () {
        $log.debug('Recaptcha expired. Resetting response object');
        vm.user.recaptchaValue = null;
        vm.isSignUpDisabled = true;
    };

    function init() {
        vm.recaptchaKey = '6Lfg9CkTAAAAAOn-d3AHHM54mES1lWMFIZa_X1_Q';
        vm.widgetId = null;
        vm.isSignUpDisabled = true;

        vm.user = {
            userIdPattern: /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/,
            passwordPattern: /^.*(?=.{4,10})(?=.*\d)(?=.*[a-zA-Z]).*$/,
            firstNamePattern: /^[a-zA-Z ]*$/,
            lastNamePattern: /^[a-zA-Z ]*$/,
            recaptchaValue: null
        };

        //wydFocusService('userUserId');

        vm.uiState.isReady = true;
    }

    init();
}
appControllers.controller('signUpController', signUpController);
*/

var dependents = [];
dependents.push('ngStorage');
dependents.push('ngclipboard');
dependents.push('green.inputmask4angular');
//dependents.push('vcRecaptcha');
dependents.push('app.filters');
dependents.push('app.directives');
dependents.push('app.services');
dependents.push('app.controllers');
var app = angular.module('app', dependents), lodash = _;

app.config(function ($httpProvider) {
    $httpProvider.interceptors.push('generalHttpInterceptor');
});

function appInit($log, $rootScope, $location, $sessionStorage) {
    $log.info('Initialization started...');


    $log.info('Initialization finished...');
}
app.run(['$log', '$rootScope', '$location', '$sessionStorage', appInit]);
