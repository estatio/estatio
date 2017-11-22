/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.module.application.spiimpl.exceprecog;

import java.util.Set;
import java.util.SortedMap;
import com.google.common.collect.Maps;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.exceprecog.ExceptionRecognizerComposite;
import org.incode.module.base.dom.utils.StringUtils;

@DomainService(menuOrder = "99")
public class ExceptionRecognizersForEstatio extends ExceptionRecognizerComposite {
    
    private final SortedMap<String,String> constraintNames = Maps.newTreeMap();
    
    public ExceptionRecognizersForEstatio() {
        
        recognizeCodeConstraint("CHARGE");
        
        recognizeDescriptionConstraint("CHARGE");
        recognizeDescriptionConstraint("CURRENCY");
        
        recognizeNameConstraint("GEOGRAPHY");
        recognizeNameConstraint("INDEX");
        recognizeNameConstraint("TAX");
        
        recognizeReferenceConstraint("ACCOUNT");
        recognizeReferenceConstraint("AGREEMENT");
        recognizeReferenceConstraint("CHARGE");
        recognizeReferenceConstraint("CHARGE_GROUP");
        recognizeReferenceConstraint("COMMUNICATION_CHANNEL");
        recognizeReferenceConstraint("CURRENCY");
        recognizeReferenceConstraint("FIXEDASSET");
        recognizeReferenceConstraint("GEOGRAPHY");
        recognizeReferenceConstraint("INDEX");
        recognizeReferenceConstraint("INVOICE");
        recognizeReferenceConstraint("PARTY");
        recognizeReferenceConstraint("TAX");
        
        recognizeTitleConstraint("AGREEMENT_TYPE");
    }

    private void recognizeCodeConstraint(final String string) {
        add(string + "_CODE_UNIQUE_IDX", 
                sanitize(string) + " code already in use");
    }
    
    private void recognizeDescriptionConstraint(final String string) {
        add(string + "_DESCRIPTION_UNIQUE_IDX", 
                sanitize(string) + " description already in use");
    }
    
    private void recognizeNameConstraint(final String string) {
        add(string + "_NAME_UNIQUE_IDX", 
                sanitize(string) + " name already in use");
    }
    
    private void recognizeReferenceConstraint(final String string) {
        add(string + "_REFERENCE_UNIQUE_IDX", 
                sanitize(string) + " reference already in use");
    }
    
    private void recognizeTitleConstraint(final String string) {
        add(string + "_TITLE_UNIQUE_IDX", 
                sanitize(string) + " title already in use");
    }
    
    protected String sanitize(final String string) {
        return StringUtils.capitalize(string.toLowerCase().replaceAll("_", " "));
    }

    private void add(final String constraintName, final String replacement) {
        add(new ExceptionRecognizerSubstituting(constraintName, replacement));
        constraintNames.put(constraintName, replacement);
    }
    
    @Programmatic
    public Set<String> getRecognizedConstraintNames() {
        return constraintNames.keySet();
    }
    
}