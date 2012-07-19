package com.danhaywood.testsupport.jmock;

public class Collaborating {
    final Collaborator collaborator;

    public Collaborating(final Collaborator collaborator) {
        this.collaborator = collaborator;
    }

    public void collaborateWithCollaborator() {
        collaborator.doOtherStuff();
    }
    
    public void dontCollaborateWithCollaborator() {
        
    }
    
}