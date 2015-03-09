var configModule = angular.module("ConfigApp", [ "cyklo", "ngResource", "ngRoute", "ngMessages",
    "ui.bootstrap", "ui.sortable" ]);

configModule.factory("backend", [ "$window", "$resource", function($window, $resource) {
    var baseUrl = $window.appBaseUri + "/rest/canonicallinkrules";

    return {
        rules : $resource(baseUrl + "/rules", {}, {
            get : {
                method : "GET",
                url : baseUrl + "/rules/:ruleId",
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

configModule.filter("categoryNames", function() {

    return function(input) {
        return input ? _.map(input, 'name').join(", ") : "";
    }

});

configModule.config([ "$routeProvider", function($routeProvider) {
    $routeProvider.when("/rules", {
        templateUrl : "canonicallinkrules.Rules",
        controller : "RulesCtrl",
        resolve : {
            rules : [ "backend", function(backend) {
                return backend.rules.query().$promise;
            } ]
        }
    }).when("/add", {
        templateUrl : "canonicallinkrules.EditRule",
        controller : "AddRulesCtrl"
    }).when("/edit/:ruleId", {
        templateUrl : "canonicallinkrules.EditRule",
        controller : "EditRulesCtrl"
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

configModule
    .controller(
        "AddRulesCtrl",
        [
            "$scope",
            "$location",
            "backend",
            function($scope, $location, backend) {

                $scope.reqRunning = false;
                $scope.reqError = {};
                $scope.categories = [];
                $scope.mode = "add";
                $scope.rule = {};
                $scope.priorityRange = [];
                $scope.linkPatternString = '((http|https)://(([a-zA-Z0-9\.\-_]+(:[a-zA-Z0-9\.\-_]+)?@)?[a-zA-Z0-9\.\-_]+\.[a-zA-Z]{2,4}(:[0-9]{2,6})?(/.*)?)?)?';
                backend.rules.query().$promise.then(function(rules) {
                    $scope.priorityRange = _.range(0, _.size(rules) + 1);
                });

                $scope.save = function() {
                    runRequest($scope, _.bind(backend.rules.save, {}, $scope.rule), function() {
                        $location.path('/rules');
                    });
                };
                $scope.list = function() {
                    $location.path('/rules');
                }

                $scope.$watch('categories', function(categories) {
                    if (categories && categories.length > 1) {
                        $scope.rule.category = null;
                        $scope.edit.category.$error = {
                            'tooMany' : true
                        };
                    } else if (categories && categories.length == 1) {
                        $scope.edit.category.$error = {
                            'tooMany' : false
                        };
                        if (categories[0].id !== undefined) {
                            $scope.rule.category = categories[0];
                        } else {
                            $scope.rule.category = null;
                            $scope.edit.category.$error = {
                                'required' : true
                            };
                        }
                    } else {
                        $scope.rule.category = null;
                        $scope.edit.category.$error = {
                            'required' : true
                        };
                    }
                }, true);

            } ]);

configModule
    .controller(
        "EditRulesCtrl",
        [
            "$scope",
            "$location",
            "backend",
            "$routeParams",
            function($scope, $location, backend, $routeParams) {

                $scope.reqRunning = true;
                $scope.reqError = {};
                $scope.mode = "edit";
                $scope.categories = [];
                $scope.rule = {};
                $scope.priorityRange = [];
                $scope.linkPatternString = '((http|https)://(([a-zA-Z0-9\.\-_]+(:[a-zA-Z0-9\.\-_]+)?@)?[a-zA-Z0-9\.\-_]+\.[a-zA-Z]{2,4}(:[0-9]{2,6})?(/.*)?)?)?';
                backend.rules.query().$promise.then(function(rules) {
                    $scope.priorityRange = _.range(0, _.size(rules) + 1);
                });

                backend.rules.get({
                    ruleId : $routeParams.ruleId
                }).$promise.then(function(rule) {
                    $scope.rule = rule;
                    $scope.categories.push(rule.category);
                    $scope.reqRunning = false;
                }, function(error) {
                    $scope.reqError[resp.status] = true;
                    $scope.reqRunning = false;
                });

                $scope.save = function() {
                    runRequest($scope, _.bind($scope.rule.$update, $scope.rule), function() {
                        $location.path('/rules');
                    });
                }

                $scope.list = function() {
                    $location.path('/rules');
                }

                $scope.$watch('categories', function(categories) {
                    if (categories && categories.length > 1) {
                        $scope.edit.category.$error = {
                            'tooMany' : true
                        };
                    } else if (categories && categories.length == 1) {
                        $scope.edit.category.$error = {
                            'tooMany' : false
                        };
                        if (categories[0].id !== undefined) {
                            $scope.rule.category = categories[0];
                        } else {
                            $scope.rule.category = null;
                            $scope.edit.category.$error = {
                                'required' : true
                            };
                        }
                    } else {
                        $scope.edit.category.$error = {
                            'required' : true
                        };
                    }
                }, true);

            } ]);

configModule.controller("RulesCtrl", [ "$scope", "$modal", "$location", "backend", "rules",
    function($scope, $modal, $location, backend, rules) {

        $scope.rules = rules;
        $scope.orderField = "priority";
        $scope.orderDirection = true;

        $scope.add = function() {
            $location.path('/add');
        };

        $scope.edit = function(rule) {
            $location.path('/edit/' + rule.id);
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