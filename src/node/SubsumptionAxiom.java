package node;
import java.util.HashSet;

import main.Ontology;
import main.main;
import main.IndexMap;
import concepts.Concept;

/**
 * The Subsumption Axiom is a form of TBox Axiom.
 * It has the Syntax X subsetof Y, where X and Y are Concepts.
 * Semantically, it means anything that is valid for X is also valid for Y.
 * Trivially, it's reflexive, but not commutative.
 * 
 * 
 * @author Bjoern Kreutz
 *
 */
public class SubsumptionAxiom extends TBoxAxiom {

	//
	private Concept left;
	private Concept right;
	//boolean fact;
	//HashSet<Axiomnode> Nachfolger;
	//HashSet<Axiomnode> Vorgänger;

	
	
	/**
	 * The default Constructor for given facts. The constructed TBoxAxioms are implicitly given and not derived, which means the "fact" flag is always true.
	 * @param leftSide
	 * @param rightSide
	 * @param o 
	 */
	public SubsumptionAxiom(Concept leftSide, Concept rightSide){
		setleft(leftSide);
		setright(rightSide);
		this.setFact(true);
		//o.truthSet.add(this);
		this.setBlocked(false);

		//printvalue();

		//createIndex(o);	
		//applyTableau(o);
		//printfollowers();
	}
	
	/**Constructor with a fact boolean. The constructed TBoxAxioms can be flagged with the fact boolean as factual(true), or as derived(false)
	 * This creation method doesn't immediately apply the tableau algorithm.
	 * 
	 * @param fact true if it is a given fact. False if it has been derived out of another Axiom
	 * @param leftSide
	 * @param rightSide
	 */
	public SubsumptionAxiom(boolean fact, Concept leftSide, Concept rightSide){
		setleft(leftSide);
		setright(rightSide);
		this.setFact(fact);
		this.setBlocked(false);
		//printvalue();
		
		
		//CreateIndex() and applyTableau()  have been shifted to the end of Axiomnode.link()
		//This means link is always performed BEFORE further deriving a derived Axiomnode.
		//createIndex(o);	
		//applyTableau();
	}
	

	
	
	/**
	 * This Method integrates a TBox Axiom into the general axiomSet and conceptIndexMap, to prevent duplicates and to enable access respectively.
	 * @param o 
	 * 
	 */
	//@SuppressWarnings("unchecked")
	public void createIndex(Ontology o) {
		
		o.add(this);
		if(this.isFact()){
			o.truthSet.add(this);
		}
		
		
		HashSet<String> m = new HashSet<String>();
		//Alle Atomaren Konzepte Finden
		m = this.findAtoms();
		HashSet<Axiomnode> c = new HashSet<Axiomnode>();
		
		 for(String str : m){				 
			o.conceptIndexMap.add( str , this );
		 }		
	}
	

	/**
	 * This Method prints a TBox Axiom into the Console.
	 * It uses concepts.Concept.printvalue() to print Concepts.
	 */
	public void printvalue() {
		// TODO Auto-generated method stub
		//if(!this.fact){System.out.print("Derived: ");}
		getleft().printvalue();
		System.out.print(" <= " );
		getright().printvalue();
		//System.out.println();
	}

	
	//this traverses through a TBoxAxiom and returns a Set with the Names of all unique Concept Atoms.
	//This is used to fill the Index HashMap with Axioms.
	public HashSet<String> findAtoms(){		
		HashSet<String> m = this.getleft().findAtoms();
		HashSet<String> n = this.getright().findAtoms();
		
		n.addAll(m);
		
		return n;
	}
	
/*
	public boolean isFact() {
		return fact;
	}


	public void setFact(boolean fact) {
		this.fact = fact;
	}
	*/
	
	
	/**Approach:
	 * solve TBox Axioms:
	 * 1.Apply Tableau at first sub-concept.
	 * 2.put results in a new TBox Axiom.
	 * 3.Cross-reference this new axiom with the old.
	 * 4.Repeat process with the new Axiom. 
	 */
	@Override
	public void applyTableau(Ontology o) {

		//if(!this.isBlocked()){
			getright().applyTableau(this,o);
		//}
		
	}

	public Concept getleft() {
		return left;
	}

	public void setleft(Concept premise) {
		this.left = premise;
	}

	public Concept getright() {
		return right;
	}

	public void setright(Concept conclusion) {
		this.right = conclusion;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((right == null) ? 0 : right.hashCode());
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SubsumptionAxiom other = (SubsumptionAxiom) obj;
		if (right == null) {
			if (other.right != null)
				return false;
		} else if (!right.equals(other.right))
			return false;
		if (left == null) {
			if (other.left != null)
				return false;
		} else if (!left.equals(other.left))
			return false;
		
		
		return true;
	}

	
	

	
	
}
