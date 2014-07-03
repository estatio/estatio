package org.estatio.dom.asset.registration;

import java.math.BigDecimal;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.InheritanceStrategy;

import com.google.common.base.Joiner;

import org.estatio.dom.JdoColumnLength;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator(
        strategy = DiscriminatorStrategy.CLASS_NAME,
        column = "discriminator")
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

    private String comuneAmministrativo;

    @Optional
    @Column(length=JdoColumnLength.NAME)
    @MemberOrder(sequence = "10")
    public String getComuneAmministrativo() {
        return comuneAmministrativo;
    }

    public void setComuneAmministrativo(final String comuneAmministrativo) {
        this.comuneAmministrativo = comuneAmministrativo;
    }

    // //////////////////////////////////////

    private String comuneCatastale;

    @Optional
    @Column(length=JdoColumnLength.NAME)
    @MemberOrder(sequence = "11")
    public String getComuneCatastale() {
        return comuneCatastale;
    }

    public void setComuneCatastale(final String comuneCatastale) {
        this.comuneCatastale = comuneCatastale;
    }

    // //////////////////////////////////////

    private String codiceComuneCatastale;

    @Optional
    @Column(length=JdoColumnLength.NAME)
    @MemberOrder(sequence = "12")
    public String getCodiceComuneCatastale() {
        return codiceComuneCatastale;
    }

    public void setCodiceComuneCatastale(final String codiceComuneCatastale) {
        this.codiceComuneCatastale = codiceComuneCatastale;
    }

    // //////////////////////////////////////

    private BigDecimal rendita;

    @Optional
    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    @MemberOrder(sequence = "13")
    public BigDecimal getRendita() {
        return rendita;
    }

    public void setRendita(final BigDecimal rendita) {
        this.rendita = rendita;
    }

    // //////////////////////////////////////

    private String foglio;

    @Optional
    @MemberOrder(sequence = "14")
    @Column(length=JdoColumnLength.NAME)
    public String getFoglio() {
        return foglio;
    }

    public void setFoglio(final String foglio) {
        this.foglio = foglio;
    }

    // //////////////////////////////////////

    private String particella;

    @Optional
    @MemberOrder(sequence = "15")
    @Column(length=JdoColumnLength.NAME)
    public String getParticella() {
        return particella;
    }

    public void setParticella(final String particella) {
        this.particella = particella;
    }

    // //////////////////////////////////////

    private String subalterno;

    @Optional
    @MemberOrder(sequence = "16")
    @Column(length=JdoColumnLength.NAME)
    public String getSubalterno() {
        return subalterno;
    }

    public void setSubalterno(final String subalterno) {
        this.subalterno = subalterno;
    }

    // //////////////////////////////////////

    private String categoria;

    @Optional
    @MemberOrder(sequence = "17")
    @Column(length=JdoColumnLength.NAME)
    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(final String categoria) {
        this.categoria = categoria;
    }

    // //////////////////////////////////////
    
    private String classe;

    @Optional
    @Column(length=JdoColumnLength.NAME)
    @MemberOrder(sequence = "18")
    public String getClasse() {
        return classe;
    }

    public void setClasse(final String classe) {
        this.classe = classe;
    }

    // //////////////////////////////////////

    private String consistenza;

    @Optional
    @Column(length=JdoColumnLength.NAME)
    @MemberOrder(sequence = "19")
    public String getConsistenza() {
        return consistenza;
    }

    public void setConsistenza(final String consistenza) {
        this.consistenza = consistenza;
    }
    
    // //////////////////////////////////////
    
    private String description;
    
    @Optional
    @Column(length=JdoColumnLength.DESCRIPTION)
    @MemberOrder(sequence = "20")
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

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
