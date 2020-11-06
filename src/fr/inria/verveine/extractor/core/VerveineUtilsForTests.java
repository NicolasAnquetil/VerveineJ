package fr.inria.verveine.extractor.core;

import ch.akuhn.fame.Repository;
import org.moosetechnology.model.famix.famix.Entity;
import org.moosetechnology.model.famix.famix.NamedEntity;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

public class VerveineUtilsForTests {
    public VerveineUtilsForTests() {
    }

    public static <T extends Entity> Collection<T> selectElementsOfType(Repository var0, Class<T> var1) {
        return var0.all(var1);
    }

    public static <T extends NamedEntity> T detectFamixElement(Repository var0, Class<T> var1, String var2) {
        Iterator var3 = selectElementsOfType(var0, var1).iterator();

        NamedEntity var4;
        do {
            if (!var3.hasNext()) {
                return null;
            }

            var4 = (NamedEntity)var3.next();
        } while(!var4.getName().equals(var2));

        return (T) var4;
    }

    public static Collection<NamedEntity> listFamixElements(Repository var0, String var1) {
        return listFamixElements(var0, NamedEntity.class, var1);
    }

    public static <T extends NamedEntity> Collection<T> listFamixElements(Repository var0, Class<T> var1, String var2) {
        Vector var3 = new Vector();
        Iterator var4 = selectElementsOfType(var0, var1).iterator();

        while(var4.hasNext()) {
            NamedEntity var5 = (NamedEntity)var4.next();
            if (var5.getName().equals(var2)) {
                var3.add(var5);
            }
        }

        return var3;
    }
}
