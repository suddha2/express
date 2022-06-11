/**
 * Created by udantha on 2/7/16.
 */
expressAdminApp
    .directive("stickyholder", ['$window', function ($window) {
        return {
            link: function (scope, element, attrs) {
                var placeHolder = $('<div style="width: 100%;"></div>');
                placeHolder.prependTo('body');

                scope.$watch(attrs.hideSticky, function (newTime) {
                    if(newTime){
                        //hide sticky
                        element.addClass('hidden');
                        //add padding to body
                        placeHolder.hide();
                    }else{
                        element.removeClass('hidden');
                        placeHolder.show().height(element.outerHeight());
                    }
                });
                //register functions on destroy
                scope.$on('$destroy', function(){
                    placeHolder.remove();
                });
            }
        };
    }]);