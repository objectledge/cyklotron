package net.cyklotron.cms.category.components;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.labeo.services.resource.CoralSession;
import net.labeo.services.resource.query.MalformedQueryException;
import net.labeo.services.resource.query.QueryResults;
import net.labeo.services.resource.query.QueryResults.Row;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * This class contains logic of component which displays lists of documents assigned
 * to queried categories.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: DocumentResourceList.java,v 1.2 2005-01-18 17:38:23 pablo Exp $
 */
public class DocumentResourceList
extends ResourceList
{
	public DocumentResourceList(CoralSession resourceService, CategoryQueryService categoryQueryService)
	{
        super(resourceService, categoryQueryService);
	}

    public BaseResourceListConfiguration createConfig(RunData data)
    throws ProcessingException
    {
        return new DocumentResourceListConfiguration();
    }
    
    private CategoryQueryResource categoryQuery;
    private boolean categoryQuerySought = false;

    public String getTableStateName(RunData data)
    {
        return "net.cyklotron.cms.category.document_resource_list";
    }

    private static String[] resourceClasses = { DocumentNodeResource.CLASS_NAME };
    /* (non-Javadoc)
     * @see net.cyklotron.cms.modules.components.category.BaseResourceList#getResourceClasses(net.labeo.webcore.RunData, net.cyklotron.cms.category.BaseResourceListConfiguration)
     */
    protected String[] getResourceClasses(RunData data, BaseResourceListConfiguration config)
	throws ProcessingException
    {
        int offset = ((DocumentResourceListConfiguration) config).getPublicationTimeOffset();
        if(offset == -1)
        {
            return resourceClasses;
        }
        return null;
    }
    
    public Set getIdSet(RunData data, BaseResourceListConfiguration config)
        throws ProcessingException
    {
        CmsData cmsData = CmsData.getCmsData(data);

        String date = null;
        int offset = ((DocumentResourceListConfiguration) config).getPublicationTimeOffset();
        if(offset != -1)
        {
            Date now = cmsData.getDate();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            calendar.add(Calendar.DAY_OF_MONTH, -offset);

            SimpleDateFormat df = 
                new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", new Locale("en","US"));

            date = df.format(calendar.getTime());

            try
            {
                QueryResults res = resourceService.getQuery().executeQuery(
                    "FIND RESOURCE FROM documents.document_node WHERE validity_start >= '"+date+"'");
                Set set = new HashSet(1024);
                for (Iterator iter = res.iterator(); iter.hasNext();)
                {
                    long[] ids = ((Row) iter.next()).getIdArray();
                    for (int i = 0; i < ids.length; i++)
                    {
                        set.add(new Long(ids[i]));
                    }
                }
                return set;
            }
            catch (MalformedQueryException e)
            {
                // should not happen
                return null;
            }
        }
        return null;
    }    
}
