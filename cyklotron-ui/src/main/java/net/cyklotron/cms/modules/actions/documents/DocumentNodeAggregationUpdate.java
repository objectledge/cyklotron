package net.cyklotron.cms.modules.actions.documents;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Permission;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.aggregation.AggregationService;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.documents.DocumentNodeResourceImpl;
import net.cyklotron.cms.structure.StructureService;

/**
 * This action copies document nodes during importing.
 * 
 * @author <a href="mailo:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: DocumentNodeAggregationUpdate.java,v 1.1 2005-01-24 04:34:39 pablo Exp $
 */
public class DocumentNodeAggregationUpdate extends BaseDocumentAction
{
    private AggregationService aggregationService;
    /** structure service */
    private StructureService structureService;
    
    public DocumentNodeAggregationUpdate()
    {
        super();
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
            DocumentNodeResource target = getDestination(data);
            DocumentNodeResource source = getSource(data);
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
			target.setPriority(source.getPriority(0));
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
            target.update(subject);
        }
        catch(EntityDoesNotExistException e)
        {
            log.error("could not get destination or source resource", e);
            templatingContext.put("result","exception");
            templatingContext.put("trace",StringUtils.stackTrace(e));
            return;
        }
        templatingContext.put("result","updated_successfully");
    }

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        try
        {
            DocumentNodeResource target = getDestination(data);
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
    
    private DocumentNodeResource getSource(RunData data)
        throws EntityDoesNotExistException
    {
        long id = parameters.getLong("src_id", -1);
        return DocumentNodeResourceImpl.getDocumentNodeResource(coralSession, id);
    }

    private DocumentNodeResource getDestination(RunData data)
        throws EntityDoesNotExistException
    {
        long id = parameters.getLong("dst_id", -1);
        return DocumentNodeResourceImpl.getDocumentNodeResource(coralSession, id);
    }
}

