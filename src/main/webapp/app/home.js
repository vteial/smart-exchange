
function rootController($log, $rootScope, $scope, sessionService, $window, $mdSidenav, $mdDialog) {
    $log.debug('rootController...');

    $scope.lodash = _;

    sessionService.properties();
    var sessionS = sessionService;
    $scope.sessionS = sessionS;

    $scope.toggleSideNavBar = function (menuId) {
        $mdSidenav(menuId).toggle();
    };

    $rootScope.$on('session:invalid', function (event, data) {
        $log.debug('session invalid started...');

        var alert = $mdDialog.alert({
            title: 'Invalid Session!',
            content: 'Your session is invalid. Please sign in and continue.',
            ok: 'Sign In'
        });
        $mdDialog.show(alert).finally(function () {
            alert = undefined;
            $log.info('redirecting to sign in...');
            $window.location = 'index.html#/sign-in';
        });

        $log.debug('session invalid finished...');
    });

    $scope.historyBack = function () {
        $window.history.back();
    };

    $scope.viewSource = function () {
        var s = 'view-source:' + $rootScope.currentViewSrcUrl;
        $log.info(s);
        $window.open(s);
    };
}
appControllers.controller('rootController', rootController);

var dependents = ['ngRoute', 'ngSanitize', 'ngMessages'];
dependents.push('ngMaterial');
dependents.push('ngStorage');
dependents.push('ngclipboard');
dependents.push('green.inputmask4angular');
dependents.push('ngMdIcons');
dependents.push('mdDataTable');
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

    $routeProvider.when('/counter', {
        templateUrl: 'app/counter/bnsTemplate.html',
        controller: 'bnsController as vm'
    });

    $routeProvider.when('/purchase-and-transfer', {
        templateUrl: 'app/counter/bntTemplate.html',
        controller: 'bntController as vm'
    });

    $routeProvider.when('/reports', {
        templateUrl: 'app/report/t.html',
        controller: 'reportController as vm'
    });

    $routeProvider.when('/profile', {
        templateUrl: 'app/session/profileTemplate.html',
        controller: 'profileController as vm'
    });

    $routeProvider.when('/sign-out', {
        templateUrl: 'app/session/signOutTemplate.html',
        controller: 'signOutController as vm'
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

    $rootScope.$on("$routeChangeStart", function (event, next, current) {
       //$rootScope.isLoading = true;

       // $log.info('Location : ', $location.path());
       var curLocPath = $location.path();
       // $log.info('Before Current Location : ', curLocPath);
       if (curLocPath == '/not-found' || curLocPath == '/sign-in'
           || curLocPath == '/sign-out') {
           return;
       }
       $sessionStorage.smartExchangeCLP = curLocPath;
       //$log.info('Stored Location : ', $sessionStorage.smartExchangeCLP);

       var srcUrl = $location.absUrl().indexOf('home');
       srcUrl = $location.absUrl().substring(0, srcUrl);
       srcUrl = srcUrl + next.templateUrl;
       $rootScope.currentViewSrcUrl = srcUrl;
       // $log.info('srcUrl = ' + srcUrl);

       if ($mdSidenav('left').isOpen()) {
           $mdSidenav('left').close();
       }
    });

    // $rootScope.$on("$routeChangeSuccess", function (event, next, current) {
    //    //$rootScope.isLoading = false;
    //
    //    // $log.info('Location : ', $location.path());
    //    var curLocPath = $location.path();
    //    // $log.info('After Current Location : ', curLocPath);
    //    //$mdSidenav('left').toggle();
    // });

    var path = $location.path();
    $log.info('Actual Location : ', path);
    if (path == '') {
       var spath = $sessionStorage.smartExchangeCLP;
       $log.info('Stored Location : ', spath);
       if (!spath || spath == '/sign-in' || spath == '/not-found') {
           path = '/home';
       }
       else {
           path = spath;
       }
    }
    $log.info('Computed Location : ', path);
    $location.path(path);

    $log.info('Initialization finished...');
}
app.run(['$log', '$rootScope', '$location', '$sessionStorage', '$mdSidenav', appInit]);
