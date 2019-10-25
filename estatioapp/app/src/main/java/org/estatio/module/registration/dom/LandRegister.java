package org.estatio.module.registration.dom;

import java.math.BigDecimal;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.InheritanceStrategy;

import com.google.common.base.Joiner;

import org.apache.isis.applib.annotation.*;
import org.joda.time.LocalDate;

import org.incode.module.base.dom.types.DescriptionType;
import org.incode.module.base.dom.types.NameType;

import org.estatio.module.asset.dom.registration.FixedAssetRegistration;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        schema = "dbo" // Isis' ObjectSpecId inferred from @Discriminator
)
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator("org.estatio.dom.asset.registration.LandRegister")
public class LandRegister extends FixedAssetRegistration {

    public String title() {
        return getName();
    }

    @Override
    public String getName() {
        String title = Joiner.on("-").skipNulls().join(
                getComuneAmministrativo(),
                Joiner.on(".").skipNulls().join(
                        getFoglio(),
                        getParticella(),
                        getSubalterno()));
        if (title == ""){
            return getContainer().titleOf(getType()).concat(": ").concat(getContainer().titleOf(getSubject()));
        }
        return title;
    }

    // //////////////////////////////////////

    @Property(optionality = Optionality.OPTIONAL)
    @Column(length= NameType.Meta.MAX_LEN)
    @MemberOrder(sequence = "10")
    @Getter @Setter
    private String comuneAmministrativo;

    // //////////////////////////////////////

    @Property(optionality = Optionality.OPTIONAL)
    @Column(length=NameType.Meta.MAX_LEN)
    @MemberOrder(sequence = "11")
    @Getter @Setter
    private String comuneCatastale;

    // //////////////////////////////////////

    @Property(optionality = Optionality.OPTIONAL)
    @Column(length=NameType.Meta.MAX_LEN)
    @MemberOrder(sequence = "12")
    @Getter @Setter
    private String codiceComuneCatastale;

    // //////////////////////////////////////

    @Property(optionality = Optionality.OPTIONAL)
    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    @MemberOrder(sequence = "13")
    @Getter @Setter
    private BigDecimal rendita;

    // //////////////////////////////////////

    @Property
    @MemberOrder(sequence = "14")
    @Column(length=NameType.Meta.MAX_LEN)
    @Getter @Setter
    private String foglio;

    // //////////////////////////////////////

    @Property(optionality = Optionality.OPTIONAL)
    @MemberOrder(sequence = "15")
    @Column(length=NameType.Meta.MAX_LEN)
    @Getter @Setter
    private String particella;

    // //////////////////////////////////////

    @Property(optionality = Optionality.OPTIONAL)
    @MemberOrder(sequence = "16")
    @Column(length=NameType.Meta.MAX_LEN)
    @Getter @Setter
    private String subalterno;

    // //////////////////////////////////////

    @Property(optionality = Optionality.OPTIONAL)
    @MemberOrder(sequence = "17")
    @Column(length=NameType.Meta.MAX_LEN)
    @Getter @Setter
    private String categoria;

    // //////////////////////////////////////

    @Property(optionality = Optionality.OPTIONAL)
    @Column(length=NameType.Meta.MAX_LEN)
    @MemberOrder(sequence = "18")
    @Getter @Setter
    private String classe;

    // //////////////////////////////////////

    @Property(optionality = Optionality.OPTIONAL)
    @Column(length=NameType.Meta.MAX_LEN)
    @MemberOrder(sequence = "19")
    @Getter @Setter
    private String consistenza;

    // //////////////////////////////////////

    @Property(optionality = Optionality.OPTIONAL)
    @Column(length= DescriptionType.Meta.MAX_LEN)
    @MemberOrder(sequence = "20")
    @Getter @Setter
    private String description;

    // //////////////////////////////////////

    public LandRegister changeRegistration(
            final @ParameterLayout(named = "Comune amministrativo") @Parameter(optionality = Optionality.OPTIONAL) String comuneAmministrativo,
            final @ParameterLayout(named = "Comune catastale") @Parameter(optionality = Optionality.OPTIONAL) String comuneCatastale,
            final @ParameterLayout(named = "Codice comuneCatastale") @Parameter(optionality = Optionality.OPTIONAL) String codiceComuneCatastale,
            final @ParameterLayout(named = "Rendita") @Parameter(optionality = Optionality.OPTIONAL) BigDecimal rendita,
            final @ParameterLayout(named = "Foglio") @Parameter(optionality = Optionality.OPTIONAL) String foglio,
            final @ParameterLayout(named = "Particella") @Parameter(optionality = Optionality.OPTIONAL) String particella,
            final @ParameterLayout(named = "Subalterno") @Parameter(optionality = Optionality.OPTIONAL) String subalterno,
            final @ParameterLayout(named = "Categoria") @Parameter(optionality = Optionality.OPTIONAL) String categoria,
            final @ParameterLayout(named = "Classe") @Parameter(optionality = Optionality.OPTIONAL) String classe,
            final @ParameterLayout(named = "Consistenza") @Parameter(optionality = Optionality.OPTIONAL) String consistenza,
            final @ParameterLayout(named = "Change start date") @Parameter(optionality = Optionality.OPTIONAL) LocalDate changeStartDate,
            final @ParameterLayout(named = "Change description") @Parameter(optionality = Optionality.OPTIONAL) String changeDescription) {
        if (changeStartDate != null) {
            LandRegister landRegister = landRegisters.newRegistration(
                    getSubject(),
                    this,
                    comuneAmministrativo,
                    comuneCatastale,
                    codiceComuneCatastale,
                    rendita,
                    foglio,
                    particella,
                    subalterno,
                    categoria,
                    classe,
                    consistenza,
                    changeStartDate,
                    changeDescription);
            landRegister.changeDates(changeStartDate, null);
            return landRegister;

        } else {
            setComuneAmministrativo(comuneAmministrativo);
            setComuneCatastale(comuneCatastale);
            setCodiceComuneCatastale(codiceComuneCatastale);
            setRendita(rendita);
            setFoglio(foglio);
            setParticella(particella);
            setSubalterno(subalterno);
            setCategoria(categoria);
            setClasse(classe);
            setConsistenza(consistenza);
            setDescription(changeDescription);
            return this;
        }
    }

    public String default0ChangeRegistration() {
        return getComuneAmministrativo();
    }

    public String default1ChangeRegistration() {
        return getComuneCatastale();
    }

    public String default2ChangeRegistration() {
        return getCodiceComuneCatastale();
    }

    public BigDecimal default3ChangeRegistration() {
        return getRendita();
    }

    public String default4ChangeRegistration() {
        return getFoglio();
    }

    public String default5ChangeRegistration() {
        return getParticella();
    }

    public String default6ChangeRegistration() {
        return getSubalterno();
    }

    public String default7ChangeRegistration() {
        return getCategoria();
    }

    public String default8ChangeRegistration() {
        return getClasse();
    }

    public String default9ChangeRegistration() {
        return getConsistenza();
    }

    // //////////////////////////////////////

    @Inject
    LandRegisters landRegisters;

}
