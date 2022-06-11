/**
 * Created by udantha on 2/18/16.
 */
expressAdminApp
    .filter('split', function() {
        return function(input, splitChar, splitIndex) {
            // do some bounds checking here to ensure it has that index
            var splitResult = input.split(splitChar);
            if(splitResult[splitIndex]){
                return splitResult[splitIndex];
            }else{
                return null;
            }
        }
    });