/**
 * Created by udantha on 8/22/16.
 */
angular.module('expressCoreConfirm', ['ui.bootstrap.modal'])
    .controller('expConfirmModalController', ["$scope", "$uibModalInstance", "data", function ($scope, $uibModalInstance, data) {
        $scope.data = angular.copy(data);

        $scope.ok = function (closeMessage) {
            $uibModalInstance.close(closeMessage);
        };

        $scope.cancel = function (dismissMessage) {
            if (angular.isUndefined(dismissMessage)) {
                dismissMessage = 'cancel';
            }
            $uibModalInstance.dismiss(dismissMessage);
        };

    }])
    .value('$expConfirmModalDefaults', {
        template: '<div class="modal-header"><h3 class="modal-title">{{data.title}}</h3></div>' +
        '<div class="modal-body">{{data.text}}</div>' +
        '<div class="modal-footer">' +
        '<button class="btn btn-primary" ng-click="ok()">{{data.ok}}</button>' +
        '<button class="btn btn-default" ng-click="cancel()">{{data.cancel}}</button>' +
        '</div>',
        controller: 'expConfirmModalController',
        defaultLabels: {
            title: 'Confirm',
            ok: 'OK',
            cancel: 'Cancel'
        },
        backdrop: 'static', //modal backdrop cannot be close on outide click
        keyboard: false, //modal not closed on 'esc' key press
        additionalTemplates: {}
    })
    .factory('$expConfirm', ["$uibModal", "$expConfirmModalDefaults", function ($uibModal, $expConfirmModalDefaults) {
        return function (data, settings) {
            var defaults = angular.copy($expConfirmModalDefaults);
            settings = angular.extend(defaults, (settings || {}));

            data = angular.extend({}, settings.defaultLabels, data || {});

            if(data.templateName){
                var customTemplateDefinition = settings.additionalTemplates[data.templateName];
                if(customTemplateDefinition != undefined) {
                    settings.template = customTemplateDefinition.template;
                    settings.templateUrl = customTemplateDefinition.templateUrl;
                }
            }

            if ('templateUrl' in settings && 'template' in settings) {
                delete settings.template;
            }

            settings.resolve = {
                data: function () {
                    return data;
                }
            };

            return $uibModal.open(settings).result;
        };
    }])
    .directive('expConfirm', ["$expConfirm", "$timeout", function ($confirm, $timeout) {
        return {
            priority: 1,
            restrict: 'A',
            scope: {
                confirmIf: "=",
                ngClick: '&',
                confirm: '@',
                confirmSettings: "=",
                confirmTemplateName: "@",
                confirmTitle: '@',
                confirmOk: '@',
                confirmCancel: '@'
            },
            link: function (scope, element, attrs) {

                function onSuccess() {
                    var rawEl = element[0];
                    if (["checkbox", "radio"].indexOf(rawEl.type) != -1) {
                        var model = element.data('$ngModelController');
                        if (model) {
                            model.$setViewValue(!rawEl.checked);
                            model.$render();
                        } else {
                            rawEl.checked = !rawEl.checked;
                        }
                    }
                    scope.ngClick();
                }

                element.unbind("click").bind("click", function ($event) {

                    $event.preventDefault();

                    $timeout(function() {

                        if (angular.isUndefined(scope.confirmIf) || scope.confirmIf) {
                            var data = {text: scope.confirm};
                            if (scope.confirmTitle) {
                                data.title = scope.confirmTitle;
                            }
                            if (scope.confirmOk) {
                                data.ok = scope.confirmOk;
                            }
                            if (scope.confirmCancel) {
                                data.cancel = scope.confirmCancel;
                            }
                            if (scope.confirmTemplateName){
                                data.templateName = scope.confirmTemplateName;
                            }
                            $confirm(data, scope.confirmSettings || {}).then(onSuccess);
                        } else {
                            scope.$apply(onSuccess);
                        }

                    });

                });

            }
        }
    }]);