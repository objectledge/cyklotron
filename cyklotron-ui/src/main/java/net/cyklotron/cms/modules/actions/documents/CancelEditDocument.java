package net.cyklotron.cms.modules.actions.documents;

import org.objectledge.pipeline.ProcessingException;

import pl.caltha.forms.Instance;

/**
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: CancelEditDocument.java,v 1.1 2005-01-24 04:34:39 pablo Exp $
 */
public class CancelEditDocument extends BaseDocumentAction
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        // prepare needed variables
        Instance instance = getInstance(data);

        // kill da instance
        formService.removeInstance(data, instance);
        
        // return to original screen
        restoreView(data);
    }
}
