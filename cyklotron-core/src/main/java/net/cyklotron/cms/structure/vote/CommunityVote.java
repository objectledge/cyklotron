package net.cyklotron.cms.structure.vote;

import static java.lang.Math.max;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;

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
        LongSet docIds = structureService.getDocumentsValidAtOrAfter(cutoffDate, coralSession);
        if(singleSite != null || categoryQuery != null)
        {
            docIds = restrictDocumentSet(categoryQuery, singleSite, docIds, coralSession);
        }
        LongIterator i = docIds.iterator();
        while(i.hasNext())
        {
            long id = i.next();
            try
            {
                NavigationNodeResource document = (NavigationNodeResource)coralSession.getStore()
                    .getResource(id);
                documents.put(id, document);
                int positive = document.isVotesPositiveDefined() ? document.getVotesPositive() : 0;
                int negative = document.isVotesNegativeDefined() ? document.getVotesNegative() : 0;
                for(SortHandler<? > handler : handlers.values())
                {
                    handler.process(id, positive, negative);
                }
            }
            catch(EntityDoesNotExistException e)
            {
                // ignore
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

    private LongSet restrictDocumentSet(CategoryQueryResource categoryQuery,
        SiteResource singleSite, LongSet idSet, CoralSession coralSession)
        throws StructureException
    {
        if(categoryQuery != null)
        {
            try
            {
                idSet = categoryService.forwardQueryIds(coralSession, categoryQuery.getQuery(), idSet);
            }
            catch(CategoryQueryException e)
            {
                throw new StructureException("failed to execute category query", e);
            }
        }
        Set<SiteResource> acceptedSites = new HashSet<SiteResource>();
        if(singleSite != null)
        {
            acceptedSites.add(singleSite);
        }
        else if(categoryQuery != null)
        {
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
                        log.error("invalid accepted site " + acceptedSiteName
                            + " in query definition" + categoryQuery.toString());
                    }
                }
            }
        }
        // no sites = all are accepted
        if(acceptedSites.size() > 0)
        {
            LongIterator i = idSet.iterator();
            LongSet result = new LongOpenHashSet(max(idSet.size(), 1));
            while(i.hasNext())
            {
                try
                {
                    long id = i.next();
                    Resource r = coralSession.getStore().getResource(id);
                    if(r instanceof NavigationNodeResource)
                    {
                        if(acceptedSites.contains(((NavigationNodeResource)r).getSite()))
                        {
                            result.add(id);
                        }                                    
                    }
                }
                catch(EntityDoesNotExistException e)
                {
                    // ignore
                }
            }
            idSet = result;
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
