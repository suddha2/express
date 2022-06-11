expressAdminApp.controller('reportController', [
		'$scope',
		'$window',
		 
		'reportService',
		function($scope, $window, $reportService ) {

			// variable definitions
			$scope.reportPlaceHolder = 'loading';
			$scope.reportData = {};
			$scope.reportList = [];
			$scope.parameters = [];
			
			
			$scope.collection = [];
			$scope.currentRpt = "";
			// Totals Field
			$scope.totalSeats = 0;
			$scope.totalFee = 0;
			$scope.totalSltbFee=0;
			$scope.totalExpressFee=0;
			$scope.totalTransaction=0;
			$scope.totalApplicableRefund=0;
			$scope.totalSeats=0;
			$scope.totalConfirmedSeats=0;
			$scope.totalCancelledSeats=0;
			$scope.totalFare=0;

			$scope.selectedDepot="";


			$scope.totalConfirmedSltbFee=0;
			$scope.totalConfirmedSltbFare=0;
			$scope.totalConfirmedExpressFee=0;
			$scope.totalConfirmedTransaction=0;
			
			$scope.totalCancelledSltbFee=0;
			$scope.totalCancelledSltbFare=0;
			$scope.totalCancelledExpressFee=0;
			$scope.totalCancelledTransaction=0;
			
			
			// populate report list
			$reportService.getReportList().then(function(data) {
				//$scope.reportList = data;
				//$scope.reportPlaceHolder = 'select';
			});
			$reportService.getSupplierList().then(function(data) {
				$scope.supplierList = data;
				$scope.supplierListPlaceHolder = 'select';
			});	
			// watch for change in report dropdown and change parameters
			// appropriately
			$scope.$watchCollection('reportData.report', function(newVal,
					oldVal) {
				if (newVal) {
					$scope.parameters = newVal.parameters;
				}
			});
			
			$scope.$watchCollection('form', function(newVal, oldVal) {
				if (newVal) {
					$scope.collection = [];
					if($scope.currentRpt==="expInv"){
						$scope.loadExpInvReport();
					}
					if($scope.currentRpt==="approvedRefund"){
						$scope.loadApprovedRefundReport();
					}
					if($scope.currentRpt==="cashReconReport"){
						$scope.loadCashReconReport();
					}
					if($scope.currentRpt==="dailyCashReconReport"){
						$scope.loadDailyCashReconReport();
					}
					if($scope.currentRpt==="futureResReport"){
						$scope.loadFutureResReport();
					}
					if($scope.currentRpt==="busFareReport"){
						$scope.loadBusFareReport();
					}
					if($scope.currentRpt==="busFeeReport"){
						$scope.loadBusFeeReport();
					}if($scope.currentRpt==="counterCashRecon"){
						$scope.loadCounterCashReconReport();
					}
				}
			},true);	
			
			$scope.$watchCollection('collection', function(newVal, oldVal) {
				if(newVal !== oldVal && newVal){
					if($scope.currentRpt==="expInv"){
						$scope.totalSeats=0;
						$scope.totalFee=0;
						
						for (i = 0; i < $scope.collection.length; i++) {
							$scope.totalSeats+=$scope.collection[i].numberSeats;
							$scope.totalFee+=$scope.collection[i].totalExpressFee;
						}
					}
					if($scope.currentRpt==="approvedRefund" || $scope.currentRpt==="approvedRefundV2"){
						$scope.totalSeats=0;
						$scope.totalFare=0;
						$scope.totalSltbFee=0;
						$scope.totalExpressFee=0;
						$scope.totalTransaction=0;
						$scope.totalApplicableRefund=0;
						
						for (i = 0; i < $scope.collection.length; i++) {
							$scope.totalSeats+=$scope.collection[i].numberSeats;
							$scope.totalFare+=Number($scope.collection[i].fare);
							$scope.totalSltbFee+=Number($scope.collection[i].sltbBookingFee);
							$scope.totalExpressFee+=Number($scope.collection[i].expressBookingFee);
							$scope.totalTransaction+=Number($scope.collection[i].transactionTotal);
							$scope.totalApplicableRefund+=Number($scope.collection[i].applicableRefund);
						}
					}
					if($scope.currentRpt==="cashReconReport"){
						$scope.totalSeats=0;
						$scope.totalFare=0;
						$scope.totalSltbFee=0;
						$scope.totalExpressFee=0;
						$scope.totalTransaction=0;
						
						for (i = 0; i < $scope.collection.length; i++) {
							$scope.totalSeats+=Number($scope.collection[i].numberSeats);
							$scope.totalFare+=Number($scope.collection[i].fare);
							$scope.totalSltbFee+=Number($scope.collection[i].sltbBookingFee);
							$scope.totalExpressFee+=Number($scope.collection[i].expressBookingFee);
							$scope.totalTransaction+=Number($scope.collection[i].transactionTotal);
						}
					}
					if($scope.currentRpt==="dailyCashReconReport"){
						$scope.totalSeats=0;
						$scope.totalConfirmedSeats=0;
						$scope.totalCancelledSeats=0;
						$scope.totalFare=0;
						$scope.totalSltbFee=0;
						$scope.totalExpressFee=0;
						$scope.totalTransaction=0;
						
						$scope.totalConfirmedSltbFee=0;
						$scope.totalConfirmedSltbFare=0;
						$scope.totalConfirmedExpressFee=0;
						$scope.totalConfirmedTransaction=0;
						
						$scope.totalCancelledSltbFee=0;
						$scope.totalCancelledSltbFare=0;
						$scope.totalCancelledExpressFee=0;
						$scope.totalCancelledTransaction=0;
						
						for (i = 0; i < $scope.collection.length; i++) {
							$scope.totalSeats+=Number($scope.collection[i].numberSeats);
							$scope.totalFare+=Number($scope.collection[i].fare);
							$scope.totalSltbFee+=Number($scope.collection[i].sltbBookingFee);
							$scope.totalExpressFee+=Number($scope.collection[i].expressBookingFee);
							$scope.totalTransaction+=Number($scope.collection[i].transactionTotal);
							$scope.totalConfirmedSltbFee+=Number($scope.collection[i].confirmedSltbBookingFee);
							$scope.totalConfirmedSltbFare+=Number($scope.collection[i].confirmedBookingFare);
							$scope.totalConfirmedExpressFee+=Number($scope.collection[i].confirmedExpressBookingFee);
							$scope.totalConfirmedTransaction+=Number($scope.collection[i].confirmedSltbBookingFee+$scope.collection[i].confirmedBookingFare+$scope.collection[i].confirmedExpressBookingFee);
							
							$scope.totalCancelledSltbFee+=Number($scope.collection[i].cancelledSltbBookingFee);
							$scope.totalCancelledSltbFare+=Number($scope.collection[i].cancelledBookingFare);
							$scope.totalCancelledExpressFee+=Number($scope.collection[i].cancelledExpressBookingFee);
							$scope.totalCancelledTransaction+=Number($scope.collection[i].cancelledSltbBookingFee+$scope.collection[i].cancelledBookingFare+$scope.collection[i].cancelledExpressBookingFee);
							
							$scope.totalConfirmedSeats+=Number($scope.collection[i].confirmedBooking);
							$scope.totalCancelledSeats+=Number($scope.collection[i].cancelledBooking);
							
						}
					}
					if($scope.currentRpt==="dailyCashReconReportV2"){
						$scope.totalSeats=0;
						$scope.totalCancelledSeats=0;
						$scope.totalFare=0;
						$scope.totalSltbFee=0;
						$scope.totalExpressFee=0;
						$scope.totalTransaction=0;
						$scope.totoalNetPayable=0;
												
						$scope.totalCancelledSltbFee=0;
						$scope.totalCancelledSltbFare=0;
						$scope.totalCancelledExpressFee=0;
						$scope.totalCancelledTransaction=0;
						
						for (i = 0; i < $scope.collection.length; i++) {
							$scope.totalSeats+=Number($scope.collection[i].numberSeats);
							$scope.totalFare+=Number($scope.collection[i].fare);
							$scope.totalSltbFee+=Number($scope.collection[i].sltbBookingFee);
							$scope.totalExpressFee+=Number($scope.collection[i].expressBookingFee);
							$scope.totalTransaction+=Number($scope.collection[i].transactionTotal);
							$scope.totalCancelledSltbFee+=Number($scope.collection[i].cxSltbBookingFee);
							$scope.totalCancelledSltbFare+=Number($scope.collection[i].cxFare);
							$scope.totalCancelledExpressFee+=Number($scope.collection[i].cxExpressBookingFee);
							$scope.totalCancelledTransaction+=Number($scope.collection[i].cxApplicableRefund);
							$scope.totoalNetPayable+=Number($scope.collection[i].netPayable);
							$scope.totalCancelledSeats+=Number($scope.collection[i].cxNumberSeats);
						}
					}
					if($scope.currentRpt==="futureResReport"){
						$scope.totalSeats=0;
						$scope.totalFare=0;
						$scope.totalSltbFee=0;
						$scope.totalExpressFee=0;
						$scope.totalTransaction=0;
						
						for (i = 0; i < $scope.collection.length; i++) {
							$scope.totalSeats+=Number($scope.collection[i].numberSeats);
							$scope.totalFare+=Number($scope.collection[i].fare);
							$scope.totalSltbFee+=Number($scope.collection[i].sltbBookingFee);
							$scope.totalExpressFee+=Number($scope.collection[i].expressBookingFee);
							$scope.totalTransaction+=Number($scope.collection[i].transactionTotal);
						}
					}
					if($scope.currentRpt==="busFareReport"){
						$scope.totalFare=0;
						$scope.totalSeats=0;
						for (i = 0; i < $scope.collection.length; i++) {
							$scope.totalFare+=Number($scope.collection[i].totalFare);
							$scope.totalSeats+=Number($scope.collection[i].numberSeats);
						}
					}
					if($scope.currentRpt==="busFeeReport"){
						 
						$scope.totalSeats=0;
						for (i = 0; i < $scope.collection.length; i++) {
							 
							$scope.totalSeats+=Number($scope.collection[i].numberSeats);
						}
					}
					if($scope.currentRpt==="counterCashRecon"){
						$scope.totalSeats=0;
						$scope.totalFare=0;
						$scope.totalSltbFee=0;
						$scope.totalExpressFee=0;
						$scope.totalTransaction=0;
						
						for (i = 0; i < $scope.collection.length; i++) {
							$scope.totalSeats+=Number($scope.collection[i].numberSeats);
							$scope.totalFare+=Number($scope.collection[i].fare);
							$scope.totalSltbFee+=Number($scope.collection[i].sltbBookingFee);
							$scope.totalExpressFee+=Number($scope.collection[i].expressBookingFee);
							$scope.totalTransaction+=Number($scope.collection[i].transactionTotal);
						}
					}
					if($scope.currentRpt==="depotBusFareReport"){
						$scope.totalSeats=0;
						$scope.totalFare=0;
						$scope.selectedDepot="";
						
						for (i = 0; i < $scope.collection.length; i++) {
							$scope.selectedDepot= $scope.collection[i].depot;
							$scope.totalSeats+=Number($scope.collection[i].numberSeats);
							$scope.totalFare+=parseFloat($scope.collection[i].totalFare);
						}
					}
					
				}
			},true);
			
			// process report generation
			// $scope.submitReport = function() {

				// var type = $scope.reportData.report.reportType;
				// var parameters = $scope.reportData.parameters;

				// $reportService.generateReport(type, parameters);
			// };
			$scope.loadExpInvReport = function(){
				$scope.loading = true;
				 
				$reportService.getExpInvList($scope.form.from )
                .then(function (data) {
					
                    if (!data.error) {
						$scope.currentRpt = "expInv";
                        $scope.collection = data ;
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
			}
			
			$scope.loadApprovedRefundReport = function(){
				$scope.loading = true;
				 
				$reportService.getApprovedRefundReport($scope.form.from )
                .then(function (data) {
					
                    if (!data.error) {
						$scope.currentRpt = "approvedRefund";
                        $scope.collection = data ;
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
			}
			$scope.loadApprovedRefundV2Report = function(){
				$scope.loading = true;
				 
				$reportService.getApprovedRefundV2Report($scope.form.from )
                .then(function (data) {
					
                    if (!data.error) {
						$scope.currentRpt = "approvedRefundV2";
                        $scope.collection = data ;
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
			}
			$scope.loadCashReconReport = function(){
				$scope.loading = true;
				 
				$reportService.getCashReconReport($scope.form.from )
                .then(function (data) {
					
                    if (!data.error) {
						$scope.currentRpt = "cashReconReport";
                        $scope.collection = data ;
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
			}
			$scope.loadDailyCashReconReport = function(){
				$scope.loading = true;
				 
				$reportService.getDailyCashReconReport($scope.form.from )
                .then(function (data) {
					
                    if (!data.error) {
						$scope.currentRpt = "dailyCashReconReport";
                        $scope.collection = data ;
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
			}
			$scope.loadDailyCashReconV2Report = function(){
				$scope.loading = true;
				 
				$reportService.getDailyCashReconV2Report($scope.form.from )
                .then(function (data) {
					
                    if (!data.error) {
						$scope.currentRpt = "dailyCashReconReportV2";
                        $scope.collection = data ;
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
			}
			$scope.loadFutureResReport = function(){
				$scope.loading = true;
				 
				$reportService.getFutureResReport($scope.form.from )
                .then(function (data) {
					
                    if (!data.error) {
						$scope.currentRpt = "futureResReport";
                        $scope.collection = data ;
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
			}
			$scope.loadBusFareReport = function(){
				$scope.loading = true;
				 
				$reportService.getBusFareReport($scope.form.from )
                .then(function (data) {
					
                    if (!data.error) {
						$scope.currentRpt = "busFareReport";
                        $scope.collection = data ;
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
			}
			
			$scope.loadBusFeeReport = function(){
				$scope.loading = true;
				// Fare Report provides required info for this report too. 
				// so as a shortcut we use fare report backend .
				$reportService.getBusFareReport($scope.form.from )
                .then(function (data) {
					
                    if (!data.error) {
						$scope.currentRpt = "busFeeReport";
                        $scope.collection = data ;
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
			}
			$scope.loadCounterCashReconReport= function(){
				$scope.loading = true;
				// Fare Report provides required info for this report too. 
				// so as a shortcut we use fare report backend .
				$reportService.getCounterCashReconReport($scope.form.from )
                .then(function (data) {
					
                    if (!data.error) {
						$scope.currentRpt = "counterCashRecon";
                        $scope.collection = data ;
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
			}
			
			$scope.loadDepotBusFareReport = function(){
				$scope.loading = true;
				 
				$reportService.getDepotBusFareReport($scope.form.from ,$scope.form.supplierList)
                .then(function (data) {
					
                    if (!data.error) {
						$scope.currentRpt = "depotBusFareReport";
                        $scope.collection = data ;
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
			}
			
		} ]);
