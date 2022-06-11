/**
 * Created by udantha on 10/22/14.
 */

expressAdminApp.controller('addUser', ['$scope', '$http',
    function ($scope, $http) {

        $scope.form = {
        };

        //open datepicker
        $scope.open = function($event) {
            $event.preventDefault();
            $event.stopPropagation();

            $scope.opened = true;
        };

        $scope.save = function() {
            $http.post('/admin-panel/user/addajax', $scope.form)
                .success(function (data) {
                    if (data.success) {
                        $scope.form = {};
                        $scope.Global.success.hasSuccess = true;
                        $scope.Global.success.message = "Saved successfully";
                    }else{
                        $scope.Global.error.hasError = true;
                        $scope.Global.error.message = "Save failed. Please try again.";
                    }
                });
        };

    }]);