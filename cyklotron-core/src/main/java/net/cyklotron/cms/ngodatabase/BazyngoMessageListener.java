// 
// Copyright (c) 2004, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
// 
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
// 
// * Redistributions of source code must retain the above copyright notice,  
//       this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//       this list of conditions and the following disclaimer in the documentation  
//       and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//       nor the names of its contributors may be used to endorse or promote products  
//       derived from this software without specific prior written permission. 
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

package net.cyklotron.cms.ngodatabase;

import java.io.IOException;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import net.cyklotron.bazy.organizations.OrganizationResourceImpl;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.MapperConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.PropertyNamingStrategy;
import org.codehaus.jackson.map.introspect.AnnotatedField;
import org.codehaus.jackson.map.introspect.AnnotatedMethod;
import org.jcontainer.dna.Logger;

public class BazyngoMessageListener
    implements MessageListener, ExceptionListener
    {    
    private final Logger logger;

    public BazyngoMessageListener(Logger logger)
        throws Exception
    {
        this.logger = logger;
    }

    @Override
    public void onMessage(Message message)
    {
        if(message instanceof TextMessage)
        {
            TextMessage textMessage = (TextMessage)message;
            try
            {
                BazyngoMessage bazyngoMessage = getMessageObject(textMessage);
                if(bazyngoMessage != null)
                {
                    if(bazyngoMessage instanceof CategoriesTreeBazyngoMessage)
                    {
                        // do your job.
                        logger.info("get category tree message: " + bazyngoMessage.toString());
                        
                    }
                    else if(bazyngoMessage instanceof OrganizationBazyngoMessage)
                    {
                        // do your job.
                        logger.info("get organization message: " + bazyngoMessage.toString());
                    }
                    else
                    {
                        logger.info("get message: " + bazyngoMessage.toString());
                    }
                }
            }
            catch(JMSException ex)
            {
                logger.error("Error reading message: " + ex);
            }
            catch(JsonParseException ex)
            {
                logger.error("Error reading message: " + ex);
            }
            catch(JsonMappingException ex)
            {
                logger.error("Error reading message: " + ex);
            }
            catch(IOException ex)
            {
                logger.error("Error reading message: " + ex);
            }
        }
        else
        {
            logger.info("Received: " + message);
        }
    }
    
    @Override
    public void onException(JMSException e)
    {
        logger.error("JMS Exception: ", e);
    }
    

    /**
     * Converts json message data to Cyklotron CiviCrmMessage object
     * @param textMessage
     * @return CiviCrmMessage
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     * @throws JMSException
     */
    public BazyngoMessage getPolymorphicMessageObject(TextMessage textMessage)
        throws JsonParseException, JsonMappingException, IOException, JMSException
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(new CamelCaseNamingStrategy());
        mapper.getDeserializationConfig().addMixInAnnotations(BazyngoMessage.class,
            PolymorphicBazyngoMessageMixIn.class);
        mapper.getSerializationConfig().addMixInAnnotations(BazyngoMessage.class,
            PolymorphicBazyngoMessageMixIn.class);
        return mapper.readValue(textMessage.getText(), BazyngoMessage.class);
    }

    /**
     * Converts json message data to Cyklotron OrganizationBazyngoMessage object
     * @param textMessage
     * @return CiviCrmMessage
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     * @throws JMSException
     */
    public BazyngoMessage getMessageObject(TextMessage textMessage)
        throws JsonParseException, JsonMappingException, IOException, JMSException
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(new CamelCaseNamingStrategy());
        return mapper.readValue(textMessage.getText(), OrganizationBazyngoMessage.class);
    }
    
    
    /**  
     * Converts standard CamelCase field and method names to   
     * typical JSON field names having all lower case characters   
     * with an underscore separating different words.  For   
     * example, all of the following are converted to JSON field   
     * name "some_name":  
     *   
     * Java field name "someName"    
     * Java method name "getSomeName"  
     * Java method name "setSomeName"  
     *   
     * Typical Use:  
     *  
     * String jsonString = "{\"foo_name\":\"fubar\"}";  
     * ObjectMapper mapper = new ObjectMapper();  
     * mapper.setPropertyNamingStrategy(  
     *     new CamelCaseNamingStrategy());  
     * Foo foo = mapper.readValue(jsonString, Foo.class);  
     * System.out.println(mapper.writeValueAsString(foo));  
     * // prints {"foo_name":"fubar"}
     *   
     * class Foo  
     * {  
     *   private String fooName;  
     *   public String getFooName() {return fooName;}  
     *   public void setFooName(String fooName)   
     *   {this.fooName = fooName;}  
     * }  
     */ 
    public class CamelCaseNamingStrategy
        extends PropertyNamingStrategy
    {
        @Override
        public String nameForGetterMethod(MapperConfig<?> config, AnnotatedMethod method,
            String defaultName)
        {
            return translate(defaultName);
        }

        @Override
        public String nameForSetterMethod(MapperConfig<?> config, AnnotatedMethod method,
            String defaultName)
        {
            return translate(defaultName);
        }

        @Override
        public String nameForField(MapperConfig<?> config, AnnotatedField field, String defaultName)
        {
            return translate(defaultName);
        }

        private String translate(String defaultName)
        {
            char[] nameChars = defaultName.toCharArray();
            StringBuilder nameTranslated = new StringBuilder(nameChars.length * 2);
            for(char c : nameChars)
            {
                if(Character.isUpperCase(c))
                {
                    nameTranslated.append("_");
                    c = Character.toLowerCase(c);
                }
                nameTranslated.append(c);
            }
            return nameTranslated.toString();
        }
    }


    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,  
        include = JsonTypeInfo.As.PROPERTY,  
        property = "message_type")
    @JsonSubTypes({  
        @Type(value = CategoriesTreeBazyngoMessage.class, name = "categories_tree"),  
        @Type(value = OrganizationBazyngoMessage.class, name = "organization") }) 
    public abstract static class PolymorphicBazyngoMessageMixIn
    {

    }

    public abstract static class BazyngoMessage
    {
        public String messageType;
        
        public String getMessageType()
        {
            return messageType;
        }
    }

    public static class CategoriesTreeBazyngoMessage
        extends BazyngoMessage
    {
        public String data;
        
        public String getData()
        {
            return data;
        }
    }

    public static class OrganizationBazyngoMessage
        extends BazyngoMessage
    {
        public String id;

        public String name;

        public String data;

        public String getId()
        {
            return id;
        }

        public String getName()
        {
            return name;
        }

        public String getData()
        {
            return data;
        }
    }
    
}