package net.cyklotron.cms.modules.actions.documents;

import net.cyklotron.cms.aggregation.AggregationException;
import net.cyklotron.cms.aggregation.AggregationService;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.documents.DocumentNodeResourceImpl;
import net.cyklotron.cms.structure.NavigationNodeAlreadyExistException;
import net.cyklotron.cms.structure.StructureException;
import net.cyklotron.cms.structure.StructureService;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.ValueRequiredException;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * This action copies document nodes during importing.
 * 
 * @author <a href="mailo:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: DocumentNodeAggregationCopy.java,v 1.1 2005-01-24 04:34:39 pablo Exp $
 */
public class DocumentNodeAggregationCopy extends BaseDocumentAction
{
    private AggregationService aggregationService;
    /** structure service */
    private StructureService structureService;
    
    public DocumentNodeAggregationCopy()
    {
        aggregationService = (AggregationService)broker.getService(AggregationService.SERVICE_NAME);
        structureService = (StructureService)(broker.getService(StructureService.SERVICE_NAME));
    }
    
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        Subject subject = coralSession.getUserSubject();
        
        try
        {
            DocumentNodeResource parent = getTargetParent(data);
            DocumentNodeResource source = getSource(data);

            String targetName = parameters.get("target_name",source.getName());
            DocumentNodeResource target = 
                structureService.addDocumentNode(targetName, source.getTitle(), parent, subject);
        
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
			target.setPriority(source.getPriority(0));
			target.setSubTitle(source.getSubTitle());
			target.setTitleCalendar(source.getTitleCalendar());

            target.setSite(parent.getSite());
            target.update(subject);
            aggregationService.createImport(source, target, subject);
        }
        catch(NavigationNodeAlreadyExistException e)
        {
            route(data, "aggregation,ImportTarget", "navi_name_repeated");
            return;
        }
        catch(StructureException e)
        {
            log.error("problem copying the document", e);
            templatingContext.put("result","exception");
            templatingContext.put("trace",StringUtils.stackTrace(e));
            return;
        }
        catch(ValueRequiredException e)
        {
            log.error("some values could not be set", e);
            templatingContext.put("result","exception");
            templatingContext.put("trace",StringUtils.stackTrace(e));
            return;
        }
        catch(EntityDoesNotExistException e)
        {
            log.error("could not get parent or source resource", e);
            templatingContext.put("result","exception");
            templatingContext.put("trace",StringUtils.stackTrace(e));
            return;
        }
        catch(AggregationException e)
        {
            log.error("problem creating import information", e);
            templatingContext.put("result","exception");
            templatingContext.put("trace",StringUtils.stackTrace(e));
            return;
        }
        templatingContext.put("result","copied_successfully");
    }

    public boolean checkAccess(RunData data)
    throws ProcessingException
    {
        try
        {
            DocumentNodeResource source = getSource(data);
            DocumentNodeResource parent = getTargetParent(data);
            return aggregationService.canImport(source, parent, coralSession.getUserSubject());
        }
        catch(Exception e)
        {
            throw new ProcessingException("cannot check security", e);
        }
    }
    
    private DocumentNodeResource getSource(RunData data)
    throws EntityDoesNotExistException
    {
        long id = parameters.getLong("res_id", -1);
        return DocumentNodeResourceImpl.getDocumentNodeResource(coralSession, id);
    }

    private DocumentNodeResource getTargetParent(RunData data)
    throws EntityDoesNotExistException
    {
        long id = parameters.getLong("parent_id", -1);
        return DocumentNodeResourceImpl.getDocumentNodeResource(coralSession, id);
    }
}

