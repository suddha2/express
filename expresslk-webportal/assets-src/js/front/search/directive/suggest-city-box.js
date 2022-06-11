/**
 * Created by udantha on 4/25/14.
 */
expressFrontApp
    .run(['$templateCache', function ($templateCache) {
        //typehead template
        $templateCache.put('tmplSuggestCityHead', '<a class="city-suggestion-head">' +
            '<span ng-bind-html="match.label | uibTypeaheadHighlight:query"></span>' +
            '<small ng-show="match.model.nameSi">{{match.model.nameSi}}</small>' +
            '<small ng-show="match.model.nameTa"> | {{match.model.nameTa}}</small>' +
            '</a>');
        //popup body
        $templateCache.put('tmplSuggestCityPopup', '' +
            '<div class="custom-popup-wrapper city-suggestion-popup"' +
                'ng-style="{top: position().top+\'px\', left: position().left+\'px\'}"' +
                'style="display: block;"' +
                'ng-show="isOpen() && !moveInProgress"' +
                'ng-class="{\'open\': (isOpen() && !moveInProgress)}" ' +
                'aria-hidden="{{!isOpen()}}">' +
                '<ul class="dropdown-menu" role="listbox">' +
                    '<li ng-repeat="match in matches track by $index" ng-class="{active: isActive($index) }"' +
                    'ng-mouseenter="selectActive($index)" ng-click="selectMatch($index)" role="option" id="{{::match.id}}">' +
                    '    <div uib-typeahead-match index="$index" match="match" query="query" template-url="templateUrl"></div>' +
                '    </li>' +
            '    </ul>' +
            '</div>');
    }])
    .directive('suggestCityBox', ['$window', '$animate', function ($window, $animate) {
        return {
            restrict: 'EA',
            replace: true,
            scope: {
                loadingTypehead: '=suggestLoading',
                dependantCity: '=dependant',
                bindModel: '='
            },
            template: '<input type="text"' +
            'ng-model="bindModel" autocomplete="off" ' +
            'uib-typeahead="city.name for city in getSuggestions($viewValue)"' +
            'typeahead-loading="loadingTypehead" typeahead-template-url="tmplSuggestCityHead" ' +
            'typeahead-popup-template-url="tmplSuggestCityPopup" typeahead-on-select="onSelect($item, $model, $label)" />',
            controller: ['$scope', '$element', 'suggestionService', '$timeout', '$rootScope', 'translate',
                function ($scope, $element, suggestionService, $timeout, $rootScope, translate) {
                    var loadTime = null;

                    /**
                     * On selecting an item, set it as the parent model
                     * @param $item
                     * @param $model
                     * @param $label
                     */
                    $scope.onSelect = function ($item, $model, $label) {
                        //change parent binded model with selected value
                        $scope.bindModel = $item;
                    };

                    /**
                     * Get suggested city names
                     * @param cityName
                     * @param type
                     * @returns {*}
                     */
                    $scope.getSuggestions = function (cityName) {
                        //clear timeout and start new one
                        $timeout.cancel(loadTime);

                        //search only if the charactor count is more than 2
                        if (cityName && cityName.length < 2) {
                            return {};
                        }

                        loadTime = $timeout(function () {
                            //call destination only if the origin has an ID
                            if($scope.dependantCity){
                                //get id
                                var id = angular.isDefined($scope.dependantCity.id)? $scope.dependantCity.id : 0;
                                //get originated city name
                                var sourceCity = (angular.isDefined($scope.dependantCity.name))?
                                    $scope.dependantCity.name : $scope.dependantCity;
                                //create source. If a valid id is available use it, otherwise check if city name is valid
                                //If so, use it. False otherwise
                                var source = id>0 ? id : ((angular.isString(sourceCity) && sourceCity.length>0)? sourceCity : false);
                                //look up in destinations if source is valid
                                if(source!==false){
                                    return suggestionService
                                        .getDestination(source, cityName)
                                        .then(onSuccess, onError);
                                }
                            }
                            //or, just call the city service to look up all the available cities
                            return suggestionService.getCity(cityName)
                                    .then(onSuccess, onError);
                        }, 500);

                        return loadTime;
                    };

                    function onSuccess(data) {
                        if (!data.error) {
                            return data;
                        } else {
                            //no need to broadcast. Die silently
                            // $rootScope.Global.error.hasError = true;
                            // $rootScope.Global.error.message = data.error;
                        }
                        $timeout.cancel(loadTime);
                    }

                    function onError() {
                        $timeout.cancel(loadTime);
                        //no need to broadcast. Die silently
                        // $rootScope.Global.error.hasError = true;
                        // $rootScope.Global.error.message = translate.get('NetworkError');
                    }
                }],
            link: function (scope, element, attrs) {
                //set input value based on ngModel value.
                //If it's an object, get name else get the string value
                scope.$watch(attrs['ngModel'], function (newValue) {
                    if (newValue) {
                        element[0].value = (newValue && newValue.name) ? newValue.name : newValue;
                    }
                });
                //select text on click only first click
                element.on('click', function () {
                    if (!$window.getSelection().toString()) {
                        // Required for mobile Safari
                        this.setSelectionRange(0, this.value.length)
                    }
                });
            }
        }
    }]);