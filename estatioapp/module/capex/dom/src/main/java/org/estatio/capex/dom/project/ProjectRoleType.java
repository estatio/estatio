package org.estatio.capex.dom.project;

import org.incode.module.base.dom.TitledEnum;
import org.incode.module.base.dom.utils.StringUtils;

public enum ProjectRoleType implements TitledEnum {

    PROJECT_MANAGER;

    public String title() {
        return StringUtils.enumTitle(this.toString());
    }

    public static class Meta {
        private Meta(){}

        public final static int MAX_LEN = 30;
    }
}
