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


/**
 * This Concept represents the "exists" Quantifier.
 * @author Bjoern Kreutz
 *
 */
public class Exists implements Concept {
	
	Role role;
	Concept a;
	
	
	
	public Exists(Role role, Concept rightSide) {
		super();
		this.role = role;
		this.a = rightSide;
	}





	public void printvalue(){
		// I want to use \u2203 but it only does "?" 	 '\u2203'
		System.out.print("Exists " +role.getName()+".");;
		a.printvalue();
		//System.out.print(")");
	}

	@Override
	public HashSet<String> findAtoms() {
		HashSet<String> m = new HashSet<String>();
		m.add(this.role.getName()); 
		HashSet<String> n = a.findAtoms();
		n.addAll(m);
		return n;
	}



	/**
	 * This Method takes a TBoxAxiom the format (X subsetof Exists r.Y) where X,Y are any concepts and r is a relation name
	 * 1. it searches all Individuals of the Concept X, a:X
	 * 2. it creates new Individuals for each of these Axioms in the form of: a:X --> (a:Exists r.Y)
	 * It creates a set of Individual Axioms (a:Exists r.Y) and then lets the implementation of applyTableau(IndivAx) sort it out.
	 * @param t the TBox Axiom that is hosting this Existence Node.
	 * @param o the Ontology
	 * 
	 */
	@Override
	public void applyTableau(SubsumptionAxiom t, Ontology o) {		
		
		//1. Finde alle Individuen Axiome X
		Concept p = t.getleft();
		HashSet<IndividualAxiom> individualAxioms = o.indivIndexMap.findIndivofConcept(p);
		
		//we now have a set of IndividualAxioms a:X
			
		//2. for each Individual, create a new IndivAxiom i with (a:Exists r.Y)
		for(IndividualAxiom i : individualAxioms){	
			
			
			
			//create the new Individual
			IndividualAxiom i2 = new IndividualAxiom(false, i.getIndiv(), this );
			
			//Link the new Axiomnodes to their Precursors
			//the new Axioms are derived from TBoxAxiom t and the Individual i.
			//i2.link(t,i,o);
			//o.addAxiom(i2);
			o.addAxiom(i2).link(t,i,o);

			
			

		}
		
		
			
		
		
	}


	/**
	 * We get an IndivAx in the form of a:Exists.r(Y), where a is an Individual, r is a role name and Y is a Concept.
	 * Check if no relation (a,Y):r exists. If it already exists, link and done. Otherwise:
	 * 1.we create a new Individual _ in X that has not been in X
	 * 2. we create a new Role r(a,_) to that new Individual
	 * We link the created Individual and role a successors to the Individual Axiom.
	 * 
	 * @param i the Individual Axiom that contains this Concept. The full Individual is needed to link(i) it at the end.
	 */
	@Override
	public void applyTableau(IndividualAxiom i, Ontology o) {
		
		//Check if no relation (a,Y):r exists. If it already exists, done. Otherwise do the algorithm.
		HashSet<Axiomnode> axNodeWithThatName =  new HashSet<Axiomnode>();
		HashSet<RoleAxiom> roles =  new HashSet<RoleAxiom>();
		if(o.roleIndexMap.get(this.role.getName())!=null){
			axNodeWithThatName.addAll(	o.roleIndexMap.get(this.role.getName()));
			
			//cast all Axiomnodes into RoleAxioms and link all roles that have "a" as first Individual and an existing Indiv of Concept Y on the right side.
			//these roles already fulfill the Exists requirements and just need to be linked to the correct Axiomnodes.
			
			for(Axiomnode a : axNodeWithThatName){
				if(a instanceof RoleAxiom){
					if(o.indivConceptMap.get(((RoleAxiom) a).getRight())!=null){
							//Check if (_,_):r has a on the left side and a Individual of Concept this.rightSide on the right
							if( ((RoleAxiom) a).getLeft().equals(i.getIndiv()) && o.indivConceptMap.get(((RoleAxiom) a).getRight()).contains(this.a)){
								roles.add((RoleAxiom) a);
								a.link(i,  new IndividualAxiom( false, ((RoleAxiom) a).getRight() , this.a ) ,o);
								//link each of these existing roles
								
							}	
					}
				}
			}
			//"roles" now contains all role Axioms with (a,Y):r
		}
		if(roles.isEmpty()){
		
			//generate a NEW Individual
			Individual b = new Individual(utility.generateIndivName(2));
			//if the Individual is already mapped to a Concept then reroll the name
			Integer l = 1;
			while (o.indivConceptMap.get(b)!=null && o.indivConceptMap.get(b).contains(this.a)) {
				//System.out.println("New Name Rolled with "+((l / 10)+1));
				//the length of the generated name is incremented by 1 every 10 rolls, so it doesn't search forever.
				b.setName( utility.generateIndivName((l / 10)+1));
				l++;
			}		
			
			IndividualAxiom i1 = new IndividualAxiom(false,b,this.a);
			RoleAxiom r = new RoleAxiom(false, this.role, i.getIndiv() , b);
			//i1.link(i,o);
			//o.addAxiom(i1);
			o.addAxiom(i1).link(i,o);
			//r.link(i,o);
			//o.addAxiom(r);
			o.addAxiom(r).link(i,o);
		
		}
		
	}
		




	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((a == null) ? 0 : a.hashCode());
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
		Exists other = (Exists) obj;
		if (a == null) {
			if (other.a != null)
				return false;
		} else if (!a.equals(other.a))
			return false;
		if (role == null) {
			if (other.role != null)
				return false;
		} else if (!role.equals(other.role))
			return false;
		return true;
	}




		
	





	

}
