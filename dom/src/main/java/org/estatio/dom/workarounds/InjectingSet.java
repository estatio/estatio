package org.estatio.dom.workarounds;

import java.util.Iterator;
import java.util.SortedSet;

import com.google.common.base.Function;
import com.google.common.collect.ForwardingSortedSet;
import com.google.common.collect.Iterators;

import org.estatio.dom.lease.LeaseTerm;

public final class InjectingSet extends ForwardingSortedSet<LeaseTerm> {
    private final SortedSet<LeaseTerm> terms;
    private final IsisJdoSupport isisServiceInjector;

    public InjectingSet(SortedSet<LeaseTerm> terms, IsisJdoSupport isisServiceInjector) {
        this.terms = terms;
        this.isisServiceInjector = isisServiceInjector;
    }

    @Override
    protected SortedSet<LeaseTerm> delegate() {
        return terms;
    }

    @Override
    public Iterator<LeaseTerm> iterator() {
        return Iterators.transform(super.iterator(), new Function<LeaseTerm, LeaseTerm>(){
            public LeaseTerm apply(LeaseTerm leaseTerm) {
                return isisServiceInjector.injected(leaseTerm);                        
            }
        });
    }
}