/**
 * Created by udantha on 10/18/16.
 */
expressFrontApp.directive('searchDatepicker', ['$compile', '$animate', function ($compile, $animate) {
    return {
        restrict: 'EA',
        replace: true,
        template: '' +
        '<input type="text"' +
        'datepicker-options="dateOptions"' +
        'uib-datepicker-popup="yyyy-MM-dd"' +
        'ng-click="isOpened=!isOpened"' +
        'readonly="readonly"' +
        'min-date="minDate"' +
        'max-date="maxDate"' +
        'is-open="isOpened"' +
        'show-button-bar="false"/>',
        controller: ['$scope', '$element',
            function ($scope, $element) {
                var curDate = new Date();
                //date picker settings
                $scope.minDate = curDate;
                $scope.maxDate = (new Date()).setMonth(curDate.getMonth() + 2);

                //datepicker options
                $scope.dateOptions = {
                    showWeeks: 'false'
                };
            }],
        link: function ($scope, element, attrs) {

        }
    }
}]);