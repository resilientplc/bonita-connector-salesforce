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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bonitasoft.engine.connector.Connector;
import org.bonitasoft.engine.connector.ConnectorException;
import org.bonitasoft.engine.connector.ConnectorValidationException;
import org.bonitasoft.engine.test.annotation.Cover;
import org.bonitasoft.engine.test.annotation.Cover.BPMNConcept;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Haris Subasic
 * 
 */

public class SalesforceCreateSObjectConnectorTest extends SalesforceConnectorTest {

    // Input parameters

    private static final String S_OBJECT_TYPE = "sObjectType";

    private static final String FIELD_VALUES = "fieldValues";

    // Output parameters

    public static CreateSObjectConnector getCreateSObjectConnector(final Map<String, Object> parameters) {
        final CreateSObjectConnector status = new CreateSObjectConnector();
        final Map<String, Object> defaultParameters = setSalesforceConnectorParameters();
        defaultParameters.putAll(parameters);
        status.setInputParameters(defaultParameters);
        return status;
    }

    @Override
    public SalesforceConnector getSalesforceConnector(final Map<String, Object> parameters) {
        return getCreateSObjectConnector(parameters);
    }

    @Override
    protected Class<? extends Connector> getConnectorClass() {
        return CreateSObjectConnector.class;
    }

    public static Map<String, Object> createAccount(String accountName) throws ConnectorValidationException, ConnectorException {
        Map<String, Object> params = new HashMap<String, Object>(2);
        params.put(S_OBJECT_TYPE, ACCOUNT_OBJECT_TYPE);

        // Map<String, Object> fieldsValuesMap = new HashMap<String, Object>();
        // fieldsValuesMap.put(ACCOUNT_NAME_FIELD, accountName);
        // fieldsValuesMap.put(ACCOUNT_PHONE_FIELD, ACCOUNT_PHONE_DEFAULT_VALUE);
        // fieldsValuesMap.put(ACCOUNT_WEBSITE_FIELD, ACCOUNT_WEBSITE_DEFAULT_VALUE);
        // params.put(FIELD_VALUES, fieldsValuesMap);
        final List<List<String>> fieldsValues = new ArrayList<List<String>>();
        List<String> values = new ArrayList<String>();
        values.add(ACCOUNT_NAME_FIELD);
        values.add(accountName);
        fieldsValues.add(values);
        values = new ArrayList<String>();
        values.add(ACCOUNT_PHONE_FIELD);
        values.add(ACCOUNT_PHONE_DEFAULT_VALUE);
        fieldsValues.add(values);
        values = new ArrayList<String>();
        values.add(ACCOUNT_WEBSITE_FIELD);
        values.add(ACCOUNT_WEBSITE_DEFAULT_VALUE);
        fieldsValues.add(values);
        params.put(FIELD_VALUES, fieldsValues);
        final CreateSObjectConnector createConnector = getCreateSObjectConnector(params);
        createConnector.validateInputParameters();
        return createConnector.execute();
    }

    public static Map<String, Object> createCustomObject(String email) throws ConnectorValidationException, ConnectorException {
        Map<String, Object> params = new HashMap<String, Object>(1);
        params.put(S_OBJECT_TYPE, CUSTOM_OBJECT_OBJECT_TYPE);

        // Map<String, Object> fieldsValuesMap = new HashMap<String, Object>();
        // fieldsValuesMap.put("Name", email);
        // fieldsValuesMap.put(CUSTOM_OBJECT_FIELD, email);
        // params.put(FIELD_VALUES, fieldsValuesMap);
        final List<List<String>> fieldsValues = new ArrayList<List<String>>();
        List<String> values = new ArrayList<String>();
        values.add("Name");
        values.add(email);
        fieldsValues.add(values);
        values = new ArrayList<String>();
        values.add(CUSTOM_OBJECT_FIELD);
        values.add(email);
        fieldsValues.add(values);
        params.put(FIELD_VALUES, fieldsValues);
        final CreateSObjectConnector createConnector = getCreateSObjectConnector(params);
        createConnector.validateInputParameters();
        return createConnector.execute();
    }

