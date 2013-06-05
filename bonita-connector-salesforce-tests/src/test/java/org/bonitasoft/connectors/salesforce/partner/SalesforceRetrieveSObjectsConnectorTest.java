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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bonitasoft.engine.connector.Connector;
import org.bonitasoft.engine.connector.ConnectorValidationException;
import org.bonitasoft.engine.test.annotation.Cover;
import org.bonitasoft.engine.test.annotation.Cover.BPMNConcept;
import org.junit.Test;

public class SalesforceRetrieveSObjectsConnectorTest extends SalesforceConnectorTest {

    // input parameters
    private static final String FIELDS_TO_RETRIEVE = "fieldsToRetrieve";

    private static final String S_OBJECT_TYPE = "sObjectType";

    private static final String S_OBJECT_IDS = "sObjectIds";

    // output parameters
    protected String S_OBJECTS = "sObjects";

    public RetrieveSObjectsConnector getRetrieveSObjectsConnector(final Map<String, Object> parameters) {
        final RetrieveSObjectsConnector status = new RetrieveSObjectsConnector();
        final Map<String, Object> defaultParameters = setSalesforceConnectorParameters();
        defaultParameters.putAll(parameters);
        status.setInputParameters(defaultParameters);
        return status;
    }

    @Override
    public SalesforceConnector getSalesforceConnector(final Map<String, Object> parameters) {
        return getRetrieveSObjectsConnector(parameters);
    }

    @Override
    protected Class<? extends Connector> getConnectorClass() {
        return RetrieveSObjectsConnector.class;
    }

    @Cover(classes = { RetrieveSObjectsConnector.class }, concept = BPMNConcept.CONNECTOR, jira = "ENGINE-461",
            keywords = { "Salesforce", "Connector", "Retrieve" }, story = "Tests retrieve of salesforce objects")
    @Test
    public void testRetrieveAccount() throws Exception {
        String accountName = "UnitTest_RetrieveAccount";
        Map<String, Object> createResult = SalesforceCreateSObjectConnectorTest.createAccount(accountName);
        String accountId = (String) createResult.get(S_OBJECT_ID);
        Map<String, Object> retrieveParams = new HashMap<String, Object>();
        List<String> fieldsList = new ArrayList<String>(2);
        fieldsList.add(ACCOUNT_NAME_FIELD);
        fieldsList.add(ACCOUNT_WEBSITE_FIELD);
        retrieveParams.put(FIELDS_TO_RETRIEVE, fieldsList);
        retrieveParams.put(S_OBJECT_TYPE, ACCOUNT_OBJECT_TYPE);
        List<String> listIds = new ArrayList<String>(1);
        listIds.add(accountId);
        retrieveParams.put(S_OBJECT_IDS, listIds);
        RetrieveSObjectsConnector retrieveConnector = getRetrieveSObjectsConnector(retrieveParams);
        retrieveConnector.validateInputParameters();
        Map<String, Object> retrieveResult = retrieveConnector.execute();
        assertNotNull(retrieveResult.get(S_OBJECTS));
        assertTrue(retrieveResult.get(S_OBJECTS).toString().contains("Account"));
        assertTrue(retrieveResult.get(S_OBJECTS).toString().contains(accountId));
        SalesforceDeleteSObjectsConnectorTest.deleteSObjects(accountId);
        retrieveResult = retrieveConnector.execute();
        assertTrue(retrieveResult.get(S_OBJECTS).toString().contains("null"));
    }

    @Cover(classes = { RetrieveSObjectsConnector.class }, concept = BPMNConcept.CONNECTOR, jira = "ENGINE-689",
            keywords = { "Salesforce", "Connector", "Retrieve" }, story = "Tests exception when retrieving salesforce objects with bad id.")
    @Test(expected = ConnectorValidationException.class)
    public void testRetrieveAccountWithBadId() throws Exception {
        String accountId = "";
        Map<String, Object> retrieveParams = new HashMap<String, Object>();
        List<String> fieldsList = new ArrayList<String>(2);
        fieldsList.add(ACCOUNT_NAME_FIELD);
        fieldsList.add(ACCOUNT_WEBSITE_FIELD);
        retrieveParams.put(FIELDS_TO_RETRIEVE, fieldsList);
        retrieveParams.put(S_OBJECT_TYPE, ACCOUNT_OBJECT_TYPE);
        List<String> listIds = new ArrayList<String>(1);
        listIds.add(accountId);
        retrieveParams.put(S_OBJECT_IDS, listIds);
        RetrieveSObjectsConnector retrieveConnector = getRetrieveSObjectsConnector(retrieveParams);
        retrieveConnector.validateInputParameters();
        retrieveConnector.execute();
    }

    @Test
    public void testWrongVersionOfAPI() {
        SalesforceConnector salesforceConnector = new RetrieveSObjectsConnector();
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
