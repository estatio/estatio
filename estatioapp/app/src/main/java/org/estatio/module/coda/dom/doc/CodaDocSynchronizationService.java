package org.estatio.module.coda.dom.doc;

import java.util.concurrent.locks.ReentrantLock;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

/**
 * Introduced to synchronise foreground and background commands.
 */
@DomainService(nature = NatureOfService.DOMAIN)
public class CodaDocSynchronizationService {


    private final ReentrantLock reentrantLock = new ReentrantLock();

    @Programmatic
    public void lock() {
        reentrantLock.lock();
    }

    @Programmatic
    public void unlock() {
        reentrantLock.unlock();
    }
}
