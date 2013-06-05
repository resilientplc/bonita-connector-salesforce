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
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.util.HashMap;
import java.util.Map;

import com.sforce.soap.partner.QueryResult;
import org.bonitasoft.engine.connector.Connector;
import org.bonitasoft.engine.connector.ConnectorException;
import org.bonitasoft.engine.connector.ConnectorValidationException;
import org.bonitasoft.engine.test.annotation.Cover;
import org.bonitasoft.engine.test.annotation.Cover.BPMNConcept;
import org.junit.Test;

/**
 * @author Haris Subasic
 * 
 */

public class SalesforceQuerySObjectsConnectorTest extends SalesforceConnectorTest {

    // input parameters
    private static final String QUERY = "query";

    // output parameters
    private static final String QUERY_RESULT = "queryResult";

    public static QuerySObjectsConnector getQuerySObjectsConnector(final Map<String, Object> parameters) {
        final QuerySObjectsConnector status = new QuerySObjectsConnector();
        final Map<String, Object> defaultParameters = setSalesforceConnectorParameters();
        defaultParameters.putAll(parameters);
        status.setInputParameters(defaultParameters);
        return status;
    }

    @Override
    public SalesforceConnector getSalesforceConnector(final Map<String, Object> parameters) {
        return getQuerySObjectsConnector(parameters);
    }

    @Override
    protected Class<? extends Connector> getConnectorClass() {
        return QuerySObjectsConnector.class;
    }

    public static int getNumberOfAccount(String name) throws ConnectorException, ConnectorValidationException {
        String strQuery = getSelectAccountByName(name);
        Map<String, Object> result = executeQuery(strQuery);
        QueryResult queryResult = (QueryResult) result.get(QUERY_RESULT);
        return queryResult.getSize();
    }

    public static int getNumberOfCustomObject(String field) throws ConnectorException, ConnectorValidationException {
        String strQuery = getSelectCustomObjectByCustomField(field);
        Map<String, Object> result = executeQuery(strQuery);
        QueryResult queryResult = (QueryResult) result.get(QUERY_RESULT);
        return queryResult.getSize();
    }

    public static String getSelectCustomObjectByCustomField(final String fieldValue) {
        final StringBuilder sb = new StringBuilder();
        sb.append("SELECT");
        sb.append(" ");
        sb.append(CUSTOM_OBJECT_FIELD);
        sb.append(" ");
        sb.append("FROM");
        sb.append(" ");
        sb.append(CUSTOM_OBJECT_OBJECT_TYPE);
        sb.append(" ");
        sb.append("WHERE");
        sb.append(" ");
        sb.append(CUSTOM_OBJECT_FIELD + " like '").append(fieldValue).append("%'");
        return sb.toString();
    }

    public static String getSelectAccountByName(final String name) {
        final StringBuilder sb = new StringBuilder();
        sb.append("SELECT");
        sb.append(" ");
        sb.append(ACCOUNT_NAME_FIELD);
        sb.append(",");
        sb.append(ACCOUNT_PHONE_FIELD);
        sb.append(",");
        sb.append(ACCOUNT_WEBSITE_FIELD);
        sb.append(" ");
        sb.append("FROM");
        sb.append(" ");
        sb.append(ACCOUNT_OBJECT_TYPE);
        sb.append(" ");
        sb.append("WHERE");
        sb.append(" ");
        sb.append(ACCOUNT_NAME_FIELD + " like '").append(name).append("%'");
        return sb.toString();
    }

    public static String getSelectAccountById(final String id) {
        final StringBuilder sb = new StringBuilder();
        sb.append("SELECT");
        sb.append(" ");
        sb.append(ACCOUNT_NAME_FIELD);
        sb.append(",");
        sb.append(ACCOUNT_PHONE_FIELD);
        sb.append(",");
        sb.append(ACCOUNT_WEBSITE_FIELD);
        sb.append(" ");
        sb.append("FROM");
        sb.append(" ");
        sb.append(ACCOUNT_OBJECT_TYPE);
        sb.append(" ");
        sb.append("WHERE");
        sb.append(" ");
        sb.append("id = '").append(id).append("'");
        return sb.toString();
    }

    public static Map<String, Object> executeQuery(String strQuery) throws ConnectorValidationException, ConnectorException {
        Map<String, Object> params = new HashMap<String, Object>(1);
        params.put(QUERY, strQuery);
        QuerySObjectsConnector queryConnector = getQuerySObjectsConnector(params);
        queryConnector.validateInputParameters();
        return queryConnector.execute();
    }

    @Cover(classes = { QuerySObjectsConnector.class }, concept = BPMNConcept.CONNECTOR, jira = "ENGINE-461",
            keywords = { "Salesforce", "Connector", "Query" }, story = "Tests query of salesforce objects")
    @Test
    public void testQueryAccount() throws Exception {
        String accountName1 = "UnitTest_QueryAccount1";
        String accountName2 = "UnitTest_QueryAccount2";
        String accountName3 = "UnitTest_QueryAccount3";

        assertEquals(0, getNumberOfAccount("UnitTest_QueryAccount"));

        Map<String, Object> createResult1 = SalesforceCreateSObjectConnectorTest.createAccount(accountName1);
        String accountId1 = (String) createResult1.get(S_OBJECT_ID);
        Map<String, Object> createResult2 = SalesforceCreateSObjectConnectorTest.createAccount(accountName2);
        String accountId2 = (String) createResult2.get(S_OBJECT_ID);
        Map<String, Object> createResult3 = SalesforceCreateSObjectConnectorTest.createAccount(accountName3);
        String accountId3 = (String) createResult3.get(S_OBJECT_ID);

        assertEquals(3, getNumberOfAccount("UnitTest_QueryAccount"));

        SalesforceDeleteSObjectsConnectorTest.deleteSObjects(accountId1, accountId2, accountId3);

        assertEquals(0, getNumberOfAccount("UnitTest_QueryAccount"));

    }

    @Test
    public void testWrongVersionOfAPI() {
        SalesforceConnector salesforceConnector = new QuerySObjectsConnector();
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
