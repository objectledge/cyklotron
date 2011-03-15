package net.cyklotron.cms.forum;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * A sorting tool used for message resource
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: MessageComparator.java,v 1.1 2005-01-12 20:45:07 pablo Exp $
 */
public class MessageComparator
    implements Comparator
{
    /** sorting by name mode */
    public static final int NONE = 0;            

    /** sorting by name mode */
    public static final int NAME_ASC = 1;
    
    /** sorting by name mode */
    public static final int NAME_DESC = 2;

    /** sorting by date mode */
    public static final int CREATION_DATE_ASC = 3;
    
    /** sorting by date mode */
    public static final int CREATION_DATE_DESC = 4;

    /** sorting by owner mode */
    public static final int OWNER_ASC = 5;
    
    /** sorting by owner mode */
    public static final int OWNER_DESC = 6;
    
    /** sorting by owner mode */
    public static final int CREATOR_ASC = 7;
    
    /** sorting by owner mode */
    public static final int CREATOR_DESC = 8;
    
    /** soring mode */
    private int mode;

    /** locale */
    private Locale locale;

    public MessageComparator(int mode, Locale locale)
    {
        if(locale == null)
        {
            throw new IllegalStateException("locale cannot be null ");
        }
        this.mode = mode;
        this.locale = locale;
    }
    
    public int compare(Object o1, Object o2)
    {
        long res;
        int cmp;
        Collator collator = Collator.getInstance(locale);
        if(!(o1 instanceof MessageResource && o2 instanceof MessageResource ))
        {
            return 0;
        }
        
        if(((MessageResource)o1).getSticky() && !((MessageResource)o2).getSticky())
        {
            return -1;
        }
        else if(!((MessageResource)o1).getSticky()
            && ((MessageResource)o2).getSticky())
        {
            return 1;
        }
        
        switch(mode) 
        {
        case 0:
            return 0;
        case 1:
            return collator.compare(((MessageResource)o1).getName(), ((MessageResource)o2).getName());
        case 2:
            return collator.compare(((MessageResource)o2).getName(), ((MessageResource)o1).getName());
        case 3:
            res = ((MessageResource)o1).getCreationTime().getTime()-((MessageResource)o2).getCreationTime().getTime();
            if(res < 0)
                return -1;
            if(res > 0)
                return 1;
            return collator.compare( ((MessageResource)o1).getName(), ((MessageResource)o2).getName());                
        case 4:
            res = ((MessageResource)o1).getCreationTime().getTime()-((MessageResource)o2).getCreationTime().getTime();
            if(res > 0)
                return -1;
            if(res < 0)
                return 1;
            return collator.compare(((MessageResource)o1).getName(), ((MessageResource)o2).getName());
        case 5:
            cmp = collator.compare( ((MessageResource)o1).getOwner().getName(), ((MessageResource)o2).getOwner().getName() );
            if(cmp == 0)
            {
                cmp = collator.compare(((MessageResource)o1).getName(), ((MessageResource)o2).getName());
            }
            return cmp;
        case 6:
            cmp =  collator.compare( ((MessageResource)o2).getOwner().getName(), ((MessageResource)o1).getOwner().getName() );
            if(cmp == 0)
            {
                cmp = collator.compare(((MessageResource)o1).getName(), ((MessageResource)o2).getName());
            }
            return cmp;
        case 7:
            cmp = collator.compare( ((MessageResource)o1).getCreatedBy().getName(), ((MessageResource)o2).getCreatedBy().getName() );
            if(cmp == 0)
            {
                cmp = collator.compare(((MessageResource)o1).getName(), ((MessageResource)o2).getName());
            }
            return cmp;
        case 8:
            cmp =  collator.compare( ((MessageResource)o2).getCreatedBy().getName(), ((MessageResource)o1).getCreatedBy().getName() );
            if(cmp == 0)
            {
                cmp = collator.compare(((MessageResource)o1).getName(), ((MessageResource)o2).getName());
            }
            return cmp;
        default:
            return 0;
        }
    }
}
