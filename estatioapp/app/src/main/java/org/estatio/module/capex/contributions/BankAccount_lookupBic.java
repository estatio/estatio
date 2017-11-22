package org.estatio.module.capex.contributions;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.base.dom.Titled;

import org.estatio.module.financial.dom.BankAccount;

/**
 * REVIEW: this could be inlined as a mixin (but domain layer -> app layer?)
 */
@Mixin(method="act")
public class BankAccount_lookupBic {

    public static enum WebSite implements Titled {
        IBANCALCULATOR("www.ibancalculator.com") {
            @Override
            URL buildUrl(String iban) throws MalformedURLException {
                return new URL("https://www.ibancalculator.com/validate/" + iban);
            }
        },
        IBAN("www.iban.com") {
            @Override
            URL buildUrl(String iban) throws MalformedURLException {
                return new URL("https://www.iban.com");
            }
        };

        private final String name;
        WebSite(final String name) {
            this.name = name;
        }

        abstract URL buildUrl(String iban) throws MalformedURLException;

        @Override
        public String title() {
            return name;
        }
    }
    private final BankAccount bankAccount;

    public BankAccount_lookupBic(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    @Action(
            semantics = SemanticsOf.SAFE
    )
    public URL act(
            final WebSite webSite,
            final String iban) throws MalformedURLException {
        return webSite.buildUrl(iban);
    }

    public WebSite default0Act() { return WebSite.IBANCALCULATOR; }
    public String default1Act() { return bankAccount.getIban(); }

}
