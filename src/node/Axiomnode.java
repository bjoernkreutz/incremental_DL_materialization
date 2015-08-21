package node;

import java.util.HashSet;

import main.Ontology;
import main.main;
import concepts.AtomicConcept;
import concepts.Concept;


/**
 * Axiomnode is the Abstract Class that represents all Axioms.
 * @author Bjoern_Kreutz
 *
 */
public abstract class Axiomnode {

	private boolean fact;
	private boolean blocked;
	private HashSet<Axiomnode> successor = new HashSet<Axiomnode>();
	protected HashSet<HashSet<Axiomnode>> precursor = new HashSet<HashSet<Axiomnode>>();
	
	
	
	/**
	 * This abstract Method prints an Axiom to the Console to make it human-readable.
	 * 
	 */
	public abstract void printvalue();
	
	/**
	 * This abstract Method integrates a created Axiomnode into the necessary axiomSets and IndexMaps, to prevent duplicates and to enable access.
	 */
	public abstract void createIndex(Ontology axiomSet);

	/**
	 * 
	 */
	public abstract void applyTableau(Ontology axiomSet);
	
	
	
	public abstract HashSet<String> findAtoms();
	


	/*
	public HashSet<Axiomnode> getNachfolger() {
		return getSuccessor();
	}

	public void setNachfolger(HashSet<Axiomnode> nachfolger) {
		setSuccessor(nachfolger);
	}
	*/

	public HashSet<HashSet<Axiomnode>> getPrecursor() {
		return precursor;
	}

	public void setPrecursor(HashSet<HashSet<Axiomnode>> precursor) {
		this.precursor = precursor;
	}
	
	

	
	
	/**
	 * This Method links a newly created Axiomnode called "successor" to it's precursor. 
	 * The precursor is the Axiomnode that was used to derive the successor.
	 * This version of the Method is used in the case that there is only one precursor.
	 * @param pre1 the Axiomnode that was used to derive the successor
	 * @param o the Ontology. Unused for now.
	 */
	public void link(Axiomnode pre1, Ontology o) {
		
		
		
		//blocks reflexivity
		if(!pre1.equals(this)){
			
			
			//pre1.getSuccessor().add(this);
			HashSet<Axiomnode> s1 = pre1.getSuccessor();
			s1.add(this);
			pre1.setSuccessor(s1);
			
			
			
			
			HashSet<Axiomnode> pre = new HashSet<Axiomnode>();
			pre.add(pre1);
			this.precursor.add(pre);	
		
		}
	}
	
	/**
	 * This Method links a newly created Axiomnode called "successor" to it's precursors. 
	 * The precursors are the Axiomnodes that were used to derive the successor.
	 * This version of the Method is used in the case that there are exactly two precursors.
	 * @param pre1 a precursor of the newly created Axiom
	 * @param pre2 another precursor of the newly created Axiom
	 * @param o Ontology. Unused for now.
	 */
	public void link(Axiomnode pre1, Axiomnode pre2, Ontology o) {
		
		//blocks reflexivity
		if(!pre1.equals(this)&& !pre2.equals(this)){
		
		
		//set this Axiomnode as successor for all precursors.
		pre1.getSuccessor().add(this);
		/*
		HashSet<Axiomnode> s1 = pre1.getSuccessor();
		s1.add(this);
		pre1.setSuccessor(s1);
		*/
		pre2.getSuccessor().add(this);
		/*
		HashSet<Axiomnode> s2 = pre2.getSuccessor();
		s2.add(this);
		pre2.setSuccessor(s2);
		*/
		
		//gather all precursors into a HashSet<Axiomnode>, and put them as precursor.
		HashSet<Axiomnode> pre = new HashSet<Axiomnode>();
		pre.add(pre1);
		pre.add(pre2);
		this.precursor.add(pre);
		
		}
	}
	
	

	/**
	 * the second run. This time, it checks if all precursors of the deletion candidates have been marked for deletion as well.
	 * This is necessary, because Axioms could prove themselves with circular derivation.
	 * 
	 * @param deletionCandidates
	 * @return boolean true if this Axiomnode is still valid and needs to be kept. False if it can stay on the list.
	 */
	public boolean recheck(HashSet<Axiomnode> deletionCandidates) {
	//public HashSet<Axiomnode> recheck(HashSet<Axiomnode> deletionCandidates) {
	
		
		//the final_flag indicates if ALL sets of precursors are invalid
		//at the end of the precursor loop:
		//true means all precursors are invalid, so this node becomes invalid as well.
		//false means there are still derivations that result in this node.
		boolean final_flag = true;
				
				
				
				
		//check precursors if they're still true
		//p are the precursor sets
		for(HashSet<Axiomnode> p : this.precursor){
			//if invalid_flag is false, then the set p is still valid
			boolean invalid_flag = false;
					
			//a are the individual precursors that make up the precursor sets.
			//if one of them is a deletion candidate, the set becomes invalid.
			for(Axiomnode a : p){
				if(deletionCandidates.contains(a)){
					//if it finds ONE or more invalid Axioms in the set, the set is flagged as invalid
					invalid_flag = true;
				}
			}	
			
			//the invalid_flag is then added upon final_flag with an AND operator
			//that means if ALL precursor sets are flagged, it returns true. 
			//if one is not flagged, then there is still a legitimate derivation to this node, and it can't be deleted.
			final_flag = final_flag && invalid_flag;
						
		}
		if(!final_flag){
			//keep the node, don't delete it.
			//deletionCandidates.remove(this);
			return true;
				
		}
		
		return false;
		
		
		
		
	}

