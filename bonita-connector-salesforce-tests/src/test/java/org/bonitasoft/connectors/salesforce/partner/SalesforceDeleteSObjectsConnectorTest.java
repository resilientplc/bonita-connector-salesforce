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

import com.sforce.soap.partner.DeleteResult;
import org.bonitasoft.engine.connector.Connector;
import org.bonitasoft.engine.connector.ConnectorException;
import org.bonitasoft.engine.connector.ConnectorValidationException;
import org.bonitasoft.engine.test.annotation.Cover;
import org.bonitasoft.engine.test.annotation.Cover.BPMNConcept;
import org.junit.Test;

public class SalesforceDeleteSObjectsConnectorTest extends SalesforceConnectorTest {

    // input parameters
    private static final String S_OBJECT_IDS = "sObjectIds";

    // output parameters
    protected DeleteResult deleteResults;

    public static DeleteSObjectsConnector getDeleteSObjectsConnector(final Map<String, Object> parameters) {
        final DeleteSObjectsConnector status = new DeleteSObjectsConnector();
        final Map<String, Object> defaultParameters = setSalesforceConnectorParameters();
        defaultParameters.putAll(parameters);
        status.setInputParameters(defaultParameters);
        return status;
    }

    @Override
    public SalesforceConnector getSalesforceConnector(final Map<String, Object> parameters) {
        return getDeleteSObjectsConnector(parameters);
    }

    @Override
    protected Class<? extends Connector> getConnectorClass() {
        return DeleteSObjectsConnector.class;
    }

    @Cover(classes = { DeleteSObjectsConnector.class }, concept = BPMNConcept.CONNECTOR, jira = "ENGINE-461",
            keywords = { "Salesforce", "Connector", "Delete" }, story = "Tests deletion of salesforce objects")
    @Test
    public void testDeleteAccount() throws Exception {
        String accountName = "UnitTest_DeleteAccount";
        assertEquals(0, SalesforceQuerySObjectsConnectorTest.getNumberOfAccount(accountName));
        Map<String, Object> results = SalesforceCreateSObjectConnectorTest.createAccount(accountName);
        String accountId = (String) results.get("sObjectId");

        assertNotNull(accountId);
        assertNotNull(results.get("saveResult"));
        assertEquals(1, SalesforceQuerySObjectsConnectorTest.getNumberOfAccount(accountName));

        SalesforceDeleteSObjectsConnectorTest.deleteSObjects(accountId);

        assertEquals(0, SalesforceQuerySObjectsConnectorTest.getNumberOfAccount(accountName));

    }

    @Cover(classes = { DeleteSObjectsConnector.class }, concept = BPMNConcept.CONNECTOR, jira = "ENGINE-689",
            keywords = { "Salesforce", "Connector", "Delete" }, story = "Tests get exception when deleting salesforce objects with bad id.")
    @Test(expected = ConnectorValidationException.class)
    public void testDeleteAccountWithBadId() throws Exception {
        String accountId = "";
        SalesforceDeleteSObjectsConnectorTest.deleteSObjects(accountId);
    }

    public static Map<String, Object> deleteSObjects(String... Ids) throws ConnectorException, ConnectorValidationException {
        Map<String, Object> paramsDelete = new HashMap<String, Object>(1);
        List<String> listIds = new ArrayList<String>();
        for (String s : Ids) {
            listIds.add(s);
        }
        paramsDelete.put(S_OBJECT_IDS, listIds);
        DeleteSObjectsConnector deleteConnector = getDeleteSObjectsConnector(paramsDelete);
        deleteConnector.validateInputParameters();
        return deleteConnector.execute();
    }

    @Test
    public void testWrongVersionOfAPI() {
        SalesforceConnector salesforceConnector = new DeleteSObjectsConnector();
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
