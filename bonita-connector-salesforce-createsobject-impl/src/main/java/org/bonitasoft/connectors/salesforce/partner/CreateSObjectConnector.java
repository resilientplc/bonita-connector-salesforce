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

import java.util.ArrayList;
import java.util.List;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;

/**
 * @author Charles Souillard, Haris Subasic
 * 
 */
public class CreateSObjectConnector extends SalesforceConnector {

    // input parameters
    private static final String S_OBJECT_TYPE = "sObjectType";

    private static final String FIELD_VALUES = "fieldValues";

    // output parameters
    protected String sObjectId;

    protected SaveResult saveResult;

    @Override
    protected final void executeFunction(final PartnerConnection connection)
            throws Exception {
        final SObject sObject = new SObject();
        sObject.setType((String) getInputParameter(S_OBJECT_TYPE));
        @SuppressWarnings("unchecked")
        // final Map<String, Object> fieldValues = (Map<String, Object>) getInputParameter(
        // FIELD_VALUES, (Serializable) Collections.emptyMap());
        // for (final Map.Entry<String, Object> field : fieldValues.entrySet()) {
        // sObject.setField(field.getKey(), field.getValue());
        // }
        final List<List<Object>> parametersList = (List<List<Object>>) getInputParameter(FIELD_VALUES);
        if (parametersList != null) {
            for (List<Object> rows : parametersList) {
                if (rows.size() == 2) {
                    Object keyContent = rows.get(0);
                    Object valueContent = rows.get(1);
                    if (keyContent != null && valueContent != null) {
                        final String key = keyContent.toString();
                        final String value = valueContent.toString();
                        sObject.setField(key, value);
                    }
                }
            }
        }

        final SObject[] sObjects = new SObject[] { sObject };

        final SaveResult[] sResults = connection.create(sObjects);
        if (sResults != null && sResults.length > 0) {
            setOutputParameter("sObjectId", sResults[0].getId());
            setOutputParameter("saveResult", sResults[0]);
        }
    }

    @Override
    protected List<String> validateExtraValues() {
        final List<String> errors = new ArrayList<String>(1);
        final String objType = ((String) getInputParameter(S_OBJECT_TYPE));
        if (objType == null || objType.length() == 0) {
            errors.add("objectType cannot be null or empty");
        }
        @SuppressWarnings("unchecked")
        // final Map<String, Object> fieldValues = (Map<String, Object>) getInputParameter(FIELD_VALUES);
        final List<List<String>> fieldValues = (List<List<String>>) getInputParameter(FIELD_VALUES);
        if (fieldValues == null || fieldValues.size() == 0) {
            errors.add("fieldValues cannot be null or empty");
        }

        return errors;
    }

}
