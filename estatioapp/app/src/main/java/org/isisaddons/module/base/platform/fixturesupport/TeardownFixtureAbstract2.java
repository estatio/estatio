package org.isisaddons.module.base.platform.fixturesupport;

import javax.inject.Inject;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.metadata.DiscriminatorMetadata;
import javax.jdo.metadata.InheritanceMetadata;
import javax.jdo.metadata.TypeMetadata;

import com.google.common.base.Strings;

import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;

import org.incode.module.fixturesupport.dom.scripts.TeardownFixtureAbstract;

public abstract class TeardownFixtureAbstract2 extends TeardownFixtureAbstract {


    @Override
    protected void deleteFrom(Class cls) {
        final String column = discriminatorColumnOf(cls);
        final String value = discriminatorValueOf(cls);
        if(column == null || value == null) {
            superDeleteFrom(cls);
            return;
        }
        final String schema = schemaOf(cls);
        final String table = tableOf(cls);

        this.preDeleteFrom(cls);
        if (Strings.isNullOrEmpty(schema)) {
            this.deleteFromWhere(table, column, value);
        } else {
            this.deleteFromWhere(schema, table, column, value);
        }
        this.postDeleteFrom(cls);
    }

    protected String schemaOf(final Class cls) {
        TypeMetadata metadata = getPersistenceManagerFactory().getMetadata(cls.getName());
        if(metadata == null) {
            return null;
        }
        final InheritanceMetadata inheritanceMetadata = metadata.getInheritanceMetadata();
        if(inheritanceMetadata != null && inheritanceMetadata.getStrategy() == InheritanceStrategy.SUPERCLASS_TABLE) {
            return schemaOf(cls.getSuperclass());
        }
        return metadata.getSchema();
    }

    protected String tableOf(final Class cls) {
        final TypeMetadata metadata = getPersistenceManagerFactory().getMetadata(cls.getName());
        if(metadata == null) {
            return cls.getSimpleName();
        }
        final InheritanceMetadata inheritanceMetadata = metadata.getInheritanceMetadata();
        if(inheritanceMetadata != null && inheritanceMetadata.getStrategy() == InheritanceStrategy.SUPERCLASS_TABLE) {
            return tableOf(cls.getSuperclass());
        }
        final String table = metadata.getTable();
        return !Strings.isNullOrEmpty(table) ? table : cls.getSimpleName();
    }

    protected String discriminatorValueOf(final Class cls) {
        TypeMetadata metadata = getPersistenceManagerFactory().getMetadata(cls.getName());
        if(metadata == null) {
            return null;
        }
        final InheritanceMetadata inheritanceMetadata = metadata.getInheritanceMetadata();
        if(inheritanceMetadata == null ||
                inheritanceMetadata.getStrategy() != InheritanceStrategy.SUPERCLASS_TABLE) {
            return null;
        }
        final DiscriminatorMetadata discriminatorMetadata = inheritanceMetadata.getDiscriminatorMetadata();
        if(discriminatorMetadata == null ||
           discriminatorMetadata.getStrategy() != DiscriminatorStrategy.VALUE_MAP) {
            return null;
        }
        return discriminatorMetadata.getValue();
    }

    protected String discriminatorColumnOf(final Class cls) {
        final String discriminator = doDiscriminatorOf(cls);
        return discriminator != null ? discriminator : "discriminator";
    }

    private String doDiscriminatorOf(final Class cls) {
        final TypeMetadata metadata = getPersistenceManagerFactory().getMetadata(cls.getName());
        if(metadata == null) {
            return null;
        }
        final InheritanceMetadata inheritanceMetadata = metadata.getInheritanceMetadata();
        if(inheritanceMetadata == null ||
                inheritanceMetadata.getStrategy() != InheritanceStrategy.SUPERCLASS_TABLE) {
            return null;
        }
        final DiscriminatorMetadata discriminatorMetadata = inheritanceMetadata.getDiscriminatorMetadata();
        if(discriminatorMetadata == null ||
           discriminatorMetadata.getStrategy() != DiscriminatorStrategy.VALUE_MAP) {
            return null;
        }
        return discriminatorMetadata.getColumn();
    }

    private PersistenceManagerFactory getPersistenceManagerFactory() {
        return isisJdoSupport.getJdoPersistenceManager().getPersistenceManagerFactory();
    }

    private void superDeleteFrom(final Class cls) {
        super.deleteFrom(cls);
    }

    protected Integer deleteFromWhere(String schema, String table, String column, String value) {
        final String sql = String.format(
                "DELETE FROM \"%s\".\"%s\" WHERE \"%s\"='%s'",
                schema, table, column, value);
        return this.isisJdoSupport.executeUpdate(sql);
    }

    protected void deleteFromWhere(String table, String column, String value) {
        final String sql = String.format(
                "DELETE FROM \"%s\" WHERE \"%s\"='%s'",
                table, column, value);
        this.isisJdoSupport.executeUpdate(sql);
    }

    protected void preDeleteFrom(Class cls) {
    }

    protected void postDeleteFrom(Class cls) {
    }

    @Inject
    protected IsisJdoSupport isisJdoSupport;
}
