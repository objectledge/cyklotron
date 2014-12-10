var quickPathModule = angular.module('QuickPathApp', [ 'ngResource' ]);

quickPathModule.filter('underscoresToSlash', function() {
    return function(input) {
        return input.replace(/__/g, "/");
    }
});

quickPathModule.filter('providerName', function() {
   var providers = {
       NavigationNodeResource: 'dokument',
       OrganizationResource: 'organizacja',
       RewriteToViewResource : 'widok'
   };
   return function(input) {
       if(_.has(providers, input)) {
           return providers[input];
       } else {
           return input;
       }
   }
});

quickPathModule.controller('QuickPathCtrl', [
    '$scope',
    '$resource',
    function($scope, $resource) {
        $scope.EntryList = $resource(QuickPathConfig.endpoint + '/:site/:path', {
            site : QuickPathConfig.site,
            path : '@path'
        });
        $scope.entries = $scope.EntryList.query();

        $scope.deleteEntry = function(entry) {
            if (confirm("Czy na pewno chcesz skasować ścieżkę " + entry.path.replace(/__/g, "/")
                + "?") == true) {
                entry.$delete(function() {
                    $scope.entries = _.reject($scope.entries, function(e) {
                        return e == entry;
                    });
                }, function(data, status) {
                    alert("nie udało się usunąć ścieżki");
                });
            }
        };
        
        $scope.query = '';
        $scope.predicate = 'path';
        $scope.reverse = false;
    } ]);
