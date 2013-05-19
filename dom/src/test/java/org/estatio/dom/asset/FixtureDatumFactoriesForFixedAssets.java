package org.estatio.dom.asset;

import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.FixedAssetRole;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.lease.UnitForLease;

import com.danhaywood.isis.wicket.gmap3.applib.Location;
import com.danhaywood.testsupport.coverage.PojoTester.FixtureDatumFactory;

public class FixtureDatumFactoriesForFixedAssets {

    public static FixtureDatumFactory<Property> properties() {
        Property prop1 = new Property();
        prop1.setName("Property 1");
        Property prop2 = new Property();
        prop2.setName("Property 2");
        return new FixtureDatumFactory<Property>(Property.class, prop1, prop2);
    }

    public static FixtureDatumFactory<FixedAssetRole> propertyActors() {
        FixedAssetRole pa1 = new FixedAssetRole();
        FixedAssetRole pa2 = new FixedAssetRole();
        return new FixtureDatumFactory<FixedAssetRole>(FixedAssetRole.class, pa1, pa2);
    }

    public static FixtureDatumFactory<Location> locations() {
        Location location =  new Location(0, 0);
        return new FixtureDatumFactory<Location>(Location.class, location);
    }

    public static FixtureDatumFactory<Unit> units() {
        Unit unit1 = new Unit();
        unit1.setName("Unit 1");
        Unit unit2 = new Unit();
        unit2.setName("Unit 2");
        return new FixtureDatumFactory<Unit>(Unit.class, unit1, unit2);
    }

}
