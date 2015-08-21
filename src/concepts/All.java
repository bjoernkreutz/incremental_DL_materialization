package concepts;

import java.util.HashSet;

import main.Ontology;
import main.utility;
import node.Axiomnode;
import node.Individual;
import node.IndividualAxiom;
import node.Role;
import node.RoleAxiom;
import node.SubsumptionAxiom;
import node.TBoxAxiom;



/**
 * This class represents the forAll-Quantifier.
 * 
 * 
 * @author Bjoern_Kreutz
 *
 */
public class All implements Concept {

	Role role;
	Concept c;
	
	
	
	
	
	public All(Role role, Concept rightSide) {
		super();
		this.role = role;
		this.c = rightSide;
	}





	public void printvalue(){
		System.out.print("ForAll " + role.getName()+".");
		c.printvalue();
		//System.out.print(")");
	}





	@Override
	public HashSet<String> findAtoms() {
		return c.findAtoms();
	}




	/**
	 * <p>This Method takes a TBoxAxiom of the format (X subsetof forAll r.Y) where X,Y are any concepts and r is a relation name.
	 * It then takes all Individual Axioms a:X and derives a set of new Individual Axiom in the form a:(forAll r.Y) </p>
	 * 
	 * We get a SubsumtionAxiom t X subsetof (forall r.Y)
	 * 1.Find all Individuals a:X
	 * 2. create new IndividualAxioms a:(forall r.Y) for each
	 * by the transitivity of the successor links, we reach the same dependencies, but with less superfluous code.
	 * 
	 */
	@Override
	public void applyTableau(SubsumptionAxiom t, Ontology o) {
		
		//Find all Individual-Axioms of concept X
		
		Concept p = t.getleft();
		HashSet<IndividualAxiom> individualAxioms = o.indivIndexMap.findIndivofConcept(p);

			//for each Individual a:X, we create a new Individual a:(forall r.Y)
		for(IndividualAxiom i : individualAxioms){	
				IndividualAxiom a = new IndividualAxiom(false, i.getIndiv(), this);
				//link a to TBoxAxiom, rolle and i.
				//a.link(t,i,o);	
				//o.addAxiom(a);
				o.addAxiom(a).link(t,i,o);
				
		}
	}


	
	
	/**
	 * <p>This Method takes an ABoxAxiom of the format (a : forAll r.Y) where Y is any concept, a is an Individual, and r is a relation name.
	 * It then derives a new Individual Axiom. </p>
	 * This method gets an Axiom (a : forAll r.Y).
	 * First, it gathers all roles in the System with (a,b):r, where b is a random existing Individual. If it finds none, it terminates.
	 * Finally, it derives a new Individual Axiom with the form (b:Y), out of the role and the Individual. It does that for each role it found.
	 * @param i The individual Axiom that is to be linked as precursor
	 * @param o the parent onthology
	 * 
	 */
	@Override
	public void applyTableau(IndividualAxiom i, Ontology o) {
		
		/*
		 * We get an IndivAx a:forAll.r(C)
		 * 1. Find all Role Axioms of r with (a,b)
		 * 2. if b isn't of Concept C, then add b:C
		 * 3. repeat that for all r(a,_)
		 * 
		 * 
		 */
				
		//find all Roles r
		HashSet<Axiomnode> axNodeWithThatName =  new HashSet<Axiomnode>();
		if(o.roleIndexMap.get(this.role.getName())!=null){
			axNodeWithThatName.addAll(	o.roleIndexMap.get(this.role.getName()));
			
			//cast all Axiomnodes into RoleAxioms and filter all roles that don't have "a" as first Individual
			HashSet<RoleAxiom> roles =  new HashSet<RoleAxiom>();
			for(Axiomnode a : axNodeWithThatName){
				if(a instanceof RoleAxiom){
						if( ((RoleAxiom) a).getLeft().equals(i.getIndiv())){
							roles.add((RoleAxiom) a);
						}
				}
			}
			
			
			//iterate over all available roles
			for(RoleAxiom r :  roles){	
				
				
				IndividualAxiom b = new IndividualAxiom(false, r.getRight(), this.c);
				//b.link(r, i, o);	
				//o.addAxiom(b);
				o.addAxiom(b).link(r, i, o);
				
				
			}
			
			
			
			
		}
		
	}




	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((c == null) ? 0 : c.hashCode());
		result = prime * result + ((role == null) ? 0 : role.hashCode());
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
		All other = (All) obj;
		if (c == null) {
			if (other.c != null)
				return false;
		} else if (!c.equals(other.c))
			return false;
		if (role == null) {
			if (other.role != null)
				return false;
		} else if (!role.equals(other.role))
			return false;
		return true;
	}










	

	
}
