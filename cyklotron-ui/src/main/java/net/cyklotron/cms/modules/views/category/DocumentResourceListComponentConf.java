package net.cyklotron.cms.modules.views.category;

import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.category.components.BaseResourceListConfiguration;
import net.cyklotron.cms.category.components.DocumentResourceListConfiguration;

/**
 * Configuration screen for DocumentResourceList component.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: DocumentResourceListComponentConf.java,v 1.1 2005-01-24 04:34:27 pablo Exp $
 */
public class DocumentResourceListComponentConf extends BaseResourceListComponentConf
{
    /* (non-Javadoc)
     * @see net.cyklotron.cms.modules.screens.category.BaseResourceListComponentConf#getConfig(net.labeo.webcore.RunData)
     */
    protected BaseResourceListConfiguration getConfig(RunData data) throws ProcessingException
    {
		return DocumentResourceListConfiguration.getConfig(data);
    }
}
