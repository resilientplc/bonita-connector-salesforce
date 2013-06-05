/**
 * Copyright (C) 2011 BonitaSoft S.A.
 * BonitaSoft, 31 rue Gustave Eiffel - 38000 Grenoble
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.bonitasoft.connectors.salesforce.partner;

import static org.junit.Assert.assertEquals;
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
import org.junit.Test;

public class SalesforceUpdateSObjectsConnectorTest extends SalesforceConnectorTest {

    private static final String S_OBJECT_TYPE = "sObjectType";

    private static final String S_OBJECT_ID = "sObjectId";

    private static final String FIELD_VALUES = "fieldValues";

    public static UpdateSObjectConnector getUpdateSObjectConnector(final Map<String, Object> parameters) {
        final UpdateSObjectConnector status = new UpdateSObjectConnector();
        final Map<String, Object> defaultParameters = setSalesforceConnectorParameters();
        defaultParameters.putAll(parameters);
        status.setInputParameters(defaultParameters);
        return status;
    }

    @Override
    public SalesforceConnector getSalesforceConnector(final Map<String, Object> parameters) {
        return getUpdateSObjectConnector(parameters);
    }

    @Override
    protected Class<? extends Connector> getConnectorClass() {
        return UpdateSObjectConnector.class;
    }

    @Cover(classes = { UpdateSObjectConnector.class }, concept = BPMNConcept.CONNECTOR, jira = "ENGINE-461", keywords = { "Salesforce", "Connector", "Update" }, story = "Tests update of salesforce objects")
    @Test
    public void testUpdateAccount() throws Exception {
        String accountName = "UnitTest_UpdateAccountInitial";
        String accountNameChanged = "UnitTest_UpdateAccountChanged";

        Map<String, Object> results = SalesforceCreateSObjectConnectorTest.createAccount(accountName);
        String accountId = (String) results.get("sObjectId");

        assertEquals(1, SalesforceQuerySObjectsConnectorTest.getNumberOfAccount(accountName));
        assertEquals(0, SalesforceQuerySObjectsConnectorTest.getNumberOfAccount(accountNameChanged));

        updateAccountName(accountId, accountNameChanged);

        assertEquals(0, SalesforceQuerySObjectsConnectorTest.getNumberOfAccount(accountName));
        assertEquals(1, SalesforceQuerySObjectsConnectorTest.getNumberOfAccount(accountNameChanged));

        updateAccountName(accountId, accountName);

        assertEquals(1, SalesforceQuerySObjectsConnectorTest.getNumberOfAccount(accountName));
        assertEquals(0, SalesforceQuerySObjectsConnectorTest.getNumberOfAccount(accountNameChanged));

        SalesforceDeleteSObjectsConnectorTest.deleteSObjects(accountId);

    }

    @Cover(classes = { UpdateSObjectConnector.class }, concept = BPMNConcept.CONNECTOR, jira = "ENGINE-689", keywords = { "Salesforce", "Connector", "Update" },
            story = "Tests get exception when updating salesforce objects with bad id.")
    @Test(expected = ConnectorValidationException.class)
    public void testUpdateAccountWithBadId() throws Exception {
        String accountNameChanged = "UpdateAccountChanged";
        String accountId = "aa";
        updateAccountName(accountId, accountNameChanged);
    }

    public static Map<String, Object> updateAccountName(final String id, final String value) throws ConnectorValidationException, ConnectorException {
        Map<String, Object> params = new HashMap<String, Object>(3);
        params.put(S_OBJECT_TYPE, ACCOUNT_OBJECT_TYPE);
        params.put(S_OBJECT_ID, id);
        // Map<String, Object> fieldValuesMap = new HashMap<String, Object>(1);
        // fieldValuesMap.put(ACCOUNT_NAME_FIELD, value);
        // params.put(FIELD_VALUES, fieldValuesMap);
        final List<List<String>> fieldsValues = new ArrayList<List<String>>();
        List<String> values = new ArrayList<String>();
        values.add(ACCOUNT_NAME_FIELD);
        values.add(value);
        fieldsValues.add(values);
        params.put(FIELD_VALUES, fieldsValues);
        UpdateSObjectConnector updateConnector = getUpdateSObjectConnector(params);
        updateConnector.validateInputParameters();
        return updateConnector.execute();
    }

    @Test
    public void testWrongVersionOfAPI() {
        SalesforceConnector salesforceConnector = new UpdateSObjectConnector();
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
