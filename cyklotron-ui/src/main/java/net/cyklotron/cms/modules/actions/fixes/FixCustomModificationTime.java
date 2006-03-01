package net.cyklotron.cms.modules.actions.fixes;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.datatypes.DateAttributeHandler;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.database.Database;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.actions.structure.BaseStructureAction;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: FixCustomModificationTime.java,v 1.3 2006-03-01 11:43:06 pablo Exp $
 */
public class FixCustomModificationTime extends BaseStructureAction
{
    Database database;
    
    public FixCustomModificationTime(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService, Database database)
    {
        super(logger, structureService, cmsDataFactory, styleService);
        this.database = database;
    }
    
    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 1971);
        SimpleDateFormat df = new SimpleDateFormat(DateAttributeHandler.DATE_TIME_FORMAT);
        Set<Integer> set = new HashSet<Integer>();
        ResourceClass rc = null;
        AttributeDefinition ad = null;
        try
        {
            rc = coralSession.getSchema().getResourceClass("documents.document_node");
            ad = rc.getAttribute("customModificationTime");
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to retrieve schema metadata", e);
        }
        String query = "SELECT resource_id FROM coral_resource "+
            "WHERE NOT EXISTS (SELECT 1 FROM coral_generic_resource "+
            "WHERE resource_id = coral_resource.resource_id AND" +
            " attribute_definition_id = "+ad.getId()+") AND resource_class_id = "+rc.getId();
        Connection conn = null;
        try 
        {
            conn = database.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            int id;
            while(rs.next())
            {
                id = rs.getInt("resource_id");
                set.add(id);
            }
            rs.close();
            stmt.close();
        }
        catch (Exception e) 
        {
            throw new ProcessingException("Query Error ",e);
        }
        finally 
        {
            try 
            {
                conn.close();
            }
            catch(Exception e) 
            {
                throw new ProcessingException("Error releasing connection "+e);
            }
        }
        
        
        
        
        try
        {
            Iterator<Integer> it = set.iterator();
            while(it.hasNext())
            {
                Resource node = coralSession.getStore().getResource(it.next());
                if(node instanceof NavigationNodeResource)
                {
                    NavigationNodeResource nnr = (NavigationNodeResource)node;
                    if(nnr.getCustomModificationTime() == null)
                    {
                        logger.debug("fixing node: "+nnr.getPath());
                        nnr.setCustomModificationTime(nnr.getModificationTime());
                        nnr.update();
                    }
                }
            }
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to invoke fix");
        }
    }
}
