// 
// Copyright (c) 2003-2005, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
//   
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//   
// * Redistributions of source code must retain the above copyright notice,  
// this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
// this list of conditions and the following disclaimer in the documentation  
// and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
// nor the names of its contributors may be used to endorse or promote products  
// derived from this software without specific prior written permission. 
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED  
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
// IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,  
// INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,  
// BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
// OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,  
// WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)  
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE  
// POSSIBILITY OF SUCH DAMAGE. 
//

package net.cyklotron.cms.periodicals.internal;

import java.io.IOException;
import java.security.Key;

import org.jmock.Mock;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.utils.LedgeTestCase;

import net.cyklotron.cms.periodicals.PeriodicalsService;
import net.cyklotron.cms.periodicals.UnsubscriptionInfo;

/**
 *
 *
 * @author <a href="rafal@caltha.pl">Rafał Krzewski</a>
 * @version $Id: PeriodicalsSubscriptionServiceImplTest.java,v 1.4 2006-05-16 14:33:11 rafal Exp $
 */
public class PeriodicalsSubscriptionServiceImplTest
    extends LedgeTestCase
{
    private FileSystem fileSystem;
    private PeriodicalsSubscriptionServiceImpl service;
    
    public void setUp() throws Exception
    {
        fileSystem = getFileSystem();
        initService();
    }
    
    private void initService() throws Exception
    {
        service = new PeriodicalsSubscriptionServiceImpl(fileSystem, "AES", 128, "12345");        
    }
    
    public void testRandomCookie()
    {
        assertEquals(16, service.getRandomCookie().length());
    }
    
    public void testKeyGen() throws Exception
    {
        if(fileSystem.exists(PeriodicalsSubscriptionServiceImpl.KEYSTORE_PATH))
        {
            fileSystem.delete(PeriodicalsSubscriptionServiceImpl.KEYSTORE_PATH);
        }
        Key k1 = service.getEncryptionKey();
        
        initService();
        Key k2 = service.getEncryptionKey();
        assertTrue(k1.equals(k2));
        
        fileSystem.delete(PeriodicalsSubscriptionServiceImpl.KEYSTORE_PATH);
        initService();
        Key k3 = service.getEncryptionKey();
        assertFalse(k1.equals(k3));
    }
    
    public void testEncryption() throws Exception
    {
        int periodicalId = 799;
        String address = "rafal@caltha.pl";
        String enc = service.createUnsubscriptionToken(periodicalId, address);
        System.out.format("sample token: %s\n", enc);
        UnsubscriptionInfo info = service.decodeUnsubscriptionToken(enc);
        assertEquals(periodicalId, info.getPeriodicalId());
        assertEquals(address, info.getAddress());
    }
    
    public void testEncryptionPerformance() throws Exception
    {
        int periodicalId = 100000;
        String address = "01234567890123456789012345678901234567890123456789";
        long time = System.currentTimeMillis();
        int count = 1000;
        for(int i = 0; i<count; i++)
        {
            service.createUnsubscriptionToken(periodicalId, address);
        }
        time = System.currentTimeMillis() - time;
        System.out.format("generated %d tokens in %.2fs\n", count, (float) time / 1000);
    }
}
