
package net.cyklotron.cms.modules.actions.documents;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import pl.caltha.forms.FormsService;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.aggregation.AggregationService;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.documents.DocumentNodeResourceImpl;
import net.cyklotron.cms.documents.DocumentService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 * This action copies document nodes during importing.
 * 
 * @author <a href="mailo:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: DocumentNodeAggregationUpdate.java,v 1.4.6.1 2005-07-26 09:00:33 pablo Exp $
 */
public class DocumentNodeAggregationUpdate extends BaseDocumentAction
{
    private AggregationService aggregationService;
    
    public DocumentNodeAggregationUpdate(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService, FormsService formsService,
        DocumentService documentService, AggregationService aggregationService)
    {
        super(logger, structureService, cmsDataFactory, styleService, formsService, documentService);
        this.aggregationService = aggregationService;
    }
        
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Subject subject = coralSession.getUserSubject();
        
        try
        {
            DocumentNodeResource target = getDestination(coralSession, parameters);
            DocumentNodeResource source = getSource(coralSession, parameters);
            target.setDescription(source.getDescription());
            target.setAbstract(source.getAbstract());
            target.setContent(source.getContent());
            target.setKeywords(source.getKeywords());
            target.setLang(source.getLang());
            target.setMeta(source.getMeta());
			target.setEventEnd(source.getEventEnd());
			target.setEventStart(source.getEventStart());
			target.setEventPlace(source.getEventPlace());
			target.setFooter(source.getFooter());
			target.setPriority(source.getPriority(structureService.getDefaultPriority()));
			target.setSubTitle(source.getSubTitle());
			target.setTitleCalendar(source.getTitleCalendar());
            if(!source.getName().equals(target.getName()))
            {
                coralSession.getStore().setName(target, source.getName());
            }
            if(!source.getOwner().equals(target.getOwner()))
            {
                coralSession.getStore().setOwner(target, source.getOwner());
            }
            target.update();
        }
        catch(EntityDoesNotExistException e)
        {
            logger.error("could not get destination or source resource", e);
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            return;
        }
        catch(InvalidResourceNameException e)
        {
            templatingContext.put("result", "navi_name_invalid");
            return;
        }        
        templatingContext.put("result","updated_successfully");
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Parameters parameters = RequestParameters.getRequestParameters(context);
        try
        {
            DocumentNodeResource target = getDestination(coralSession, parameters);
            Permission importPermission = coralSession.getSecurity()
                 .getUniquePermission("cms.aggregation.import");
            if(!coralSession.getUserSubject().hasPermission(target, importPermission))
            {
                return false;
            }
            return true;
        }
        catch(Exception e)
        {
            throw new ProcessingException("cannot check security", e);
        }
    }
    
    private DocumentNodeResource getSource(CoralSession coralSession, Parameters parameters)
        throws EntityDoesNotExistException
    {
        long id = parameters.getLong("src_id", -1);
        return DocumentNodeResourceImpl.getDocumentNodeResource(coralSession, id);
    }

    private DocumentNodeResource getDestination(CoralSession coralSession, Parameters parameters)
        throws EntityDoesNotExistException
    {
        long id = parameters.getLong("dst_id", -1);
        return DocumentNodeResourceImpl.getDocumentNodeResource(coralSession, id);
    }
}

