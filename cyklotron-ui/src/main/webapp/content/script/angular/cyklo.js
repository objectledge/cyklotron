/**
	Angular module "cyklo"
	Contains directives common for Cyklotron projects
	
	Requires:
	
		underscore.js
		CMSPopups.js
	
	
	var AngularAppConfiguration = {
	    Directives : {
	    	CategoryChooserTemplate : '$link.view('popups.CategoryChooser')'
	    }
  }
*/

var cykloModule = angular.module("cyklo", []);

cykloModule.directive('categoryChooser', [function ()
{
	window.CATEGORY_CHOOSER_SEPARATOR = '/ ';
	
	return {
        restrict: 'E',
        replace: true,
        transclude: true,
        templateUrl: AngularAppConfiguration.Directives.CategoryChooserTemplate,
        scope: {
            categories: '='
        },
        link: function postLink(scope, iElement, iAttrs)
        {
            // set form id
            var formId = 'form-' + scope.$id;
            var form = iElement.find('form.legacy-form').attr('id', formId);
            // set categories names ID
            var categoriesNamesID = 'categories-names' + scope.$id;
            var $categoriesNames = iElement.find('input.category-names').attr('id', categoriesNamesID);

            // set categoires ids ID
            var categoriesIdsID = 'categories-ids' + scope.$id;
            var $categoriesIds = iElement.find('input.category-ids').attr('id', categoriesIdsID);

            iElement.find('button.popup').click(function (evt)
            {
                selectCategories('name id',
                formId,
                categoriesNamesID + ' ' + categoriesIdsID,
                iAttrs.categoryPopupLink,
                categoriesIdsID,
                iAttrs.resourceClass);
            });

            scope.clearCategories = function ()
            {
                Forms.setValue(formId, categoriesNamesID, '');
                Forms.setValue(formId, categoriesIdsID, '');
                scope.categories = [];
            };

            $categoriesIds.change(function ()
            {
                var categoriesIdsString = this.value;
                var categoriesIds = _.filter(categoriesIdsString.split(" "), function (id)
                {
                    return id !== " " && id !== "";
                });
                var categoriesNamesString = $categoriesNames.get(0).value;
                var categoriesNames = categoriesNamesString.split("/");
                categoriesNames = _.map(categoriesNames, function (categoryName)
                {
                    return categoryName.trim();
                });
                var namesAndIds = _.zip(categoriesIds, categoriesNames);
                var categories = _.map(namesAndIds, function (category)
                {
                    return {
                        name: category[1],
                        id: category[0]
                    };
                });
                scope.$apply(function (scope)
                {
                    scope.categories = categories;
                });
            });
            
            if(_.size(scope.categories) > 0)
        	{
            	var idsOnly = _.map(scope.categories, function(category){ return category.id; }).join(' ');
            	$categoriesIds.val(idsOnly);
        	}
        }
    };
}]);