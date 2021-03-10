package org.estatio.module.coda.dom.supplier;

import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = CodaAddress.class,
        objectType = "coda.CodaAddressRepository"
)
public class CodaAddressRepository {

    @Programmatic
    public List<CodaAddress> listAll() {
        return repositoryService.allInstances(CodaAddress.class);
    }

    @Programmatic
    public CodaAddress findBySupplierAndTag(
            final CodaSupplier codaSupplier,
            final short tag) {
        return repositoryService.uniqueMatch(
                new org.apache.isis.applib.query.QueryDefault<>(
                        CodaAddress.class,
                        "findBySupplierAndTag",
                        "supplier", codaSupplier,
                        "tag", tag));
    }

    @Programmatic
    public List<CodaAddress> findBySupplier(
            final CodaSupplier codaSupplier) {
        return repositoryService.allMatches(
                new org.apache.isis.applib.query.QueryDefault<>(
                        CodaAddress.class,
                        "findBySupplier",
                        "supplier", codaSupplier));
    }

    @Programmatic
    public CodaAddress create(
            final CodaSupplier codaSupplier, final short tag, final String name,
            final boolean defaultAddress,
            final String address1,
            final String address2,
            final String address3,
            final String address4,
            final String address5,
            final String address6,
            final String postCode,
            final String tel,
            final String fax,
            final String country,
            final String language,
            final String category,
            final String eMail
        ) {
        final CodaAddress address = new CodaAddress(codaSupplier, tag, name);
        address.setDefaultAddress(defaultAddress);
        address.setAddress1(address1);
        address.setAddress2(address2);
        address.setAddress3(address3);
        address.setAddress4(address4);
        address.setAddress5(address5);
        address.setAddress6(address6);
        address.setPostCode(postCode);
        address.setTel(tel);
        address.setFax(fax);
        address.setCountry(country);
        address.setLanguage(language);
        address.setCategory(category);
        address.setEMail(eMail);

        return repositoryService.persist(address);
    }

    @Programmatic
    public CodaSupplier delete(
            final CodaSupplier codaSupplier,
            final short tag) {
        final CodaAddress codaAddress = findBySupplierAndTag(codaSupplier, tag);
        if(codaAddress != null) {
            repositoryService.removeAndFlush(codaAddress);
        }
        return codaSupplier;
    }

    /**
     * Similar to {@link #upsert(CodaSupplier, short, String, boolean, String, String, String, String, String, String, String, String, String, String, String, String, String)}, but will NOT update any fields for a {@link CodaAddress} that already exists.
     */
    @Programmatic
    public CodaAddress findOrCreate(
            final CodaSupplier codaSupplier,
            final short tag,
            final String name,
            final boolean defaultAddress,
            final String address1,
            final String address2,
            final String address3,
            final String address4,
            final String address5,
            final String address6,
            final String postCode,
            final String tel,
            final String fax,
            final String country,
            final String language,
            final String category,
            final String eMail
    ) {
        CodaAddress address = findBySupplierAndTag(codaSupplier, tag);
        if (address == null) {
            address = create(codaSupplier, tag, name, defaultAddress, address1, address2, address3, address4, address5, address6, postCode, tel, fax, country, language, category, eMail);
        }
        return address;
    }

    /**
     * Same as {@link #findOrCreate(CodaSupplier, short, String, boolean, String, String, String, String, String, String, String, String, String, String, String, String, String)}, but will update any non-key fields
     * if the {@link CodaAddress} already exists.
     */
    @Programmatic
    public CodaAddress upsert(
            final CodaSupplier codaSupplier,
            final short tag,
            final String name,
            boolean defaultAddress,
            String address1,
            String address2,
            String address3,
            String address4,
            String address5,
            String address6,
            String postCode,
            String tel,
            String fax,
            String country,
            String language,
            String category,
            String eMail
    ) {
        CodaAddress address = findBySupplierAndTag(codaSupplier, tag);
        if (address == null) {
            address = create(codaSupplier, tag, name, defaultAddress, address1, address2, address3, address4, address5, address6, postCode, tel, fax, country, language, category, eMail);
        } else {
            address.setName(name);
            address.setDefaultAddress(defaultAddress);
            address.setAddress1(address1);
            address.setAddress2(address2);
            address.setAddress3(address3);
            address.setAddress4(address4);
            address.setAddress5(address5);
            address.setAddress6(address6);
            address.setPostCode(postCode);
            address.setTel(tel);
            address.setFax(fax);
            address.setCountry(country);
            address.setLanguage(language);
            address.setCategory(category);
            address.setEMail(eMail);
        }
        return address;
    }

    @javax.inject.Inject
    RepositoryService repositoryService;
}
