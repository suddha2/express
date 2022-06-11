
/**
 * Created by udantha on 4/25/14.
 */
/**
 * Remove seats row
 */
expressFrontApp.directive('removeSeats', ['$compile', function ($compile) {
    return {
        link: function (scope, element, attrs) {
            //attach click handler to remove seats row
            scope.close = function(event){
                event.preventDefault();
                event.stopPropagation();
                element.closest('.booking-item-details').remove();
                //remove selected row classes
                $('.seats-displayed').removeClass('seats-displayed');
                //remove displayed class
                $('.seatlayout-displayed').removeClass('seatlayout-displayed');
            }

        }
    }
}]);