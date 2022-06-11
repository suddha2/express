/**
 * Created by udantha on 10/22/14.
 */
expressAdminApp.controller('viewUser', ['$scope', 'ngTableParams', '$http',
    function ($scope, ngTableParams, $http) {
        $scope.form = {};

        //load data from server
        $scope.tableParams = new ngTableParams({
            page: 1,            // show first page
            count: 10
        }, {
            total: 0,           // length of data
            getData: function ($defer, params) {
                // ajax request to api
                $http.post('/admin-panel/user/viewajax', $scope.form)
                    .success(function (data) {
                        if (data.success) {
                            var result = data.success;
                            // update table params
                            params.total(data.total);
                            //set new data
                            $defer.resolve(result.slice((params.page() - 1) * params.count(), params.page() * params.count()));
                        }
                    });
            }
        });

        $scope.search = function() {
            $scope.tableParams.reload();
        }

    }]);