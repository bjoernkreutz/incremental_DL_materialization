package concepts;

import java.util.HashSet;

import main.Ontology;
import node.IndividualAxiom;
import node.RoleAxiom;
import node.SubsumptionAxiom;
import node.TBoxAxiom;

/**
 * This interface represents the general idea of Concepts. 
 * The Instance of a Concept is always represented by a Subclass of this Interface, which implements it's Methods.
 * 
 * Concepts 
 * @author Bjoern Kreutz
 *
 */
public interface Concept {

	//the Axiom that the concept belongs to.
	//Axiomnode host;
	
	
	/**
	 * A print Method. It creates a Console output to make a Concept human readable.
	 */
	public abstract void printvalue();


	/**
	 * This Method finds the Atomic Concepts in a branching Concept Structure.
	 * It traverses the nested Concept Tree and puts the Atom Names in a HashSet.
	 * this list of Concept Atoms is used to fill the Concept Index Map.
	 * @return a HashSet of Strings containing the Atomic Literals that occur in this Axiom.
	 */
	public abstract HashSet<String> findAtoms();


	/**
	 * 
	 * This creates new Axiomnodes depending on the available Axiomnodes.
	 * It is named this way because it applies the Expansion Rules of the Tableau-Algorithm to a TBoxAxiom. 
	 * @param t the original Axiom that is being resolved
	 */
	public abstract void applyTableau(SubsumptionAxiom t, Ontology o);
	
	/**
	 * This creates new Axiomnodes depending on the available Axiomnodes.
	 * It is named this way because it applies the Expansion Rules of the Tableau-Algorithm to an IndividualAxiom. 
	 * @param i	the original Axiom that is being resolved
	 */
	public abstract void applyTableau(IndividualAxiom i, Ontology o);

	
	
	public abstract int hashCode();
	
	public abstract boolean equals(Object obj);

	
	
}
