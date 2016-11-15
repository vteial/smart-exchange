function bntController($log, $rootScope, $scope, wydNotifyService, wydFocusService, dataService, bntService) {
    var cmpId = 'bntController', cmpName = 'Purchase And Transfer';
    $log.debug(cmpId + '...');
    $rootScope.viewName = cmpName;

    var vm = this;
    vm.uiState = {isReady: false, isBlocked: false};

    vm.model = bntService.model;

    vm.onFromCustomer = bntService.onFromCustomer;

    vm.onTranUnit = bntService.onTranUnit;

    vm.onTranRate = bntService.onTranRate;

    vm.onTranProfitRate = bntService.onTranProfitRate;

    vm.onTransferAmount = bntService.onTransferAmount;

    vm.addToCustomer = bntService.addTransfer;

    vm.proceedToStepOne = function () {
        vm.isStepOne = true;
        vm.isStepTwo = false;
    };

    vm.proceedToStepTwo = function () {
        vm.isStepOne = false;
        vm.isStepTwo = true;
    };

    vm.execute = bntService.execute;

    vm.init = function(){
        var tranAccount = bntService.model.tranAccount;
        bntService.init();
        bntService.model.tranAccount = tranAccount;
        vm.isStepOne = true;
        vm.isStepTwo = false;
        wydFocusService('fromCustomer');
    };

    function init() {
        vm.isStepOne = true;
        vm.isStepTwo = false;

        var fromCustomers = [];
        if (dataService.customers && dataService.customers.length > 2) {
            //fromCustomers.push(dataService.customers[0]);
            fromCustomers.push(dataService.customers[1]);
            fromCustomers.push(dataService.customers[2]);
        }
        vm.fromCustomers = fromCustomers;
        //$log.debug(vm.fromCustomers);

        var toCustomers = [];
        if (dataService.customers && dataService.customers.length > 5) {
            toCustomers.push(dataService.customers[3]);
            toCustomers.push(dataService.customers[4]);
            toCustomers.push(dataService.customers[5]);
        }
        vm.toCustomers = toCustomers;
        //$log.debug(vm.toCustomers);

        var stocks = _.filter(dataService.accounts, function (item) {
            return item.type == 'product' && item.aliasName == 'AED';
        });
        vm.model.tranAccount = stocks[0];
        $log.debug(vm.model.tranAccount);

        vm.stocks = _.filter(dataService.accounts, function (item) {
            return item.type == 'product' && (item.aliasName == 'BDD' || item.aliasName == 'INR');
        });
        //$log.debug(vm.stocks);

        wydFocusService('fromCustomer');
    }

    $scope.$on('session:properties', function (event, data) {
        $log.debug(cmpId + ' on ' + event.name + ' started...');
        //bntService.init();
        init();
        $log.debug(cmpId + ' on ' + event.name + ' finished...');
    });

    init();
}
appControllers.controller('bntController', bntController);
