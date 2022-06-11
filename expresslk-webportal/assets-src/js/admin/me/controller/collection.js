/**
 * Created by udantha on 12/17/16.
 */
expressAdminApp.controller('meCollection', ['$scope', '$window','$filter','meReportService',
    function ($scope,$window, $filter, meReportService) {

        $scope.form = {
            from: new Date(),
            to: (moment().add(2, 'months').toDate())
        };
        $scope.collection = [];
        $scope.total = 0;
		$scope.total_res_fee = 0;
		$scope.total_bus_fare = 0;
		// Function to Get the latest time and reset totals when 'Generate' button click is performed 
		$scope.updateTime= function() {
			temp = new Date();
			
			$scope.form.from.setHours(temp.getHours());
			$scope.form.from.setMinutes(temp.getMinutes());
			$scope.form.to.setHours(temp.getHours());
			$scope.form.to.setMinutes(temp.getMinutes());
			
			
			// Rest totals 
			$scope.collection = [];
			$scope.total = 0;
			$scope.total_res_fee = 0;
			$scope.total_bus_fare = 0;
		};
		
        $scope.loadCollection = function() {
			
            $scope.loading = true;
            meReportService.getCollection($scope.form.from, $scope.form.to)
                .then(function (data) {
					
                    if (!data.error) {
                        $scope.total = 0;
						
                        $scope.collection = data.collection;
                        for(var i in $scope.collection){
                            var paid = parseFloat($scope.collection[i].totalPaid);
                            if(!isNaN(paid)){
                                $scope.total += paid;
                            }
							var busFare = parseFloat($scope.collection[i].fare);
							if(!isNaN(busFare)){
                                $scope.total_bus_fare += busFare;
                            }
							var res_fee = parseFloat($scope.collection[i].totalPaid) - parseFloat($scope.collection[i].fare);
							if(!isNaN(res_fee)){
                                $scope.total_res_fee += res_fee;
                            }
                        }
                    }else {
                        $scope.Global.error.hasError = true;
                        $scope.Global.error.message = data.error;
                    }
                    $scope.loading = false;
                }, function (error) {
                    $scope.Global.error.hasError = true;
                    $scope.Global.error.message = error;
                    $scope.loading = false;
                });
        };
			
		$scope.printRpt=function(data) {	
			var width = 960,
                height = 500;
            $window.open('/admin-panel/me/collectionprint?from=' + $filter('date')($scope.form.from, "yyyy-MM-dd") +'&to='+$filter('date')($scope.form.to, "yyyy-MM-dd"),
                "Collection Report",
                "status=no,width=" + width + ",height=" + height + ",top=" + ((screen.height / 2) - (height / 2)) + ",left=" + ((screen.width / 2) - (width / 2)) + ",toolbar=no,menubar=no,scrollbars=no,location=no");
			return false;
		};
		
        $scope.loadCollection();
		
		

    }]);