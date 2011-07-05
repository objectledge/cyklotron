/**
 * @author <a href="mailto:zwierzem@caltha.pl">Damian Gajda</a>
 * @version $Id: DeleteResource.js,v 1.1 2005-05-17 10:23:07 zwierzem Exp $
 */
/**
 * This displays a delete pormpt
 *
 * @param name <i>string</i> - name of a resource to be deleted
 * @param url <i>string</i> - url to the delete action
 */
function deleteResource(name, url)
{
    if(document._popupMenuSingleton != null)
    {
        document._popupMenuSingleton.hideMenu();
    }
    if(confirm("Czy napewno chcesz usunąć: '"+name+"'"))
    {
        document.location.href = url;
    }
}

