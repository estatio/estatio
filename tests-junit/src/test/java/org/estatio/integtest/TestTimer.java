package org.estatio.integtest;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class TestTimer extends TestWatcher {

    private long t0, t1;
    private String prefix;
    
    public TestTimer(String prefix) {
        this.prefix = prefix;
    }

    @Override
    protected void starting(
                            Description description) {
        t0 = System.currentTimeMillis();
    }
    
    @Override
    protected void finished(
                            Description description) {
        t1 = System.currentTimeMillis();
        
        System.out.println(prefix + ": " + description.getDisplayName() + "took: " + (t1-t0) + "millis");
    }
}
