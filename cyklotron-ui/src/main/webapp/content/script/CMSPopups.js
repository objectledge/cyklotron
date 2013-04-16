/*
 * This script was created by Rafa� Krzewski (rkrzewsk@ngo.pl)
 * Copyright 2002
 *
 * $Id: CMSPopups.js,v 1.2 2008-10-30 15:49:30 rafal Exp $
 */
scriptLoader.loadCommon('WinPopup.js');
scriptLoader.loadCommon('Forms.js');
scriptLoader.loadCommon('PropertySelector.js');

function selectUser(attributeNames, form, elementNames, baseLink)
{
  window.propertySelector = new PropertySelector(attributeNames, form, elementNames);
  getWinPopup('UserList').open(baseLink, 800, 400, 'center middle');
}

function selectNode(attributeNames, form, elementNames, baseLink)
{
  window.propertySelector = new PropertySelector(attributeNames, form, elementNames);
  getWinPopup('NodeList').open(baseLink, 400, 600, 'center middle');
}

function selectItem(attribute, form, element, baseLink)
{
  window.propertySelector = new PropertySelector(attribute, form, element);
  getWinPopup('Directory').open(baseLink, 800, 400, 'center middle');
}

function selectFile(attribute, form, element, baseLink)
{
  window.propertySelector = new PropertySelector(attribute, form, element);
  getWinPopup('Directory').open(baseLink, 650, 550);
}

function selectCategoryQueryPool(attribute, form, element, baseLink)
{
  window.propertySelector = new PropertySelector(attribute, form, element);
  getWinPopup('ChooseCategoryQueryPool').open(baseLink, 800, 400, 'center middle');
}

function selectCategory(attribute, form, element, baseLink)
{
  window.propertySelector = new PropertySelector(attribute, form, element);
  getWinPopup('ChooseCategory').open(baseLink, 800, 400, 'center middle');
}

function selectCategories(attribute, form, element, baseLink, selected_element, res_class_name)
{
   baseLink += "&selected=" + document.getElementById(selected_element).value;
   baseLink += "&res_class_name=" + res_class_name;
   window.propertySelector = new PropertySelector(attribute, form, element, {
	   beforeValuesSet : function(propertySelector){ /* do nothing */ },
	   afterValuesSet : function(propertySelector){ 
		   if(jQuery)
		   {
			   jQuery(document.getElementById(selected_element)).change();
		   }
	   } 
   });
   getWinPopup('ChooseCategory').open(baseLink, 350, 0.9, 'right');
}

function selectIndexPool(attribute, form, element, baseLink)
{
  window.propertySelector = new PropertySelector(attribute, form, element);
  getWinPopup('ChooseIndexPool').open(baseLink, 800, 400, 'center middle');
}

function layoutPreview(layoutId, baseLink)
{
  getWinPopup('LayoutPreview'+layoutId).open(baseLink, 800, 400, 'center middle');
}

