package fr.inria.verveine.extractor.java.utils;

import java.util.Collection;
import java.util.Vector;

import ch.akuhn.fame.Repository;
import eu.synectique.verveine.core.gen.famix.Entity;
import eu.synectique.verveine.core.gen.famix.NamedEntity;

/**
 * Some utilities to help test extractors derived from verveine.core
 * @author anquetil
 */
public class VerveineUtilsForTests {

	/**
	 * Returns a Collection of all FAMIXEntities in repository of the given fmxClass.
	 * Same method as listAll(Class<T extends Entity>) defined in VerveineParser
	 */
	public static <T extends Entity> Collection<T> selectElementsOfType(Repository repository, Class<T> fmxClass) {
		return( repository.all(fmxClass));
	}

	/** Returns the first FAMIXEntities in repository of the given fmxClass and with the given name
	 */
	public static <T extends NamedEntity> T detectFamixElement(Repository repository, Class<T> fmxClass, String name) {
		for (T ent : selectElementsOfType(repository, fmxClass)) {
			if (ent.getName().equals(name) ) {
				return ent;
			}
		}
		return null;
	}

	/**
	 * Returns a Collection of FAMIXEntities in repository with the given name
	 */
	public static Collection<NamedEntity> listFamixElements(Repository repository, String name) {
		return listFamixElements(repository, NamedEntity.class, name);
	}

	/**
	 * Returns a Collection of FAMIXEntities in repository of the given fmxClass and with the given name
	 */
	public static <T extends NamedEntity> Collection<T> listFamixElements(Repository repository, Class<T> fmxClass, String name) {
		Collection<T> selection = new Vector<T>();
		for (T ent : selectElementsOfType(repository, fmxClass)) {
			if ( ent.getName().equals(name) ) {
				selection.add(ent);
			}
		}
		return selection;
	}

}
