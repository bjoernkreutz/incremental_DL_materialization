package node;

import java.util.HashSet;

import concepts.All;
import main.Ontology;
import main.main;

public class RoleAxiom extends ABoxAxiom {
	

	private Role role;
	private Individual left;
	private Individual right;
	
	

	public RoleAxiom(Role role, Individual left, Individual right) {
		super();
		this.role = role;
		this.left = left;
		this.right = right;
		this.setFact(true);
		//axiomSet.truthSet.add(this);
		this.setBlocked(false);

	}
	
	public RoleAxiom(Boolean fact, Role role, Individual left, Individual right) {
		super();
		this.role = role;
		this.left = left;
		this.right = right;
		this.setFact(fact);
		this.setBlocked(false);

	}


	public void printvalue() {
			System.out.print("(");	
			left.printvalue();
			System.out.print(",");
			right.printvalue();
			System.out.print("):"+role.getName());
			//System.out.println();

	}




	/**
	 * 
	 * @see node.Axiomnode#createIndex()
	 */
	@Override
	public void createIndex(Ontology o) {
		o.add(this);
		if(this.isFact()){
			o.truthSet.add(this);
		}

		o.roleIndexMap.add( role.getName() , this );
	}


	@Override
	public void applyTableau(Ontology o) {
		
		//get all Axioms that mention the left individual and the role, and apply the Tableau to them.
		HashSet<Axiomnode> ind = o.indivIndexMap.get(this.getLeft());
		HashSet<Axiomnode> role = o.indivIndexMap.get(this.getRole());
		if(ind!=null && role!=null){
			ind.retainAll(role);
			for(Axiomnode n : ind){
				n.applyTableau(o);
				
				
			}
		}
	}


	public Individual getLeft() {
		return left;
	}


	public void setLeft(Individual left) {
		this.left = left;
	}


	public Individual getRight() {
		return right;
	}


	public void setRight(Individual right) {
		this.right = right;
	}


	@Override
	public HashSet<String> findAtoms() {
		HashSet<String> m = new HashSet<String>();
		m.add(this.role.getName());
		m.add(this.left.name);
		m.add(this.right.name);
		return m;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
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
		RoleAxiom other = (RoleAxiom) obj;
		if (left == null) {
			if (other.left != null)
				return false;
		} else if (!left.equals(other.left))
			return false;
		if (right == null) {
			if (other.right != null)
				return false;
		} else if (!right.equals(other.right))
			return false;
		if (role == null) {
			if (other.role != null)
				return false;
		} else if (!role.equals(other.role))
			return false;
		
		//added, to synch all Precursors.
		/*
		this.precursor.addAll(other.precursor);
		other.precursor.addAll(this.precursor);
		this.successor.addAll(other.successor);
		other.successor.addAll(this.successor);
		*/
		return true;
	}




	
	
	

}
