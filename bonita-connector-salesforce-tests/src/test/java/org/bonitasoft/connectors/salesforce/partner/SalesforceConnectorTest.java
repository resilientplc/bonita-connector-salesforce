/**
 * Copyright (C) 2011 BonitaSoft S.A.
 * BonitaSoft, 31 rue Gustave Eiffel - 38000 Grenoble
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.bonitasoft.connectors.salesforce.partner;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bonitasoft.engine.connector.Connector;
import org.bonitasoft.engine.connector.ConnectorValidationException;
import org.bonitasoft.engine.exception.BonitaException;
import org.bonitasoft.engine.test.annotation.Cover;
import org.bonitasoft.engine.test.annotation.Cover.BPMNConcept;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Charles Souillard, Haris Subasic
 * 
 */
public abstract class SalesforceConnectorTest {

    protected static final String ACCOUNT_OBJECT_TYPE = "Account";

    protected static final String CUSTOM_OBJECT_OBJECT_TYPE = "Profile__c";

    public static final String CUSTOM_OBJECT_FIELD = "Email__c";

    protected static final String ACCOUNT_NAME_FIELD = "Name";

    protected static final String ACCOUNT_PHONE_FIELD = "Phone";

    protected static final String ACCOUNT_WEBSITE_FIELD = "Website";

    protected static final String ACCOUNT_PHONE_DEFAULT_VALUE = "010-123456789";

    protected static final String ACCOUNT_WEBSITE_DEFAULT_VALUE = "http://www.bonitasoft.org";

    protected static final Logger LOG = Logger.getLogger(SalesforceConnectorTest.class.getName());

    protected static String S_OBJECT_ID = "sObjectId";

    @Before
    public void initialize() throws Exception {
        if (SalesforceConnectorTest.LOG.isLoggable(Level.WARNING)) {
            SalesforceConnectorTest.LOG.warning("======== Starting test: " + this.getClass().getName() + "() ==========");
        }
        getProperties();
    }

    @After
    public void addLogEnding() throws Exception {
        if (SalesforceConnectorTest.LOG.isLoggable(Level.WARNING)) {
            SalesforceConnectorTest.LOG.warning("======== Ending test: " + this.getClass().getName() + " ==========");
        }
    }

    private static String username;

    private static String password;

    private static String securityToken;

    private static String authEndpoint;

    private static String serviceEndpoint;

    private static String restEndpoint;

    private static String proxyHost;

    private static int proxyPort;

    private static String proxyUsername;

    private static String proxyPassword;

    private static int connectionTimeout;

    private static int readTimeout;

    public static Map<String, Object> setSalesforceConnectorParameters() {
        final Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("username", username);
        parameters.put("password", password);
        parameters.put("securityToken", securityToken);
        parameters.put("authEndpoint", authEndpoint);
        parameters.put("serviceEndpoint", serviceEndpoint);
        parameters.put("restEndpoint", restEndpoint);
        parameters.put("proxyHost", proxyHost);
        parameters.put("proxyPort", proxyPort);
        parameters.put("proxyUsername", proxyUsername);
        parameters.put("proxyPassword", proxyPassword);
        parameters.put("connectionTimeout", connectionTimeout);
        parameters.put("readTimeout", readTimeout);
        return parameters;
    }

    private void getProperties() throws Exception {
        Properties prop = new Properties();
        prop.load(this.getClass().getResourceAsStream("/salesforce.properties"));
        username = prop.getProperty("username");
        password = prop.getProperty("password");
        securityToken = prop.getProperty("securityToken");
        authEndpoint = prop.getProperty("authEndpoint");
        serviceEndpoint = prop.getProperty("serviceEndpoint");
        restEndpoint = prop.getProperty("restEndpoint");
        proxyHost = prop.getProperty("proxyHost");
        try {
            proxyPort = Integer.parseInt(prop.getProperty("proxyPort"));
        } catch (NumberFormatException nfe) {
            proxyPort = 0;
        }
        proxyUsername = prop.getProperty("proxyUsername");
        proxyPassword = prop.getProperty("proxyPassword");
        try {
            connectionTimeout = Integer.parseInt(prop.getProperty("connectionTimeout"));
        } catch (NumberFormatException nfe) {
            connectionTimeout = 0;
        }
        try {
            readTimeout = Integer.parseInt(prop.getProperty("readTimeout"));
        } catch (NumberFormatException nfe) {
            readTimeout = 0;
        }
    }

    protected abstract Class<? extends Connector> getConnectorClass();

    @Cover(classes = { SalesforceConnector.class }, exceptions = ConnectorValidationException.class, concept = BPMNConcept.CONNECTOR,
            jira = "ENGINE-461", keywords = { "Salesforce", "Connector" }, story = "Check value of parameter username for a Salesforce connector")
    @Test(expected = ConnectorValidationException.class)
    public void testEmptyUserName() throws BonitaException {
        final Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("username", "");
        SalesforceConnector connector = getSalesforceConnector(parameters);
        connector.setInputParameters(parameters);
        connector.validateInputParameters();
        // test null username
        parameters.put("username", null);
        connector.setInputParameters(parameters);
        connector.validateInputParameters();
        // test empty username
        parameters.put("username", "   ");
        connector.setInputParameters(parameters);
        connector.validateInputParameters();
    }

