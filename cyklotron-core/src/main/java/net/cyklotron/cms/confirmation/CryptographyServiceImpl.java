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

package net.cyklotron.cms.confirmation;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Base64;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.objectledge.ComponentInitializationError;
import org.objectledge.filesystem.FileSystem;


public class CryptographyServiceImpl
    implements CryptographyService
{
    private static final String KEYSTORE_PATH = "/data/cipher.ks";
    
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

    public CryptographyServiceImpl(FileSystem fileSystem, String cipher, int keySize,
        String digest, String keystorePass)
    {
        this(fileSystem, DEFAULT_RANDOM_PROVIDER, DEFAULT_RANDOM_ALGORITHM,
                        DEFAULT_CIPHER_PROVIDER, cipher, keySize, DEFAULT_DIGEST_PROVIDER, digest,
                        DEFAULT_KEYSTORE_PROVIDER, DEFAULT_KEYSTORE_TYPE, keystorePass);
    }
    
    public CryptographyServiceImpl(FileSystem fileSystem, Configuration config)
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

    public CryptographyServiceImpl(FileSystem fileSystem, String randomProvider,
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
    public void createEncryptionKey() throws ConfirmationRequestException
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
            throw new ConfirmationRequestException("failed to create new encryption key", e);
        }
    }
    
    @Override
    public String getRandomCookie()
    {
        return String.format("%016x", random.nextLong());
    }
    
    @Override
    public byte[] encryptAndDigest(long resId, String address) throws Exception
    {
        return encryptAndDigest(toBytes(resId, address));
    }
    
    @Override
    public String bytesToString(byte[] bytes)
        throws UnsupportedEncodingException
    {
        byte[] b64 = base64.encode(bytes);
        return URLEncoder.encode(new String(b64, TOKEN_CHAR_ENCODING), TOKEN_CHAR_ENCODING);
    }

    @Override
    public byte[] stringToBytes(String encoded, boolean urlEncoded)
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
