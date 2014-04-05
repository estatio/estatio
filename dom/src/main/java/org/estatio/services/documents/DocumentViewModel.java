package org.estatio.services.documents;

import java.net.MalformedURLException;
import java.net.URL;

import com.danhaywood.isis.domainservice.stringinterpolator.StringInterpolatorService;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.commons.PropertyIds;

import org.apache.isis.applib.ViewModel;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Title;

public class DocumentViewModel implements ViewModel {


    //// //////////////////////////////////////
    
    @Override
    public String viewModelMemento() {
        return cmisId;
    }

    @Override
    public void viewModelInit(String memento) {
        this.cmisId = memento;
        init(getCmisObject());
    }


    //// //////////////////////////////////////
    
    @Programmatic
    public DocumentViewModel init(CmisObject obj) {
        this.cmisObject = obj;
        setName(obj.getName());
        final String propertyValue = obj.getPropertyValue(PropertyIds.VERSION_LABEL);
        setVersionLabel(propertyValue);
        return this;
    }

    //// //////////////////////////////////////
    
    private String cmisId;

    @Programmatic
    public String getCmisId() {
        return cmisId;
    }

    
    //// //////////////////////////////////////
    
    private CmisObject cmisObject;
    
    /**
     * Lazily loaded
     * 
     * @return
     */
    @Programmatic
    public CmisObject getCmisObject() {
        if (cmisObject == null) {
            cmisObject = cmisRepository.findById(cmisId);
        }
        return cmisObject;
    }

    
    // //////////////////////////////////////
    
    private String name;

    @Title
    @MemberOrder(sequence="2")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    
    //// //////////////////////////////////////

    private String versionLabel;

    @MemberOrder(sequence="2")
    public String getVersionLabel() {
        return versionLabel;
    }

    public void setVersionLabel(String versionLabel) {
        this.versionLabel = versionLabel;
    }

    
    //// //////////////////////////////////////
    
    public URL showMe() throws MalformedURLException {
        return new URL(
                stringInterpolatorService.interpolate(
                        this, "${properties['cmisServerDefaultRepoBaseUrl']}/${this.cmisId}/view_documents"));
    }

    // cmisServerDefaultRepoBaseUrl=http://cmis.demo.nuxeo.org/nuxeo/nxdoc/default
    
    //// //////////////////////////////////////
    
    @javax.inject.Inject
    private CmisRepository cmisRepository;

    @javax.inject.Inject
    private StringInterpolatorService stringInterpolatorService;
    
}