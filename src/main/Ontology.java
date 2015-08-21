package main;

import java.util.HashSet;

import concepts.AtomicConcept;
import concepts.Concept;
import node.Axiomnode;
import node.Individual;
import node.IndividualAxiom;
import node.Role;
import node.RoleAxiom;
import node.SubsumptionAxiom;

/**
 * The Ontology Class manages all sets and maps required to store the Facts and Materialization.
 * It extends HashSet<Axiomnode> which is used to store the Axiomnodes
 * 
 * @author Bjoern_Kreutz
 *
 */
public class Ontology extends HashSet<Axiomnode> {
	
	
	/**
	 * This HashSet contains all factual Axioms, in case the materialization needs to be flushed.
	 */
	public HashSet<Axiomnode> truthSet = new HashSet<Axiomnode>();

	
	/**
	 * This Map is an Index about the Concepts that appear in Axioms.
	 * It contains all TBox Axioms sorted by the Atomic Concepts that occur in them.
	 *
	 */
	public IndexMap conceptIndexMap = new IndexMap();
	
	/**
	 * A Map that contains all Individuals sorted by Literals for easier access.
	 */
	public IndexMap indivIndexMap = new IndexMap();
	
	/**
	 * A Map that contains all usages of the respective Roles for easier access.
	 */
	public IndexMap roleIndexMap = new IndexMap();
	
	
	
	
	/**
	 * The idea behind this indivConceptMap is to list all Concepts for each Individual.
	 * This should help with the implementation of Disjunktion and Not(), as it requires non-determinism and fact checks to make decisions.
	 */
	public IndividualMap indivConceptMap = new IndividualMap();
	
	
	
	/**
	 * This Method fills the Ontology with a set amount of Axioms, then derives the Materialization.
	 * This is needed for testing purposes, to generate large enough Materializations to test on.
	 * 
	 * 
	 * 
	 * @param T Amount of TBox Axioms
	 * @param I	Amount of Individual Axioms
	 * @param R	Amount of Role Axioms
	 */
	public void fillOntology(Integer T, Integer I, Integer R){
		
		System.out.println("Filling Ontology:");
		
		System.out.println("TBox:");
		for(int t=0; t<T; t++){
			
			System.out.println(((double)t/(double)T)*100);
			
			Concept c = utility.generateConcept(4);
			String con = utility.getLowestConcept(c.findAtoms());
			Concept d = new AtomicConcept(utility.generateConceptNameCapped(2, con));
			SubsumptionAxiom ALC = new SubsumptionAxiom(d, c );
			truthSet.add(ALC);
			
		}
		
		System.out.println("ABox:");
		for(int i=0; i<I; i++){
			
			System.out.println(((double)i/(double)I)*100);

			String name = utility.generateIndivName(2);
			truthSet.add( new IndividualAxiom(new Individual(name),(AtomicConcept) utility.generateConcept(0)));
			
		}
		
		for(int r=0; r<R; r++){
			
			System.out.println(((double)r/(double)R)*100);

			truthSet.add( new RoleAxiom(new Role(utility.generateRoleName(2)), 
										new Individual(utility.generateIndivName(2)), 
										new Individual (utility.generateIndivName(2))));
			

			
		}
		
		this.deriveMaterialization(true);
		
		
	}
	
	
	
	
	
	/**
	 * this method flushes all the derived knowledge in this Ontology, and is used as part of the naive Algorithm.
	 * Only the truth Set remains, which contains only facts.
	 */
	public void flushMaterialization(){
		
		HashSet<Axiomnode> c = (HashSet) this.clone();
		
		//destroy all Axioms that are not fact
		c.removeAll(truthSet);
		destroy(c);
		
		//clear all indexes
		conceptIndexMap.clear();
		indivIndexMap.clear();
		roleIndexMap.clear();
		indivConceptMap.clear();
		
		//clear all references from the remaining facts
		for(Axiomnode d : this){
			d.setSuccessor( new HashSet<Axiomnode>());
			d.setPrecursor(new HashSet<HashSet<Axiomnode>>());
			
		}
		
		//remove the facts from the ontology so that only the truthSet remains.
		this.clear();
				
		
		
	}
	
	
	/**
	 * This method adds a given Axiom to the Ontology.
	 * 
	 * 
	 * 
	 * @param a the Axiom that is to be added
	 * @return the Axiomnode that was added, so it can be linked properly. If it's a new Axiomnode: return the parameter a. if it's not new: return the original instance of this Axiom. Otherwise the precursor and successor could point at different instances of the same Axiom.
	 */
	public Axiomnode addAxiom(Axiomnode a){
		
		//if an Axiom equivalent to a is already in the ontology
		if(this.contains(a)){
			
			//then find the original c
			Axiomnode c = null;
			for(Axiomnode n : this){
				if(n.equals(a)){
					c = n;
				}
			}
			
			//apply all pre and succ to the existing c
			//not necessary since we 
			/*
			HashSet<Axiomnode> succ_a = a.getSuccessor();
			HashSet<Axiomnode> succ_c = c.getSuccessor();
			succ_c.addAll(succ_a);
			c.setSuccessor(succ_c);
			
			HashSet<HashSet<Axiomnode>> prec_a = a.getPrecursor();
			HashSet<HashSet<Axiomnode>> prec_c = c.getPrecursor();
			prec_c.addAll(prec_a);
			c.setPrecursor(prec_c);
			*/
			c.setFact(c.isFact() || a.isFact());
			//c.setBlocked(true);
			
			//c.applyTableau(this);
			return c;
			
			
		}else{
			
			a.createIndex(this);
			a.applyTableau(this);
			//return true;
			return a;
		}		
	}
	
