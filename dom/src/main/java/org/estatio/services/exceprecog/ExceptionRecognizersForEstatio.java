package org.estatio.services.exceprecog;

import java.util.Set;
import java.util.SortedMap;

import com.google.common.collect.Maps;

import org.apache.isis.applib.services.exceprecog.ExceptionRecognizerComposite;

import org.estatio.dom.utils.StringUtils;

public class ExceptionRecognizersForEstatio extends ExceptionRecognizerComposite {
    
    private final SortedMap<String,String> constraintNames = Maps.newTreeMap();
    
    public ExceptionRecognizersForEstatio() {
        
        recognizeCodeConstraint("CHARGE");
        
        recognizeDescriptionConstraint("CHARGE");
        recognizeDescriptionConstraint("CURRENCY");
        
        recognizeNameConstraint("GEOGRAPHY");
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

    private void recognizeCodeConstraint(String string) {
        add(string + "_CODE_UNIQUE_IDX", 
                sanitize(string) + " code already in use");
    }
    
    private void recognizeDescriptionConstraint(String string) {
        add(string + "_DESCRIPTION_UNIQUE_IDX", 
                sanitize(string) + " description already in use");
    }
    
    private void recognizeNameConstraint(String string) {
        add(string + "_NAME_UNIQUE_IDX", 
                sanitize(string) + " name already in use");
    }
    
    private void recognizeReferenceConstraint(String string) {
        add(string + "_REFERENCE_UNIQUE_IDX", 
                sanitize(string) + " reference already in use");
    }
    
    private void recognizeTitleConstraint(String string) {
        add(string + "_TITLE_UNIQUE_IDX", 
                sanitize(string) + " title already in use");
    }
    
    protected String sanitize(String string) {
        return StringUtils.capitalize(string.toLowerCase().replaceAll("_", " "));
    }

    private void add(String constraintName, String replacement) {
        add(new ExceptionRecognizerSubstituting(constraintName, replacement));
        constraintNames.put(constraintName, replacement);
    }
    
    public Set<String> getRecognizedConstraintNames() {
        return constraintNames.keySet();
    }
    
}