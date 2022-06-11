/**
 * Created by udantha on 12/30/16.
 */
expressFrontApp.directive('paymentIframe', ['$window', function ($window) {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            var oIframe = $(element).find('iframe');

            //check if the current iframe url is from current domain

            //if so, enable parent url change
            //else, prompt message saying you need cannot abort

            // oIframe.on('load', function () {
            //     console.log($window.location.hostname)
            //     console.log(oIframe[0].contentWindow.location)
            // });
            //
            // function onUnload() {
            //     console.log($window.location.hostname)
            //     console.log(oIframe[0].contentWindow.location)
            //     //check if this is not the origin page.
            //     //warn users if they are moving away while on IPG
            //     if($window.location.hostname != oIframe[0].contentWindow.location.hostname){
            //         return confirm('Going back will clear the current payment! \n' +
            //             'If you have already made the payment, do not reload or go away from the page. ');
            //     }
            // }
            //
            // $(window).on('unload.payiframe', onUnload);
            // $(oIframe[0].contentWindow).on('unload.payiframe', onUnload);
            // //unbind events on destroy
            // scope.$on("$destroy",function() {
            //     console.log('delete events');
            //     $(window).off('unload.payiframe');
            //     $(oIframe[0].contentWindow).off('unload.payiframe');
            // });
        }
    }
}]);