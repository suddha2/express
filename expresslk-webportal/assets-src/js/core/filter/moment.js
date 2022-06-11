/**
 * Created by udantha on 2/16/16.
 */
angular.module('expressCore')
    .filter('moment', function () {
        return function (dateString, format) {
            //create moment timezone wrapped date object
            var date = moment(dateString);
            if(format){
                return date.format(format);
            }

            return date;
        }
    });