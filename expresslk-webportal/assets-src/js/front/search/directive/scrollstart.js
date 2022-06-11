/**
 * Created by udantha on 10/12/15.
 */
expressFrontApp.directive('scrollStart', ['$compile', '$window', function ($compile, $window) {
    return {
        link: function (scope, element, attrs) {
            angular.element($window).bind("scroll", function() {
                if (this.pageYOffset >= 50) {
                    //add scroll class
                    element.addClass('scrolling');
                } else {
                    element.removeClass('scrolling');
                }
               // scope.$apply();
            });
            angular.element($window).trigger("scroll");
        }
    }
}]);