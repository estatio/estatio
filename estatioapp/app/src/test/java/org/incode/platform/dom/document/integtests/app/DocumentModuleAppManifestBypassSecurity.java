package org.incode.platform.dom.document.integtests.app;

/**
 * Bypasses security, meaning any user/password combination can be used to login.
 */
public class DocumentModuleAppManifestBypassSecurity extends DocumentModuleAppManifest {

    @Override
    protected String overrideAuthMechanism() {
        return "bypass";
    }

}
