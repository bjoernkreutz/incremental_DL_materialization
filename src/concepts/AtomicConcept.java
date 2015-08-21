package concepts;

import java.util.HashSet;

import main.Ontology;
import node.Axiomnode;
import node.IndividualAxiom;
import node.SubsumptionAxiom;


/**
 * The Atomic Concept represents the end of a Concept Branch. It does not consist of any more Concepts but instead contains a String that Represents it's Atomic name.
 * @author Bjoern Kreutz
 *
 */
public class AtomicConcept implements Concept {

	public String value;
	
	
	public AtomicConcept(String input ){
		this.value = input;
	}
	
	
	public void printvalue() {
		System.out.print(value);
	}
	
	/**
	 * This Method finds the Atomic Concepts in a branching Concept Structure.
	 * @return the value:String of the AtomicConcept as a HashSet. These hashSets are then accumulated and passed down the Concept tree until the initial Axiomnode receives a HashSet of all Atomic Names.
	 */
	public HashSet<String> findAtoms(){
		HashSet<String> m = new HashSet<String>();
		m.add(this.value); 
		return m;
	}



	/**
	 * This Method gets a SubsumptionAxiom t in the form X subsetof Y, where Y is an atomic concept.
	 * It seeks out all Individual Axioms with a:X.
	 * For each found Axiom, it creates a new Individual Axiom a:Y.
	 * 
	 * @param t the SubsumptionAxiom X subsetof Y
	 * @see concepts.Concept#applyTableau(node.SubsumptionAxiom)
	 */
	@Override
	public void applyTableau(SubsumptionAxiom t, Ontology o) {	
		/*
		 *1.alle Individuen Axiome finden die tBoxAxiom.premise als Konzept besitzen
		 *2.neues IndivAx mit Individuum : tBoxAxiom.premise
		 *3. Link().
		 * 
		 * 
		 * 
		 */
		
		//Alle Axiomnodes die die prämisse des TBoxAxiomes enthalten
	
		//Finde alle Individuen Axiome X
		//New HashSet
		//the set possibleIndivAxioms is now filled with all IndivAxioms that mention concepts that appear in X
		//This is done because the HashMap main.main.indivIndexMap only maps to Concept names, not nested concepts.
		Concept p = t.getleft();
		HashSet<String> premise = p.findAtoms();
		
		/*
		HashSet<IndividualAxiom> possibleIndivAxioms = new HashSet<IndividualAxiom>();
		HashSet<Axiomnode> n = new HashSet<Axiomnode>();
		//Add all ABox Axioms that contain an Atom from the premise
		for(String i : premise){
			if(main.main.indivIndexMap.get(i)!=null){
				n.addAll(main.main.indivIndexMap.get(i));
				for (Axiomnode j : n){
					if(j instanceof IndividualAxiom){
						possibleIndivAxioms.add((IndividualAxiom) j);
					}
				}
			}
		}
		
		HashSet<IndividualAxiom> IndividualAxioms = new HashSet<IndividualAxiom>();
		for(IndividualAxiom axiom : possibleIndivAxioms){	
			if ( axiom.getConcept().equals(p)){
						IndividualAxioms.add(axiom);
			}
		}
		*/
		HashSet<IndividualAxiom> individualAxioms = o.indivIndexMap.findIndivofConcept(p);

		
		//apply all the found Individual Axioms to the TBoxAxiom t and derive a new Individual Axiom for each.
		for(IndividualAxiom i : individualAxioms){
			
			//create a new IndivAx i2 with the Individual of i, and the concept from the right side of t
			IndividualAxiom i2 = new IndividualAxiom(false, i.getIndiv(), t.getright());
			//Link the new individual to the Axioms i and t.
			//i2.link(i,t,o);
			//o.addAxiom(i2);
			o.addAxiom(i2).link(i,t,o);
			
			
			
		}	
	}
	
	/**
	 * This Method gets an Individual Axiom i of the Form a:X, where X is an Atomic Concept
	 * First, it adds the Atomic Concept to the IndivConceptMap
	 * then it finds all 
	 * @param i IndividualAxiom a:X
	 * @see concepts.Concept#applyTableau(node.IndivAxm)
	 */
	@Override
	public void applyTableau(IndividualAxiom i, Ontology o) {
		
		
		HashSet<Concept> c = new HashSet<Concept>() ;
		c.add(this);
		if (o.indivConceptMap.containsKey(i.getIndiv())){
			c.addAll(o.indivConceptMap.get(i.getIndiv()));
		}
		o.indivConceptMap.put(  i.getIndiv() , (HashSet<Concept>) c.clone() );
		
		
		

		
//----------------------------------------------------------------------------------------------------------------------
		//new IndivAx if i is a:X and X subsetof Y ---> a:Y
		//activating this causes infinite loops.
		
		//find all TBoxAxioms with X
		
		Concept p = i.getConcept();
		HashSet<String> premise = p.findAtoms();
		HashSet<SubsumptionAxiom> possibleTBoxAxioms = new HashSet<SubsumptionAxiom>();
		HashSet<Axiomnode> n = new HashSet<Axiomnode>();
		//Add all TBox Axioms that contain an Atom from the premise
		for(String j : premise){
			if(o.conceptIndexMap.get(j)!=null){
				n.addAll(o.conceptIndexMap.get(j));
				for (Axiomnode k : n){
					if(k instanceof SubsumptionAxiom){
						possibleTBoxAxioms.add((SubsumptionAxiom) k);
					}
				}
			}
		}
		
		HashSet<SubsumptionAxiom> TBoxAxioms = new HashSet<SubsumptionAxiom>();
		for(SubsumptionAxiom axiom : possibleTBoxAxioms){	
			if ( axiom.getleft().equals(p)){
						TBoxAxioms.add(axiom);
			}
		}
		
		
		//apply all the found TBoxAxioms to the IndivAxiom i and derive a new Individual Axiom for each.
		for(SubsumptionAxiom t : TBoxAxioms){
			
			
			//create a new IndivAx i2 with the Individual of i, and the concept from the right side of t
			IndividualAxiom i2 = new IndividualAxiom(false, i.getIndiv(), t.getright());
			//Link the new individual to the Axioms i and t.
			//i2.link(i,t,o);
			//o.addAxiom(i2);
			o.addAxiom(i2).link(i,t,o);
			
			
			
		}		
	}	
		
		
		
		
		
	


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		AtomicConcept other = (AtomicConcept) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}



	
}
