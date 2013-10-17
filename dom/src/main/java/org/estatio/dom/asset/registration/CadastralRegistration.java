package org.estatio.dom.asset.registration;

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.util.TitleBuffer;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME, column = "discriminator")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
public class CadastralRegistration extends FixedAssetRegistration {

    public String title() {
        TitleBuffer tb = new TitleBuffer()
                .append(getComuneAmministrativo())
                .append(",", getFoglio())
                .append(",", getParticella())
                .append(",", getRendita())
                .append(",", getSubalterno());
        return tb.toString();
    }

    private String comuneAmministrativo;

    @Optional
    public String getComuneAmministrativo() {
        return comuneAmministrativo;
    }

    public void setComuneAmministrativo(String comuneAmministrativo) {
        this.comuneAmministrativo = comuneAmministrativo;
    }

    // //////////////////////////////////////

    private String comuneCatastale;

    @Optional
    public String getComuneCatastale() {
        return comuneCatastale;
    }

    public void setComuneCatastale(String comuneCatastale) {
        this.comuneCatastale = comuneCatastale;
    }

    // //////////////////////////////////////

    private String codiceComuneCatastale;

    @Optional
    public String getCodiceComuneCatastale() {
        return codiceComuneCatastale;
    }

    public void setCodiceComuneCatastale(String codiceComuneCatastale) {
        this.codiceComuneCatastale = codiceComuneCatastale;
    }

    // //////////////////////////////////////

    private String rendita;

    @Optional
    public String getRendita() {
        return rendita;
    }

    public void setRendita(String rendita) {
        this.rendita = rendita;
    }

    // //////////////////////////////////////

    private String foglio;

    @Optional
    public String getFoglio() {
        return foglio;
    }

    public void setFoglio(String foglio) {
        this.foglio = foglio;
    }

    // //////////////////////////////////////

    private String particella;

    @Optional
    public String getParticella() {
        return particella;
    }

    public void setParticella(String particella) {
        this.particella = particella;
    }

    // //////////////////////////////////////

    private String subalterno;

    @Optional
    public String getSubalterno() {
        return subalterno;
    }

    public void setSubalterno(String subalterno) {
        this.subalterno = subalterno;
    }

    // //////////////////////////////////////

    private String categoria;

    @Optional
    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    // //////////////////////////////////////

    private String classe;

    @Optional
    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    // //////////////////////////////////////

    private String consistenza;

    @Optional
    public String getConsistenza() {
        return consistenza;
    }

    public void setConsistenza(String consistenza) {
        this.consistenza = consistenza;
    }
}
