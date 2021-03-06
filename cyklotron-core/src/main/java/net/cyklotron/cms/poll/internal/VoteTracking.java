package net.cyklotron.cms.poll.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.servlet.http.Cookie;

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
    
    private final String cookiePath;

    public VoteTracking(URL baseUrl)
    {
        cookiePath = baseUrl == null ? null : baseUrl.getPath();
    }

    public boolean hasVoted(HttpContext httpContext, Resource item)       
    {
        LongDeque history;
        history = (LongDeque)httpContext.getSessionAttribute(STATE_ID);
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
                        httpContext.setSessionAttribute(STATE_ID, history);
                        break;
                    }
                }
            }
        }
        if(history != null)
        {
            synchronized(history)
            {
                return history.contains(item.getId());
            }
        }
        else
        {
            return false;
        }
    }

    public void trackVote(HttpContext httpContext, Resource item)
    {
        LongDeque history;
        history = (LongDeque)httpContext.getSessionAttribute(STATE_ID);
        if(history == null)
        {
            history = new LongArrayDeque();
            httpContext.setSessionAttribute(STATE_ID, history);
        }
        String encoded;
        synchronized(history)
        {
            history.addLast(item.getId());
            if(history.size() > LIMIT)
            {
                history.removeFirst();
            }
            encoded = encode(history);
        }
        Cookie cookie = new Cookie(STATE_ID, encoded);
        if(cookiePath != null && cookiePath.length() > 0)
        {
            cookie.setPath(cookiePath);
        }
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
