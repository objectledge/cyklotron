package net.cyklotron.cms.modules.actions.documents;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;
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
import net.cyklotron.cms.structure.NavigationNodeAlreadyExistException;
import net.cyklotron.cms.structure.StructureException;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 * This action copies document nodes during importing.
 * 
 * @author <a href="mailo:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: DocumentNodeAggregationRecursiveCopy.java,v 1.1.2.2 2005-08-09 04:28:54 rafal Exp $
 */
public class DocumentNodeAggregationRecursiveCopy extends BaseDocumentAction
{
    private AggregationService aggregationService;
    
    public DocumentNodeAggregationRecursiveCopy(Logger logger, StructureService structureService,
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
        try
        {
            DocumentNodeResource parent = getTargetParent(coralSession, parameters);
            DocumentNodeResource source = getSource(coralSession, parameters);
            String targetName = parameters.get("target_name",source.getName());
            copySubTree(coralSession, source, parent, targetName);
        }
        catch(NavigationNodeAlreadyExistException e)
        {
            route(mvcContext, templatingContext, "aggregation.ImportTarget", "navi_name_repeated");
            return;
        }
        catch(InvalidResourceNameException e)
        {
            route(mvcContext, templatingContext, "aggregation.ImportTarget", "navi_name_invalid");
            return;
        }
        catch(StructureException e)
        {
            logger.error("problem copying the document", e);
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            return;
        }
        catch(ValueRequiredException e)
        {
            logger.error("some values could not be set", e);
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            return;
        }
        catch(EntityDoesNotExistException e)
        {
            logger.error("could not get parent or source resource", e);
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            return;
        }
        catch(Exception e)
        {
            logger.error("problem creating import information", e);
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            return;
        }
        templatingContext.put("result","copied_successfully");
    }

    private void copySubTree(CoralSession coralSession, DocumentNodeResource source,
        DocumentNodeResource parent, String dstName)
        throws Exception
    {
        Subject subject = coralSession.getUserSubject();
        if(aggregationService.canImport(coralSession, source, parent, subject))
        {
            DocumentNodeResource target = structureService.addDocumentNode(coralSession,
                dstName, source.getTitle(), parent, subject);
            target.setDescription(source.getDescription());
            target.setSequence(0);
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
            target.setSite(parent.getSite());
            target.update();
            aggregationService.createImport(coralSession, source, target, subject);
            Resource[] children = coralSession.getStore().getResource(source);
            for(Resource child: children)
            {
                if(child instanceof DocumentNodeResource)
                {
                    copySubTree(coralSession, (DocumentNodeResource)child, target, child.getName());
                }
            }
        }        
    }
    
    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Parameters parameters = RequestParameters.getRequestParameters(context);
        try
        {
            DocumentNodeResource source = getSource(coralSession, parameters);
            DocumentNodeResource parent = getTargetParent(coralSession, parameters);
            return aggregationService.canImport(coralSession, source, parent, coralSession.getUserSubject());
        }
        catch(Exception e)
        {
            throw new ProcessingException("cannot check security", e);
        }
    }
    
    private DocumentNodeResource getSource(CoralSession coralSession, Parameters parameters)
    throws EntityDoesNotExistException
    {
        long id = parameters.getLong("res_id", -1);
        return DocumentNodeResourceImpl.getDocumentNodeResource(coralSession, id);
    }

    private DocumentNodeResource getTargetParent(CoralSession coralSession, Parameters parameters)
    throws EntityDoesNotExistException
    {
        long id = parameters.getLong("parent_id", -1);
        return DocumentNodeResourceImpl.getDocumentNodeResource(coralSession, id);
    }
}

