/*
 * This script was created by Rafa³ Krzewski (rkrzewsk@ngo.pl)
 * Copyright 2002
 *
 * $Id: CMSPopups.js,v 1.1 2005-01-28 02:45:19 pablo Exp $
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

function selectIndexPool(attribute, form, element, baseLink)
{
  window.propertySelector = new PropertySelector(attribute, form, element);
  getWinPopup('ChooseIndexPool').open(baseLink, 800, 400, 'center middle');
}

function layoutPreview(layoutId, baseLink)
{
  getWinPopup('LayoutPreview'+layoutId).open(baseLink, 800, 400, 'center middle');
}

