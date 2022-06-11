/**
 * Created by udantha on 11/7/14.
 */

//expressCoreApp.directive('validateOnBlur', function() {
//
//    /**
//     * After blur has occurred on a field, reapply change monitoring
//     * @param scope
//     * @param elm
//     * @param attr
//     * @param ngModelCtrl
//     */
//    var removeBlurMonitoring = function(scope, elm, attr, ngModelCtrl) {
//        elm.unbind('blur');
//
//        // Reapply regular monitoring for the field
//        elm.bind('keydown input change blur', function() {
//            scope.$apply(function() {
//                ngModelCtrl.$setViewValue(elm.val());
//            });
//        });
//    };
//
//    return {
//        restrict: 'A',
//        require: 'ngModel',
//        link: function(scope, elm, attr, ngModelCtrl) {
//            var allowedTypes = ['text', 'email', 'password'];
//            if (allowedTypes.indexOf(attr.type)  === -1) {
//                return;
//            }
//
//            // Unbind onchange event so that validation will be triggerd onblur
//            elm.unbind('input').unbind('keydown').unbind('change');
//
//            // Set OnBlur event listener
//            elm.bind('blur', function() {
//
//                scope.$apply(function() {
//                    ngModelCtrl.$setViewValue(elm.val());
//                });
//
//                removeBlurMonitoring(scope, elm, attr, ngModelCtrl);
//            });
//        }
//    };
//});