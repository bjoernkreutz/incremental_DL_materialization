package node;

import java.util.HashSet;

import main.Ontology;
import main.main;
import concepts.AtomicConcept;
import concepts.Concept;

public class IndividualAxiom extends ABoxAxiom {
	
	private Individual a;
	
	private Concept c;
	
	
	//Konstruktor
	/*
	public IndivAx(Indiv newInd, AtomicConcept newConcept ){
		setIndiv(newInd);
		setConcept(newConcept);
		createIndex();
		//printvalue();
	}
	*/
	
	
	public IndividualAxiom(Individual newInd, Concept newConcept){
		setIndiv(newInd);
		setConcept(newConcept);
		this.setFact(true);
		
		
		
		this.setBlocked(false);

		//createIndex(o);
		//applyTableau(o);
		//printvalue();
	}
	
	public IndividualAxiom(boolean fact, Individual newInd, Concept newConcept){
	
		setIndiv(newInd);
		setConcept(newConcept);
		this.setFact(fact);
		this.setBlocked(false);
		
		//check if this Individual already exists
		//TODO verify
		/*
		if(o.contains(this)){
			//System.out.println("Duplicate");
			this.setBlocked(true);
		}
		*/
		
		//createIndex(o);
		//applyTableau();
		//printvalue();
	}


	@Override
	public void printvalue() {
		//if(!this.fact){System.out.print("Derived: ");}
		getIndiv().printvalue();		
		System.out.print(":");
		getConcept().printvalue();
		//System.out.println();
		
		
	}


	public void createIndex(Ontology o) {
		
		//the set of all Axioms
		//if( !o.contains(this)  )
		o.add(this);
		if(this.isFact()){
			o.truthSet.add(this);
		}

		
		HashSet<String> m = new HashSet<String>();
		m = this.findAtoms();
		for(String str : m){	
			o.indivIndexMap.add( str , this );
		}
	}
	


	public HashSet<String> findAtoms() {
		HashSet<String> m = new HashSet<String>();
		m.add(this.a.getName()); 
		HashSet<String> n = c.findAtoms();
		n.addAll(m);
		return n;
		
		
	}

	@Override
	public void applyTableau(Ontology o) {

		
		//if(!this.isBlocked()){
			c.applyTableau(this,o);
		//}
		
		
		
	}


	public Individual getIndiv() {
		return a;
	}


	public void setIndiv(Individual indiv) {
		this.a = indiv;
	}


	public Concept getConcept() {
		return c;
	}


	public void setConcept(Concept newConcept) {
		this.c = newConcept;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((a == null) ? 0 : a.hashCode());
		result = prime * result + ((c == null) ? 0 : c.hashCode());
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
		IndividualAxiom other = (IndividualAxiom) obj;
		if (a == null) {
			if (other.a != null)
				return false;
		} else if (!a.equals(other.a))
			return false;
		if (c == null) {
			if (other.c != null)
				return false;
		} else if (!c.equals(other.c))
			return false;
		
		return true;
	}
	
	
	
}