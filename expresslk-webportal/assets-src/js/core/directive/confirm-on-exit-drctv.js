/**
 * Created by udantha on 11/18/15.
 */
angular.module("expressCore")
    .directive('confirmOnExit', function() {
    return {
        link: function($scope, elem, attrs) {
            var message = attrs.coeMessage;
            window.onbeforeunload = function(){
                if ($scope.$eval(attrs.coeIsDirty)) {
                    return message;
                }
            };

            $scope.$on('$locationChangeStart', function(event, next, current) {
                if ($scope.$eval(attrs.coeIsDirty)) {
                    if(!confirm(message)) {
                        event.preventDefault();
                    }
                }
            });
        }
    };
});