package org.estatio.services.documents;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.PropertyData;
import org.junit.Before;
import org.junit.Test;

public class DocumentContributionsTest_relevantDocuments {

    private DocumentContributions documentContributions;
    private DocumentViewModelFactory documentViewModelFactory;
    private CmisRepository cmisRepository;

    @Before
    public void setUp() throws Exception {
        cmisRepository = new CmisRepository();
        Map<String, String> properties = Maps.newLinkedHashMap();

        documentViewModelFactory = new DocumentViewModelFactory();
        documentContributions = new DocumentContributions();
        documentContributions.cmisRepository = cmisRepository;
        cmisRepository.init(properties);
    }
    
    @Test
    public void testRelevantDocuments() {
//        Property property = new Property();
//        property.setCity("Oxford");
        String queryFor = "SELECT * FROM cmis:document WHERE CONTAINS('Oxford')"; 
        ItemIterable<QueryResult> results = cmisRepository.query(queryFor);
        for (QueryResult qr : results) {
            System.out.println("--------------");
            System.out.println(qr.toString());
            List<PropertyData<?>> properties = qr.getProperties();
            for (PropertyData<?> propertyData : properties) {
                System.out.println("  " + propertyData.getId() + ": " + propertyData.getValues());
            }
            ;
            String cmisId = qr.getPropertyValueById(PropertyIds.OBJECT_ID);
            CmisObject obj = cmisRepository.findById(cmisId);
            //String name = obj.getName();
            Object name = obj.getPropertyValue(PropertyIds.NAME);
            System.out.println("  NAME = " + name);
        }
    }

}
