package net.cyklotron.cms.poll.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.objectledge.coral.store.Resource;
import org.objectledge.web.HttpContext;

import bak.pcj.LongIterator;
import bak.pcj.list.LongArrayDeque;
import bak.pcj.list.LongDeque;

public class VoteTracking
{
    public static final int LIMIT = 740;

    public static String STATE_ID = "cyklotron.vote_history";

    private static final int COOKIE_MAX_AGE = 365 * 24 * 60 * 60;
    
    public boolean hasVoted(HttpContext httpContext, Resource item)       
    {
        HttpSession session = httpContext.getRequest().getSession();
        LongDeque history;
        synchronized(session)
        {
            history = (LongDeque)session.getAttribute(STATE_ID);
            if(history == null)
            {
                Cookie[] cookies = httpContext.getRequest().getCookies();
                if(cookies != null)
                {
                    for(Cookie cookie : cookies)
                    {
                        if(cookie.getName().equals(STATE_ID))
                        {
                            history = decode(cookie.getValue());
                            session.setAttribute(STATE_ID, history);
                            break;
                        }
                    }
                }
            }
            if(history != null)
            {
                return history.contains(item.getId());
            }
            else
            {
                return false;
            }
        }
    }

    public void trackVote(HttpContext httpContext, Resource item)
    {
        HttpSession session = httpContext.getRequest().getSession();
        LongDeque history;
        synchronized(session)
        {
            history = (LongDeque)session.getAttribute(STATE_ID);
            if(history == null)
            {
                history = new LongArrayDeque();
                session.setAttribute(STATE_ID, history);
            }
            history.addLast(item.getId());
            if(history.size() > LIMIT)
            {
                history.removeFirst();
            }
        }
        Cookie cookie = new Cookie(STATE_ID, encode(history));
        cookie.setPath(httpContext.getRequest().getContextPath()
            + httpContext.getRequest().getServletPath());
        cookie.setMaxAge(COOKIE_MAX_AGE);
        httpContext.getResponse().addCookie(cookie);
    }

    public LongDeque decode(String encoded)
    {
        try
        {
            byte[] bytes = encoded.replace('.', '=').getBytes("ASCII");
            DataInputStream is = new DataInputStream(new Base64InputStream(
                new ByteArrayInputStream(bytes)));
            LongDeque list = new LongArrayDeque();
            int cnt = (int)is.readShort();
            while(cnt-- > 0)
            {
                list.add((long)is.readInt());
            }
            return list;
        }
        catch(IOException e)
        {
            throw new RuntimeException("Unexpected IOException", e);
        }
    }

    public String encode(LongDeque list)
    {
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream os = new DataOutputStream(new Base64OutputStream(baos));
            LongIterator i = list.iterator();
            int cnt = list.size() < LIMIT ? list.size() : LIMIT;
            os.writeShort((short)cnt);
            while(i.hasNext() && cnt-- > 0)
            {
                os.writeInt((int)i.next());
            }
            os.close();
            return new String(baos.toByteArray(), "ASCII").replaceAll("\r\n", "");
        }
        catch(IOException e)
        {
            throw new RuntimeException("Unexpected IOException", e);
        }
    }
}
