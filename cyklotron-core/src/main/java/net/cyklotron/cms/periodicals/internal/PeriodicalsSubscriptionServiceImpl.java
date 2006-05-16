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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Base64;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.objectledge.coral.entity.EntityInUseException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.Resource;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.UnsupportedCharactersInFilePathException;

import net.cyklotron.cms.periodicals.EmailPeriodicalResource;
import net.cyklotron.cms.periodicals.PeriodicalsException;
import net.cyklotron.cms.periodicals.PeriodicalsNodeResource;
import net.cyklotron.cms.periodicals.PeriodicalsNodeResourceImpl;
import net.cyklotron.cms.periodicals.PeriodicalsService;
import net.cyklotron.cms.periodicals.PeriodicalsSubscriptionService;
import net.cyklotron.cms.periodicals.SubscriptionRequestResource;
import net.cyklotron.cms.periodicals.SubscriptionRequestResourceImpl;
import net.cyklotron.cms.periodicals.UnsubscriptionInfo;
import net.cyklotron.cms.site.SiteResource;

/**
 * @author <a href="rafal@caltha.pl">Rafał Krzewski</a>
 * @version $Id: PeriodicalsSubscriptionServiceImpl.java,v 1.4 2006-05-16 14:13:16 rafal Exp $
 */
