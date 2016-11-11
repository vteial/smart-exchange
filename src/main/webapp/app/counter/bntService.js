function bntService($log, $q, wydNotifyService, sessionService, dataService, $http) {
    var service = {}, model = {}, initSize = 1, defaultCustomer = {
        id: 0,
    };

    service.model = model;

    service.init = function () {
        $log.debug("bntService initialize started...")

        model.id = 0;
        model.forUserId = '';

        model.totalAmount = 0;
        model.totalAmountLabel = '';
        model.customerAmount = '';
        model.customerAmountRaw = 0;
        model.customerAmountLabel = '';
        model.balanceAmount = 0;
        model.balanceAmountLabel = '';

        model.trans = [];
        addTransaction(initSize);
        model.curTranIndex = 0;
        model.curTran = model.trans[0];

        $log.debug("bntService initialize finished...")
    };

    function addTransaction(times) {
        var trans = model.trans;
        for (var i = 0; i < times; i++) {
            var tran = {
                type: 'buy',
                accountId: '',
                unit: '',
                unitRaw: 0,
                rate: '',
                rateRaw: 0,
                profitRate: '',
                profitRateRaw: '',
                amount: '',
                profitAmmount: '',
                revertAmount: '',
                message: ''
            };
            trans.push(tran);
            model.curTran = tran;
            model.curTranIndex = trans.length - 1;
            service.computeTotalAmount();
        }
    }

    service.newTransaction = function () {
        if (model.id > 0) {
            service.init();
        } else {
            addTransaction(initSize);
        }
    };

    service.removeTransaction = function (index) {
        model.trans.splice(index, 1);
        if (index === 0) {
            service.newTransaction();
            return;
        }
        if (index === model.trans.length) {
            index--;
        }
        model.curTranIndex = index;
        model.curTran = model.trans[index];
        service.computeTotalAmount();
    };

    service.removeAllTransactions = function () {
        model.trans.length = 0;
        model.curTranIndex = -1;
        model.curTran = {};
        service.newTransaction();
    };

    service.onTransactionSelect = function (index) {
        if (model.curTranIndex === index) {
            return;
        }
        model.curTranIndex = index;
        model.curTran = model.trans[model.curTranIndex];
    }

    service.onTransactionStock = function (tran) {
        service.onTransactionType(tran);
    };

    service.onTransactionType = function (tran) {
        if (tran.type != '') {
            if (tran.type == 'buy') {
                tran.rate = tran.account.product.buyRate + '';
            } else {
                tran.rate = tran.account.product.sellRate + '';
            }
            service.onTransactionRate(tran);
        }
    };

    service.onTransactionUnit = function (tran) {
        var unit = tran.unit;
        if (unit == '' || _.isUndefined(unit)) {
            return;
        }
        if (!_.isNumber(unit)) {
            unit = unit.split(',').join('')
            unit = parseFloat(unit);
        }
        tran.unitRaw = unit;
        service.computeTransactionAmount(tran);
    };

    service.onTransactionRate = function (tran) {
        var rate = tran.rate;
        if (rate == '' || _.isUndefined(rate)) {
            return;
        }
        if (!_.isNumber(rate)) {
            rate = rate.split(',').join('')
            rate = parseFloat(rate);
        }
        tran.rateRaw = rate;
        service.computeTransactionAmount(tran);
    };

    service.computeTransactionAmount = function (tran) {
        var amount = tran.unitRaw * (tran.rateRaw / tran.account.product.baseUnit);
        if (tran.type == 'buy') {
            amount *= -1;
        }
        tran.amount = amount;
        service.computeTotalAmount();
    };

    service.onTransactionProfitRate = function (tran) {
        var rate = tran.profitRate;
        if (rate == '' || _.isUndefined(rate)) {
            return;
        }
        if (!_.isNumber(rate)) {
            rate = rate.split(',').join('')
            rate = parseFloat(rate);
        }
        tran.profitRateRaw = rate;
        service.computeTransactionProfitAmount(tran);
    };

    service.computeTransactionProfitAmount = function (tran) {
        var amount = tran.unitRaw * (tran.profitRateRaw / tran.account.product.baseUnit);
        if (tran.type == 'buy') {
            amount *= -1;
        }
        tran.profitAmount = amount;
        //service.computeProfitTotalAmount();
    };

    service.computeTotalAmount = function () {
        var trans = model.trans;
        var totalAmount = 0, i = 0, amount = 0;
        for (i = 0; i < trans.length; i++) {
            amount = trans[i].amount;
            // if (trans[i].type == 'buy') {
            // amount *= -1;
            // }
            totalAmount += amount;
        }
        model.totalAmount = totalAmount;
        if (totalAmount < 0) {
            model.totalAmountLabel = 'pay';
            model.customerAmountLabel = 'pay';
        } else {
            model.totalAmountLabel = 'receive';
            model.customerAmountLabel = 'receive';
        }
        //service.onCustomerAmount();
    };

    service.onCustomerAmount = function () {
        var rawValue = model.customerAmount, totalAmount = model.totalAmount;
        if (rawValue == '' || rawValue == '0') {
            model.balanceAmount = 0;
            return;
        }
        rawValue = rawValue.split(',').join('');
        rawValue = parseFloat(rawValue);
        model.customerAmountRaw = rawValue;
        if (totalAmount < 0) {
            totalAmount *= -1;
        }
        var balanceAmount = totalAmount - rawValue;
        totalAmount = model.totalAmount;
        if (totalAmount < 0) {
            balanceAmount *= -1;
        }
        model.balanceAmount = balanceAmount;

        if (balanceAmount < 0) {
            model.balanceAmountLabel = 'Pay';
        } else {
            model.balanceAmountLabel = 'Receive';
        }
    };

    service.onRevertAmount = function () {
        var revertAmount = model.revertAmount;
        if (revertAmount == '' || _.isUndefined(revertAmount)) {
            return;
        }
        if (!_.isNumber(revertAmount)) {
            revertAmount = revertAmount.split(',').join('')
            revertAmount = parseFloat(revertAmount);
        }
        model.revertAmountRaw = revertAmount;
        var tran = model.curTran;
        var v0 = tran.rateRaw / tran.account.product.baseUnit;
        var v1 = revertAmount / v0;
        var v2 = v1 % tran.account.product.denominator;
        var v3 = v1 - v2
        tran.unit = v3 + '';
        service.onTransactionUnit(tran);
    };

    service.validateTransaction = function (tran) {
        tran.message = '';
        if (!tran.account.id) {
            tran.message = 'Missing Product! Please select product...';
            return;
        }
        if (tran.type === '') {
            tran.message = 'Missing Type! Please select type...';
            return;
        }
        if (tran.unit == '' || tran.unitRaw <= 0) {
            tran.message = 'Quantity should be greater than 0';
            return;
        }
        if (tran.rate == '' || tran.rateRaw <= 0) {
            tran.message = 'Rate should be greater than 0';
            return;
        }
        // if (tran.type === service.TRAN_TYPE_BUY) {
        // if (tran.account.product.buyPercentageRate <= tran.rateRaw) {
        // var s = "Rate is more than ";
        // s += tran.account.product.buyPercentageRate;
        // tran.message = s;
        // }
        // } else {
        // if (tran.account.product.sellPercentageRate >= tran.rateRaw) {
        // var s = "Rate is less than ";
        // s += tran.account.product.sellPercentageRate;
        // tran.message = s;
        // }
        // }
    };

    service.saveModelAsTransaction = function () {
        $log.debug('saveModelAsTransaction started...');

        if (model.forUser.type == 'employee') {
            var message = 'Transaction can\'t be done for employees...';
            wydNotifyService.showWarning(message, true);
            return;
        }

        $log.debug("model before process...")
        $log.debug(model);

        var reqModel = {
            forUserId: model.forUser.id,
            amount: model.customerAmountRaw,
            balanceAmount: model.balanceAmount,
            trans: []
        }, reqTran = null, totalSaleAmount = 0, rowIds = [];

        if (model.forUser.type == 'dealer') {
            reqModel.category = 'dealer'
        } else {
            reqModel.category = 'customer'
        }

        for (var i = 0; i < model.trans.length; i++) {
            var tran = model.trans[i]
            service.validateTransaction(tran);
            if (tran.message != '') {
                rowIds.push(i + 1);
                continue;
            }
            reqTran = {
                category: reqModel.category,
                accountId: tran.account.id,
                type: tran.type,
                unit: tran.unitRaw,
                rate: tran.rateRaw,
                profitRate: tran.profitRateRaw
            };
            reqModel.trans.push(reqTran);
        }

        if (rowIds.length > 0) {
            var msg = "Row's " + rowIds.join(', ') + ' has issues...';
            wydNotifyService.showError(msg, true);
            return;
        }

        var totalAmount = model.totalAmount;
        if (totalAmount < 0) {
            totalAmount *= -1;
        }
        // if (model.forUser.firstName == 'guest'
        //     && model.customerAmountRaw < totalAmount) {
        //     var msg = 'Please provide the amount to ';
        //     msg += model.customerAmountLabel
        //     msg += ', which should be greater then or equal to '
        //     msg += totalAmount;
        //     wydNotifyService.showError(msg, true);
        //     return;
        // }
        //
        // if (model.customerAmountRaw === 0) {
        //     var s = 'Are you sure to proceed without the amount to ';
        //     s += model.customerAmountLabel + '?';
        //     var params = {
        //         title: 'Confirm',
        //         text: s,
        //         type: 'warning',
        //         showCancelButton: true,
        //         confirmButtonText: 'Yes',
        //         cancelButtonText: 'No',
        //     };
        //     var callback = function () {
        //         submit(reqModel);
        //     };
        //     wydNotifyService.sweet.show(params, callback);
        // } else {
        //     submit(reqModel);
        // }

        submit(reqModel);

        $log.debug("model before post...")
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
            $log.debug('saveModelAsTransaction finished...');
        });
    }

    function success(resModel, message) {
        $log.debug("model after post...");
        $log.debug(message);
        $log.debug(resModel);

        wydNotifyService.showSuccess(message, true);
        model.id = resModel.id;

        // _.forEach(resmodel.trans, function (tran) {
        //     sessionService.updateAccount(tran.account);
        // });
        // sessionService.computeStockWorth();
    }

    function fail(resModel, message) {
        $log.debug("model after post...")
        $log.debug(message);
        $log.debug(resModel);
        wydNotifyService.showError(message, true);
    }

    return service;
}
appServices.factory('bntService', bntService);