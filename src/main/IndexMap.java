package main;

import java.util.HashMap;
import java.util.HashSet;

import concepts.Concept;
import node.Axiomnode;
import node.Individual;
import node.IndividualAxiom;

/**
 * This Class implements the Indexes that the Ontology uses to keep track of all Literals.
 * 
 * @author Bjoern Kreutz
 * 
 */

@SuppressWarnings("serial")
public class IndexMap extends HashMap<String, HashSet<Axiomnode>> {

	/**
	 * add an Axiomnode to the Bucket s. 
	 * Since HashMap only allows to put and overwrite, not add, a helper Method is needed to streamline the process.
	 * @param s the Name of the key String. The Axiomnode is added into that Bucket.
	 * @param a The Axiomnode that is being added to the Map  
	 */
	@SuppressWarnings("unchecked")
	public void add(String s, Axiomnode a){
		HashSet<Axiomnode> c = new HashSet<Axiomnode>() ;
		c.add(a);
		if (this.containsKey(s)){
			c.addAll(this.get(s));
		}
		this.put( s , (HashSet<Axiomnode>) c.clone() );
	}	
		
	
	
	//returns all Individuals of Concept c
	public HashSet<IndividualAxiom> findIndivofConcept(Concept c){
		
		HashSet<String> premise = c.findAtoms();
		
		//the set possibleIndivAxioms is now filled with all IndivAxioms that mention concepts that appear in X
		//This is done because the HashMap main.main.indivIndexMap only maps to Concept names, not nested concepts.
		HashSet<IndividualAxiom> possibleIndivAxioms = new HashSet<IndividualAxiom>();
		HashSet<Axiomnode> n = new HashSet<Axiomnode>();
		//Add all ABox Axioms that contain an Atom from the premise
		for(String i : premise){
			if(this.get(i)!=null){
				n.addAll(this.get(i));
				for (Axiomnode j : n){
					if(j instanceof IndividualAxiom){
						possibleIndivAxioms.add((IndividualAxiom) j);
					}
				}
			}
		}
		
		HashSet<IndividualAxiom> individualAxioms = new HashSet<IndividualAxiom>();
		for(IndividualAxiom a : possibleIndivAxioms){	
			if ( a.getConcept().equals(c)){
						individualAxioms.add(a);
			}
		}
		
		return individualAxioms;
		
	}
	
	/**
	 * This Method removes the Axiomnode a from all Index buckets.
	 * 
	 * 
	 * @param a the Axiomnode that is being removed
	 * @return true
	 */
	public boolean removeAxiomnode(Axiomnode a ){
		
		for(HashSet<Axiomnode> s : this.values()){
			s.remove(a);
		}
		return true;
	}
	

}
