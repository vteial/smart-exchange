appDirectives.directive('wydCapitalize', function ($parse) {
    return {
        require: 'ngModel',
        link: function (scope, element, attrs, modelCtrl) {
            var transform = function (inputValue) {
                if (inputValue === undefined) {
                    inputValue = '';
                }
                var outputValue = _.capitalize(inputValue);
                if (outputValue !== inputValue) {
                    modelCtrl.$setViewValue(outputValue);
                    modelCtrl.$render();
                }
                return outputValue;
            }
            modelCtrl.$parsers.push(transform);
            transform($parse(attrs.ngModel)(scope));
        }
    };
});

appDirectives.directive('wydUpperCase', function ($parse) {
    return {
        require: 'ngModel',
        link: function (scope, element, attrs, modelCtrl) {
            var transform = function (inputValue) {
                if (inputValue === undefined) {
                    inputValue = '';
                }
                var outputValue = inputValue.toUpperCase();
                if (outputValue !== inputValue) {
                    modelCtrl.$setViewValue(outputValue);
                    modelCtrl.$render();
                }
                return outputValue;
            }
            modelCtrl.$parsers.push(transform);
            transform($parse(attrs.ngModel)(scope));
        }
    };
});

appDirectives.directive('wydLowerCase', function ($parse) {
    return {
        require: 'ngModel',
        link: function (scope, element, attrs, modelCtrl) {
            var transform = function (inputValue) {
                if (inputValue === undefined) {
                    inputValue = '';
                }
                var outputValue = inputValue.toLowerCase();
                if (outputValue !== inputValue) {
                    modelCtrl.$setViewValue(outputValue);
                    modelCtrl.$render();
                }
                return outputValue;
            }
            modelCtrl.$parsers.push(transform);
            transform($parse(attrs.ngModel)(scope));
        }
    };
});

appDirectives.directive('wydFocusOn', function () {
    return function (scope, elem, attr) {
        return scope.$on('wydFocusOn', function (e, name) {
            if (name === attr.wydFocusOn) {
                return elem[0].focus();
            }
        });
    };
});

function wydFocusService($rootScope, $timeout) {
    return function (name) {
        return $timeout(function () {
            return $rootScope.$broadcast('wydFocusOn', name);
        });
    };
}
appServices.factory('wydFocusService', ['$rootScope', '$timeout', wydFocusService]);

appDirectives.directive('historyBack', ['$window', function ($window) {
    return {
        restrict: 'A',
        link: function (scope, elem, attrs) {
            elem.on('click', function () {
                $window.history.back();
            });
        }
    };
}]);

/*
 <img wyd-holder="holder.js/200x200/text:?">
 <img wyd-holder data-src="holder.js/200x200/text:?">
 */
appDirectives.directive('wydHolder', [
    function () {
        return {
            link: function (scope, element, attrs) {
                if (attrs.holder) {
                    attrs.$set('data-src', attrs.holder);
                }
                Holder.run({images: element[0]});
            }
        };
    }
]);

appDirectives.directive('wydClipboard', function () {
    return {
        restrict: 'A',
        scope: {
            ngclipboardSuccess: '&',
            ngclipboardError: '&'
        },
        link: function (scope, element) {
            var clipboard = new Clipboard(element[0]);

            clipboard.on('success', function (e) {
                scope.ngclipboardSuccess({
                    e: e
                });
            });

            clipboard.on('error', function (e) {
                scope.ngclipboardError({
                    e: e
                });
            });
        }
    };
});

appDirectives.constant('YT_event', {
    STOP:            0,
    PLAY:            1,
    PAUSE:           2,
    STATUS_CHANGE:   3
});

/*

appDirectives.directive('youtube', function($window, YT_event) {
    return {
        restrict: "E",

        scope: {
            height: "@",
            width: "@",
            videoid: "@"
        },

        template: '<div></div>',

        link: function(scope, element, attrs, $rootScope) {

            var tag = document.createElement('script');
            tag.src = "https://www.youtube.com/iframe_api";

            var firstScriptTag = document.getElementsByTagName('script')[0];
            firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

            var player;

            $window.onYouTubeIframeAPIReady = function() {

                player = new YT.Player(element.children()[0], {
                    playerVars: {
                        autoplay: 0,
                        html5: 1,
                        theme: "light",
                        modesbranding: 0,
                        color: "white",
                        iv_load_policy: 3,
                        showinfo: 1,
                        controls: 1
                    },

                    height: scope.height,
                    width: scope.width,
                    videoId: scope.videoid,

                    events: {
                        'onStateChange': function(event) {

                            var message = {
                                event: YT_event.STATUS_CHANGE,
                                data: ""
                            };

                            switch(event.data) {
                                case YT.PlayerState.PLAYING:
                                    message.data = "PLAYING";
                                    break;
                                case YT.PlayerState.ENDED:
                                    message.data = "ENDED";
                                    break;
                                case YT.PlayerState.UNSTARTED:
                                    message.data = "NOT PLAYING";
                                    break;
                                case YT.PlayerState.PAUSED:
                                    message.data = "PAUSED";
                                    break;
                            }

                            scope.$apply(function() {
                                scope.$emit(message.event, message.data);
                            });
                        }
                    }
                });
            };

            scope.$watch('height + width', function(newValue, oldValue) {
                if (newValue == oldValue) {
                    return;
                }

                player.setSize(scope.width, scope.height);

            });

            scope.$watch('videoid', function(newValue, oldValue) {
                if (newValue == oldValue) {
                    return;
                }

//                player.cueVideoById(scope.videoid, 5, 'medium');

            });

            scope.$on(YT_event.STOP, function () {
                player.seekTo(0);
                player.stopVideo();
            });

            scope.$on(YT_event.PLAY, function () {
                player.playVideo();
            });

            scope.$on(YT_event.PAUSE, function () {
                player.pauseVideo();
            });

        }
    };
});

*/

function generalHttpInterceptor($log, $rootScope, $q, $window) {
    return {
        'request': function (config) {
            $rootScope.isProgress = true;
            var xUserId = $rootScope.xUserId || 'null';
            $log.info('xUserId = ' + xUserId);
            config.headers['X-UserId'] = xUserId;
            return config;
        },

        'requestError': function (rejection) {
            $rootScope.isProgress = false;
            $log.error(rejection);
            return rejection;
        },

        'response': function (response) {
            $rootScope.isProgress = false;
            return response;
        },

        'responseError': function (rejection) {
            $rootScope.isProgress = false;
            $log.error(rejection);
            if (rejection.status == 419) {
                $rootScope.$emit('session:invalid', 'Invalid session...');
            }
            return rejection;
        }
    };
}
appServices.factory('generalHttpInterceptor', generalHttpInterceptor);