public class PeriodicalsSubscriptionServiceImpl
    implements PeriodicalsSubscriptionService
{
    static final String KEYSTORE_PATH = "/data/periodicals.ks";
    
    private static final String TOKEN_CHAR_ENCODING = "UTF-8";
    
    private static final String KEY_ALIAS = "unsub_token";

    private static final String DEFAULT_RANDOM_PROVIDER = "SUN";
    
    private static final String DEFAULT_RANDOM_ALGORITHM = "NativePRNG";
    
    private static final String DEFAULT_CIPHER_PROVIDER = "SunJCE";

    private static final String DEFAULT_KEYSTORE_PROVIDER = "SunJCE";
    
    private static final String DEFAULT_KEYSTORE_TYPE = "JCEKS";
    
    private final PeriodicalsService periodicalsService;

    private final FileSystem fileSystem;
    
    /** Java Cryptography API provider for Cipher & KeyGenerator */
    private final String cipherProvider;
    
    /** spec of algorithm for encoding unsubscription link tokens */
    private final String cipherAlgorithm;
    
    /** key size for encoding unsubscription link tokens */
    private final int cipherKeySize;

    /** Java Cryptography API provider for KeyStore */
    private final String keyStoreProvider;
    
    /** key store type */
    private final String keyStoreType;

    /** password for the keystore */
    private final String keystorePass;

    /** pseudo-random number generator */
    private final SecureRandom random;
    
    private final Base64 base64 = new Base64();

    private SecretKey encryptionKey;

    public PeriodicalsSubscriptionServiceImpl(PeriodicalsService periodicalsService,
        FileSystem fileSystem, String cipher, int keySize, String keystorePass)
        throws NoSuchAlgorithmException, NoSuchProviderException
    {
        this(periodicalsService, fileSystem, DEFAULT_RANDOM_PROVIDER, DEFAULT_RANDOM_ALGORITHM,
            DEFAULT_CIPHER_PROVIDER, cipher, keySize, 
            DEFAULT_KEYSTORE_PROVIDER, DEFAULT_KEYSTORE_TYPE, keystorePass);
    }
    
    public PeriodicalsSubscriptionServiceImpl(PeriodicalsService periodicalsService,
        FileSystem fileSystem, Configuration config)
        throws NoSuchAlgorithmException, NoSuchProviderException, ConfigurationException
    {
        this(periodicalsService, fileSystem, 
            config.getChild("random-provider").getValue(DEFAULT_RANDOM_PROVIDER),
            config.getChild("random").getValue(DEFAULT_RANDOM_ALGORITHM),
            config.getChild("cipher-provider").getValue(DEFAULT_CIPHER_PROVIDER),
            config.getChild("cipher").getValue(),
            config.getChild("key-size").getValueAsInteger(),
            config.getChild("keystore-provider").getValue(DEFAULT_KEYSTORE_PROVIDER),
            config.getChild("keystore").getValue(DEFAULT_KEYSTORE_TYPE),
            config.getChild("keystore-password").getValue());
    }

    public PeriodicalsSubscriptionServiceImpl(PeriodicalsService periodicalsService,
        FileSystem fileSystem, String randomProvider, String randomAlgorithm,
        String cipherProvider, String cipherAlgorithm, int cipherKeySize, String keyStoreProvider,
        String keyStoreType, String keystorePass)
        throws NoSuchAlgorithmException, NoSuchProviderException
    {
        this.periodicalsService = periodicalsService;
        this.fileSystem = fileSystem;
        this.cipherProvider = cipherProvider;
        this.cipherAlgorithm = cipherAlgorithm;
        this.cipherKeySize = cipherKeySize;
        this.keyStoreProvider = keyStoreProvider;
        this.keyStoreType = keyStoreType;
        this.keystorePass = keystorePass;
            
        this.random = SecureRandom.getInstance(randomAlgorithm, randomProvider);
    }

    // inherit doc
    public synchronized SubscriptionRequestResource getSubscriptionRequest(
        CoralSession coralSession, String cookie)
        throws PeriodicalsException
    {
        Resource[] res = coralSession.getStore().getResourceByPath(
            "/cms/sites/*/applications/periodicals/requests/" + cookie);
        if(res.length > 0)
        {
            return (SubscriptionRequestResourceImpl)res[0];
        }
        else
        {
            return null;
        }
    }

    // interit doc
    public synchronized String createSubsriptionRequest(CoralSession coralSession,
        SiteResource site, String email, String items)
        throws PeriodicalsException
    {
        Resource root = getSubscriptionChangeRequestsRoot(coralSession, site);
        String cookie;
        Resource[] res;
        do
        {
            cookie = getRandomCookie();
            res = coralSession.getStore().getResource(root, cookie);
        }
        while(res.length > 0);
        try
        {
            SubscriptionRequestResource request = SubscriptionRequestResourceImpl
                .createSubscriptionRequestResource(coralSession, cookie, root, email);
            request.setItems(items);
            request.update();
        }
        catch(Exception e)
        {
            throw new PeriodicalsException("failed to create subscription request record", e);
        }
        return cookie;
    }

    // inherit doc
    public synchronized void discardSubscriptionRequest(CoralSession coralSession, String cookie)
        throws PeriodicalsException
    {
        SubscriptionRequestResource r = getSubscriptionRequest(coralSession, cookie);
        if(r != null)
        {
            try
            {
                coralSession.getStore().deleteResource(r);
            }
            catch(EntityInUseException e)
            {
                throw new PeriodicalsException("failed to delete subscription change request", e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public EmailPeriodicalResource[] getSubscribedEmailPeriodicals(CoralSession coralSession,
        SiteResource site, String email)
        throws PeriodicalsException
    {
        EmailPeriodicalResource[] periodicals = periodicalsService.getEmailPeriodicals(
            coralSession, site);
        List temp = new ArrayList();
        for(int i = 0; i < periodicals.length; i++)
        {
            EmailPeriodicalResource periodical = periodicals[i];
            if(periodical.getAddresses().indexOf(email) >= 0)
            {
                temp.add(periodical);
            }
        }
        EmailPeriodicalResource[] result = new EmailPeriodicalResource[temp.size()];
        temp.toArray(result);
        return result;
    }
    
    /**
     * {@inheritDoc}
     */
    public String createUnsubscriptionToken(long periodicalId, String address) throws PeriodicalsException
    {
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Cipher cipher = Cipher.getInstance(cipherAlgorithm, cipherProvider);
            cipher.init(Cipher.ENCRYPT_MODE, getEncryptionKey());
            CipherOutputStream cos = new CipherOutputStream(baos, cipher);
            DataOutputStream dos = new DataOutputStream(cos);
            dos.writeInt(random.nextInt()); // write salt
            dos.writeLong(periodicalId);
            dos.writeUTF(address);
            dos.close();
            return bytesToString(baos.toByteArray());
        }
        catch(Exception e)
        {
            throw new PeriodicalsException("failed to create unsubscription token", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public UnsubscriptionInfo decodeUnsubscriptionToken(String encoded) throws PeriodicalsException
    {
        try
        {
            ByteArrayInputStream bais = new ByteArrayInputStream(stringToBytes(encoded));
            Cipher cipher = Cipher.getInstance(cipherAlgorithm, cipherProvider);
            cipher.init(Cipher.DECRYPT_MODE, getEncryptionKey());
            CipherInputStream cis = new CipherInputStream(bais, cipher);
            DataInputStream dis = new DataInputStream(cis);
            dis.readInt(); // discard salt
            long periodicalId = dis.readLong();
            String address = dis.readUTF();
            return new UnsubscriptionInfo(periodicalId, address);
        }
        catch(Exception e)
        {
            throw new PeriodicalsException("failed to decode unsubscription token", e);
        }
    }
    
    /**
     * Return the root node for email periodicals
     * 
     * @param site the site.
     * @return the periodicals root.
     */
    private PeriodicalsNodeResource getSubscriptionChangeRequestsRoot(CoralSession coralSession,
        SiteResource site)
        throws PeriodicalsException
    {
        PeriodicalsNodeResource applicationRoot = periodicalsService.getApplicationRoot(
            coralSession, site);
        Resource[] res = coralSession.getStore().getResource(applicationRoot, "requests");
        if(res.length == 0)
        {
            try
            {
                return PeriodicalsNodeResourceImpl.createPeriodicalsNodeResource(coralSession,
                    "requests", applicationRoot);
            }
            catch(InvalidResourceNameException e)
            {
                throw new RuntimeException("unexpected exception", e);
            }
        }
        else
        {
            return (PeriodicalsNodeResource)res[0];
        }
    }
    
    protected String getRandomCookie()
    {
        return String.format("%016x", random.nextLong());
    }
    
    protected synchronized Key getEncryptionKey()
        throws Exception
    {
        if(encryptionKey == null)
        {
            KeyStore keyStore = KeyStore.getInstance(keyStoreType, keyStoreProvider);
            char[] passChars = keystorePass.toCharArray();
            if(fileSystem.exists(KEYSTORE_PATH) && fileSystem.canRead(KEYSTORE_PATH))
            {
                keyStore.load(fileSystem.getInputStream(KEYSTORE_PATH), passChars);
                encryptionKey = (SecretKey)keyStore.getKey(KEY_ALIAS, passChars);
            }
            else
            {
                KeyGenerator keyGen = KeyGenerator.getInstance(cipherAlgorithm, cipherProvider);
                keyGen.init(cipherKeySize, random);
                encryptionKey = keyGen.generateKey();
                keyStore.load(null, null);
                keyStore.setKeyEntry(KEY_ALIAS, encryptionKey, passChars, null);
                fileSystem.mkdirs(FileSystem.directoryPath(KEYSTORE_PATH));
                keyStore.store(fileSystem.getOutputStream(KEYSTORE_PATH), passChars);
            }
        }
        return encryptionKey;
    }

    private String bytesToString(byte[] bytes)
        throws UnsupportedEncodingException
    {
        byte[] b64 = base64.encode(bytes);
        return URLEncoder.encode(new String(b64, TOKEN_CHAR_ENCODING), TOKEN_CHAR_ENCODING);
    }

    private byte[] stringToBytes(String encoded)
        throws UnsupportedEncodingException
    {
        byte[] b64 = URLDecoder.decode(encoded, TOKEN_CHAR_ENCODING).getBytes(TOKEN_CHAR_ENCODING);
        return base64.decode(b64);
    }
}
