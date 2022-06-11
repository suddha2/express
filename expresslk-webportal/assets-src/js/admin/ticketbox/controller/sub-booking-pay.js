/**
 * Created by udantha on 1/31/16.
 */
expressAdminApp.controller('subBookingPay', ['$scope',
    function ($scope) {

        //setup tab data for diff pay
        $scope.diffPayMaxDate = new Date();
        $scope.$watchCollection('journeyData', function (newVal, oldVal) {
            if(newVal){
                $scope.diffPayMaxDate = moment($scope.journeyData.departure).subtract(1, 'days');
            }
        });
    }]);