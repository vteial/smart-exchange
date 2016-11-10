function rootController($rootScope, $scope, $log, $mdSidenav, sessionService) {
    $log.debug('rootController...');

    $scope.lodash = _;

    $scope.toggleSideNavBar = function (menuId) {
        $mdSidenav(menuId).toggle();
    };

    $scope.historyBack = function () {
        $window.history.back();
    };
}
appControllers.controller('rootController', rootController);

var dependents = ['ngRoute', 'ngSanitize', 'ngMessages'];
dependents.push('ngMaterial');
//dependents.push('md.data.table');
dependents.push('ngStorage');
dependents.push('ngclipboard');
dependents.push('green.inputmask4angular');
//dependents.push('ngYoutubeEmbed');
dependents.push('pouchdb');
dependents.push('app.filters');
dependents.push('app.directives');
dependents.push('app.services');
dependents.push('app.controllers');
var app = angular.module('app', dependents), lodash = _;


app.config(function ($httpProvider) {
    $httpProvider.interceptors.push('generalHttpInterceptor');
});

app.config(function ($mdThemingProvider) {
    //var theme = $mdThemingProvider.theme('default');
    //theme.primaryPalette('your-primary-color');
    //theme.accentPalette('your-accent-color');
    //theme.warnPalette('your-warn-color');
    //theme.backgroundPalette('your-background-color');

    $mdThemingProvider.theme("info-toast");
    $mdThemingProvider.theme("success-toast");
    $mdThemingProvider.theme("warning-toast");
    $mdThemingProvider.theme("error-toast");
});

function appConfig($routeProvider, $locationProvider) {
    $routeProvider.when('/', {
        redirectTo: '/home'
    });

    $routeProvider.when('/home', {
        templateUrl: 'app/home/homeTemplate.html',
        controller: 'homeController as vm'
    });

    $routeProvider.when('/message', {
        templateUrl: 'app/zgeneral/messageTemplate.html',
        controller: 'messageController as vm'
    });

    $routeProvider.when('/not-found', {
        templateUrl: 'app/zgeneral/notFoundTemplate.html'
    });

    $routeProvider.otherwise({
        redirectTo: '/not-found'
    });
};
app.config(appConfig);

function appInit($log, $rootScope, $location, $sessionStorage, $mdSidenav) {
    $log.info('Initialization started...');

    $log.info('Initialization finished...');
}
app.run(['$log', '$rootScope', '$location', '$sessionStorage', '$mdSidenav', appInit]);
