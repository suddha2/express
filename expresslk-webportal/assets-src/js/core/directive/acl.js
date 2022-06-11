/**
 * Created by udantha on 5/8/16.
 */
angular.module("expressCore")
    .directive("aclFragment", ['$window', function ($window) {
        return {
            restrict: 'A',
            link: function ($scope, $element, $attrs) {
                //this is tail end key of Fragment.Name object
                var fragmentName = Fragment.Name[$attrs.hasPermission];
                //this is tail end key of the object ACL.Fragment
                var operation = ACL.Fragment[$attrs.aclFragment];
                
                //get actual values
                var hasPermission = ACL.fragmentHasPermission(operation, fragmentName);
                //hide the element if doesn't have permission
                if(!hasPermission){
                    $element.hide();
                }else {
                    $element.show();
                }
            }
        };
    }]);