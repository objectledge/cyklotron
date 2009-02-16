package net.cyklotron.cms.modules.views.files;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.preferences.PreferencesService;


/**
 * Screen to configure files component.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id$
 */
public class FilesConf2
    extends BaseFilesScreen
{
    public FilesConf2(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, FilesService filesService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, filesService);
        
    }
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            Parameters componentConfig = prepareComponentConfig(parameters, templatingContext);
            long dirId = componentConfig.getLong("dir",-1);
            if(dirId != -1)
            {
                try
                {
                    Resource dir = coralSession.getStore().getResource(dirId);
                    templatingContext.put("directory",dir);
                }
                catch(EntityDoesNotExistException e)
                {
                    //non existing pool may be configured
                }
            }
            templatingContext.put("header", componentConfig.get("header",""));
        }
        catch(Exception e)
        {
            throw new ProcessingException("Component configuration exception ", e);
        }

    }
}
