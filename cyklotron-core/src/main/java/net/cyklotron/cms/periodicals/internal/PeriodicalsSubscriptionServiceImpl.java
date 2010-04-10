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
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Base64;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.objectledge.ComponentInitializationError;
import org.objectledge.coral.entity.EntityInUseException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.Resource;
import org.objectledge.filesystem.FileSystem;

import net.cyklotron.cms.periodicals.EmailPeriodicalResource;
import net.cyklotron.cms.periodicals.PeriodicalsException;
import net.cyklotron.cms.periodicals.PeriodicalsNodeResource;
import net.cyklotron.cms.periodicals.PeriodicalsNodeResourceImpl;
import net.cyklotron.cms.periodicals.PeriodicalsSubscriptionService;
import net.cyklotron.cms.periodicals.SubscriptionRequestResource;
import net.cyklotron.cms.periodicals.SubscriptionRequestResourceImpl;
import net.cyklotron.cms.periodicals.UnsubscriptionInfo;
import net.cyklotron.cms.site.SiteResource;

/**
 * @author <a href="rafal@caltha.pl">Rafał Krzewski</a>
 * @version $Id: PeriodicalsSubscriptionServiceImpl.java,v 1.9 2007-11-18 21:23:25 rafal Exp $
 */
public class PeriodicalsSubscriptionServiceImpl
    implements PeriodicalsSubscriptionService
{
    private static final String KEYSTORE_PATH = "/data/periodicals.ks";
    
    private static final String TOKEN_CHAR_ENCODING = "UTF-8";
    
    private static final String KEY_ALIAS = "unsub_token";

    private static final String DEFAULT_RANDOM_PROVIDER = "SUN";
    
    private static final String DEFAULT_RANDOM_ALGORITHM = "NativePRNG";
    
    private static final String DEFAULT_CIPHER_PROVIDER = "SunJCE";

    private static final String DEFAULT_DIGEST_PROVIDER = "SUN";

    private static final String DEFAULT_KEYSTORE_PROVIDER = "SunJCE";
    
    private static final String DEFAULT_KEYSTORE_TYPE = "JCEKS";
    
    private final FileSystem fileSystem;
    
    /** Java Cryptography API provider for Cipher & KeyGenerator */
    private final String cipherProvider;
    
    /** spec of algorithm for encoding unsubscription link tokens */
    private final String cipherAlgorithm;
    
    /** key size for encoding unsubscription link tokens */
    private final int cipherKeySize;

    /** Java Cryptography API provider for MessageDigest */
    private final String digestProvider;

    /** MessageDigest algorithm spec */
    private final String digest;
    
    /** Java Cryptography API provider for KeyStore */
    private final String keyStoreProvider;

    /** KeyStore type */
    private final String keyStoreType;

    /** KeyStore password */
    private final String keystorePass;
    
    /** pseudo-random number generator */
    private final SecureRandom random;

    /** Base64 codec */
    private final Base64 base64 = new Base64();

    /** encryption key */
    private SecretKey encryptionKey;

    public PeriodicalsSubscriptionServiceImpl(FileSystem fileSystem, String cipher, int keySize,
        String digest, String keystorePass)
    {
        this(fileSystem, DEFAULT_RANDOM_PROVIDER, DEFAULT_RANDOM_ALGORITHM,
                        DEFAULT_CIPHER_PROVIDER, cipher, keySize, DEFAULT_DIGEST_PROVIDER, digest,
                        DEFAULT_KEYSTORE_PROVIDER, DEFAULT_KEYSTORE_TYPE, keystorePass);
    }
    
    public PeriodicalsSubscriptionServiceImpl(FileSystem fileSystem, Configuration config)
        throws ConfigurationException
    {
        this(fileSystem, 
            config.getChild("random-provider").getValue(DEFAULT_RANDOM_PROVIDER),
            config.getChild("random").getValue(DEFAULT_RANDOM_ALGORITHM),
            config.getChild("cipher-provider").getValue(DEFAULT_CIPHER_PROVIDER),
            config.getChild("cipher").getValue(),
            config.getChild("key-size").getValueAsInteger(),
            config.getChild("digest-provider").getValue(DEFAULT_DIGEST_PROVIDER),
            config.getChild("digest").getValue(),
            config.getChild("keystore-provider").getValue(DEFAULT_KEYSTORE_PROVIDER),
            config.getChild("keystore").getValue(DEFAULT_KEYSTORE_TYPE),
            config.getChild("keystore-password").getValue());
    }

    public PeriodicalsSubscriptionServiceImpl(FileSystem fileSystem, String randomProvider,
        String randomAlgorithm, String cipherProvider, String cipherAlgorithm, int cipherKeySize,
        String digestProvider, String digest, String keyStoreProvider, String keyStoreType,
        String keystorePass)
    {
        this.fileSystem = fileSystem;
        this.cipherProvider = cipherProvider;
        this.cipherAlgorithm = cipherAlgorithm;
        this.cipherKeySize = cipherKeySize;
        this.digestProvider = digestProvider;
        this.digest = digest;
        this.keyStoreProvider = keyStoreProvider;
        this.keyStoreType = keyStoreType;
        this.keystorePass = keystorePass;
            
        try
        {
            this.random = SecureRandom.getInstance(randomAlgorithm, randomProvider);

            if(fileSystem.exists(KEYSTORE_PATH) && fileSystem.canRead(KEYSTORE_PATH))
            {
                KeyStore keyStore = KeyStore.getInstance(keyStoreType, keyStoreProvider);
                char[] passChars = keystorePass.toCharArray();
                keyStore.load(fileSystem.getInputStream(KEYSTORE_PATH), passChars);
                encryptionKey = (SecretKey)keyStore.getKey(KEY_ALIAS, passChars);
            }
            else
            {
                createEncryptionKey();
            }
        }
        catch(Exception e)
        {
            throw new ComponentInitializationError("failed to initialize crypto support", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void createEncryptionKey() throws PeriodicalsException
    {
        try
        {
            char[] passChars = keystorePass.toCharArray();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType, keyStoreProvider);
            KeyGenerator keyGen = KeyGenerator.getInstance(cipherAlgorithm, cipherProvider);
            keyGen.init(cipherKeySize, random);
            encryptionKey = keyGen.generateKey();
            keyStore.load(null, null);
            keyStore.setKeyEntry(KEY_ALIAS, encryptionKey, passChars, null);
            fileSystem.mkdirs(FileSystem.directoryPath(KEYSTORE_PATH));
            keyStore.store(fileSystem.getOutputStream(KEYSTORE_PATH), passChars);
        }
        catch(Exception e)
        {
            throw new PeriodicalsException("failed to create new encryption key", e);
        }
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
        EmailPeriodicalResource[] periodicals = PeriodicalsServiceUtils.getEmailPeriodicals(
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
            DataOutputStream dos = new DataOutputStream(baos);
            dos.writeUTF(Long.toString(periodicalId));
            dos.writeUTF(address);
            dos.write(encryptAndDigest(toBytes(periodicalId, address)));
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
    public UnsubscriptionInfo decodeUnsubscriptionToken(String encoded, boolean urlEncoded) throws PeriodicalsException
    {
        try
        {
            ByteArrayInputStream bais = new ByteArrayInputStream(stringToBytes(encoded, urlEncoded));
            DataInputStream dis = new DataInputStream(bais);
            long periodicalId = Long.parseLong(dis.readUTF());
            String address = dis.readUTF();
            byte[] digest1 = encryptAndDigest(toBytes(periodicalId, address));
            byte[] digest2 = new byte[digest1.length]; 
            dis.readFully(digest2);
            return new UnsubscriptionInfo(periodicalId, address, MessageDigest
                .isEqual(digest1, digest2));
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
        PeriodicalsNodeResource applicationRoot = PeriodicalsServiceUtils.getApplicationRoot(
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
    
    private String bytesToString(byte[] bytes)
        throws UnsupportedEncodingException
    {
        byte[] b64 = base64.encode(bytes);
        return URLEncoder.encode(new String(b64, TOKEN_CHAR_ENCODING), TOKEN_CHAR_ENCODING);
    }

    private byte[] stringToBytes(String encoded, boolean urlEncoded)
        throws UnsupportedEncodingException
    {
        String b64s = urlEncoded ? URLDecoder.decode(encoded, TOKEN_CHAR_ENCODING) : encoded; 
        byte[] b64 = b64s.getBytes(TOKEN_CHAR_ENCODING);
        return base64.decode(b64);
    }
    
    private byte[] toBytes(Object ... data) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        for(Object obj : data)
        {
            Class cl = obj.getClass();
            if(String.class.equals(cl))
            {
                dos.writeUTF((String)obj);
            }
            if(Long.class.equals(cl))
            {
                dos.writeLong(((Long)obj).longValue());
            }
            if(Boolean.class.equals(cl))
            {
                dos.writeBoolean(((Boolean)obj).booleanValue());
            }
            if(Byte.TYPE.equals(cl.getComponentType()))
            {
                dos.write((byte[])obj);
            }
        }
        return baos.toByteArray();
    }
    
    private byte[] encryptAndDigest(byte[] in) throws Exception
    {
        Cipher cipher = Cipher.getInstance(cipherAlgorithm, cipherProvider);
        cipher.init(Cipher.ENCRYPT_MODE, encryptionKey);
        MessageDigest md = MessageDigest.getInstance(digest, digestProvider);
        return md.digest(cipher.doFinal(in));
    }
}
