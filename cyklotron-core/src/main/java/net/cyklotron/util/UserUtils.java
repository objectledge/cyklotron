package net.cyklotron.util;

import java.util.ArrayList;
import java.util.List;

import org.objectledge.authentication.DefaultPrincipal;
import org.objectledge.authentication.UserManager;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.directory.DirectoryParameters;

/**
 * A class created to factor out some code shared by CMS and Groups.
 */
public class UserUtils
{
    public static List<Subject> filteredUserList(CoralSession coralSession,
        UserManager userManager, String show, String search)
    {
        Subject[] subjects = coralSession.getSecurity().getSubject();
        List<Subject> filtered = new ArrayList<>();
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
                    Parameters pc = new DirectoryParameters(userManager.
                        getPersonalData(new DefaultPrincipal(subjects[i].getName())));
                    String[] keys = pc.getParameterNames();
                    for(int j=0; j<keys.length; j++)
                    {
                        String[] values = pc.getStrings(keys[j]);
                        for(int k=0; k<values.length; k++)
                        {
                            if(values[k].indexOf(search) >= 0)
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
