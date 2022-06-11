/**
 * Created by udantha on 8/31/16.
 */
angular.module("expressCore")
    .directive('uiSelectFocusInput', ['$timeout', function ($timeout) {
        return {
            require: 'ui-select',
            link: function (scope, $element, $attributes, selectController) {

                scope.$on('uis:activate', function () {
                    // Give it time to appear before focus
                    $timeout(focusInput, 5);
                    $timeout(focusInput, 100);
                    $timeout(focusInput, 500);
                    $timeout(focusInput, 800);
                });

                function focusInput() {
                    $($element).find(':text').focus();
                    //scope.$select.searchInput[0].focus();
                }

            }
        }
    }]);