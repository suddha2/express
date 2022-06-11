/**
 * Created by udantha on 4/16/14.
 * Preloader tag
 */
expressCoreApp.directive('loadingContainer', function () {
    return {
        restrict: 'A',
        scope: false,
        link: function(scope, element, attrs) {
            var loadingLayer = angular.element('<div class="loading"></div>');
            var setDimensions = function(parentEl, targetEl){
                var pOffset = parentEl.position(),
                    css = {
                        top: pOffset.top,
                        left: pOffset.left,
                        width: parentEl.width(),
                        height: parentEl.height(),
                        position: 'absolute'
                    };
                //set dimensions
                targetEl.css(css);
            };

            element.append(loadingLayer);
            element.addClass('loading-container');
            scope.$watch(attrs.loadingContainer, function(value) {
                loadingLayer.toggleClass('ng-hide', !value);
                //set main dimensions
                setDimensions(element, loadingLayer);
            });
            //set main dimensions
            //setDimensions(element, loadingLayer);
        }
    };
});