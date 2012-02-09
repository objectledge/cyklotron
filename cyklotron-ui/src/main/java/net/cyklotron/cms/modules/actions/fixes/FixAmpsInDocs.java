package net.cyklotron.cms.modules.actions.fixes;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.encodings.HTMLEntityDecoder;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.documents.DocumentNodeResourceImpl;
import net.cyklotron.cms.modules.actions.structure.BaseStructureAction;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: FixAmpsInDocs.java,v 1.4 2007-11-18 21:24:37 rafal Exp $
 */
public class FixAmpsInDocs extends BaseStructureAction
{
    public FixAmpsInDocs(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService)
    {
        super(logger, structureService, cmsDataFactory, styleService);
    }
    
    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        String idsString = parameters.get("doc_id", "");
        String[] ids = idsString.split(",");
        
        HTMLEntityDecoder decoder = new HTMLEntityDecoder();
        
        for (int i = 0; i < ids.length; i++)
        {
            try
            {
                String id = ids[i].trim();
                long lId = Long.parseLong(id);
                DocumentNodeResource doc = DocumentNodeResourceImpl.getDocumentNodeResource(
                    coralSession, lId);
                //doc.setAbstract(decoder.decodeAndFixXML(doc.getAbstract()));
                doc.setContent(decoder.decodeAndFixXML(doc.getContent()));
                //doc.setDescription(decoder.decodeAndFixXML(doc.getDescription()));
                //doc.setEventPlace(decoder.decodeAndFixXML(doc.getEventPlace()));
                //doc.setFooter(decoder.decodeAndFixXML(doc.getFooter()));
                //doc.setKeywords(decoder.decodeAndFixXML(doc.getKeywords()));
                doc.setMeta(decoder.decodeAndFixXML(doc.getMeta()));
                //doc.setSubTitle(decoder.decodeAndFixXML(doc.getSubTitle()));
                //doc.setTitle(decoder.decodeAndFixXML(doc.getTitle()));
                //doc.setTitleCalendar(decoder.decodeAndFixXML(doc.getTitleCalendar()));
                doc.update();
                doc.clearCache();
            }
            catch(NumberFormatException e)
            {
                logger.error("token not a document id", e);
            }
            catch(EntityDoesNotExistException e)
            {
                logger.error("document not found", e);
            }
        }
    }
    
    /**
     * @{inheritDoc}
     */
    public boolean checkAccessRights(Context context)
        throws Exception
    {
        return false;
    }
}
