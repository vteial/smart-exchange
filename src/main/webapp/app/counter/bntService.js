function bntService($log, $q, wydNotifyService, sessionService, dataService, $http) {
    var service = {}, model = {};

    service.model = model;

    service.init = function () {
        $log.debug("bntService initialize started...");

        model.id = 0;

        model.fromCustomerId = 0;
        model.tranAccount = null;
        model.tranType = 'buy';
        model.tranUnit = '';
        model.tranUnitRaw = 0;
        model.tranRate = '';
        model.tranRateRaw = 0;
        model.tranProfitRate = '';
        model.tranProfitRateRaw = 0;
        model.tranAmount = '';
        model.tranAmountAbs = 0
        model.tranProfitAmount = '';
        model.tranProfitAmountAbs = 0;

        model.dstCurrencyAccountId = 0

        model.transferCustomerId = 0;
        model.transferAmount = '';
        model.transferAmountRaw = 0;
        model.transfers = [];

        $log.debug("bntService initialize finished...");
    };

    service.onFromCustomer = function () {
        var customer = dataService.customersMap[model.fromCustomerId];
        if (customer) {
            model.fromCustomer = customer;
        }
    };

    service.onTranUnit = function () {
        var unit = model.tranUnit;
        if (unit == '' || _.isUndefined(unit)) {
            return;
        }
        if (!_.isNumber(unit)) {
            unit = unit.split(',').join('')
            unit = parseFloat(unit);
        }
        model.tranUnitRaw = unit;
        service.computeTranAmount();
    };

    service.onTranRate = function () {
        var rate = model.tranRate;
        if (rate == '' || _.isUndefined(rate)) {
            return;
        }
        if (!_.isNumber(rate)) {
            rate = rate.split(',').join('')
            rate = parseFloat(rate);
        }
        model.tranRateRaw = rate;
        service.computeTranAmount();
    };

    service.computeTranAmount = function () {
        var amount = model.tranUnitRaw * (model.tranRateRaw / model.tranAccount.product.baseUnit);
        model.tranAmountAbs = amount;
        if (model.tranType == 'buy') {
            amount *= -1;
        }
        model.tranAmount = amount;
    };

    service.onTranProfitRate = function () {
        var rate = model.tranProfitRate;
        if (rate == '' || _.isUndefined(rate)) {
            return;
        }
        if (!_.isNumber(rate)) {
            rate = rate.split(',').join('')
            rate = parseFloat(rate);
        }
        model.tranProfitRateRaw = rate;
        service.computeTranProfitAmount();
    };

    service.computeTranProfitAmount = function () {
        var amount = model.tranUnitRaw * (model.tranProfitRateRaw / model.tranAccount.product.baseUnit);
        model.tranProfitAmountAbs = amount;
        if (model.tranType == 'buy') {
            amount *= -1;
        }
        model.tranProfitAmount = amount;
        //service.computeProfitTotalAmount();
    };

    service.onTransferAmount = function () {
        var unit = model.transferAmount;
        if (unit == '' || _.isUndefined(unit)) {
            return;
        }
        if (!_.isNumber(unit)) {
            unit = unit.split(',').join('')
            unit = parseFloat(unit);
        }
        model.transferAmountRaw = unit;
    };

    service.addTransfer = function () {
        var customer = dataService.customersMap[model.transferCustomerId];
        var transfer = {
            id: (model.transfers.length + 1),
            customer: customer,
            amount: model.transferAmountRaw
        };
        model.transfers.push(transfer);
        model.transferCustomerId = 0;
        model.transferAmount = '';
        model.transferAmountRaw = 0;
    };

    service.execute = function () {
        $log.debug('execute started...');

        $log.debug("model before process...")
        $log.debug(model);

        var reqModel = {
            fromAccountId: model.fromCustomer.cashAccountId,
            tranAccountId: model.tranAccount.id,
            tranType: model.tranType,
            tranUnit: model.tranUnitRaw,
            tranRate: model.tranRateRaw,
            tranProfitRate: model.tranProfitRateRaw,
            transfers: []
        }, reqTransfer = null;
        for (var i = 0; i < model.transfers.length; i++) {
            var transfer = model.transfers[i]
            reqTransfer = {
                accountId: transfer.customer.cashAccountId,
                unit: transfer.amount
            };
            reqModel.transfers.push(reqTransfer);
        }
        submit(reqModel);

        $log.debug("model before post...");
        $log.debug(reqModel);
    };

    function submit(reqModel) {
        var path = '/sessions/buy-and-transfer';
        $http.post(path, reqModel).success(function (response) {
            // $log.debug(response);
            if (response.type === 0) {
                success(response.data, response.message);
            } else {
                fail(response.data, response.message);
            }
            $log.debug('execute finished...');
        });
    }

    function success(resModel, message) {
        $log.debug("model after post...");
        $log.debug(message);
        $log.debug(resModel);

        wydNotifyService.showSuccess(message, true);
        model.id = resModel.id;
    }

    function fail(resModel, message) {
        $log.debug("model after post...")
        $log.debug(message);
        $log.debug(resModel);
        wydNotifyService.showError(message, true);
    }

    service.init();

    return service;
}
appServices.factory('bntService', bntService);