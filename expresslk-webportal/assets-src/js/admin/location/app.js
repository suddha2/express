/**
 * Created by udantha on 7/2/16.
 */
expressAdminApp.config(['uiGmapGoogleMapApiProvider', function(uiGmapGoogleMapApiProvider) {
    uiGmapGoogleMapApiProvider.configure({
        key: 'AIzaSyCjCcLUvFOVw4kfwfxRnISAxtTrQb3zz5c',
        //v: '3.24', //defaults to latest 3.X anyhow
        libraries: 'weather,geometry,visualization'
    });
}]);