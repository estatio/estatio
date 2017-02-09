package org.estatio.dom.asset.registration;

import java.math.BigDecimal;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.InheritanceStrategy;

import com.google.common.base.Joiner;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Property;

import org.incode.module.base.dom.types.DescriptionType;
import org.incode.module.base.dom.types.NameType;

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
            final @Named("Comune amministrativo") @Optional String comuneAmministrativo,
            final @Named("Comune catastale") @Optional String comuneCatastale,
            final @Named("Codice comuneCatastale") @Optional String codiceComuneCatastale,
            final @Named("Rendita") @Optional BigDecimal rendita,
            final @Named("Foglio") @Optional String foglio,
            final @Named("Particella") @Optional String particella,
            final @Named("Subalterno") @Optional String subalterno,
            final @Named("Categoria") @Optional String categoria,
            final @Named("Classe") @Optional String classe,
            final @Named("Consistenza") @Optional String consistenza,
            final @Named("Change start date") @Optional LocalDate changeStartDate,
            final @Named("Change description") @Optional String changeDescription) {
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
