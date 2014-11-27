var configModule = angular.module("ConfigApp", [ "ngResource", "ngRoute",
		"ngMessages", "ui.bootstrap" ]);

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

configModule.controller("RulesCtrl", [ "$scope", function($scope) {

} ]);

function runRequest(scope, func, success, error) {
	scope.reqRunning = true;
	scope.reqError = {};
	func(function(value, headers) {
		scope.reqRunning = false;
		success(value, headers);
	}, function(resp) {
		scope.reqRunning = false;
		scope.reqError[resp.status] = true;
		if (_.isFunction(error)) {
			error(resp);
		}
	})
}

configModule.controller("ActionsCtrl", [
		"$scope",
		"$modal",
		"backend",
		"actions",
		function($scope, $modal, backend, actions) {
			$scope.actions = actions;

			$scope.add = function() {
				$scope.reqRunning = false;
				$scope.reqError = {};
				$modal.open({
					templateUrl : "accesslimits.EditAction",
					size : "lg",
					scope : angular.extend($scope, {
						mode : "add",
						action : {},
						save : function(action, $close) {
							runRequest($scope, _.bind(backend.actions.save, {},
									action), function() {
								actions.push(action);
								actions.sort(function(a, b) {
									return a.name.localeCompare(b.name);
								})
								$close();
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
							runRequest($scope, _.bind(action.$update, action),
									function() {
										$close();
									});
						}
					})
				});
			};

			$scope.askRemove = function(action) {
				$modal.open({
					templateUrl : "accesslimits.RemoveAction",
					size : "lg",
					scope : angular.extend($scope, {
						action : action,
						remove : function(action, $close) {
							runRequest($scope, _.bind(action.$remove, action),
									function() {
										actions.splice(_.findIndex(actions, {
											name : action.name
										}), 1);
										$close();
									});
						}
					})
				});
			};
		} ]);
