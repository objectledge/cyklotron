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

package net.cyklotron.cms.ngodatabase.jms;

import javax.jms.Connection;
import javax.jms.JMSException;

import net.cyklotron.cms.ngodatabase.BrokerConnectionProvider;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.jcontainer.dna.Configuration;

public class NgoConnectionProvider
    implements BrokerConnectionProvider
{
    private String brokerUrl;

    private String brokerUser;

    private String brokerPassword;
    
    private String brokerQueue;
    
    private String brokerTopic;
    
    private final ActiveMQConnectionFactory connectionFactory;

    public NgoConnectionProvider(Configuration config)
    {
        this.brokerUrl = config.getChild("broker_url").getValue(
            ActiveMQConnectionFactory.DEFAULT_BROKER_URL);
        this.brokerUser = config.getChild("broker_user").getValue(
            ActiveMQConnectionFactory.DEFAULT_USER);
        this.brokerPassword = config.getChild("broker_password").getValue(
            ActiveMQConnectionFactory.DEFAULT_PASSWORD);        
        this.brokerPassword = config.getChild("broker_password").getValue(
            ActiveMQConnectionFactory.DEFAULT_PASSWORD);
        
        this.connectionFactory = new ActiveMQConnectionFactory(brokerUser, brokerPassword,
            brokerUrl);
    }

    public Connection createConnection()
        throws JMSException
    {
        return connectionFactory.createConnection();
    }

}
