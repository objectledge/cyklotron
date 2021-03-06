var configModule = angular.module("ConfigApp", [ "ngResource", "ngRoute",
		"ngMessages", "ui.bootstrap", "ui.sortable" ]);

configModule.factory("backend", [ "$window", "$resource",
		function($window, $resource) {
			var baseUrl = $window.appBaseUri + "/rest/accesslimits";
			return {
				actions : $resource(baseUrl + "/actions", {}, {
					get : {
						method : "GET",
						url : baseUrl + "/actions/:name"
					},
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
				lists : $resource(baseUrl + "/lists", {}, {
					get : {
						method : "GET",
						url : baseUrl + "/lists/:id"
					},
					update : {
						method : "PUT",
						url : baseUrl + "/lists/:id",
						params : {
							id : "@id"
						}
					},
					remove : {
						method : "DELETE",
						url : baseUrl + "/lists/:id",
						params : {
							id : "@id"
						}
					},
				}),
				listItems : $resource(baseUrl + "/lists/items", {}, {}),
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
					},
					addressBlock : {
						method : "POST",
						url : baseUrl + "/lists/validate/addressBlock"
					}
				}),
				notifications : $resource(baseUrl + "/notifications", {}, {
					save : {
						method : 'PUT'
					}
				})
			};
		} ]);

function customValidator(name, resource) {
	configModule.directive(name, [
			"$q",
			"backend",
			function($q, backend) {
				return {
					restrict : "A",
					require : "ngModel",
					link : function(scope, element, attr, ctrl) {
						ctrl.$asyncValidators[resource] = function(modelValue,
								viewValue) {
							ctrl.validationStatus = {};
							var deferred = $q.defer();
							if(attr.required) {
								backend.validate[resource]({
									text : viewValue
								}).$promise.then(function(response) {
									if (response.valid) {
										deferred.resolve(true);
									} else {
										ctrl.validationStatus[200] = true;
										ctrl.validationError = response.error;
										deferred.reject();
									}
								}, function(response) {
									ctrl.validationStatus[response.status] = true;
									deferred.reject();
								});								
							} else {
								deferred.resolve(true); // field not required, skip validation
							}
							return deferred.promise;
						};
					}
				};
			} ]);
}

customValidator("cfgValidUrlPattern", "urlPattern");
customValidator("cfgValidRule", "rule");
customValidator("cfgValidAddressBlock", "addressBlock");

