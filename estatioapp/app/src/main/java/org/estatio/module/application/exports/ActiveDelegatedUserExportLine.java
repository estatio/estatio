package org.estatio.module.application.exports;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Nature;

import javax.annotation.Nullable;
import java.util.List;


@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.module.application.exports.ActiveDelegatedUserExportLine"
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActiveDelegatedUserExportLine {

    @MemberOrder(sequence = "1")
    @Nullable
    private String username;

    @MemberOrder(sequence = "2")
    @Nullable
    private String status;

    @MemberOrder(sequence = "3")
    @Nullable
    private String atPath;

    @MemberOrder(sequence = "4")
    @Nullable
    private String familyName;

    @MemberOrder(sequence = "5")
    @Nullable
    private String givenName;

    @MemberOrder(sequence = "6")
    @Nullable
    private String personRef;

    @MemberOrder(sequence = "7")
    @Nullable
    private String partyRoles;
}
