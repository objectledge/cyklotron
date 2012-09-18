package net.cyklotron.cms.ngodatabase;

import javax.jms.Connection;
import javax.jms.JMSException;

public interface BrokerConnectionProvider
{
    /**
     * Return connection to JMS Broker.
     * @throws JMSException 
     */
    public Connection createConnection() throws JMSException;
}
