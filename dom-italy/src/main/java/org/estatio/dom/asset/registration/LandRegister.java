package org.estatio.dom.asset.registration;

import java.math.BigDecimal;

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.InheritanceStrategy;

import com.google.common.base.Strings;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.util.TitleBuffer;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator(
        strategy = DiscriminatorStrategy.CLASS_NAME,
        column = "discriminator")
public class LandRegister extends FixedAssetRegistration {

    public String title() {
        TitleBuffer tb = new TitleBuffer()
                .append(getComuneAmministrativo())
                .append(",", getFoglio())
                .append(",", getParticella())
                .append(",", getRendita())
                .append(",", getSubalterno());
        final String title = tb.toString();
        if (Strings.isNullOrEmpty(title)) {
            // the @Title annotations of supertype
            return getContainer().titleOf(getType())
                    + ": "
                    + getContainer().titleOf(getSubject());
        }
        return title;
    }

    // //////////////////////////////////////

    private String comuneAmministrativo;

    @Optional
    @MemberOrder(sequence="10")
    public String getComuneAmministrativo() {
        return comuneAmministrativo;
    }

    public void setComuneAmministrativo(final String comuneAmministrativo) {
        this.comuneAmministrativo = comuneAmministrativo;
    }

    // //////////////////////////////////////

    private String comuneCatastale;

    @Optional
    @MemberOrder(sequence="11")
    public String getComuneCatastale() {
        return comuneCatastale;
    }

    public void setComuneCatastale(final String comuneCatastale) {
        this.comuneCatastale = comuneCatastale;
    }

    // //////////////////////////////////////

    private String codiceComuneCatastale;

    @Optional
    @MemberOrder(sequence="12")
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
    @MemberOrder(sequence="13")
    public BigDecimal getRendita() {
        return rendita;
    }

    public void setRendita(final BigDecimal rendita) {
        this.rendita = rendita;
    }

    // //////////////////////////////////////

    private String foglio;

    @Optional
    @MemberOrder(sequence="14")
    public String getFoglio() {
        return foglio;
    }

    public void setFoglio(final String foglio) {
        this.foglio = foglio;
    }

    // //////////////////////////////////////

    private String particella;

    @Optional
    @MemberOrder(sequence="15")
    public String getParticella() {
        return particella;
    }

    public void setParticella(final String particella) {
        this.particella = particella;
    }

    // //////////////////////////////////////

    private String subalterno;

    @Optional
    @MemberOrder(sequence="16")
    public String getSubalterno() {
        return subalterno;
    }

    public void setSubalterno(final String subalterno) {
        this.subalterno = subalterno;
    }

    // //////////////////////////////////////

    private String categoria;

    @Optional
    @MemberOrder(sequence="17")
    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(final String categoria) {
        this.categoria = categoria;
    }

    // //////////////////////////////////////

    private String classe;

    @Optional
    @MemberOrder(sequence="18")
    public String getClasse() {
        return classe;
    }

    public void setClasse(final String classe) {
        this.classe = classe;
    }

    // //////////////////////////////////////

    private String consistenza;

    @Optional
    @MemberOrder(sequence="19")
    public String getConsistenza() {
        return consistenza;
    }

    public void setConsistenza(final String consistenza) {
        this.consistenza = consistenza;
    }
}
