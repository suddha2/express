/**
 * Created by udantha on 10/22/14.
 */
expressAdminApp.controller('addBus', ['$scope', '$http',
    function ($scope, $http) {

        $scope.form = {};


        $scope.save = function() {
            $http.post('/admin-panel/bus/addbusajax', $scope.form)
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