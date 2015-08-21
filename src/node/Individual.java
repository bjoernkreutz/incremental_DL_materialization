package node;

import main.utility;

public class Individual {

	String name;
	
	
	public Individual(String name) {
		super();
		this.name = name;
	}
	
	public Individual() {
		super();
		this.name = utility.generateIndivName(1);
	}
	
	
	
	public void printvalue() {
		// TODO Auto-generated method stub
		System.out.print(name);
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Individual other = (Individual) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	

}
