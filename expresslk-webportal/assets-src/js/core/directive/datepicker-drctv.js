/**
 * Created by udantha on 4/13/14.
 */
expressCoreApp.directive('attachDatepicker', function(){
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {
            var $picker = $(element);
            //$(element).toolbar(scope.$eval(attrs.toolbarTip));
            $picker.datepicker({
                dateFormat: 'yy-mm-dd'
            });
        }
    }
});