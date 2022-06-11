/**
 * Created by udantha on 6/28/15.
 */

angular.module('expressAdminCrud')
    .factory('httpInterceptor', ['$q', '$injector', 'notification', function ($q, $injector, notification) {
    return {
        'request': function(config) {
            // light entities
            var lightEntities = [
                'booking',
                'bus',
                'busSchedule',
                'busRoute',
                'user'
            ];
            var useDotNotations = false;
            for (var key in config.params) {
                if (config.params.hasOwnProperty(key) && key.indexOf('.') > -1 && config.params[key]) {
                    useDotNotations = true;
                }
            }
            var isLightEntity = false;
            for (var i = 0; i < lightEntities.length; i++) {
                if (config.url.match(new RegExp('/' + lightEntities[i] + '$', 'g'))) {
                    isLightEntity = true;
                    break;
                }
            }
            if (isLightEntity && config.method == 'GET' && !useDotNotations) {
                config.url += 'Light';
            }
            return config;
        },
        'response': function(response) {
            if(response && response.data && response.data.error){
                notification.log('Error : '+ response.data.error, { addnCls: 'humane-flatty-error' });
                // Replace response with rejected promise to prevent rest of execution
                return $q.reject(response.data);
            }
            return response;
        },
        'responseError': function(rejection) {
            if (rejection.status === 401) {
                notification.log('Your session timed-out', { addnCls: 'humane-flatty-error' });
                $injector.get('$state').go('logout');
            }
            if (rejection.status === 403) {
                notification.log('You are not authorized to access this function ', { addnCls: 'humane-flatty-error' });
                $injector.get('$state').go('dashboard');
            }
            if (rejection.status === 500) {
                notification.log('Error : '+ rejection.statusText, { addnCls: 'humane-flatty-error' });
            }
            // Replace response with rejected promise to prevent rest of execution
            return $q.reject(rejection);
        }
    };
}]);

//main configs
angular.module('expressAdminCrud')
    .config(['RestangularProvider', '$httpProvider', function(RestangularProvider, $httpProvider){

        $httpProvider.interceptors.push('httpInterceptor');

        // use the custom query parameters function to format the API request correctly
        RestangularProvider.addFullRequestInterceptor(function(element, operation, what, url, headers, params) {
            if (operation == "getList") {
                // custom pagination params
                if (params._page) {
                    params.pageStart = (params._page - 1) * params._perPage;
                    params.pageRows = params._perPage;
                }
                delete params._page;
                delete params._perPage;
                // custom sort params
                if (params._sortField) {
                    params.sortField = params._sortField;
                    delete params._sortField;
                    
                    if (params._sortDir) {
                        params.sortDir = params._sortDir;
                        delete params._sortDir;
                    }
                }
                // custom filters
                if (params._filters) {
                    for (var filter in params._filters) {
                        params[filter] = params._filters[filter];
                    }
                    delete params._filters;
                }
            }
            return { params: params };
        });

        //transform the response
        RestangularProvider.addResponseInterceptor(function(data, operation, what, url, response, deferred){

            function flatternMultiLevel(el){
                if(el && el.constructor === Array){
                    var newAr = [];
                    //extract the id and set new array
                    for(var i in el){
                        if(el[i].hasOwnProperty('id')){
                            newAr.push(el[i].id)
                        }
                    }
                    return newAr;
                }
                //put id in array if flat object
                if($.isPlainObject(el)){
                    if(el.hasOwnProperty('id')){
                        return el.id;
                    }
                }
                return el;
            }

            //flattern objects
            if(operation==='getList'){
                for(var k in data){
                    //loop for the second level to check children are arrays
                    for(var kk in data[k]){
                        data[k][kk] = flatternMultiLevel(data[k][kk]);
                    }
                }
            }else{
                if (what !== 'booking') {
                    for(var k in data){
                        data[k] = flatternMultiLevel(data[k]);
                    }
                }
            }
            //console.dir(data)
            return data;
        });
    }]);