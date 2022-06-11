/**
 * Created by Shehanis on 6/9/14.
 */
expressAdminApp.controller('adminCommon', ['$scope', '$timeout', '$rootScope',
    function ($scope, $timeout, $rootScope) {
        $scope.Global = {
            error: {
                hasError: false,
                message: ''
            },
            success: {
                isSuccess: false,
                message: ''
            }
        };
        $rootScope.Global = {
            error: {
                hasError: false,
                message: ''
            },
            success: {
                isSuccess: false,
                message: ''
            }
        };
        //preloader model
        $scope._showPreLoader = false;
        var timeOutPreloader;

        var hasErrorHandler = function(newVal, oldVal){
            if(newVal===true){
                //clear message after timeout
                $timeout(function(){
                    $scope.Global.error.hasError = false;
                    $scope.Global.error.message = '';
                    $rootScope.Global.error.hasError = false;
                    $rootScope.Global.error.message = '';
                }, 10000);
            }
        };
        /**
         * Watch for errors and show errors
         */
        $scope.$watch('Global.error.hasError', hasErrorHandler);
        $rootScope.$watch('Global.error.hasError', function(newVal, oldVal){
            if(newVal===true){
                $scope.Global.error.hasError = true;
                $scope.Global.error.message = $rootScope.Global.error.message;
            }
        });

        /**
         * Watch for success message and show messages
         */
        var successHandler = function(newVal, oldVal){
            if(newVal===true){
                //clear message after timeout
                $timeout(function(){
                    $scope.Global.success.isSuccess = false;
                    $scope.Global.success.message = '';
                    $rootScope.Global.success.isSuccess = false;
                    $rootScope.Global.success.message = '';
                }, 10000);
            }
        };
        $scope.$watch('Global.success.isSuccess', successHandler);
        $rootScope.$watch('Global.success.isSuccess', function(newVal, oldVal){
            if(newVal===true){
                $scope.Global.success.isSuccess = true;
                $scope.Global.success.message = $rootScope.Global.success.message;
            }
        });

        /**
         * Watch for activation of pre-loader and activate/deactivate it
         */
        var preloaderHandler = function(newVal, oldVal){
            //show loader
            if(newVal===true){
                //clear timeout if exists
                if(timeOutPreloader){
                    $timeout.cancel(timeOutPreloader);
                }
                $scope._showPreLoader = true;
            }else{
                //hide loader
                //clear preloader after timeout
                timeOutPreloader = $timeout(function(){
                    $scope._showPreLoader = false;
                }, 100);
            }
        };
        $scope.$watch('Global.loading', preloaderHandler);
        $rootScope.$watch('Global.loading', preloaderHandler);

        $rootScope.$on('$routeChangeStart', function() {
            //show loading gif
            //$scope.Global.loading = true;
        });

        $rootScope.$on('$routeChangeSuccess', function() {
            //hide loading gif
            //$scope.Global.loading = false;
        });

        $rootScope.$on('$routeChangeError', function() {
            //hide loading gif
            //$scope.Global.loading = false;
        });

    }]);