configModule.filter("wrap", function() {
	return function(input, width) {
		var lines = input.split("\n");
		var out = "";
		_.forEach(lines, function(line) {
			while (line.length > width) {
				out += line.substring(0, width) + "\n";
				line = line.substring(width);
			}
			out += line + "\n";
		});
		return out;
	}
})

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
	$routeProvider.when("/lists", {
		templateUrl : "accesslimits.AccessLists",
		controller : "AccessListsCtrl",
		resolve : {
			lists : [ "backend", function(backend) {
				return backend.lists.query().$promise;
			} ]
		}
	});
	$routeProvider.when("/lists/submit/:address", {
		templateUrl : "accesslimits.AccessListsSubmission",
		controller : "AccessListsSubmissionCtrl",
		resolve : {
			lists : [ "backend", function(backend) {
				return backend.lists.query().$promise;
			} ]
		}
	});
	$routeProvider.when("/notifications", {
		templateUrl : "accesslimits.Notifications",
		controller : "NotificationsCtrl",
		resolve : {
			config : [ "backend", function(backend) {
				return backend.notifications.get().$promise;
			} ]
		}
	})
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
						save : function($close) {
							runRequest($scope, _.bind(backend.actions.save, {},
									$scope.action), function() {
								backend.actions.get({
									name : $scope.action.name
								}).$promise.then(function(newAction) {
									actions.push(newAction);
									actions.sort(function(a, b) {
										return a.name.localeCompare(b.name);
									});
									$close();
								}, function(resp) {
									$scope.reqError[resp.status] = true;
								});
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

configModule.controller("AccessListsCtrl", [
		"$scope",
		"$modal",
		"backend",
		"lists",
		function($scope, $modal, backend, lists) {
			$scope.lists = lists;

			$scope.add = function() {
				$scope.reqRunning = false;
				$scope.reqError = {};
				$modal.open({
					templateUrl : "accesslimits.EditAccessList",
					size : "lg",
					scope : angular.extend($scope, {
						mode : "add",
						list : {
							items : []
						},
						save : function($close) {
							runRequest($scope, _.bind(backend.lists.save, {},
									$scope.list), function(resp, headers) {
								backend.lists.get({
									id : headers("X-Item-Id")
								}).$promise.then(function(newList) {
									lists.push(newList);
									lists.sort(function(a, b) {
										return a.name.localeCompare(b.name);
									});
									$close();
								}, function(resp) {
									$scope.reqError[resp.status] = true;
								});
							});
						}
					})
				});
			};

			$scope.edit = function(list) {
				$scope.reqRunning = false;
				$scope.reqError = {};
				backend.lists.get({
					id : list.id
				}).$promise.then(function(editedList) {
					$modal.open({
						templateUrl : "accesslimits.EditAccessList",
						size : "lg",
						scope : angular.extend($scope, {
							mode : "edit",
							list : editedList,
							save : function($close) {
								_.assign(list, editedList);
								runRequest($scope, _.bind(list.$update, list),
										function() {
											$close();
										});
							}
						})
					});
				});
			};

			$scope.askRemove = function(list) {
				$modal.open({
					templateUrl : "accesslimits.RemoveAccessList",
					size : "lg",
					scope : angular.extend($scope, {
						list : list,
						remove : function(list, $close) {
							runRequest($scope, _.bind(list.$remove, list),
									function() {
										lists.splice(_.findIndex(lists, {
											name : list.name
										}), 1);
										$close();
									});
						}
					})
				});
			};

			$scope.editItem = function(rule, index) {
				$scope.editingItem = true;
				$scope.addingItem = false;
				$scope.curItem = _.clone(rule);
				$scope.editIndex = index;
			};

			$scope.updateItem = function() {
				$scope.editingItem = false;
				$scope.list.items.splice($scope.editIndex, 1, $scope.curItem);
				delete $scope.curItem;
				delete $scope.editIndex;
			};

			$scope.revertItem = function() {
				$scope.editingItem = false;
				delete $scope.curItem
			};

			$scope.addItem = function() {
				$scope.addingItem = true;
				$scope.editingItem = false;
				$scope.newItem = {};
			};

			$scope.confirmAddItem = function() {
				$scope.list.items.push($scope.newItem);
				$scope.addingItem = false;
			};

			$scope.cancelAddItem = function() {
				$scope.addingItem = false;
			};

			$scope.removeItem = function(index) {
				$scope.list.items.splice(index, 1);
			};
		} ]);

configModule.controller("AccessListsSubmissionCtrl", [ "$scope",
		"$routeParams", "$location", "backend", "lists",
		function($scope, $routeParams, $location, backend, lists) {
			$scope.address = $routeParams.address;
			$scope.lists = lists;
			$scope.range = "32";
			if (lists.length > 0) {
				$scope.list = lists[0];
			}
			$scope.submit = function() {
				runRequest($scope, _.bind(backend.listItems.save, {}, {
					address : $routeParams.address,
					range : parseInt($scope.range, 10),
					description : $scope.description,
					listId : $scope.list.id
				}), function() {
					$location.path("/lists");
				}, function() {
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

configModule.controller("NotificationsCtrl", [ "$scope", "backend", "config",
		function($scope, backend, config) {
			$scope.config = config;
			$scope.save = function() {
				runRequest($scope, _.bind(config.$save, config), function(data, headers) {
				}, function(resp) {
				});
			}
		} ]);