var configModule = angular.module("ConfigApp", [ "ngResource", "ngRoute", "ngMessages",
    "ui.bootstrap", "ui.sortable" ]);

configModule.factory("backend", [ "$window", "$resource", function($window, $resource) {
    var baseUrl = $window.appBaseUri + "/rest/canonicallinkrules";
    return {
        rules : $resource(baseUrl + "/rules", {}, {
            get : {
                method : "GET",
                url : baseUrl + "/rules/:ruleId"
            },
            update : {
                method : "PUT",
                url : baseUrl + "/rules/:ruleId",
                params : {
                    ruleId : "@id"
                }
            },
            remove : {
                method : "DELETE",
                url : baseUrl + "/rules/:ruleId",
                params : {
                    ruleId : "@id"
                }
            },
        })
    };
} ]);

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
        templateUrl : "canonicallinkrules.Rules",
        controller : "RulesCtrl",
        resolve : {
            rules : [ "backend", function(backend) {
                return backend.rules.query().$promise;
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

configModule.controller("RulesCtrl", [ "$scope", "$modal", "backend", "rules",
    function($scope, $modal, backend, rules) {
        $scope.rules = rules;

        $scope.add = function() {
            $scope.reqRunning = false;
            $scope.reqError = {};
            $modal.open({
                templateUrl : "canonicallinkrules.EditRule",
                size : "lg",
                scope : angular.extend($scope, {
                    mode : "add",
                    rule : {},
                    save : function($close) {
                        runRequest($scope, _.bind(backend.rules.save, {}, $scope.rule), function() {
                            backend.rules.get({
                                ruleId : headers("X-Rule-Id")
                            }).$promise.then(function(newRule) {
                                rules.push(newRule);
                                rules.sort(function(a, b) {
                                    return a.priority.localeCompare(b.priority);
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

        $scope.edit = function(rule) {
            $scope.reqRunning = false;
            $scope.reqError = {};
            var editedAction = _.clone(rule);
            $modal.open({
                templateUrl : "canonicallinkrules.EditRule",
                size : "lg",
                scope : angular.extend($scope, {
                    mode : "edit",
                    action : editedAction,
                    save : function($close) {
                        _.assign(rule, editedAction);
                        runRequest($scope, _.bind(rule.$update, rule), function() {
                            $close();
                        });
                    }
                })
            });
        };

        $scope.askRemove = function(rule) {
            $modal.open({
                templateUrl : "canonicallinkrules.RemoveRule",
                size : "lg",
                scope : angular.extend($scope, {
                    rule : rule,
                    remove : function(rule, $close) {
                        runRequest($scope, _.bind(rule.$remove, rule), function() {
                            rules.splice(_.findIndex(rules, {
                                id : rule.id
                            }), 1);
                            $close();
                        });
                    }
                })
            });
        };

    } ]);