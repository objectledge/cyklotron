package net.cyklotron.cms.structure.vote;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.relation.MalformedRelationQueryException;
import org.objectledge.coral.relation.ResourceIdentifierResolver;
import org.objectledge.coral.session.CoralSession;

import net.cyklotron.cms.category.query.CategoryQueryException;
import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.site.SiteException;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureException;
import net.cyklotron.cms.structure.StructureService;

import bak.pcj.LongIterator;
import bak.pcj.map.LongKeyMap;
import bak.pcj.map.LongKeyOpenHashMap;
import bak.pcj.set.LongOpenHashSet;
import bak.pcj.set.LongSet;

public class CommunityVote
{
    private final StructureService structureService;

    private final CategoryQueryService categoryService;

    private final SiteService siteService;

    private final Logger log;

    public CommunityVote(StructureService structureService,
        CategoryQueryService categoryQueryService, SiteService siteService, Logger log)
    {
        this.structureService = structureService;
        this.categoryService = categoryQueryService;
        this.siteService = siteService;
        this.log = log;
    }

    public Map<SortOrder, List<NavigationNodeResource>> getResults(Date cutoffDate,
        Set<SortOrder> sortOrders, Comparator<NavigationNodeResource> secondarySortOrder,        
        CoralSession coralSession, SiteResource singleSite, CategoryQueryResource categoryQuery)
        throws StructureException
    {
        Map<SortOrder, List<NavigationNodeResource>> result = new HashMap<SortOrder, List<NavigationNodeResource>>();
        Map<SortOrder, SortHandler<? >> handlers = new HashMap<SortOrder, SortHandler<? >>(
            sortOrders.size());
        for(SortOrder sortOrder : sortOrders)
        {
            handlers.put(sortOrder, newHandlerInstance(sortOrder));
        }

        LongKeyMap documents = new LongKeyOpenHashMap();
        LongSet docIds = null;
        if(cutoffDate != null)
        {
            docIds = structureService.getDocumentsValidAtOrAfter(cutoffDate, coralSession);
        }
        if(singleSite != null)
        {
            docIds = restrictDocumentSet(Collections.singletonList(singleSite), docIds,
                coralSession);
        }
        if(categoryQuery != null)
        {
            docIds = restrictDocumentSet(categoryQuery, docIds, coralSession);
        }
        if(docIds != null)
        {
            LongIterator i = docIds.iterator();
            while(i.hasNext())
            {
                long id = i.next();
                try
                {
                    NavigationNodeResource document = (NavigationNodeResource)coralSession
                        .getStore().getResource(id);
                    documents.put(id, document);
                    for(SortHandler<?> handler : handlers.values())
                    {
                        handler.process(id, document.getVotesPositive(0),
                            document.getVotesNegative(0));
                    }
                }
                catch(EntityDoesNotExistException e)
                {
                    // ignore
                }
            }
        }

        for(SortOrder sortOrder : sortOrders)
        {
            List<NavigationNodeResource> nodes = new ArrayList<NavigationNodeResource>();
            SortHandler<? > handler = handlers.get(sortOrder);
            List<LongSet> equivClasses = handler.results();
            for(LongSet equivClass : equivClasses)
            {
                List<NavigationNodeResource> equivClassDocs = new ArrayList<NavigationNodeResource>(
                    equivClass.size());
                for(LongIterator docIter = equivClass.iterator(); docIter.hasNext();)
                {
                    long id = docIter.next();
                    NavigationNodeResource doc = (NavigationNodeResource)documents.get(id);
                    equivClassDocs.add(doc);
                }
                Collections.sort(equivClassDocs, secondarySortOrder);
                nodes.addAll(equivClassDocs);
            }
            result.put(sortOrder, nodes);
        }

        return result;
    }

    private LongSet restrictDocumentSet(Collection<SiteResource> acceptedSites, LongSet idSet,
        CoralSession coralSession)
        throws StructureException
    {
        StringBuffer query = new StringBuffer();
        query.append("MAP('structure.SiteDocs'){");
        Iterator<SiteResource> i = acceptedSites.iterator();
        while(i.hasNext())
        {
            query.append(" RES(").append(i.next().getIdString()).append(") ");
            if(i.hasNext())
            {
                query.append("+ ");
            }
        }
        query.append("};");
        try
        {
            return coralSession.getRelationQuery().queryIds(query.toString(),
                new ResourceIdentifierResolver()
                {
                    @Override
                    public LongSet resolveIdentifier(String identifier)
                        throws EntityDoesNotExistException
                    {
                        LongSet set = new LongOpenHashSet(1);
                        set.add(Long.parseLong(identifier));
                        return set;
                    }
                }, idSet);
        }
        catch(MalformedRelationQueryException | EntityDoesNotExistException e)
        {
            throw new StructureException("failed to execute category query", e);
        }
    }

    private LongSet restrictDocumentSet(CategoryQueryResource categoryQuery, LongSet idSet,
        CoralSession coralSession)
        throws StructureException
    {
        try
        {
            idSet = categoryService.forwardQueryIds(coralSession, categoryQuery.getQuery(), idSet);
        }
        catch(CategoryQueryException e)
        {
            throw new StructureException("failed to execute category query", e);
        }

        Set<SiteResource> acceptedSites = new HashSet<SiteResource>();
        String[] acceptedSiteNames = categoryQuery.getAcceptedSiteNames();
        if(acceptedSiteNames != null && acceptedSiteNames.length > 0)
        {
            for(String acceptedSiteName : acceptedSiteNames)
            {
                SiteResource acceptedSite;
                try
                {
                    acceptedSite = siteService.getSite(coralSession, acceptedSiteName);
                    acceptedSites.add(acceptedSite);
                }
                catch(SiteException e)
                {
                    log.error("invalid accepted site " + acceptedSiteName + " in query definition"
                        + categoryQuery.toString());
                }
            }
        }

        // no sites = all are accepted
        if(acceptedSites.size() > 0)
        {
            idSet = restrictDocumentSet(acceptedSites, idSet, coralSession);
        }        
        return idSet;
    }

    private SortHandler<? > newHandlerInstance(SortOrder sortOrder)
    {
        switch(sortOrder)
        {
        case POSITIVE:
            return new PositiveSortHandler();
        case NEGATIVE:
            return new NegativeSortHandler();
        case TOTAL:
            return new TotalSortHander();
        case POSITIVE_RATIO:
            return new PositiveRatioSortHandler();
        case NEGATIVE_RATIO:
            return new NegativeRatioSortHandler();
        default:
            throw new IllegalArgumentException("internal error: " + sortOrder + " not supported");
        }
    }
}