    @Cover(classes = { SalesforceConnector.class }, exceptions = ConnectorValidationException.class, concept = BPMNConcept.CONNECTOR,
            jira = "ENGINE-461", keywords = { "Salesforce", "Connector" }, story = "Check value of parameter password for a Salesforce connector")
    @Test(expected = ConnectorValidationException.class)
    public void testEmptyPassword() throws BonitaException {
        final Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("password", "");
        SalesforceConnector connector = getSalesforceConnector(parameters);
        connector.setInputParameters(parameters);
        connector.validateInputParameters();
        // test null password_securityToken
        parameters.put("password", null);
        connector.setInputParameters(parameters);
        connector.validateInputParameters();
    }

    @Cover(classes = { SalesforceConnector.class }, exceptions = ConnectorValidationException.class, concept = BPMNConcept.CONNECTOR,
            jira = "ENGINE-461", keywords = { "Salesforce", "Connector" }, story = "Check value of parameter security token for a Salesforce connector")
    @Test(expected = ConnectorValidationException.class)
    public void testEmptySecurityToken() throws BonitaException {
        final Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("securityToken", "");
        SalesforceConnector connector = getSalesforceConnector(parameters);
        connector.setInputParameters(parameters);
        connector.validateInputParameters();
        // test null password_securityToken
        parameters.put("securityToken", null);
        connector.setInputParameters(parameters);
        connector.validateInputParameters();
    }

    @Cover(classes = { SalesforceConnector.class }, exceptions = ConnectorValidationException.class, concept = BPMNConcept.CONNECTOR,
            jira = "ENGINE-461", keywords = { "Salesforce", "Connector" }, story = "Check value of parameter proxyport for a Salesforce connector")
    @Test(expected = ConnectorValidationException.class)
    public void testProxyPort() throws BonitaException {
        final Map<String, Object> parameters = new HashMap<String, Object>();
        SalesforceConnector connector = getSalesforceConnector(parameters);
        // large number port test
        parameters.put("proxyPort", 68099);
        connector.setInputParameters(parameters);
        connector.validateInputParameters();
    }

    @Cover(classes = { SalesforceConnector.class }, exceptions = ConnectorValidationException.class, concept = BPMNConcept.CONNECTOR,
            jira = "ENGINE-689", keywords = { "Salesforce", "Connector" }, story = "Check value of parameter proxyport for a Salesforce connector")
    @Test(expected = ConnectorValidationException.class)
    public void testBadProxyPort() throws BonitaException {
        final Map<String, Object> parameters = new HashMap<String, Object>();
        SalesforceConnector connector = getSalesforceConnector(parameters);
        // negative port number
        parameters.put("proxyPort", -51);
        connector.setInputParameters(parameters);
        connector.validateInputParameters();
    }

    @Cover(classes = { SalesforceConnector.class }, exceptions = ConnectorValidationException.class, concept = BPMNConcept.CONNECTOR,
            jira = "ENGINE-461", keywords = { "Salesforce", "Connector" }, story = "Check value of parameter connectionTimeout for a Salesforce connector")
    @Test(expected = ConnectorValidationException.class)
    public void testConnectionTimeout() throws BonitaException {
        final Map<String, Object> parameters = new HashMap<String, Object>();
        SalesforceConnector connector = getSalesforceConnector(parameters);
        parameters.put("connectionTimeout", -100);
        connector.setInputParameters(parameters);
        connector.validateInputParameters();
    }

    @Cover(classes = { SalesforceConnector.class }, exceptions = ConnectorValidationException.class, concept = BPMNConcept.CONNECTOR,
            jira = "ENGINE-461", keywords = { "Salesforce", "Connector" }, story = "Check value of parameter readTimeout for a Salesforce connector")
    @Test(expected = ConnectorValidationException.class)
    public void testReadTimeout() throws BonitaException {
        final Map<String, Object> parameters = new HashMap<String, Object>();
        SalesforceConnector connector = getSalesforceConnector(parameters);
        parameters.put("readTimeout", -100);
        connector.setInputParameters(parameters);
        connector.validateInputParameters();
    }

    @Cover(classes = { SalesforceConnector.class }, concept = BPMNConcept.CONNECTOR, keywords = { "Salesforce", "Connector" }, story =
            "Check value of account name.", jira = "ENGINE-689")
    @Test
    public void testGetAccountName() throws BonitaException {
        String accountName = SalesforceTool.getAccountName();
        assertTrue(accountName.startsWith("UnitTest_"));
    }



    protected abstract SalesforceConnector getSalesforceConnector(Map<String, Object> parameters);

}
