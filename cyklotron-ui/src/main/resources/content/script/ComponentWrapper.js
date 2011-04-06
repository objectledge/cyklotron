/**
 * Pupup menus for CMSComponentWrapper.
 *
 * $Id: ComponentWrapper.js,v 1.1 2005-01-28 02:45:19 pablo Exp $
 */
scriptLoader.loadCommon('PopupMenu.js');
scriptLoader.loadCommon('WinPopup.js');

function openWinPopup(name, url)
{
    getWinPopup(name).open(url, 600, 400, 'center middle');
    document._popupMenuSingleton.hideMenu();
}

