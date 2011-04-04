package tests.fr.inria.verveine.extractor.java;

import static org.junit.Assert.*;

import org.junit.Test;

import test.fr.inria.verveine.core.TestVerveineUtils;
import ch.akuhn.fame.Repository;
import fr.inria.verveine.core.gen.famix.Association;
import fr.inria.verveine.core.gen.famix.BehaviouralEntity;
import fr.inria.verveine.core.gen.famix.PrimitiveType;
import fr.inria.verveine.core.gen.famix.StructuralEntity;
import fr.inria.verveine.core.gen.famix.Type;
import fr.inria.verveine.extractor.java.VerveineJParser;

public abstract class VerveineJTest_Basic {

	protected Repository repo;
	protected VerveineJParser parser;

	@Test
	public void testAssociation() {
		for (Association ass : TestVerveineUtils.selectElementsOfType(repo, Association.class) ) {
			assertNotNull(ass.getClass().getSimpleName()+(ass.getTo()==null?"":" to: "+ass.getTo().getName())+" as no From", ass.getFrom());
			assertNotNull(ass.getClass().getSimpleName()+" from: "+ass.getFrom().getName()+" as no To", ass.getTo());
		}
		
		for (Association ass : TestVerveineUtils.selectElementsOfType(repo, Association.class) ) {
			Association n = ass.getNext();
			if (n!=null) {
				assertSame(ass, n.getPrevious());
			}
		}
		
		for (Association ass : TestVerveineUtils.selectElementsOfType(repo, Association.class) ) {
			Association p = ass.getPrevious();
			if (p!=null) {
				assertSame(ass, p.getNext());
			}
		}
	}

	@Test
	public void testBelongsTo() {
		for ( Type e : repo.all(Type.class) ) {
			if (! (e instanceof PrimitiveType) ) {
				assertNotNull("a Type '"+e.getName()+"' does not belong to anything", e.getBelongsTo());
			}
		}
		for ( BehaviouralEntity e : repo.all(BehaviouralEntity.class) ) {
			assertNotNull("a BehaviouralEntity '"+e.getName()+"' does not belong to anything", e.getBelongsTo());
		}
		for ( StructuralEntity e : repo.all(StructuralEntity.class) ) {
			assertNotNull("a StructuralEntity '"+e.getName()+"' does not belong to anything", e.getBelongsTo());
		}
	}

}