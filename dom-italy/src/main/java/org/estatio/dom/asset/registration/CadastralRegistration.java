package org.estatio.dom.asset.registration;

import javax.jdo.annotations.InheritanceStrategy;

import com.google.common.base.Strings;

import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.util.TitleBuffer;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
//no @DataStoreIdentity nor @Version, since inherited from supertype
public class CadastralRegistration extends FixedAssetRegistration {

    public String title() {
        TitleBuffer tb = new TitleBuffer()
                .append(getComuneAmministrativo())
                .append(",", getFoglio())
                .append(",", getParticella())
                .append(",", getRendita())
                .append(",", getSubalterno());
        final String title = tb.toString();
        if(Strings.isNullOrEmpty(title)) {
            // the @Title annotations of supertype
            return  getContainer().titleOf(getType())
                    +": "
                    +getContainer().titleOf(getSubject());
        }
        return title;
    }

    // //////////////////////////////////////

    private String comuneAmministrativo;

    @Optional
    public String getComuneAmministrativo() {
        return comuneAmministrativo;
    }

    public void setComuneAmministrativo(final String comuneAmministrativo) {
        this.comuneAmministrativo = comuneAmministrativo;
    }

    // //////////////////////////////////////

    private String comuneCatastale;

    @Optional
    public String getComuneCatastale() {
        return comuneCatastale;
    }

    public void setComuneCatastale(final String comuneCatastale) {
        this.comuneCatastale = comuneCatastale;
    }

    // //////////////////////////////////////

    private String codiceComuneCatastale;

    @Optional
    public String getCodiceComuneCatastale() {
        return codiceComuneCatastale;
    }

    public void setCodiceComuneCatastale(final String codiceComuneCatastale) {
        this.codiceComuneCatastale = codiceComuneCatastale;
    }

    // //////////////////////////////////////

    private String rendita;

    @Optional
    public String getRendita() {
        return rendita;
    }

    public void setRendita(final String rendita) {
        this.rendita = rendita;
    }

    // //////////////////////////////////////

    private String foglio;

    @Optional
    public String getFoglio() {
        return foglio;
    }

    public void setFoglio(final String foglio) {
        this.foglio = foglio;
    }

    // //////////////////////////////////////

    private String particella;

    @Optional
    public String getParticella() {
        return particella;
    }

    public void setParticella(final String particella) {
        this.particella = particella;
    }

    // //////////////////////////////////////

    private String subalterno;

    @Optional
    public String getSubalterno() {
        return subalterno;
    }

    public void setSubalterno(final String subalterno) {
        this.subalterno = subalterno;
    }

    // //////////////////////////////////////

    private String categoria;

    @Optional
    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(final String categoria) {
        this.categoria = categoria;
    }

    // //////////////////////////////////////

    private String classe;

    @Optional
    public String getClasse() {
        return classe;
    }

    public void setClasse(final String classe) {
        this.classe = classe;
    }

    // //////////////////////////////////////

    private String consistenza;

    @Optional
    public String getConsistenza() {
        return consistenza;
    }

    public void setConsistenza(final String consistenza) {
        this.consistenza = consistenza;
    }
}
