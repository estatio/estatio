package org.estatio.module.lease.fixtures.docfrag.builders;

import java.util.Objects;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.incode.module.docfragment.dom.impl.DocFragment;
import org.incode.module.docfragment.dom.impl.DocFragmentRepository;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"objectType", "name", "atPath"}, callSuper = false)
@ToString(of={"objectType", "name", "atPath"})
@Accessors(chain = true)
public class DocFragmentBuilder extends BuilderScriptAbstract<DocFragment, DocFragmentBuilder> {

    @Getter @Setter
    String objectType;
    @Getter @Setter
    String name;
    @Getter @Setter
    String atPath;

    @Getter @Setter
    String templateText;

    @Getter
    DocFragment object;

    @Override
    public void execute(final ExecutionContext executionContext) {

        final DocFragment docFrag = docFragmentRepository
                .findByObjectTypeAndNameAndApplicableToAtPath(objectType, name, atPath);
        if(docFrag != null && Objects.equals(docFrag.getAtPath(), atPath)) {
            return;
        }

        docFragmentRepository.create(objectType, name, atPath, getTemplateText());
    }

    @Inject
    DocFragmentRepository docFragmentRepository;
}
