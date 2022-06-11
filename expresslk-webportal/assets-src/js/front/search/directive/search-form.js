/**
 * Created by udantha on 10/18/16.
 */
expressFrontApp
    .directive('searchForm', ['$compile', '$animate', function ($compile, $animate) {
        return {
            restrict: 'A',
            scope: {
                fromCity: '=searchFromCity',
                toCity: '=searchToCity',
                date: '=searchDate',
                offerCode: '=offerCode'
            },
            controller: ['$scope', '$element', '$state', '$rootScope', 'translate',
                function ($scope, $element, $state, $rootScope, translate) {

                    //process search form
                    $scope.submitSearch = function () {
                        //validate
                        if(!$scope.fromCity || !$scope.toCity || !$scope.date){
                            $rootScope.Global.error.hasError = true;
                            $rootScope.Global.error.message = translate.get('FromToMandatory');
                            return;
                        }

                        var from = angular.isDefined($scope.fromCity.id)? $scope.fromCity.id : 0;
                        var fromName = angular.isDefined($scope.fromCity.name)? $scope.fromCity.name : $scope.fromCity;
                        var to = angular.isDefined($scope.toCity.id)? $scope.toCity.id : 0;
                        var toName = angular.isDefined($scope.toCity.name)? $scope.toCity.name : $scope.toCity;
                        var rawdate = $scope.date;

                        var date = rawdate.getFullYear() +'-'+ ("0" + (rawdate.getMonth() + 1)).slice(-2) +'-'+ ("0" + rawdate.getDate()).slice(-2);

                        //redirect
                        $state.go('search-result', {
                            fromName: encodeURIComponent(fromName),
                            toName: encodeURIComponent(toName),
                            from: from,
                            to: to,
                            date: date,
                            offerCode: $scope.offerCode
                        });
                    };
                }],
            link: function (scope, element, attrs) {
                //attach submit event
                element.on('submit', function (event) {
                    event.preventDefault();
                    event.stopPropagation();
                    //call submit
                    scope.submitSearch();
                })
            }
        }
    }]);