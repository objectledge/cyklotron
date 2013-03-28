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

cykloModule.directive('categoryChooser', ['$q', function ($q)
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
            
            scope.$watch('categories', function(cat){
            	if($categoriesIds.val().length == 0)
            	{
            		var idsOnly = _.map(cat, function(category){ return category.id; }).join(' ');            		
            		$categoriesIds.val(idsOnly);
            	}
            }, true)
        }
    };
}]);

cykloModule.directive('cykloUpload', ['$q', function($q){
	
	return {
		restrict : 'EA',
		transclude : true,
		replace : true,
		scope: { 
			uploadUrl :'=',
			fileName : '@',
			valid :'=',
			method : '='
		},
		controller : function($scope, $element){
			this.uploadFile = function()
			{
				var deferred = $q.defer();
				var inputFile = $('input[type="file"]', $element).get(0);
				var oData = new FormData();
				oData.append($scope.fileName, inputFile.files[0]);
				console.log(oData); 
				var oReq = new XMLHttpRequest();
				oReq.open($scope.method, $scope.uploadUrl, true);
				oReq.onload = function(oEvent) {
					if (oReq.status >= 200 && oReq.status < 300) {
						deferred.resolve();
					} else {
						deferred.reject();
					}
					$scope.$apply();
				};
				
				oReq.send(oData);
				$scope.$emit('UploadingFile', deferred.promise)
			};
			
			this.show = function()
			{
				$('input[type="file"]', $element).click();
			};
		},
		template : '<div><input name="{{fileName}}" type="file" style="display:none"><div ng-transclude></div></div>',
		link : function(scope, element, attrs)
		{
			scope.valid = false;
			var $upload = $('input[type="file"]', element); 

			scope.clickUpload = function()
			{
				$upload.click();
			}
			
			$upload.change(function(){
				var fileList = this.files; 
				scope.fileReady = true;
				scope.valid = true;
				
				scope.$apply();
			});
		}
	};
}]);


cykloModule.directive('cykloUploadSubmit', [function(){	
	return {
		require : "^cykloUpload",
		link : function(scope, element, attrs, cykloUploadCtrl)
		{
			element.bind('click', function(){
				cykloUploadCtrl.uploadFile();
			});
			
			scope.$on('UploadSubmit', function(event){
				console.log('got upload submit event');
				cykloUploadCtrl.uploadFile();
			});
		}
	}
}]);

cykloModule.directive('cykloUploadShow', [function(){
	return {
		require : "^cykloUpload",
		link : function(scope, element, attrs, cykloUploadCtrl)
		{
			element.bind('click', function(){
				cykloUploadCtrl.show();
			});
		
		}
	}
}]);