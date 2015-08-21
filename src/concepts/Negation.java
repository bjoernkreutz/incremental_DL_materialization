package concepts;

import java.util.HashSet;

import main.Ontology;
import node.Axiomnode;
import node.IndividualAxiom;
import node.SubsumptionAxiom;

/**
 * This class represents a Negated Concept.
 * 
 * 
 * @author Bjoern_Kreutz
 *
 */
public class Negation implements Concept{
	
	
	private Concept c;
	
	public Negation(Concept a) {
		super();
		setConcept(a);
	}


	@Override
	public void printvalue() {
		System.out.print("!(");
		getConcept().printvalue();
		System.out.print(")");
		
	}

	@Override
	public HashSet<String> findAtoms() {
		HashSet<String> n = getConcept().findAtoms();
		return n;
	}

	@Override
	public void applyTableau(SubsumptionAxiom t, Ontology o) {
		//A subsetof Neg(Neg(C)) -->A subsetof C
		if(this.getConcept() instanceof Negation){
			Negation c = (Negation) getConcept();
			
			SubsumptionAxiom t1 = new SubsumptionAxiom(false,t.getleft(), c.getConcept());
			
			//t1.link(t,o);	
			//o.addAxiom(t1);
			o.addAxiom(t1).link(t,o);	
			
			
		}else //DeMorgan Rules
			if(this.getConcept() instanceof Disjunction){
				// -(A or B) --> (-A) and (-B) --> 
				SubsumptionAxiom t1 = new SubsumptionAxiom(false,t.getleft(), 
																new Conjunction(
																				new Negation(( (Disjunction) this.getConcept()).a ),
																				new Negation(((Disjunction) this.getConcept()).b )
																)
											);
				//t1.link(t,o);
				//o.addAxiom(t1);
				o.addAxiom(t1).link(t,o);
				
			
		}else 
			if(this.getConcept() instanceof Conjunction){
				// -(A and B) --> (-A) or (-B)
				SubsumptionAxiom t1 = new SubsumptionAxiom(false,t.getleft(), 
																new Disjunction(
																				new Negation(( (Conjunction) this.getConcept()).a ),
																				new Negation(((Conjunction) this.getConcept()).b )
																				)
											);
				//t1.link(t,o);
				//o.addAxiom(t1);
				o.addAxiom(t1).link(t,o);
				
		
		}else 
			if(this.getConcept() instanceof All){
				//-All.r(C) --> Exists.r (-C)
				SubsumptionAxiom t1 = new SubsumptionAxiom(false,t.getleft(), 
																new Exists(
																				((All) this.getConcept()).role,
																				new Negation(((All) this.getConcept()).c )
																				)
											);
				//t1.link(t,o);
				//o.addAxiom(t1);
				o.addAxiom(t1).link(t,o);
				
				
		}else 
			if(this.getConcept() instanceof Exists){
				//-Exists.r(C) --> All.r(-C)
				SubsumptionAxiom t1 = new SubsumptionAxiom(false,t.getleft(), 
																new All(
																				((Exists) this.getConcept()).role,
																				new Negation(((Exists) this.getConcept()).a )
																				)
											);
				//t1.link(t,o);
				//o.addAxiom(t1);
				o.addAxiom(t1).link(t,o);

		
		}else 
			//X subsetof Not(Y)
			//find all a:X, for each, create a:Not(Y)
		if(this.getConcept() instanceof AtomicConcept){
			
			//Find all a:X where X is this.
			//1. Find all Individual Axioms X
			Concept p = t.getleft();
			HashSet<IndividualAxiom> individualAxioms = o.indivIndexMap.findIndivofConcept(p);

			
			
			//For all found a:X, we create an a:Not(Y)
			for(IndividualAxiom i : individualAxioms){	
				IndividualAxiom i2 = new IndividualAxiom(false, i.getIndiv(), this );
				//i2.link(t,i, o);
				//o.addAxiom(i2);
				o.addAxiom(i2).link(t,i,o);


			}
			
			
			
		}
		
	}

	@Override
	public void applyTableau(IndividualAxiom i, Ontology o) {
		//a:Neg(Neg(C)) --> a:C
		if(this.getConcept() instanceof Negation){
			Negation c = (Negation) getConcept();
			IndividualAxiom i1 = new IndividualAxiom(false, i.getIndiv(), c.getConcept());
			//i1.link(i,o);
			//o.addAxiom(i1);
			o.addAxiom(i1).link(i,o);
			
		
		
		}else //DeMorgan Rules
			if(this.getConcept() instanceof Disjunction){
				// -(A or B) --> (-A) and (-B) --> 
				IndividualAxiom i1 = new IndividualAxiom(false,i.getIndiv(), 
																new Conjunction(
																				new Negation(((Disjunction) this.getConcept()).a ),
																				new Negation(((Disjunction) this.getConcept()).b )
																)
				);
				//i1.link(i,o);
				//o.addAxiom(i1);
				o.addAxiom(i1).link(i,o);
				
			
		}else 
			if(this.getConcept() instanceof Conjunction){
				// -(A and B) --> (-A) or (-B)
				IndividualAxiom i1 = new IndividualAxiom(false,i.getIndiv(), 
																new Disjunction(
																				new Negation(((Conjunction) this.getConcept()).a ),
																				new Negation(((Conjunction) this.getConcept()).b )
																				)
				);
				//i1.link(i,o);
				//o.addAxiom(i1);
				o.addAxiom(i1).link(i,o);
				
		
		}else 
			if(this.getConcept() instanceof All){
				//-All.r(C) --> Exists.r (-C)
				IndividualAxiom i1 = new IndividualAxiom(false,i.getIndiv(), 
																new Exists(
																				((All) this.getConcept()).role,
																				new Negation(((All) this.getConcept()).c )
																				)
				);
				//i1.link(i,o);
				//o.addAxiom(i1);
				o.addAxiom(i1).link(i,o);
				
				
		}else 
			if(this.getConcept() instanceof Exists){
				//-Exists.r(C) --> All.r(-C)
				IndividualAxiom i1 = new IndividualAxiom(false,i.getIndiv(), 
																new All(
																				((Exists) this.getConcept()).role,
																				new Negation(((Exists) this.getConcept()).a )
																				)
				);
				//i1.link(i,o);
				//o.addAxiom(i1);
				o.addAxiom(i1).link(i,o);

		
		}else 
		if(this.getConcept() instanceof AtomicConcept){
			
			//create a new entry in the indivConceptMap
			HashSet<Concept> c = new HashSet<Concept>() ;
			c.add(this);
			if (o.indivConceptMap.containsKey(i.getIndiv())){
				c.addAll(o.indivConceptMap.get(i.getIndiv()));
			}
			o.indivConceptMap.put(  i.getIndiv() , (HashSet<Concept>) c.clone() );
		}		
		
		
			
			
		

	
	}
	
	
	
	


	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getConcept() == null) ? 0 : getConcept().hashCode());
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
		Negation other = (Negation) obj;
		if (getConcept() == null) {
			if (other.getConcept() != null)
				return false;
		} else if (!getConcept().equals(other.getConcept()))
			return false;
		return true;
	}


	public Concept getConcept() {
		return c;
	}


	public void setConcept(Concept c) {
		this.c = c;
	}



}
