package net.cyklotron.cms.aggregation.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import net.cyklotron.cms.CmsNodeResourceImpl;
import net.cyklotron.cms.CmsTool;
import net.cyklotron.cms.ProtectedResource;
import net.cyklotron.cms.aggregation.AggregationConstants;
import net.cyklotron.cms.aggregation.AggregationException;
import net.cyklotron.cms.aggregation.AggregationService;
import net.cyklotron.cms.aggregation.ImportResource;
import net.cyklotron.cms.aggregation.ImportResourceImpl;
import net.cyklotron.cms.aggregation.RecommendationCommentResource;
import net.cyklotron.cms.aggregation.RecommendationCommentResourceImpl;
import net.cyklotron.cms.aggregation.RecommendationResource;
import net.cyklotron.cms.aggregation.RecommendationResourceImpl;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.integration.ResourceClassResource;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.util.CmsResourceClassFilter;

import org.jcontainer.dna.Logger;
import org.objectledge.ComponentInitializationError;
import org.objectledge.authentication.UserManager;
import org.objectledge.context.Context;
import org.objectledge.coral.BackendException;
import org.objectledge.coral.entity.EntityInUseException;
import org.objectledge.coral.event.ResourceDeletionListener;
import org.objectledge.coral.query.QueryResults;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.Resource;


/**
 * A generic implementation of the aggregation service.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: AggregationServiceImpl.java,v 1.5 2005-01-28 05:59:36 pablo Exp $
 */
