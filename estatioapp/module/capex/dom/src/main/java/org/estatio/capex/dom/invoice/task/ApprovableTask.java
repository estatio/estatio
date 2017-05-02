package org.estatio.capex.dom.invoice.task;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.InvokeOn;
import org.apache.isis.applib.annotation.Programmatic;

public interface ApprovableTask {

    // REVIEW: commented out, don't think that approval is responsibility of task...
    // @Mixin
    public static class _approve {

        private final ApprovableTask approvableTask;

        public _approve(final ApprovableTask approvableTask) {
            this.approvableTask = approvableTask;
        }

        @Action(invokeOn = InvokeOn.OBJECT_AND_COLLECTION)
        @ActionLayout(contributed = Contributed.AS_ACTION)
        public ApprovableTask $$() {
            doApprove();
            return approvableTask;
        }

        public boolean hide$$() {
            return false;
        }

        public String disable$$() {
            return null;
        }

        @Programmatic
        public void doApprove() {
            // Bulk guard
            if (!hide$$() && disable$$() == null) {
                // Do something
            }
        }
    }

    // REVIEW: commented out, don't think that approval is responsibility of task...
    // @Mixin
    public static class _decline {

        private final ApprovableTask approvableTask;

        public _decline(final ApprovableTask approvableTask) {
            this.approvableTask = approvableTask;
        }

        @Action(invokeOn = InvokeOn.OBJECT_AND_COLLECTION)
        @ActionLayout(contributed = Contributed.AS_ACTION)
        public ApprovableTask $$() {
            doApprove();
            return approvableTask;
        }

        public boolean hide$$() {
            return false;
        }

        public String disable$$() {
            return null;
        }

        @Programmatic
        public void doApprove() {
            // Bulk guard
            if (!hide$$() && disable$$() == null) {
                // Do something
            }
        }
    }


}
