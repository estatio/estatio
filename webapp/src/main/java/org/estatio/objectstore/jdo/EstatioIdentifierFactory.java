/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.estatio.objectstore.jdo;

import java.util.Map;

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.FieldRole;
import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;
import org.datanucleus.store.rdbms.identifier.AbstractIdentifierFactory;
import org.datanucleus.store.rdbms.identifier.DN2IdentifierFactory;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifierImpl;
import org.datanucleus.store.rdbms.identifier.IdentifierFactory;
import org.datanucleus.store.rdbms.identifier.IdentifierType;

public class EstatioIdentifierFactory extends DN2IdentifierFactory {

    public EstatioIdentifierFactory(DatastoreAdapter dba, ClassLoaderResolver clr, Map props) {
        super(dba, clr, props);
    }

    @Override
    public DatastoreIdentifier newJoinTableFieldIdentifier(AbstractMemberMetaData ownerFmd, AbstractMemberMetaData relatedFmd, DatastoreIdentifier destinationId, boolean embedded, int fieldRole) {
        return super.newJoinTableFieldIdentifier(ownerFmd, relatedFmd, destinationId, embedded, fieldRole);
    }

    public DatastoreIdentifier newColumnIdentifier(String javaName, boolean embedded, int fieldRole)
    {
        DatastoreIdentifier identifier = null;
        String key = "[" + (javaName == null ? "" : javaName) + "][" + embedded + "][" + fieldRole; // TODO Change this to a string form of fieldRole
        identifier = columns.get(key);
        if (identifier == null)
        {
            if (fieldRole == FieldRole.ROLE_CUSTOM)
            {
                // If the user has provided a name (CUSTOM) so dont need to generate it and dont need a suffix
                String baseID = truncate(javaName, dba.getDatastoreIdentifierMaxLength(IdentifierType.COLUMN));
                identifier = new ColumnIdentifier(this, baseID);
            }
            else
            {
                String suffix = getColumnIdentifierSuffix(fieldRole, embedded);
                String datastoreID = generateIdentifierNameForJavaName(javaName);
                String baseID = truncate(datastoreID, dba.getDatastoreIdentifierMaxLength(IdentifierType.COLUMN) - suffix.length());
                identifier = new ColumnIdentifier(this, baseID + suffix);
            }
            columns.put(key, identifier);
        }
        return identifier;
    }

    @Override
    public DatastoreIdentifier newForeignKeyFieldIdentifier(AbstractMemberMetaData ownerFmd, DatastoreIdentifier destinationId, boolean embedded, int fieldRole) {
        return super.newForeignKeyFieldIdentifier(ownerFmd, destinationId, embedded, fieldRole);
    }

    @Override
    public DatastoreIdentifier newVersionFieldIdentifier() {
        return super.newVersionFieldIdentifier();
    }

    @Override
    public DatastoreIdentifier newIndexFieldIdentifier(AbstractMemberMetaData mmd) {
        return super.newIndexFieldIdentifier(mmd);
    }

    @Override
    public DatastoreIdentifier newAdapterIndexFieldIdentifier() {
        return super.newAdapterIndexFieldIdentifier();
    }

    @Override
    public String generateIdentifierNameForJavaName(String javaName) {
        return super.generateIdentifierNameForJavaName(javaName);
    }

    @Override
    public DatastoreIdentifier newTableIdentifier(AbstractMemberMetaData fmd) {
        return super.newTableIdentifier(fmd);
    }

    @Override
    public DatastoreIdentifier newTableIdentifier(AbstractClassMetaData cmd) {
        return super.newTableIdentifier(cmd);
    }

    @Override
    public DatastoreIdentifier newReferenceFieldIdentifier(AbstractMemberMetaData refMetaData, AbstractClassMetaData implMetaData, DatastoreIdentifier implIdentifier, boolean embedded, int fieldRole) {
        return super.newReferenceFieldIdentifier(refMetaData, implMetaData, implIdentifier, embedded, fieldRole);
    }

    @Override
    public DatastoreIdentifier newForeignKeyFieldIdentifier(AbstractMemberMetaData ownerFmd, AbstractMemberMetaData relatedFmd, DatastoreIdentifier destinationId, boolean embedded, int fieldRole) {
        return super.newForeignKeyFieldIdentifier(ownerFmd, relatedFmd, destinationId, embedded, fieldRole);
    }

    @Override
    public DatastoreIdentifier newDiscriminatorFieldIdentifier() {
        return super.newDiscriminatorFieldIdentifier();
    }

    @Override
    protected String getColumnIdentifierSuffix(int role, boolean embedded) {
        //return super.getColumnIdentifierSuffix(role, embedded);
        String suffix;

        switch (role)
        {
            case FieldRole.ROLE_NONE :
            default :
                suffix = !embedded ? "_ID" : "";
                break;

            case FieldRole.ROLE_CUSTOM :
                suffix = "";
                break;

            case FieldRole.ROLE_OWNER :
                //suffix = !embedded ? "_OID" : "_OWN";
                suffix = !embedded ? "" : "_OWN";
                break;

            case FieldRole.ROLE_FIELD :
            case FieldRole.ROLE_COLLECTION_ELEMENT :
            case FieldRole.ROLE_ARRAY_ELEMENT :
                suffix = !embedded ? "_EID" : "_ELE";
                break;

            case FieldRole.ROLE_MAP_KEY :
                suffix = !embedded ? "_KID" : "_KEY";
                break;

            case FieldRole.ROLE_MAP_VALUE :
                suffix = !embedded ? "_VID" : "_VAL";
                break;

            case FieldRole.ROLE_INDEX :
                suffix = !embedded ? "_XID" : "_IDX";
                break;
        }

        return suffix;
    }

}
/**
 * Identifier for a Column.
 */
class ColumnIdentifier extends DatastoreIdentifierImpl
{
    /**
     * Constructor for a column identifier
     * @param factory Identifier factory
     * @param sqlIdentifier the sql identifier
     */    
    public ColumnIdentifier(IdentifierFactory factory, String sqlIdentifier)
    {
        super(factory, sqlIdentifier);
    }
}