package main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

import node.Axiomnode;
import node.Individual;
import concepts.AtomicConcept;
import concepts.Concept;
import concepts.Disjunction;
import concepts.Negation;



/**
 * This map contains Concepts mapped to individuals.
 * These Concepts can be Atomic or Negations of Atomic Concepts.
 * It also contains a Set of Disjunctions that keeps track of the OR operations that occur in the database and haven't been solved yet.
 * 
 * <p>The naive approach will try to resolve the inconsistencies by brute force, and the incremental approach solves disjunctions based on which Axiom was added to the Ontology.<p>
 * 
 * @author Bjoern Kreutz
 *
 */
public class IndividualMap extends HashMap<Individual, HashSet<Concept>> {
	
	/**
	 * Tracks all unsolved OR Operations that can still potentially be solved.
	 */
	public HashSet<Axiomnode> disjunctionSet = new HashSet<Axiomnode>();

	
	
	
	
	/**
	 * Checks the entire Ontology for Contradictions.
	 * 
	 * 
	 * @return true if there are contradictions
	 */
	public boolean detectContradictions(){
		
		//for every key, 
		for(Individual i : keySet()){
			//check all the Concepts.
			for(Concept c : this.get(i)){
				//if C is an atomic concept and the individual I is mapped to it's negation
				if(c instanceof AtomicConcept && this.get(i).contains(   new Negation(c)    )){
					return true;
				}
				//if c is a negated Atom and i is also mapped to it's atom.
				if(c instanceof Negation && this.get(i).contains( ((Negation) c).getConcept()   )){
					return true;
				}
			}
			
			
		}
		return false;
		
	
		
		
		
	}
	
	/**
	 * Checks the Individual i for Contradictions.
	 * 
	 * 
	 * @param i an Individual
	 * @return true if i:C and i:!C both exist.
	 */
	public boolean detectContradictions(Individual i){
		
		for(Concept c : this.get(i)){
			//if C is an atomic concept and the individual I is mapped to it's negation
			if(c instanceof AtomicConcept && this.get(i).contains(   new Negation(c)    )){
				System.out.print("Contradiction in ");
				i.printvalue();
				System.out.print(" ");
				c.printvalue();
				return true;
			}
			//if c is a negated Atom and i is also mapped to it's atom.
			if(c instanceof Negation && this.get(i).contains( ((Negation) c).getConcept()   )){
				System.out.print("Contradiction in ");
				i.printvalue();
				System.out.print(" ");
				c.printvalue();
				return true;
				
			}
		}
		
		
		
		
		
		
		return false;
		
		
		
	}

	
	
	
	/**
	 * this method takes all unresolved Disjunctions from the Database and tries to resolve them.
	 * This makes sure that unresolved ORs are retried at the end.
	 * It attempts to resolve all unresolved Disjunctions. 
	 * If it resolves one of them, it retries all the remaining ones.
	 * If it spends an entire run without solving one, i.e. last round's disjunctionset = this round's disjunctionset, then it gives up.
	 * 
	 * @param axiomSet the Ontology that is supposed to be resolved.
	 * @return boolean false if there are still unresolved Disjunctions left. Otherwise true.
	 */
	public boolean resolveDisjunction(Ontology axiomSet){
		
		HashSet<Axiomnode> c = null;
		//traverse all Disjunction Nodes in the entire Ontology
		if(!this.disjunctionSet.isEmpty()){
		do{
			c = (HashSet<Axiomnode>) this.disjunctionSet.clone();
			this.disjunctionSet.clear();
			for(Axiomnode d : c){
				//re-apply the Tableau
				d.setBlocked(false);
				d.applyTableau(axiomSet);
			}
		
		}while(!c.equals(this.disjunctionSet));
			if(!c.isEmpty()){
				return false;
			}	
		}
		return true;
		

	}

	
	/**
	 * the Incremental Version of resolveDisjunction().
	 * It only checks disjunctions that share at least one literal with the newly added Axiom
	 * This improves the runtime greatly compared to the naive approach.
	 * 
	 * 
	 * @param o the Ontology
	 * @param a the newly added Axiomnode
	 * @return true
	 */
	public boolean resolveDisjunction(Ontology o, Axiomnode a) {

		//track a copy of the disjunction set, so we can see if anything changed
		HashSet<Axiomnode> disjunctionSet_old = new HashSet<Axiomnode>();
		disjunctionSet_old.addAll(this.disjunctionSet);

		//todo is the list that tracks all relevant Disjunctionaxioms to check
		HashSet<Axiomnode> todo = new HashSet<Axiomnode>();
		todo.addAll(this.disjunctionSet);

		//the not_todo set tracks all Disjunctions that aren't related to the new Axiom.
		HashSet<Axiomnode> not_todo = new HashSet<Axiomnode>();

		//if an axiom from the disjunction set doesn't share a literal with the new axiom, put it in the not_todo list
		HashSet<String> atoms = a.findAtoms();
		for(Axiomnode t : todo){
			HashSet<String> checkatoms = t.findAtoms();
			checkatoms.retainAll(atoms);
			if(checkatoms.isEmpty()){
				not_todo.add(t);
			}
			
		}
		//then subtract all not_todo Axioms from the todo set.
		todo.removeAll(not_todo);
		
		
		HashSet<Axiomnode> todo_old = new HashSet<Axiomnode>();
		todo_old.addAll(todo);
		
		//traverse all Disjunction Nodes in the todo list
		if(!todo.isEmpty()){
			for(Axiomnode d : todo){
				//re-apply the Tableau
				d.setBlocked(false);
				d.applyTableau(o);
			}
		
			//Compare the new and the old disjunction set. if the disjunctionset has been altered in the course of this method, call this method again.
			if(!disjunctionSet.equals(disjunctionSet_old)){	
				resolveDisjunction(o, a);
			}	
		}
		return true;
	}
	
	
	

}