	/**
	 * The naive way of adding an Axiom to the Ontology. 
	 * It flushes the Materialization, adds the Axiomnode to the truth set, and then derives a new Materialization from the truthSet.
	 * 
	 * 
	 * @param a the Axiomnode that is added.
	 * @return true 
	 */
	public boolean addAxiomNaive(Axiomnode a){
		
		
		this.flushMaterialization();
		
		this.truthSet.add(a);
		
		this.deriveMaterialization(false);
		
		return true;

	}
	
	
	
	/**
	 * The naive way of removing an Axiom from the Ontology. 
	 * It flushes the Materialization, removes the Axiomnode to the truth set, and then derives a new Materialization from the truthSet.
	 * 
	 * 
	 * @param a the Axiomnode that is removed.
	 * @return
	 */
	public void removeAxiomNaive(Axiomnode a){
		
		
		this.flushMaterialization();
		
		this.truthSet.remove(a);
		
		this.deriveMaterialization(false);
		
		

	}
	
	

	/**
	 * <p>This Method starts the process of deleting this Axiomnode and all it's successors from the Materialization. 
	 * This will not delete anything that is supported by another node.</p>
	 * 
	 * if fact, set fact=false.
	 * get all it's precursors and save them for later.
	 * create a new set with deletion candidates that is filled with the initial node.
	 * move to the successor.
	 * if there are unmarked precursor sets in this node, don't mark the node for deletion and end.
	 * once it has marked all possible successors, check the stored precursors. 
	 * If they haven't been marked for deletion, keep everything, but keep the initial node as not-fact.
	 * If they have been marked for deletion, or don't exist, then destroy every deletion candidate.
	 * 
	 * 
	 */
	public void removeAxiom(Axiomnode a){
		
		assert(this.contains(a));
		
		a.setFact(false);
		
		
		 //this set is used to mark Nodes for deletion.
		HashSet<Axiomnode> deletionCandidates = new HashSet<Axiomnode>();
		//HashSet<Axiomnode> deletionCandidates2 = new HashSet<Axiomnode>();

		
		HashSet<HashSet<Axiomnode>> storedPrecursors = a.getPrecursor();
		
		
		//mark this Node for deletion
		deletionCandidates.add(a);
		
		
		//System.out.println(a.getSuccessor());
		
		//check all successors for Deletion
		for(Axiomnode s : a.getSuccessor()){
			deletionCandidates.addAll(s.markForDeletion(deletionCandidates));	
		}
		
		
		
		
		
		//check stored precursors if they're still valid
		//the final_flag indicates if ALL sets of precursors are invalid
		//at the end of the precursor loop:
		//true means all precursors are invalid, so this node becomes invalid as well.
		//false means there are still derivations that result in this node.
		boolean final_flag = true;
		
		if(!storedPrecursors.isEmpty()){
			for(HashSet<Axiomnode> p : storedPrecursors){
				//if invalidflag is false, then the set p is still valid
				boolean invalid_flag = false;
				
				//a are the individual precursors that make up the precursor sets.
				//if one of them is a deletion candidate, the set becomes invalid.
				for(Axiomnode n : p){
					if(deletionCandidates.contains(n)){
						//if it finds ONE or more invalid Axioms in the set, the set is flagged as invalid
						invalid_flag = true;
					}
				}	
				
				//the invalid_flag is the added upon final_flag with an AND operator
				//that means if all precursor sets are flagged, it returns true. 
				//if one is not flagged, then there is still a legitimate derivation to this node, and it can't be deleted.
				final_flag = final_flag && invalid_flag;	
			}
		
		
		}
		//If at this point the final_flag==false, then there is still a derivation to this initial node
		//this means even though it's not a fact anymore, it can stil be derived, stays legit, and all deletion candidates remain as well.
		//if final_flag==true, then destroy all deletion candidates
		
		if(final_flag){
			
			//recheck all successor's precursors.
			for(Axiomnode s : a.getSuccessor()){
				deletionCandidates.retainAll(recheck(deletionCandidates));	
			}
			
			
			//destroy deletion candidates
			destroy(deletionCandidates);
			
			
			
		}
	}
	
	
	/**
	 * Rechecks all Deletion Candidates.
	 * 
	 * 
	 * 
	 * @param deletionCandidates the initial overestimation of Axioms that need to be deleted.
	 * @return the Set of Axioms that after the second check aren't supported by facts anymore.
	 */
	public HashSet<Axiomnode> recheck(HashSet<Axiomnode> deletionCandidates) {
		
		HashSet<Axiomnode> finaldeletionCandidates = (HashSet<Axiomnode>) deletionCandidates.clone();
		boolean changeflag = false;
		
		do{
			changeflag = false;
			for(Axiomnode a : deletionCandidates){
				if(a.recheck(deletionCandidates)){
					finaldeletionCandidates.remove(a);
					changeflag = true;
				}
			}
			deletionCandidates.retainAll(finaldeletionCandidates);
		}while(changeflag);
		
		
		
		
		return finaldeletionCandidates;

	}

		

	
	
	
	

	
	
	
	/**
	 * This Method is used to destroy a Set of Axiomnodes that are no longer valid.
	 * Destroy means they're removed for good.
	 * 
	 * @param derivedKnowledge The list of Axiomnodes that needs to be destroyed
	 */
	private void destroy(HashSet<Axiomnode> derivedKnowledge) {
		
		//perform the deletion for all Axioms in the deletion Set.
		//a is to be destroyed
		for(Axiomnode a : derivedKnowledge){
			/*
			 * checklist:
			 * 1.remove from the Ontology and the truthSet
			 * 2.in all it's unmarked precursors, remove itself from the successor list.
			 * 3. in all it's unmarked successors, remove any precursor set that involves them. AND in all those precursors, then remove the associated successor relation.
			 * 
			 * 4. IndivConceptMap: if Axiomnode a is IndivAx with x:X, X is an atomic concept, then remove X from the bucket x in indivConceptMap
			 * 
			 * 
			 */
			
			//1.
			this.remove(a);
			this.truthSet.remove(a);
			
			//2. 
			//traverse every single precursor
			for(HashSet<Axiomnode> p : a.getPrecursor()){
				for(Axiomnode q : p){
					//if unmarked
					if(!derivedKnowledge.contains(q)){
						//remove a from the successor list
						HashSet<Axiomnode> s1 = q.getSuccessor();
						s1.remove(a);
						q.setSuccessor(s1);
					}
				}
			}

			//3.
			//clone set so I can iterate
			HashSet<Axiomnode> iter = (HashSet<Axiomnode>) a.getSuccessor().clone();
			for(Axiomnode s : iter){
				//if the successor node is not marked for destruction anyways, then:
				if(!derivedKnowledge.contains(s)){
					//traverse precursor sets of the successor s
					//clone the precursor set s.precursor first, so I can edit it while I traverse it's original form.
					HashSet<HashSet<Axiomnode>> p2 = (HashSet<HashSet<Axiomnode>>) s.getPrecursor().clone();
					for(HashSet<Axiomnode> p : p2){
						if(p.contains(a)){
							HashSet<HashSet<Axiomnode>> s0 = s.getPrecursor();
							s0.remove(p);
							s.setPrecursor(s0);
							//s.getPrecursor().remove(p);
							//remove all successor relations to this s in all the precursors p
							for(Axiomnode n : p){
								HashSet<Axiomnode> s1 = n.getSuccessor();
								s1.remove(s);
								n.setSuccessor(s1);
								//n.getSuccessor().remove(s);
							}
						}	
					}
				}		
			}

			
			//4.
			//as in an Individual axiom
			if(a instanceof IndividualAxiom){
				//a has an atomic concept
				if( ((IndividualAxiom) a).getConcept() instanceof AtomicConcept){
					//assert that the mapping in indivConceptMap is not null
					if(this.indivConceptMap.get(((IndividualAxiom) a).getIndiv())!=null){
						//remove a's concept from the mapping
						this.indivConceptMap.get(((IndividualAxiom) a).getIndiv()).remove(((IndividualAxiom) a).getConcept());
					}
				}
			}
			
			a.setPrecursor( new HashSet<HashSet<Axiomnode>>());
			a.setSuccessor( new HashSet<Axiomnode>());
			
			
			
			//5.
			this.conceptIndexMap.removeAxiomnode(a);
			this.indivIndexMap.removeAxiomnode(a);
			this.roleIndexMap.removeAxiomnode(a);
			this.indivConceptMap.disjunctionSet.remove(a);
			
			
			
			
		}
	}
	
	
	/**
	 * this method derives the Materialization on this Ontology
	 * 
	 * @param print a flag that indicates whether the progress percentage should be printed in the console. It's handy on the initial generation, but not for the naive Algorithm.
	 */
	public void deriveMaterialization(boolean print){
		
		HashSet<Axiomnode> iter = (HashSet<Axiomnode>) this.truthSet.clone();
		Integer i = 1;
		for(Axiomnode c : iter){

			this.addAxiom(c);
			if(print){
				float p = ((float)i/(float)iter.size())*100;
				System.out.println("%"+p+"     \t ");
				i++;
			}
			
		}
		
		//attempt to resolve all disjunctions.
		this.indivConceptMap.resolveDisjunction(this);

	}
	
	
	

}
