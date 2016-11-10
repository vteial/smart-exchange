appServices.factory('wydNotifyService', function ($log, $timeout, ngNotify, sweet) {

    ngNotify.config({
        theme: 'pure',
        position: 'top',
        duration: 3000,
        type: 'info',
        sticky: false
    });

    return {
        sweet: sweet,

        info: function (message, clear) {
            if (clear) {
                ngNotify.dismiss();
            }
            ngNotify.set(message, {
                type: 'info',
                duration: 4000
            });
        },

        success: function (message, clear) {
            if (clear) {
                ngNotify.dismiss();
            }
            ngNotify.set(message, {
                type: 'success',
                duration: 3000
            });
        },

        warning: function (message, clear) {
            if (clear) {
                ngNotify.dismiss();
            }
            ngNotify.set(message, {
                type: 'warn',
                duration: 2000
            });
        },

        error: function (message, clear) {
            if (clear) {
                ngNotify.dismiss();
            }
            ngNotify.set(message, {
                sticky: true,
                type: 'error',
                duration: 5000
            });
        },

        hide: function () {
            ngNotify.dismiss();
        }
    };
});