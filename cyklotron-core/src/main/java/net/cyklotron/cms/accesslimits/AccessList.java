package net.cyklotron.cms.accesslimits;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.store.Resource;
import org.objectledge.net.CIDRBlock;
import org.objectledge.net.IPAddressUtil;

public class AccessList
{
    private final List<CIDRBlock> blocks;

    public AccessList(AccessListResource list, Logger log)
    {
        Resource[] children = list.getChildren();
        blocks = new ArrayList<>(children.length);
        for(Resource child : children)
        {
            if(child instanceof AccessListItemResource)
            {
                AccessListItemResource item = (AccessListItemResource)child;
                try
                {
                    blocks.add(parse(item.getAddressBlock()));
                }
                catch(UnknownHostException | IllegalArgumentException e)
                {
                    log.error(
                        "invalid address " + item.getAddressBlock() + " in " + item.toString(), e);
                }
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

    private static CIDRBlock parse(String spec)
        throws UnknownHostException
    {
        String[] tok = spec.split("/");
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
}