	/**
	 * Traverse the successor structure to mark all affected Axiomnodes as possibly invalid.
	 * Generously mark in the first run.
	 * @param deletionCandidates the Set of Deletion Candidates so far.
	 * @return return the altered bigger set of deletion candidates.
	 */
	public HashSet<Axiomnode> markForDeletion(HashSet<Axiomnode> deletionCandidates){
				
		HashSet<Axiomnode> result = new HashSet<Axiomnode>();
		//mark this node for deletion if it's not a fact
		if(!this.isFact()){
			
			//result.add(this);
			deletionCandidates.add(this);
		
			//then, for all successors
			if(this.getSuccessor()!=null){
				for(Axiomnode s : getSuccessor()){
					//if they are not already marked for deletion, then mark them for deletion.
					if(!deletionCandidates.contains(s)){
						//result.addAll( s.markForDeletion(deletionCandidates));
						deletionCandidates.addAll( s.markForDeletion(deletionCandidates));
					}	
				}
			}
		}
		
		
		result.addAll(deletionCandidates);
		return result;
		
	}

	

	/**
	 * This Method prints this Axiom.
	 * Then it prints all it's successors and the Axioms used to derive them, then does the same for each successor.
	 * 
	 * 
	 * @param i keeps track of the Derivation depth. Every recursive call increments this Variable and adds this amount of arrows before an Axiom.
	 *
	 */
	public void printfollowers(Integer i){
		
		//prints arrows to display derivation depth 
		for (int j=i; j > 0; j--) {
			System.out.print("->");	

		}
		
		//this is not needed it just saves space if the Ontology is too big.
		if(i<25){		
		
			this.printvalue();
			System.out.println();
				
			//call printprecursors of the successors, to print all the successors together with the Derivation used to get them
			if(!getSuccessor().isEmpty()) {	
				HashSet<Axiomnode> s = (HashSet<Axiomnode>) getSuccessor().clone();
				for(Axiomnode nach : s){
	
						for (int k=i; k >= 0; k--) {
								System.out.print("->");	
						}
						nach.printprecursors(this);
				}
			}
				
			//the recursive call for all successors.
			//This is fine because due to the naming convention of the Axioms, there can't be any loops.
			if(!getSuccessor().isEmpty()){	
				HashSet<Axiomnode> s = (HashSet<Axiomnode>) getSuccessor().clone();
				for(Axiomnode nach2 : s){
					nach2.printfollowers(i+1);
				}
			}
		
		}else{
			System.out.println("[...]");
		}
	}


	
	
	/**
	 * This Method prints Axioms to the Console.
	 * It is applied on an Axiom, and prints all the derivations that result in this Axiom and involve the direct precursor.
	 * This lets the user follow the reasoning.
	 * @param directPre the relevant precursor.
	 */
	public void printprecursors(Axiomnode directPre) {
		
		for(HashSet<Axiomnode> pre : this.precursor){
			//boolean notfirst = false;
			if(pre.contains(directPre)){
				boolean notfirst = false;
				for(Axiomnode s : pre){	
					
					//simple check to make the list easier to read. Adds a "*" before any listed Axiom that isn't the first.
					if(notfirst){
						System.out.print(" * ");
					}
					System.out.print("(");
					s.printvalue();
					System.out.print(")");
					notfirst=true;	
				}
			
				System.out.print(" ---> ");
				this.printvalue();
				System.out.println();
			}
		}
	}

	public boolean isFact() {
		return fact;
	}

	public void setFact(boolean fact) {
		this.fact = fact;
	}


	public abstract int hashCode();
	
	public abstract boolean equals(Object obj);

	public boolean isBlocked() {
		return blocked;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

	public HashSet<Axiomnode> getSuccessor() {
		return successor;
	}

	public void setSuccessor(HashSet<Axiomnode> successor) {
		this.successor = successor;
	}

	
	
	
	
	
	
	
}
