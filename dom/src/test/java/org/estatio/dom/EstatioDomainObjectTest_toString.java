package org.estatio.dom;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import org.apache.isis.applib.annotation.MemberOrder;

public class EstatioDomainObjectTest_toString {

    public static class WithCodeGetterImpl implements WithCodeGetter {
        
        private String code;
        @Override
        public String getCode() {
            return code;
        }
        public void setCode(String code) {
            this.code = code;
        }
    }

    public static class WithDescriptionGetterImpl implements WithDescriptionGetter {
        
        private String description;
        @Override
        public String getDescription() {
            return description;
        }
        public void setDescription(String description) {
            this.description = description;
        }
    }
    
    public static class WithNameGetterImpl implements WithNameGetter {
        
        private String name;
        @Override
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
    }
    
    public static class WithReferenceUniqueImpl implements WithReferenceGetter {
        
        private String reference;
        @Override
        public String getReference() {
            return reference;
        }
        public void setReference(String reference) {
            this.reference = reference;
        }
    }
    
    public static class WithTitleGetterImpl implements WithTitleGetter {
        
        private String title;
        @Override
        public String getTitle() {
            return title;
        }
        public void setTitle(String title) {
            this.title = title;
        }
    }
    
    public static class SomeDomainObject extends EstatioDomainObject<SomeDomainObject>  {
        public SomeDomainObject() {
            super("withCode,withReference,withName,withDescription,withTitle");
        }

        // //////////////////////////////////////

        private WithCodeGetter withCode;

        @MemberOrder(sequence = "1")
        public WithCodeGetter getWithCode() {
            return withCode;
        }

        public void setWithCode(final WithCodeGetter withCode) {
            this.withCode = withCode;
        }

        // //////////////////////////////////////
        
        private WithDescriptionGetter withDescription;
        
        @MemberOrder(sequence = "1")
        public WithDescriptionGetter getWithDescription() {
            return withDescription;
        }
        
        public void setWithDescription(final WithDescriptionGetter withDescription) {
            this.withDescription = withDescription;
        }
        
        // //////////////////////////////////////
        
        private WithNameGetter withName;
        
        @MemberOrder(sequence = "1")
        public WithNameGetter getWithName() {
            return withName;
        }
        
        public void setWithName(final WithNameGetter withName) {
            this.withName = withName;
        }

        // //////////////////////////////////////
        
        private WithReferenceGetter withReference;
        
        @MemberOrder(sequence = "1")
        public WithReferenceGetter getWithReference() {
            return withReference;
        }
        
        public void setWithReference(final WithReferenceGetter withReference) {
            this.withReference = withReference;
        }
        
        // //////////////////////////////////////
        
        private WithTitleGetter withTitle;
        
        @MemberOrder(sequence = "1")
        public WithTitleGetter getWithTitle() {
            return withTitle;
        }
        
        public void setWithTitle(final WithTitleGetter withTitle) {
            this.withTitle = withTitle;
        }
    }

    
    @Test
    public void test() {
        final WithCodeGetterImpl wcgi = new WithCodeGetterImpl();
        wcgi.setCode("A");
        final WithDescriptionGetterImpl wdgi = new WithDescriptionGetterImpl();
        wdgi.setDescription("B");
        final WithNameGetterImpl wngi = new WithNameGetterImpl();
        wngi.setName("C");
        final WithReferenceUniqueImpl wrgi = new WithReferenceUniqueImpl();
        wrgi.setReference("D");
        final WithTitleGetterImpl wtgi = new WithTitleGetterImpl();
        wtgi.setTitle("E");
        
        final SomeDomainObject someDomainObject = new SomeDomainObject();
        someDomainObject.setWithCode(wcgi);
        someDomainObject.setWithDescription(wdgi);
        someDomainObject.setWithName(wngi);
        someDomainObject.setWithReference(wrgi);
        someDomainObject.setWithTitle(wtgi);
        
        assertThat(someDomainObject.toString(), is("SomeDomainObject{withCode=A, withReference=D, withName=C, withDescription=B, withTitle=E}"));
    }

}