    @Cover(classes = { CreateSObjectConnector.class }, concept = BPMNConcept.CONNECTOR, jira = "ENGINE-461",
            keywords = { "Salesforce", "Connector", "Create" }, story = "Tests creation of salesforce objects")
    @Test
    public void testCreateAccount() throws Exception {
        String accountName = "UnitTest_CreateAccount";
        int numberOfAccountsAtStart = SalesforceQuerySObjectsConnectorTest.getNumberOfAccount(accountName);

        Map<String, Object> results = createAccount(accountName);
        String accountId = (String) results.get(S_OBJECT_ID);

        assertNotNull(accountId);
        assertNotNull(results.get("saveResult"));
        assertEquals(numberOfAccountsAtStart + 1, SalesforceQuerySObjectsConnectorTest.getNumberOfAccount(accountName));

        SalesforceDeleteSObjectsConnectorTest.deleteSObjects(accountId);

        assertEquals(numberOfAccountsAtStart, SalesforceQuerySObjectsConnectorTest.getNumberOfAccount(accountName));

    }

    @Ignore("Custom datatype seems not accessible in free account")
    @Cover(classes = { CreateSObjectConnector.class }, concept = BPMNConcept.CONNECTOR, jira = "ENGINE-461",
            keywords = { "Salesforce", "Connector", "Create", "Custom object" }, story = "Tests creation of salesforce custom objects")
    @Test
    public void testCreateCustomObject() throws Exception {
        String fieldEmail = "test.unittest@bonitasoft.com";
        assertEquals(0, SalesforceQuerySObjectsConnectorTest.getNumberOfCustomObject(fieldEmail));
        Map<String, Object> results = createCustomObject(fieldEmail);
        String accountId = (String) results.get("sObjectId");

        assertNotNull(accountId);
        assertNotNull(results.get("saveResult"));
        assertEquals(1, SalesforceQuerySObjectsConnectorTest.getNumberOfCustomObject(fieldEmail));

        SalesforceDeleteSObjectsConnectorTest.deleteSObjects(accountId);

        assertEquals(0, SalesforceQuerySObjectsConnectorTest.getNumberOfCustomObject(fieldEmail));
    }

    @Test
    public void testValidateInputParametersWithNullValues() {
        SalesforceConnector salesforceConnector = new CreateSObjectConnector();
        try {
            salesforceConnector.validateInputParameters();
            fail("Connector ConnectorValidationException should fail");
        } catch (ConnectorValidationException e) {
            assertThat(e.getMessage(), containsString("username"));
            assertThat(e.getMessage(), containsString("password"));
            assertThat(e.getMessage(), containsString("security token"));
        }
    }

    @Test
    public void testValidateInputParametersWithEmptyValues() {
        SalesforceConnector salesforceConnector = new CreateSObjectConnector();
        final Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("username", "");
        parameters.put("password", "");
        parameters.put("securityToken", "");
        parameters.put("authEndpoint", "");
        parameters.put("serviceEndpoint", "");
        parameters.put("restEndpoint", "");
        parameters.put("proxyHost", "");
        parameters.put("proxyPort", "");
        parameters.put("proxyUsername", "");
        parameters.put("proxyPassword", "");
        salesforceConnector.setInputParameters(parameters);
        try {
            salesforceConnector.validateInputParameters();
            fail("Connector ConnectorValidationException should fail");
        } catch (ConnectorValidationException e) {
            assertThat(e.getMessage(), containsString("username"));
            assertThat(e.getMessage(), containsString("password"));
            assertThat(e.getMessage(), containsString("security token"));
        }
    }

    @Test
    public void testWrongVersionOfAPI() {
        SalesforceConnector salesforceConnector = new CreateSObjectConnector();
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("serviceEndpoint", "https://login.salesforce.com/services/Soap/u/19.0");
        salesforceConnector.setInputParameters(parameters);
        try {
            salesforceConnector.validateInputParameters();
            fail("Connector validation should fail with an error according to API version");
        } catch (ConnectorValidationException e) {
            assertThat(e.getMessage(), containsString("API version"));
        }
    }
}
