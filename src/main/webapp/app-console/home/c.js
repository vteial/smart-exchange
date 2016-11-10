function homeController($log, $rootScope, $scope, $http, $filter) {
    var cmpId = 'homeController', cmpName = 'Home';
    $rootScope.viewName = cmpName;
    $log.debug(cmpId + '...');

    var vm = this;
    vm.uiState = {isReady: false};

    vm.message = '';

    vm.execute = function (path) {
        $http.get(path).success(function (response) {
            // $log.info(response);
            vm.message = response;
        })
    };

    vm.branchsJson = [{
        id: '-',
        name: '<Select JSON File>'
    }];
    vm.branchJson = vm.branchsJson[0];

    $http.get('app-console/json/masters.json').success(function (response) {
        // $log.info(response);
        _.forEach(response, function (item) {
            vm.branchsJson.push(item);
        })
    });

    vm.onBranchChange = function () {
        var branchJson = vm.branchJson;
        if (branchJson.id == '-') {
            return;
        }
        //$log.info(branchJson);

        var path = 'app-console/json/' + branchJson.id;
        $http.get(path).success(function (response) {
            //$log.info(response);
            branchJson.value = response;
            branchJson.json = $filter('json')(branchJson.value, '    ');
            //$log.info(branchJson.json);
        });
    };

    vm.saveMaster = function () {
        createBranch(_.clone(vm.branchJson.value, true));
    };

    function createBranch(branch) {
        var products = branch.products;
        delete branch['products'];
        //$log.info(products);

        var employees = branch.employees;
        delete branch['employees'];
        //$log.info(employees);

        var customers = branch.customers;
        delete branch['customers'];
        //$log.info(customers);

        vm.message = [];

        $log.info('creating branch started...');
        //$log.info(branch);
        var f = $http.post('console/branchs/branch', branch);
        f.success(function (response) {
            vm.message.push(response.message);
            $log.info('creating branch finished...');
            branch.id = response.data.id;
            branch.products = products;
            branch.employees = employees;
            branch.customers = customers;
            createBranchProducts(branch);
        });
    }

    function createBranchProducts(branch) {
        $log.info('creating branch products started...');
        //$log.info(branch.products);
        var path = 'console/branchs/branch/' + branch.id + '/products';
        var f = $http.post(path, branch.products);
        f.success(function (response) {
            vm.message.push(response.message);
            $log.info('creating branch products finished...');
            createBranchEmployees(branch);
        });
    }

    function createBranchEmployees(branch) {
        $log.info('creating branch employees started...');
        //$log.info(branch.employees);
        var path = 'console/branchs/branch/' + branch.id + '/employees';
        var f = $http.post(path, branch.employees);
        f.success(function (response) {
            vm.message.push(response.message);
            $log.info('creating branch employees finished...');
            createBranchCustomers(branch);
        });
    }

    function createBranchCustomers(branch) {
        $log.info('creating branch customers started...');
        //$log.info(branch.customers);
        var path = 'console/branchs/branch/' + branch.id + '/customers'
        var f = $http.post(path, branch.customers);
        f.success(function (response) {
            vm.message.push(response.message);
            $log.info('creating branch customers finished...');
        });
    }
}
appControllers.controller('homeController', homeController);

function settingsController($log, $rootScope, $scope, $location) {
    var cmpId = 'settingsController', cmpName = 'Settings';
    $rootScope.viewName = cmpName;
    $log.debug(cmpId + '...');

    var vm = this;
    vm.uiState = {isReady: false};
}
appControllers.controller('settingsController', settingsController);

function branchListController($log, $rootScope, $scope, wydNotifyService, sessionService) {
    var cmpId = 'branchListController', cmpName = 'Branchs';
    $rootScope.viewName = cmpName;
    $log.debug(cmpId + '...');

    var vm = this;
    vm.uiState = {isReady: false};

    vm.items = sessionService.branchs;

    vm.reset = function (id) {
        var s = 'Are you sure to reset the shop?';
        var params = {
            title: 'Confirm',
            text: s,
            type: 'warning',
            showCancelButton: true,
            confirmButtonText: 'Yes',
            cancelButtonText: 'No',
        };
        var callback = function () {
            sessionService.resetBranch(id);
        };
        wydNotifyService.sweet.show(params, callback);
    };

    vm.remove = function (id) {
        var s = 'Are you sure to delete the shop?';
        var params = {
            title: 'Confirm',
            text: s,
            type: 'warning',
            showCancelButton: true,
            confirmButtonText: 'Yes',
            cancelButtonText: 'No',
        };
        var callback = function () {
            sessionService.deleteBranch(id);
        };
        wydNotifyService.sweet.show(params, callback);
    };

    vm.refresh = function () {
        sessionService.getBranchs();
    };

    vm.refresh();
}
appControllers.controller('branchListController', branchListController);


