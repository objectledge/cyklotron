package net.cyklotron.util;

import java.util.ArrayList;
import java.util.List;

import net.labeo.services.ServiceBroker;
import net.labeo.services.personaldata.PersonalDataService;
import net.labeo.services.resource.CoralSession;
import net.labeo.services.resource.Subject;
import net.labeo.util.configuration.Parameter;
import net.labeo.util.configuration.ParameterContainer;

/**
 * A class created to factor out some code shared by CMS and Groups.
 */
public class UserUtils
{
    public static List filteredUserList(ServiceBroker broker, String show, String search)
    {
        CoralSession resourceService = (CoralSession)broker.
            getService(CoralSession.SERVICE_NAME);
        PersonalDataService personalDataService = (PersonalDataService)broker.
            getService(PersonalDataService.SERVICE_NAME);

        Subject[] subjects = resourceService.getSecurity().getSubject();
        ArrayList filtered = new ArrayList();
        if((show == null && search == null) || (show != null && show.equals("all")))
        {
            for(int i=0; i<subjects.length; i++)
            {
                filtered.add(subjects[i]);
            }
        }
        else if(show != null && !show.equals("other"))
        {
            char showChar = show.toLowerCase().charAt(0);
            for(int i=0; i<subjects.length; i++)
            {
                // DN starts with uid=...
                char firstChar = subjects[i].getName().charAt(4);
                if(firstChar == showChar)
                {
                    filtered.add(subjects[i]);
                }
            }                
        }
        else if(show != null && show.equals("other"))
        {
            for(int i=0; i<subjects.length; i++)
            {
                // DN starts with uid=...
                char firstChar = subjects[i].getName().charAt(4);
                if(firstChar < 'a' || firstChar > 'z')
                {
                    filtered.add(subjects[i]);
                }
            }                
        }
        else if(search != null)
        {
            // This is an ineffective solution. When it becomes
            // insufficient, true search engine like Lucene could be used
            // to index the user information for fast searching.
            outer: for(int i=0; i<subjects.length; i++)
            {
                try
                {
                    ParameterContainer pc = personalDataService.
                        getData(subjects[i].getName());
                    String[] keys = pc.getKeys();
                    for(int j=0; j<keys.length; j++)
                    {
                        Parameter[] values = pc.getArray(keys[j]);
                        for(int k=0; k<values.length; k++)
                        {
                            if(values[k].asString().indexOf(search) >= 0)
                            {
                                filtered.add(subjects[i]);
                                continue outer;
                            }
                        }
                    }
                }
                catch(Exception e)
                {
                    continue outer;
                }
            }
        }
        return filtered;
    }
}
