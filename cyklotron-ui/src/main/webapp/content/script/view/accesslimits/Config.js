var configModule = angular.module("ConfigApp", [ "ngResource", "ngRoute",
		"ui.bootstrap" ]);

configModule.factory("backend", [ "$window", "$resource",
		function($window, $resource) {
			var baseUrl = $window.appBaseUri + "/rest/accesslimits";
			return {
				actions : $resource(baseUrl + "/actions", {}, {
					update : {
						method : "PUT",
						url : baseUrl + "/actions/:actionName",
						params : {
							actionName : "@name"
						}
					},
					remove : {
						method : "DELETE",
						url : baseUrl + "/actions/:actionName",
						params : {
							actionName : "@name"
						}
					},
				})
			};
		} ]);

configModule.config([ "$routeProvider", function($routeProvider) {
	$routeProvider.when("/rules", {
		templateUrl : "accesslimits.Rules",
		controller : "RulesCtrl"
	});
	$routeProvider.when("/actions", {
		templateUrl : "accesslimits.Actions",
		controller : "ActionsCtrl",
		resolve : {
			actions : [ "backend", function(backend) {
				return backend.actions.query().$promise;
			} ]
		}
	});
	$routeProvider.otherwise({
		redirectTo : "/rules"
	});
} ]);

configModule.controller("ConfigCtrl", [ "$scope", function($scope) {

} ]);

configModule.controller("RulesCtrl", [ "$scope", function($scope) {

} ]);

configModule.controller("ActionsCtrl", [ "$scope", "$modal", "backend",
		"actions", function($scope, $modal, backend, actions) {
			$scope.actions = actions;

			$scope.add = function() {
				$modal.open({
					templateUrl : "accesslimits.EditAction",
					size : "lg",
					scope : angular.extend($scope, {
						mode : "add",
						action : {},
						save : function(action, $close) {
							backend.actions.save(action, function() {
								actions.push(action);
								actions.sort(function(a, b) {
									return a.name.localeCompare(b.name);
								})
								$close();
							}, function(resp) {
								alert(resp.status);
							});
						}
					})
				});
			};

			$scope.edit = function(action) {
				$modal.open({
					templateUrl : "accesslimits.EditAction",
					size : "lg",
					scope : angular.extend($scope, {
						mode : "edit",
						action : action,
						save : function(action, $close) {
							action.$update(function() {
								$close();
							}, function(resp) {
								alert(resp.status);
							});
						}
					})
				});
			};

			$scope.remove = function(action) {
				$modal.open({
					templateUrl : "accesslimits.RemoveAction",
					size : "lg",
					scope : angular.extend($scope, {
						action : action,
						remove : function(action, $close) {
							action.$remove(function() {
								actions.splice(_.findIndex(actions, {
									name : action.name
								}), 1);
								$close();
							}, function(resp) {
								alert(resp.status);
							});
						}
					})
				});
			};
		} ]);