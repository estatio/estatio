package org.estatio.dom.tag;

import java.util.List;

import org.estatio.dom.ComparableContractTest_compareTo;


public class TagTest_compareTo extends ComparableContractTest_compareTo<Tag> {

    @SuppressWarnings("unchecked")
    @Override
    protected List<List<Tag>> orderedTuples() {
        return listOf(
                listOf(
                        newLeaseItem(null, null),
                        newLeaseItem("com.mycompany.Boo", null),
                        newLeaseItem("com.mycompany.Boo", null),
                        newLeaseItem("com.mycompany.Foo", null)
                        ),
                listOf(
                        newLeaseItem("com.mycompany.Boo", null),
                        newLeaseItem("com.mycompany.Boo", "Abc"),
                        newLeaseItem("com.mycompany.Boo", "Abc"),
                        newLeaseItem("com.mycompany.Foo", "Def")
                        )
                );
    }

    private Tag newLeaseItem(
            String appliesToClassName, 
            String name) {
        final Tag tag = new Tag();
        tag.setObjectType(appliesToClassName);
        tag.setName(name);
        return tag;
    }

}
