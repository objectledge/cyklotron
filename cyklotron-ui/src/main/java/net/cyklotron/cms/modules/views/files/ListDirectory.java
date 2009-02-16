package net.cyklotron.cms.modules.views.files;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.ResourceListTableModel;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableException;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.files.FilesException;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.util.ProtectedViewFilter;

/**
 * Directory listing screen.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: ListDirectory.java,v 1.8 2007-12-27 17:32:08 rafal Exp $
 */
public class ListDirectory
    extends BaseFilesScreen
{

    
    private final RelatedService relatedService;
    public ListDirectory(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, FilesService filesService, RelatedService relatedService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, filesService);
        this.relatedService = relatedService;
        
    }
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            long directoryId = parameters.getLong("dir_id",filesService.getFilesRoot(coralSession, getSite()).getId());
            Resource directory = coralSession.getStore().getResource(directoryId);
            templatingContext.put("current_directory",directory);
            Resource[] resources = coralSession.getStore().getResource(directory);
            TableState state = tableStateManager.getState(context, "cms:screens:files,ListDirectory");
            if(state.isNew())
            {
                state.setTreeView(false);
                state.setPageSize(10);
                state.setSortColumnName("name");
            }
            
            TableModel model = new ResourceListTableModel(resources, i18nContext.getLocale());
            try
            {
                ArrayList filters = new ArrayList();
                filters.add(new ProtectedViewFilter(coralSession, coralSession.getUserSubject()));
                TableTool helper = new TableTool(state, filters, model);
                templatingContext.put("table", helper);
            }
            catch(TableException e)
            {
                throw new ProcessingException("failed to initialize table", e);
            }
            templatingContext.put("relation_tool", new RelationTool(relatedService, coralSession));
        }
        catch(FilesException e)
        {
            throw new ProcessingException("FilesException ",e);
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("FilesException ",e);
        }
        catch(TableException e)
        {
            throw new ProcessingException("FilesException ",e);
        }
    } 
    
    public class RelationTool
    {
        private final CoralSession coralSession;
        private final RelatedService relatedService2;

        public RelationTool(RelatedService relatedService, CoralSession coralSession)
        {
            relatedService2 = relatedService;
            this.coralSession = coralSession;
        }
        
        public List getRelatedFrom(Resource res)
        {
            return Arrays.asList(relatedService.getRelatedFrom(coralSession, res));
        }
    }
}