public class AggregationServiceImpl
    implements AggregationService, ResourceDeletionListener
{
    protected UserManager userManager;
    
    protected IntegrationService integrationService;
    
    protected Logger log;

    protected Subject anonymous;

    protected SiteService siteService;

    protected Role importerRole;
    
    protected Permission importPermission;
    
    protected CoralSessionFactory sessionFactory;
    
    protected Context context;
    
    public AggregationServiceImpl(Context context, CoralSessionFactory sessionFactory, 
        Logger logger, UserManager userManager, IntegrationService integrationService, 
        SiteService siteService)
    {
        this.context = context;
        this.sessionFactory = sessionFactory;
        this.userManager = userManager;
        this.integrationService = integrationService;
        this.log = logger;
        this.siteService = siteService;
        CoralSession coralSession = sessionFactory.getRootSession();
        try
        {
            anonymous = coralSession.getSecurity().getSubject(userManager.getAnonymousAccount().getName());
            importerRole = coralSession.getSecurity().
                getUniqueRole("cms.aggregation.importer");
            importPermission = coralSession.getSecurity().
                getUniquePermission("cms.aggregation.import");
            //TODO we should move it to start....
            coralSession.getEvent().addResourceDeletionListener(this, null);
        }
        catch(Exception e)
        {
            throw new ComponentInitializationError("Could not find the anonymous user");
        }
        finally
        {
            coralSession.close();
        }
    }
    
    /* overriden */
    public RecommendationResource[] getPendingRecommendations(CoralSession coralSession, SiteResource site)
        throws AggregationException
    {
        Resource[] res = coralSession.getStore().getResource(site, "applications");
        if(res.length == 0)
        {
            throw new AggregationException("failed to lookup applications node in site "+site.getName());
        }
        res = coralSession.getStore().getResource(res[0], "aggregation");
        if(res.length == 0)
        {
            return new RecommendationResource[0];
        }
        res = coralSession.getStore().getResource(res[0], "recommendations");
        if(res.length == 0)
        {
            return new RecommendationResource[0];
        }
        res = coralSession.getStore().getResource(res[0]);
        ArrayList temp = new ArrayList(res.length);
        for(int i=0; i<res.length; i++)
        {
            if(((RecommendationResource)res[i]).getStatus() == 
                AggregationConstants.RECOMMENDATION_PENDING)
            {
                temp.add(res[i]);
            }
        }
        RecommendationResource[] result = new RecommendationResource[temp.size()];
        temp.toArray(result);
        return result;
    }
        
    /* overriden */
    public RecommendationResource[] getSubmittedRecommendations(CoralSession coralSession,  
        SiteResource site, Subject subject)
        throws AggregationException
    {
        try
        {
            QueryResults rset = coralSession.getQuery().executeQuery(
                "FIND RESOURCE FROM cms.aggregation.recommendation "+
                "WHERE created_by = "+subject.getIdString()+" AND sourceSite = "+site.getIdString());
            Resource[] res = rset.getArray(1);
            RecommendationResource[] result = new RecommendationResource[res.length];
            System.arraycopy(res, 0, result, 0, res.length);
            return result;
        }
        catch(Exception e)
        {
            throw new AggregationException("failed to lookup recommendations", e);
        }
    }
            
    /* overriden */
    public RecommendationCommentResource[] getComments(CoralSession coralSession, RecommendationResource 
        rec)
        throws AggregationException
    {
        Resource[] res = coralSession.getStore().getResource(rec);
        ArrayList temp = new ArrayList();
        temp.addAll(Arrays.asList(res));
        Collections.sort(temp, new Comparator()
            {
                public int compare(Object o1, Object o2)
                {
                    RecommendationCommentResource r1 = 
                        (RecommendationCommentResource)o1;
                    RecommendationCommentResource r2 = 
                        (RecommendationCommentResource)o2;
                    return r1.getCreationTime().compareTo(r2.getCreationTime());
                }
            });
        RecommendationCommentResource[] result = 
            new RecommendationCommentResource[temp.size()];
        temp.toArray(result);
        return result;        
    }
            
    /* overriden */
    public void submitRecommendation(CoralSession coralSession, Resource resource, SiteResource site, 
        String comment, Subject subject)
        throws AggregationException
    {
        SiteResource srcSite = CmsTool.getSite(resource);
        if(srcSite == null)
        {
            throw new AggregationException("failed to determine site for #"+resource.getIdString());        
        }
        try
        {            
            Resource[] res = coralSession.getStore().getResource(site, "applications");
            Resource p;
            if(res.length == 0)
            {
                throw new AggregationException("failed to lookup applications node in site "+site.getName());
            }
            else
            {
                p = res[0];
            }
            res = coralSession.getStore().getResource(p, "aggregation");
            if(res.length == 0)
            {
                p = CmsNodeResourceImpl.createCmsNodeResource(coralSession, "aggregation", p);
            }
            else
            {
                p = res[0];
            }
            res = coralSession.getStore().getResource(p, "recommendations");
            if(res.length == 0)
            {
                p = CmsNodeResourceImpl.createCmsNodeResource(coralSession, "recommendations", p);
            }
            else
            {
                p = res[0];
            }
            RecommendationResource rec =
                RecommendationResourceImpl.createRecommendationResource(
                    coralSession,
                    resource.getIdString(),
                    p,
                    resource,
                    srcSite,
                    AggregationConstants.RECOMMENDATION_PENDING);
            if(comment != null && comment.length() > 0)
            {
                addComment(coralSession, rec, comment, subject);
            }
        }
        catch(Exception e)
        {
            throw new AggregationException("failed to create recommendation", e);    
        }
    }
        
    /* overriden */
    public void rejectRecommendation(CoralSession coralSession, RecommendationResource rec, 
        String comment, Subject subject)
        throws AggregationException
    {
        if(comment != null && comment.length() > 0)
        {
            addComment(coralSession, rec, comment, subject);
        }
        else
        {
            throw new AggregationException("empty comment");
        }
        try
        {
            rec.setStatus(AggregationConstants.RECOMMENDATION_REJECTED);
            rec.update();
        }
        catch(Exception e)
        {
            throw new AggregationException("failed to modify recommendation resource", e);
        }
    }

    /* overriden */
    public void resubmitRecommendation(CoralSession coralSession, RecommendationResource rec, 
        String comment, Subject subject)
        throws AggregationException
    {
        if(comment != null && comment.length() > 0)
        {
            addComment(coralSession, rec, comment, subject);
        }
        else
        {
            throw new AggregationException("empty comment");
        }
        try
        {
            rec.setStatus(AggregationConstants.RECOMMENDATION_PENDING);
            rec.update();
        }
        catch(Exception e)
        {
            throw new AggregationException("failed to modify recommendation resource", e);
        }
    }
    
    /* overriden */
    public void discardRecommendation(CoralSession coralSession, RecommendationResource rec, 
        Subject subject)
        throws AggregationException
    {
        RecommendationCommentResource[] comments = getComments(coralSession, rec);
        try
        {
            for (int i = 0; i < comments.length; i++)
            {
                coralSession.getStore().deleteResource(comments[i]);
            }
            coralSession.getStore().deleteResource(rec);
        }
        catch(Exception e)
        {
            throw new AggregationException("failed to delete recommendation resource", e);
        }
    }

    /* overriden */
    public void createImport(CoralSession coralSession, Resource source, Resource destination, Subject subject)
        throws AggregationException
    {
        SiteResource srcSite = CmsTool.getSite(source);
        if(srcSite == null)
        {
            throw new AggregationException("failed to determine site for #"+source.getIdString());        
        }
        SiteResource destSite = CmsTool.getSite(destination);
        if(destSite == null)
        {
            throw new AggregationException("failed to determine site for #"+destination.getIdString());        
        }

        try
        {            
            Resource[] res = coralSession.getStore().getResource(destSite, "applications");
            Resource p;
            if(res.length == 0)
            {
                throw new AggregationException("failed to lookup applications node in site "+destSite.getName());
            }
            else
            {
                p = res[0];
            }
            res = coralSession.getStore().getResource(p, "aggregation");
            if(res.length == 0)
            {
                p = CmsNodeResourceImpl.createCmsNodeResource(coralSession, "aggregation", p);
            }
            else
            {
                p = res[0];
            }
            Resource aggregationNode = p;
            res = coralSession.getStore().getResource(p, "imports");
            if(res.length == 0)
            {
                p = CmsNodeResourceImpl.createCmsNodeResource(coralSession, "imports", p);
            }
            else
            {
                p = res[0];
            }
            ImportResourceImpl.createImportResource(
                coralSession,
                source.getIdString(),
                p,
                srcSite,
                source.getId(),
                destination);
            // auto-clean recommendation if present
            res = coralSession.getStore().getResource(aggregationNode, "recommendations");
            if(res.length != 0)
            {
                p = res[0];
                res = coralSession.getStore().getResource(p, source.getIdString());
                if(res.length == 1)
                {
                    discardRecommendation(coralSession, (RecommendationResource)res[0], subject);
                }
                if(res.length > 1)
                {
                    log.error("inconsistent data - more than one recommendation for #"+source+
                              " to "+destSite.getName());
                }
            }
        }
        catch(Exception e)
        {
            throw new AggregationException("failed to create import resource", e);
        }
    }
    
    /* overriden */    
    public ImportResource[] getImports(CoralSession coralSession, SiteResource destination)
       throws AggregationException
    {
        Resource[] res = coralSession.getStore().getResource(destination, "applications");
        if(res.length == 0)
        {
            throw new AggregationException("failed to lookup applications node in site "+destination.getName());
        }
        res = coralSession.getStore().getResource(res[0], "aggregation");
        if(res.length == 0)
        {
            return new ImportResource[0];
        }
        res = coralSession.getStore().getResource(res[0], "imports");
        if(res.length == 0)
        {
            return new ImportResource[0];
        }
        res = coralSession.getStore().getResource(res[0]);
        ImportResource[] result = new ImportResource[res.length];
        System.arraycopy(res, 0, result, 0, res.length);
        return result;
    }

    /* overriden */    
     public ImportResource[] getExports(CoralSession coralSession, SiteResource source)
        throws AggregationException
     {
         try
         {
             QueryResults rset = coralSession.getQuery().executeQuery(
                 "FIND RESOURCE FROM cms.aggregation.import "+
                 "WHERE sourceSite = "+source.getIdString());
             Resource[] res = rset.getArray(1);
             ImportResource[] result = new ImportResource[res.length];
             System.arraycopy(res, 0, result, 0, res.length);
             return result;
         }
         catch(Exception e)
         {
             throw new AggregationException("failed to lookup recommendations", e);
         }
     }
    
    /* overriden */    
    public boolean isRecommendedTo(CoralSession coralSession, Resource resource, SiteResource site)
        throws AggregationException
    {
        Resource[] res = coralSession.getStore().getResource(site, "applications");
        if(res.length == 0)
        {
            throw new AggregationException("failed to lookup applications node in site "+site.getName());
        }
        res = coralSession.getStore().getResource(res[0], "aggregation");
        if(res.length == 0)
        {
            return false;
        }
        res = coralSession.getStore().getResource(res[0], "recommendations");
        if(res.length == 0)
        {
            return false;
        }
        res = coralSession.getStore().getResource(res[0], resource.getIdString());
        return res.length > 0;
    }
    
    /* overriden */    
    public SiteResource[] getValidImportSites(CoralSession coralSession, Resource res)
        throws AggregationException
    {
        SiteResource[] allSites = siteService.getSites(coralSession);
        ArrayList temp = new ArrayList(allSites.length);
        for (int i = 0; i < allSites.length; i++)
        {
            if(allSites[i].getTeamMember().isSubRole(importerRole) &&
               allSites[i].getSiteRole().hasPermission(res, importPermission))
            {
                temp.add(allSites[i]);
            }
        }
        SiteResource[] result = new SiteResource[temp.size()];
        temp.toArray(result);
        return result;
    }

    /* overriden */    
    public SiteResource[] getValidRecommendationSites(CoralSession coralSession, Resource res)
        throws AggregationException
    {
        SiteResource[] allSites = siteService.getSites(coralSession);
        ArrayList temp = new ArrayList(allSites.length);
        for (int i = 0; i < allSites.length; i++)
        {
            if(allSites[i].getTeamMember() == null)
            {
                throw new IllegalStateException("The site: '"+allSites[i].getName()+"' has null TeamMember role");
            }
            if(allSites[i].getSiteRole() == null)
            {
                throw new IllegalStateException("The site: '"+allSites[i].getName()+"' has null SiteRole role");
            }
            if(allSites[i].getTeamMember().isSubRole(importerRole) &&
               allSites[i].getSiteRole().hasPermission(res, importPermission) &&
                !isRecommendedTo(coralSession, res, allSites[i]))
            {
                temp.add(allSites[i]);
            }
        }
        SiteResource[] result = new SiteResource[temp.size()];
        temp.toArray(result);
        return result;
    }    
    
    // implementation ///////////////////////////////////////////////////////
    
    /**
     * Appends a comment to the specified recommendation resource.
     * 
     * @param rec the recommendation.
     * @param comment the comments's contents
     * @param subject the subject that makes the comment.
     * @throws AggregationException if the operation fails.
     */
    protected void addComment(CoralSession coralSession, RecommendationResource rec, String comment, 
        Subject subject)
        throws AggregationException
    {
        try
        {
            String name = ""+System.currentTimeMillis();
            RecommendationCommentResourceImpl.createRecommendationCommentResource(
                coralSession,
                name,
                rec,
                comment);
        }
        catch(Exception e)
        {
            throw new AggregationException("failed to create comment resource", e);
        }
    }

    /** 
     * Remove import information if the resource was imported from another site.
     * 
     * @param resource the resource being deleted.
     */
    public void resourceDeleted(Resource resource)
    {
        CoralSession coralSession = sessionFactory.getRootSession();
        try
        {
            // imports
            QueryResults rset = coralSession.getQuery().executeQuery(
                "FIND RESOURCE FROM cms.aggregation.import "+
                "WHERE destination = "+resource.getIdString());
            Resource[] res = rset.getArray(1);
            if(res.length > 1)
            {
                StringBuffer imports = new StringBuffer();
                for(int i=0; i<res.length; i++)
                {
                    imports.append('#').append(res[i].getIdString()).
                        append(", ");
                }
                imports.setLength(imports.length()-2);
                throw new BackendException("inconsitent import information: #"+resource.getIdString()+
                    " listed as destination in "+imports.toString()); 
            }
            if(res.length == 1)
            {
                try
                {
                    log.debug("deleting import record for resource #"+resource.getIdString());
                    coralSession.getStore().deleteResource(res[0]);
                }
                catch(EntityInUseException e)
                {
                    throw new BackendException("failed to delete import record", e);
                }
            }
            // recommendations
            rset = coralSession.getQuery().executeQuery(
                "FIND RESOURCE FROM cms.aggregation.recommendation "+
                "WHERE source = "+resource.getIdString());
            res = rset.getArray(1);
            try
            {
                for(int i=0; i<res.length; i++)
                {
                    log.debug("deleting recommendation record for resource #"+resource.getIdString());
                    RecommendationCommentResource[] comments = 
                        getComments(coralSession, (RecommendationResource)res[i]);
                    for(int j = 0; j < comments.length; j++)
                    {
                        coralSession.getStore().deleteResource(comments[j]);
                    }
                    coralSession.getStore().deleteResource(res[i]);
                }
            }
            catch(EntityInUseException e)
            {
                throw new BackendException("failed to delete recommendation record", e);
            }
        }            
        catch(Exception e)
        {
                throw new BackendException("failed to lookup aggregation information", e);
        }
        finally
        {
            coralSession.close();
        }
    }

    /**
      * Checks whether the resource can be imported.
      * 
      * @param source the resource to be copied.
      * @param target the target parent resource.
      * @param subject the subject.
      * @return resources exported from a site.
      * @throws AggregationException if the operation fails.
      */
    public boolean canImport(CoralSession coralSession, Resource source, Resource target, Subject subject)
    {
        Permission importPermission = coralSession.getSecurity()
            .getUniquePermission("cms.aggregation.import");
        if(!subject.hasPermission(target, importPermission))
        {
            log.debug("Cannot import - no import permission on traget");
            return false;
        }
        if(source instanceof ProtectedResource)
        {
            if(!((ProtectedResource)source).canView(context, anonymous))
            {
                log.debug("Cannot import - no view permission for anonymous");
                return false;
            }
        }
        if(target instanceof ProtectedResource)
        {
            if(!((ProtectedResource)target).canAddChild(context, subject))
            {
                log.debug("Cannot import - no add child permission on traget");
                return false;
            }
        }
        SiteResource importer = CmsTool.getSite(target);
        if(!importer.getTeamMember().isSubRole(importerRole))
        {
            log.debug("Cannot import - target site is not the importer");
            return false;
        }
        if(!importer.getSiteRole().hasPermission(source, importPermission))
        {
            log.debug("Cannot import - target site is not allowed to import this resource");
            return false;
        }
        ResourceClassResource resourceClassResource = integrationService.
            getResourceClass(coralSession, source.getResourceClass());
        String[] classes = resourceClassResource.getAggregationParentClassesList();
        CmsResourceClassFilter filter = new CmsResourceClassFilter(coralSession, integrationService, classes);
        if(!filter.accept(target))
        {
            log.debug("Cannot import - parent resource is not instance of accepted class");
            return false;
        }
        return true;
    }
    
}
