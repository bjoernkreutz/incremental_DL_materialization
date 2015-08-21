package concepts;

import java.util.HashSet;



import main.Ontology;
//import main.main;
import main.IndividualMap;
import node.Axiomnode;
import node.IndividualAxiom;
import node.SubsumptionAxiom;

public class Disjunction implements Concept {
	Concept a;
	Concept b;
	
	
	public Disjunction(Concept left, Concept right){
		a = left;
		b = right;
		
	}
	
	
	public void printvalue(){
		System.out.print("(");
		a.printvalue();
		System.out.print( " || " );
		b.printvalue();
		System.out.print(")");
		
		
	}


	public HashSet<String> findAtoms() {
		HashSet<String> m = a.findAtoms();
		HashSet<String> n = b.findAtoms();
		
		//throws null pointer exceptions!
		n.addAll(m);
		return n;
	}

	/**
	 * This method gets a Subsumption Axiom X subsetof Y or Z.
	 * It then searches all Individuals in the form a:X, where a is any Individual.
	 * For each found Axiom it creates a new Individual Axiom a:Y or Z
	 * 
	 * 
	 * 
	 * 
	 */
	@Override
	public void applyTableau(SubsumptionAxiom t, Ontology o) {
	
		

			
		//if X subsetof(Y or Y) --> X subsetof Y	
		if(a.equals(b)){
			SubsumptionAxiom t1 = new SubsumptionAxiom(false, t.getleft() , a);
			//t1.link(t, o);
			//o.addAxiom(t1);
			o.addAxiom(t1).link(t,o);
				
		/*	
		//if X subsetof(Not(X) or Y) --> X subsetof Y	
		}else if(a.equals(t.getleft()) && !b.equals(t.getleft())){
			SubsumptionAxiom t1 = new SubsumptionAxiom(false, t.getleft() , b);
			t1.link(t);
				
		//if X subsetof(Y or Not(X)) --> X subsetof Y	
		}else if(b.equals(t.getleft()) && !a.equals(t.getleft())){
			SubsumptionAxiom t1 = new SubsumptionAxiom(false, t.getleft() , a);
			t1.link(t);
		*/		
			
			
		}else{ 
			//if(!o.indivConceptMap.detectContradictions()){

			Concept p = t.getleft();			
			HashSet<IndividualAxiom> individualAxioms = o.indivIndexMap.findIndivofConcept(p);
			
			//2. for each Individual, create a new IndivAxiom i with (a:X or Y)
			for(IndividualAxiom i : individualAxioms){		
				//create the new Individual
				IndividualAxiom i2 = new IndividualAxiom(false, i.getIndiv(), this );
				//i2.link(t,i,o);
				//o.addAxiom(i2);
				o.addAxiom(i2).link(t,i,o);
			}
		
		}
		
	}
	
	
	/**
	 * We get an Individual a: (X or Y)
	 * if a:(X or X) create a:X. end.
	 * We now search for a:Not(X) and a:Not(Y).
	 * If we find neither, we put a: (X or Y) in the disjunctionSet, and solve it later.
	 * If we find the former, but not the latter, we create a:Y.
	 * If we find the latter, but not the former, we create a:X.
	 * If we find both, we can't solve this and we end.
	 * 
	 * 
	 */
	@Override
	public void applyTableau(IndividualAxiom i, Ontology o) {
		
		o.indivConceptMap.disjunctionSet.remove(i);
		
		//a:(X or X) ---> a:X
		if(a.equals(b)){
			IndividualAxiom i1 = new IndividualAxiom(false, i.getIndiv() , a);
			i1.link(i, o);
			o.addAxiom(i1);
			
		}else{
			//the Axioms that can disprove a and b respectively
			IndividualAxiom ka = null;
			IndividualAxiom kb = null;

			HashSet<IndividualAxiom> individualAxiomsA = null;
			HashSet<IndividualAxiom> individualAxiomsB = null;

			//check b for contradictions:
			//check if b is a negation of a concept, not(Y). If yes, search for X. Else b is a Concept Y, search for not(Y).
			if(b instanceof Negation){
				individualAxiomsB = o.indivIndexMap.findIndivofConcept(((Negation) b).getConcept());
			}else{
				individualAxiomsB = o.indivIndexMap.findIndivofConcept(new Negation(b));
			}
			for(IndividualAxiom j : individualAxiomsB){
				if(j.getIndiv().equals(i.getIndiv())){
					kb=j;
				}
			}
			//kb now contains all axioms that disprove b
			
			
			//check a for contradictions:
			//check if a is a negation of a concept, not(X). If yes, search for X. Else a is a Concept X, search for not(X).
			if(a instanceof Negation){
				individualAxiomsA = o.indivIndexMap.findIndivofConcept(((Negation) a).getConcept());
			}else{
				individualAxiomsA = o.indivIndexMap.findIndivofConcept(new Negation(a));
			}
			for(IndividualAxiom j : individualAxiomsA){
				if(j.getIndiv().equals(i.getIndiv())){
					ka=j;
				}
			}
			//ka now contains all axioms that disprove a

			//if we don't find contradictions for either, we need to recheck at the end.
			if(kb == null && ka == null){
				o.indivConceptMap.disjunctionSet.add(i);
			}
				
				
			
			//Branch A
			//if we do find Contradictions for B, BUT can't disprove a, then create branch a
			else if(kb!=null && ka == null){
				IndividualAxiom i1 = new IndividualAxiom(false, i.getIndiv(), a);
				//i1.link(i,kb, o);
				//o.addAxiom(i1);
				o.addAxiom(i1).link(i,kb,o);
			}
		
			//Branch B
			//if we do find Contradictions for a, BUT can't disprove b, then create branch b
			else if (ka!=null && kb == null){
					IndividualAxiom i2 = new IndividualAxiom(false, i.getIndiv(), b);
					//i2.link(i,ka, o);
					//o.addAxiom(i2);
					o.addAxiom(i2).link(i,ka,o);
			}else{
				
				
				//if we reach this point, we've disproven both possible deductions, and can't deduce any further.
				
				
			}
		
		
			
			
		}
		
	}

	
	
	
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((a == null) ? 0 : a.hashCode());
		result = prime * result
				+ ((b == null) ? 0 : b.hashCode());
		return result;
	}

	/**
	 * 
	 * This equals() method as been modified to support the Commutative Property to the Conjunction's Parameters.
	 * 	if the initial equality test fails, it performs the same algorithm again but with switched parameters.
	 *	This means a is compared to other.b, and b is compared to other.a
	 *
	 * As a result, the order of parameters is now irrelevant. 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Disjunction other = (Disjunction) obj;
	
		// c is an internal flag that indicates if it's necessary to check the other way around.	 
		boolean c = false;
		if (a == null) {
			if (other.a != null)
				c=true;
		} else if (!a.equals(other.a))
			c=true;
		if (b == null) {
			if (other.b != null)
				c=true;
		} else if (!b.equals(other.b))
			c=true;
		
		//this block was added to add commutative property to the Conjunction.
		//if the initial test fails, it performs the same algorithm again but with switched parameters.
		//a is compared to other.b, and b is compared to other.a
		if(c){
			if (a == null) {
				if (other.b != null)
					return false;
			} else if (!a.equals(other.b))
				return false;
			if (b == null) {
				if (other.a != null)
					return false;
			} else if (!b.equals(other.a))
				return false;
		}
		return true;
	}





	
}
