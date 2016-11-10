function purchaseAndTransferController($rootScope, $log) {
    var cmpId = 'purchaseAndTransferController', cmpName = 'Counter';
    $log.debug(cmpId + '...');
    $rootScope.viewName = cmpName;

    var vm = this;
    vm.uiState = {isReady: false};
}
appControllers.controller('purchaseAndTransferController', purchaseAndTransferController);


