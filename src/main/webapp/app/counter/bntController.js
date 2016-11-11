function bntController($log, $rootScope, $scope, wydNotifyService, wydFocusService, dataService, bntService) {
    var cmpId = 'bntController', cmpName = 'Counter';
    $log.debug(cmpId + '...');
    $rootScope.viewName = cmpName;

    var vm = this;
    vm.uiState = {isReady: false, isBlocked: false};

    vm.model = bntService.model;

    vm.onForCustomer = function() {
        $log.info('forUserId = ' + vm.model.forUserId);
        vm.model.forUser = dataService.customersMap[vm.model.forUserId];
        $log.info(vm.model.forUser);
    };

    vm.onTranStock = function() {
        var accountId = vm.model.trans[0].accountId;
        $log.info('accountId = ' + accountId);
        vm.model.trans[0].account = dataService.accountsMap[accountId];
        $log.info(vm.model.trans[0].account);
        bntService.onTransactionStock(vm.model.trans[0]);
    };

    vm.onTranUnit = function() {
        bntService.onTransactionUnit(vm.model.trans[0]);
    };

    vm.onTranRate = function() {
        bntService.onTransactionRate(vm.model.trans[0]);
    };

    vm.onTranProfitRate = function() {
        bntService.onTransactionProfitRate(vm.model.trans[0]);
    };

    vm.proceedToStepOne = function () {
        vm.isStepOne = true;
        vm.isStepTwo = false;
    };

    vm.proceedToStepTwo = function () {
        vm.isStepOne = false;
        vm.isStepTwo = true;
    };

    vm.execute = bntService.saveModelAsTransaction;

    vm.init = function(){
        bntService.init();
        vm.isStepOne = true;
        vm.isStepTwo = false;
        wydFocusService('forCustomer');
    };

    $scope.$on('session:properties', function (event, data) {
        $log.debug(cmpId + ' on ' + event.name + ' started...');
        bntService.init();
        init();
        $log.debug(cmpId + ' on ' + event.name + ' finished...');
    });

    function init() {
        vm.isStepOne = true;
        vm.isStepTwo = false;
        vm.forCustomers = dataService.customers;
        //$log.debug(vm.forCustomers);
        vm.stocks = _.filter(dataService.accounts, function(item) {
            return item.type == 'product';
        });
        //$log.debug((vm.stocks);
        wydFocusService('forCustomer');
    }

    init();

}
appControllers.controller('bntController', bntController);


