package concepts;

import java.util.HashSet;

import main.Ontology;
import node.Axiomnode;
import node.IndividualAxiom;
import node.SubsumptionAxiom;

/**
 * A Conjunction is a Concept that contains two Concepts a and b.
 * Syntactically, the contained concepts a and b are commutative. This is achieved by expanding the .equals() method with a reverse check if the initial check fails.
 * The Semantics of this class describes the AND operation. For each individual a: (X and Y), then a:X and a:Y is always true.
 * 
 * @author Bjoern Kreutz
 *
 */
public class Conjunction implements Concept{
	Concept a;
	Concept b;
	
	/**
	 * The constructor for conjuctions
	 * @param a one Concept
	 * @param b another concept
	 */
	public Conjunction(Concept a, Concept b){
		this.a = a;
		this.b = b;
		
	}

	public void printvalue(){
		System.out.print("(");
		a.printvalue();
		System.out.print( " && " );
		b.printvalue();
		System.out.print(")");
	}


	@Override
	public HashSet<String> findAtoms() {
		HashSet<String> m = a.findAtoms();
		HashSet<String> n = b.findAtoms();
		
		n.addAll(m);
		return n;
	}




	@Override
	public void applyTableau(SubsumptionAxiom t, Ontology o) {
		
		//if X subsetof(Y and Y) --> X subsetof Y	
		if(a.equals(b)){
			SubsumptionAxiom t1 = new SubsumptionAxiom(false, t.getleft() , a);
			//t1.link(t,o);
			//o.addAxiom(t1);
			o.addAxiom(t1).link(t,o);
			
		//if X subsetof(X and Y) --> X subsetof Y	
		}else if(a.equals(t.getleft()) && !b.equals(t.getleft())){
			SubsumptionAxiom t1 = new SubsumptionAxiom(false, t.getleft() , b);
			//t1.link(t,o);
			//o.addAxiom(t1);
			o.addAxiom(t1).link(t,o);
			
		//if X subsetof(Y and X) --> X subsetof Y	
		}else if(b.equals(t.getleft()) && !a.equals(t.getleft())){
			SubsumptionAxiom t1 = new SubsumptionAxiom(false, t.getleft() , a);
			//t1.link(t,o);
			//o.addAxiom(t1);
			o.addAxiom(t1).link(t,o);

			
		//X subsetof(Y and Z) --> X subsetof Y , X subsetof Z
		}else{
			SubsumptionAxiom t1 = new SubsumptionAxiom(false, t.getleft() , a);
			SubsumptionAxiom t2 = new SubsumptionAxiom(false, t.getleft() , b);
		
			//Link erzeugt die Kreuzreferenzierung.
			/*
			t1.link(t,o);
			o.addAxiom(t1);
			t2.link(t,o);
			o.addAxiom(t2);
			*/
			o.addAxiom(t1).link(t, o);
			o.addAxiom(t2).link(t, o);
		
		}
	}

	@Override
	public void applyTableau(IndividualAxiom i, Ontology o) {

		//if a:(X and X) --> a:X
		if(a.equals(b)){
			IndividualAxiom i1 = new IndividualAxiom(false, i.getIndiv() , a);
			i1.link(i, o);
			o.addAxiom(i1);
		
		
		}else{

		IndividualAxiom i1 = new IndividualAxiom(false, i.getIndiv() , a);
		IndividualAxiom i2 = new IndividualAxiom(false, i.getIndiv() , b);
		
		//Link erzeugt die Kreuzreferenzierung.
		/*
		i1.link(i, o);
		o.addAxiom(i1);
		i2.link(i, o);
		o.addAxiom(i2);
		*/ 
		
		o.addAxiom(i1).link(i, o);
		o.addAxiom(i2).link(i, o);
		
		
		
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
		Conjunction other = (Conjunction) obj;
		
		//c indicates if it's necessary to check the other way around.
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