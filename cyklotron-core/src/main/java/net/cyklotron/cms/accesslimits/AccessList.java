package net.cyklotron.cms.accesslimits;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.net.CIDRBlock;
import org.objectledge.net.IPAddressUtil;

public class AccessList
{
    private final List<CIDRBlock> blocks;

    public AccessList(AccessListResource resource, Logger log)
    {
        final String[] lines = resource.getContents().split("\n");
        blocks = new ArrayList<>(lines.length);
        int i = 1;
        for(String line : lines)
        {
            try
            {
                blocks.add(parseLine(line));
            }
            catch(UnknownHostException | IllegalArgumentException e)
            {
                log.error(
                    "invalid address " + line + " at line " + i + " in list " + resource.getPath(),
                    e);
            }
            finally
            {
                i++;
            }
        }
    }

    public boolean contains(InetAddress addr)
    {
        for(CIDRBlock block : blocks)
        {
            if(block.contains(addr))
            {
                return true;
            }
        }
        return false;
    }

    public static List<ValidationError> validate(String contents)
    {
        List<ValidationError> errors = new ArrayList<>();
        final String[] lines = contents.split("\n");
        int i = 1;
        for(String line : lines)
        {
            try
            {
                parseLine(line);
            }
            catch(UnknownHostException | IllegalArgumentException e)
            {
                errors.add(new ValidationError(i, e.getMessage()));
            }
            finally
            {
                i++;
            }
        }
        return errors;
    }

    private static CIDRBlock parseLine(String line)
        throws UnknownHostException
    {
        String[] tok = line.split("/");
        InetAddress addr = IPAddressUtil.byAddress(tok[0]);
        int length;
        if(tok.length == 2)
        {
            length = Integer.parseInt(tok[1]);
        }
        else
        {
            length = addr.getAddress().length * 8;
        }
        return new CIDRBlock(addr, length);
    }

    public static class ValidationError
    {
        private final int line;

        private final String message;

        public ValidationError(int line, String message)
        {
            this.line = line;
            this.message = message;
        }

        public int getLine()
        {
            return line;
        }

        public String getMessage()
        {
            return message;
        }
    }
}
