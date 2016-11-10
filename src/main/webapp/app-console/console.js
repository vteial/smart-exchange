function rootController($log, $rootScope, $scope, sessionService, $window) {
    var cmpId = 'rootController', cmpName = '-';
    $rootScope.viewName = cmpName;
    $log.debug(cmpId + '...');

    $scope.lodash = _;

    sessionService.properties();
    var sessionS = sessionService;
    $scope.sessionS = sessionS;

    $scope.$on('session:properties', function (event, data) {
        $log.debug(cmpId + ' on ' + event.name + ' started...');
        if (sessionService.context.applicationUser) {
            $scope.adminUserEmailId = sessionService.context.applicationUser.email
            $scope.adminLoginOrLogoutUrl = sessionService.context.adminLogoutUrl
            $scope.adminLoginOrLoginIcon = 'fa-sign-in'
            $scope.adminLoginOrLoginText = 'Logout'
        } else {
            $scope.adminLoginOrLogoutUrl = sessionService.context.adminLoginUrl
            $scope.adminLoginOrLoginIcon = 'fa-sign-out'
            $scope.adminLoginOrLoginText = 'Login'
        }
        $log.debug(cmpId + ' on ' + event.name + ' finished...');
    });

    $scope.historyBack = function () {
        $window.history.back();
    };
}
appControllers.controller('rootController', rootController);

var dependents = ['ngRoute', 'ngSanitize', 'ngMessages'];
dependents.push('ngStorage');
dependents.push('ngclipboard');
dependents.push('green.inputmask4angular');
dependents.push('hSweetAlert');
dependents.push('ngNotify');
dependents.push('app.filters');
dependents.push('app.directives');
dependents.push('app.services');
dependents.push('app.controllers');
var app = angular.module('app', dependents), lodash = _;

app.config(function ($httpProvider) {
    $httpProvider.interceptors.push('generalHttpInterceptor');
});

function appConfig($routeProvider, $locationProvider) {
    $routeProvider.when('/', {
        redirectTo: '/home'
    });
    $routeProvider.when('/home', {
        templateUrl: 'app-console/home/t.html',
        controller: 'homeController as vm'
    });
    $routeProvider.when('/branchs', {
        templateUrl: 'app-console/home/t-branchList.html',
        controller: 'branchListController as vm'
    });
    $routeProvider.when('/settings', {
        templateUrl: 'app-console/home/t-settings.html',
        controller: 'settingsController as vm'
    });
    $routeProvider.when('/not-found', {
        templateUrl: 'app-console/zgeneral/t-notFound.html'
    });
    $routeProvider.otherwise({
        redirectTo: '/not-found'
    });
};
app.config(appConfig);

function appInit($log, $rootScope, $location, $sessionStorage) {
    $log.info('initialization started...');

    $log.info('initialization finished...');
}
app.run(['$log', '$rootScope', '$location', '$sessionStorage', appInit]);
