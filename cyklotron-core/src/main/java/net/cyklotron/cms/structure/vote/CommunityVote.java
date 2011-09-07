package net.cyklotron.cms.structure.vote;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureException;
import net.cyklotron.cms.structure.StructureService;

import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;

import bak.pcj.LongIterator;
import bak.pcj.map.LongKeyMap;
import bak.pcj.map.LongKeyOpenHashMap;
import bak.pcj.set.LongSet;

public class CommunityVote
{
    private final StructureService structureService;

    public CommunityVote(StructureService structureService)
    {
        this.structureService = structureService;
    }

    public Map<SortOrder, List<NavigationNodeResource>> getResults(Date cutoffDate,
        Set<SortOrder> sortOrders, Comparator<NavigationNodeResource> secondarySortOrder,
        CoralSession coralSession)
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
