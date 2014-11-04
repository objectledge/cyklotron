var configModule = angular.module("ConfigApp", [ "ngResource", "ngRoute" ]);

configModule.config([ "$routeProvider", function($routeProvider) {
	$routeProvider.when("/rules", {
		templateUrl : "accesslimits.Rules",
		controller : "RulesCtrl"
	});
	$routeProvider.when("/actions", {
		templateUrl : "accesslimits.Actions",
		controller : "ActionsCtrl"
	});
	$routeProvider.otherwise({
		redirectTo : "/rules"
	});
} ]);

configModule.controller("ConfigCtrl", [ "$scope", function($scope) {

} ]);

configModule.controller("RulesCtrl", [ "$scope", function($scope) {

} ]);

configModule.controller("ActionsCtrl", [ "$scope", function($scope) {

} ]);