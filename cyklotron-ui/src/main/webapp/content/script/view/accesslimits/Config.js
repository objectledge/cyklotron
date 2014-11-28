var configModule = angular.module("ConfigApp", [ "ngResource", "ngRoute",
		"ngMessages", "ui.bootstrap", "ui.sortable" ]);

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
				}),
				items : $resource(baseUrl + "/rules/items", {}, {
					get : {
						method : "GET",
						url : baseUrl + "/rules/items/:itemId"
					},
					update : {
						method : "PUT",
						url : baseUrl + "/rules/items/:itemId",
						params : {
							itemId : "@id"
						}
					},
					remove : {
						method : "DELETE",
						url : baseUrl + "/rules/items/:itemId",
						params : {
							itemId : "@id"
						}
					}
				}),
				validate : $resource(baseUrl, {}, {
					urlPattern : {
						method : "POST",
						url : baseUrl + "/rules/validate/urlPattern"
					},
					rule : {
						method : "POST",
						url : baseUrl + "/rules/validate/rule"
					}
				})
			};
		} ]);

configModule.config([ "$routeProvider", function($routeProvider) {
	$routeProvider.when("/rules", {
		templateUrl : "accesslimits.Rules",
		controller : "RulesCtrl",
		resolve : {
			items : [ "backend", function(backend) {
				return backend.items.query().$promise;
			} ]
		}
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

function runRequest(scope, func, success, error) {
	scope.reqRunning = true;
	scope.reqError = {};
	func(function(value, headers) {
		success(value, headers);
		scope.reqRunning = false;
	}, function(resp) {
		scope.reqRunning = false;
		scope.reqError[resp.status] = true;
		if (_.isFunction(error)) {
			error(resp);
		}
		scope.reqRunning = false;
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
				$scope.reqRunning = false;
				$scope.reqError = {};
				var editedAction = _.clone(action);
				$modal.open({
					templateUrl : "accesslimits.EditAction",
					size : "lg",
					scope : angular.extend($scope, {
						mode : "edit",
						action : editedAction,
						save : function($close) {
							_.assign(action, editedAction);
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

configModule.controller("RulesCtrl", [
		"$scope",
		"$modal",
		"backend",
		"items",
		function($scope, $modal, backend, items) {
			$scope.items = items;

			$scope.add = function() {
				$scope.reqRunning = false;
				$scope.reqError = {};
				$modal.open({
					templateUrl : "accesslimits.EditItem",
					size : "lg",
					scope : angular.extend($scope, {
						mode : "add",
						item : {
							rules : []
						},
						editingRule : false,
						addingRule : false,
						save : function($close) {
							runRequest($scope, _.bind(backend.items.save, {},
									$scope.item), function(data, headers) {
								backend.items.get({
									itemId : headers("X-Item-Id")
								}).$promise.then(function(newItem) {
									items.push(newItem);
									$close();									
								}, function(resp) {
									$scope.reqError[resp.status] = true;
								});
							});
						}
					})
				});
			};

			$scope.edit = function(item) {
				$scope.reqRunning = false;
				$scope.reqError = {};
				var editedItem = _.clone(item);
				$modal.open({
					templateUrl : "accesslimits.EditItem",
					size : "lg",
					scope : angular.extend($scope, {
						mode : "edit",
						item : editedItem,
						editingRule : false,
						addingRule : false,
						save : function($close) {
							_.assign(item, editedItem);
							runRequest($scope, _.bind(item.$update, item),
									function() {
										$close();
									});
						}
					})
				});
			};

			$scope.askRemove = function(item) {
				$modal.open({
					templateUrl : "accesslimits.RemoveItem",
					size : "lg",
					scope : angular.extend($scope, {
						item : item,
						remove : function(item, $close) {
							runRequest($scope, _.bind(item.$remove, item),
									function() {
										items.splice(_.findIndex(items, {
											id : item.id
										}), 1);
										$close();
									});
						}
					})
				});
			};

			$scope.editRule = function(rule, index) {
				$scope.editingRule = true;
				$scope.addingRule = false;
				$scope.curRule = _.clone(rule);
				$scope.editIndex = index;
			};

			$scope.updateRule = function() {
				$scope.editingRule = false;
				$scope.item.rules.splice($scope.editIndex, 1, $scope.curRule);
				delete $scope.curRule;
				delete $scope.editIndex;
			};

			$scope.revertRule = function() {
				$scope.editingRule = false;
				delete $scope.curRule
			};

			$scope.addRule = function() {
				$scope.addingRule = true;
				$scope.editingRule = false;
				$scope.newRule = {};
			};

			$scope.confirmAddRule = function() {
				$scope.item.rules.push($scope.newRule);
				$scope.addingRule = false;
			};

			$scope.cancelAddRule = function() {
				$scope.addingRule = false;
			};
			
			$scope.removeRule = function(index) {
				$scope.item.rules.splice(index, 1);
			};
		} ]);