package main;


import java.util.HashSet;
import java.util.Random;

import node.Individual;
import node.IndividualAxiom;
import node.Role;
import node.RoleAxiom;
import node.SubsumptionAxiom;
import concepts.All;
import concepts.AtomicConcept;
import concepts.Concept;
import concepts.Disjunction;
import concepts.Exists;
import concepts.Conjunction;
import concepts.Negation;



/**
 * This Class contains a number of Helper Methods that would clutter the main. 
 * These include Methods for generating random Axioms for testing purposes, Math operations, and random Name generators.
 * @author Bjoern_Kreutz
 *
 */
public class utility {

	
	
	
	
	/**
	 * 
	 * @return a boolean that is true or false with 50/50 propability. A simple Coinflip.
	 */
	static Boolean coinflip() {
		Random rnd = new Random();
		return rnd.nextBoolean();
	}
	
	/**
	 * 
	 * @return a boolean that is true or false with 0.1 propability.
	 */
	static Boolean randomZeroPointOne() {
		Random rnd = new Random();
		int r = rnd.nextInt(10);
		if(r==0){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * This Method calculates the Average value over an Array.
	 * 
	 * 
	 * @param runtimes the Array
	 * @return the Average Value
	 */
	static double calculateAverage(double[] runtimes){
		double sum = 0.0;
		for(int i=0;i<runtimes.length; i++){
			sum+=runtimes[i];
		}
		double average = sum/runtimes.length;
		return average;
		
		
	}
	
	/**
	 * this Method calculates the Variance of values in an Array
	 * 
	 * 
	 * @param runtimes the Array with the values
	 * @return the Variance
	 */
	static double calculateVariance(double[] runtimes){
		
		double sum = 0.0;
		for(int i=0;i<runtimes.length; i++){
			sum+=runtimes[i];
		}
		double average = sum/runtimes.length;
		//System.out.println("Average: "+average);
		
		double v = 0.0;
		for(int i=0;i<runtimes.length; i++){
			v+=(runtimes[i]-average)*(runtimes[i]-average);
		}
		v = v/runtimes.length;
		
		return v;
	}
	
	


	
	
	
	
	/**
	 * Generates a new TBoxAxiom by filling a TBoxAxiom with random ALC Concepts.
	 * Then add it to the Ontology.
	 * It also makes sure that the left side concept is always lexically smaller than all the right side axioms This way there can't be any loops.
	 * @param MaxDepth the Maximum nesting Depth of the Used Concepts.
	 * @param o Ontology 
	 */
	public static void generateTBoxAxiom(Integer MaxDepth, Ontology o) {
		Concept c = generateConcept(MaxDepth);
		String con = getLowestConcept(c.findAtoms());
		Concept d = new AtomicConcept(generateConceptNameCapped(2, con));
		SubsumptionAxiom ALC = new SubsumptionAxiom(d, c );
		
		//SubsumptionAxiom ALC = new SubsumptionAxiom(generateConcept(0), generateConcept(MaxDepth) );
		o.addAxiom(ALC);
		o.indivConceptMap.resolveDisjunction(o, ALC);
	}
	
	
	/**
	 * This Method takes a set of Concept names, and returns the lexically smallest, i.e. the first of them in alphabetical order.
	 * This Name Value is needed for the generation of loop-free TBox Axioms.
	 * 
	 * 
	 * @param Atoms a Set of Atomic Concept names.
	 * @return The lexically smallest Concept name
	 */
	static String getLowestConcept(HashSet<String> Atoms) {
		String res = "ZZZZZZ";
		for(String i : Atoms){
			//only check upper case letters, because they're concept names
			//return the smallest one
			if(i.toUpperCase().equals(i) && i.compareTo(res) < 0){
				res = i;
			}
		}
		return res;
	}

	/**
	 * generate a random TBoxAxiom and add it to the ontology the naive way.
	 * 
	 * @param MaxDepth the Maximum nesting Depth of the Used Concepts.
	 */
	public static void generateTBoxAxiomNaive(Integer MaxDepth, Ontology o) {
		
		Concept c = generateConcept(MaxDepth);
		String con = getLowestConcept(c.findAtoms());
		Concept d = new AtomicConcept(generateConceptNameCapped(2, con));
		SubsumptionAxiom ALC = new SubsumptionAxiom(d, c );
		
		
		//SubsumptionAxiom ALC = new SubsumptionAxiom(true, generateConcept(0), generateConcept(MaxDepth));
		o.addAxiomNaive(ALC);
	}
	
	
	
	
	
	/**
	 * Generates a new TBoxAxiom by filling a TBoxAxiom with random EL Concepts.
	 * @param MaxDepth the Maximum nesting Depth of the Used Concepts.
	 */
	public static void generateELTBoxAxiom(Integer MaxDepth, Ontology o) {
		//TBoxAxiom ALC = new TBoxAxiom(generateConcept(0), generateConcept(MaxDepth));
		Concept c = generateConcept(MaxDepth);
		String con = getLowestConcept(c.findAtoms());
		Concept d = new AtomicConcept(generateConceptNameCapped(2, con));
		SubsumptionAxiom EL = new SubsumptionAxiom(d, c );
		//SubsumptionAxiom EL = new SubsumptionAxiom(generateELConcept(0), generateELConcept(MaxDepth));
		o.addAxiom(EL);
	}
	
	
	
	/**
	 * Generates Random combinations of concepts for an ALC TBox Axiom with nesting depth given as an Integer. 
	 * @param MaxDepth the Maximum nesting depth of the generated Axiom. With each recursive call, it is decremented. If it reaches Zero, it always creates an Atomic Concept, terminating the branch.
	 * @return A generated ALC Concept
	 */
	static Concept generateConcept(Integer MaxDepth) {
		Random roll = new Random();
				
		int r = roll.nextInt(6);
		
		if (MaxDepth<=0){ 
			r=0;
		}
		
		//EL is 0-2, AL is 0-4
		switch (r) {
		case 0:
			return new AtomicConcept(generateConceptName(2));
		case 1:
			return new Conjunction(generateConcept(MaxDepth-1), generateConcept(MaxDepth-1));
		case 2:
			return new Exists(new Role(generateRoleName(2)) , generateConcept(MaxDepth-1));
		case 3:
			return new All(new Role(generateRoleName(2)), generateConcept(MaxDepth-1));
		case 4:
			return new Disjunction(generateConcept(MaxDepth-1), generateConcept(MaxDepth-1));
		case 5:
			return new Negation(generateConcept(MaxDepth-1));
		default:
			break;
		}
		
		return null;
		
	}
	
	/**
	 * Generates Random combinations of concepts for an EL TBox Axiom with nesting depth given as an Integer
	 * @param MaxDepth the Maximum nesting depth of the generated Axiom. With each recursive call, it is decremented. If it reaches Zero, it always creates an Atomic Concept, terminating the branch.
	 * @return a generated EL Concept
	 */
	@SuppressWarnings("unused")
	private static Concept generateELConcept(Integer MaxDepth) {
		Random roll = new Random();		
		int r = roll.nextInt(3);

		if (MaxDepth<=1){ 
			r=0;
			}
		switch (r) {
		case 0:
			return new AtomicConcept(generateConceptName(2));
		case 1:
			return new Conjunction(generateELConcept(MaxDepth-1), generateELConcept(MaxDepth-1));
		case 2:
			return new Exists(new Role(generateRoleName(2)) , generateELConcept(MaxDepth-1));
		default:
			break;
		}
		
		return null;
		
	}




	/**
	 * Generates a random Role and two random Individuals.
	 * For testing purposes.
	 * @return 
	 * @return a random RoleAxiom
	 */
	static void generateRoleAxiom(Ontology o) {
		RoleAxiom r = new RoleAxiom(new Role(generateRoleName(2)), new Individual(generateIndivName(2)), new Individual (generateIndivName(2)));
		o.addAxiom(r);

	}
	
	/**
	 * Generates a random Role and two random Individuals.
	 * For testing purposes.
	 * @return 
	 * @return a random RoleAxiom
	 */
	static void generateRoleAxiomNaive(Ontology o) {
		RoleAxiom r = new RoleAxiom(new Role(generateRoleName(2)), new Individual(generateIndivName(2)), new Individual (generateIndivName(2)));
		o.addAxiomNaive(r);
	}
	

	
	
	
	
	
	/**
	 * Creates random Concept names with the length given
	 * @param length the Length of the generated Concept name
	 */
	private static String generateConceptName(Integer length) {
	    final String uCase = "BCDEFGHIJKLMNOPQRSTUVWXYZ";
	    String name = "";
	    Random r = new Random();
	    while (name.length()<length){
            int spot = r.nextInt (25);
            name += uCase.charAt(spot);
	    }
		return name;
	}
	
	/**
	 * Creates random Concept names with the length given, AND lexically lower than the String max
	 * e.g. a parameter max of DF would let this Method randomly generate a Concept name between AA and DE.
	 * @param length the Length of the generated Concept name
	 * @param max the lexically highest possible String to generate.
	 */
	static String generateConceptNameCapped(Integer length, String max) {
	    final String uCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	    String uCasenew = null;
	    int index = 25;
	   
	    for(int i = 0; i <= uCase.length()-1; i++){
	    	if(max.compareTo(uCase.substring(i, i+1)) > 0 ){
	    		//uCasenew = uCase.substring(0, i);
	    		index = i;
	    	}
	    	
	    }
	    
	    String name = "";
	    Random r = new Random();
	    int spot = r.nextInt (index+1);
	   // int spot = r.nextInt (uCasenew.length());
        name += uCase.charAt(spot);
	    while (name.length()<length){
	    	
            spot = r.nextInt (uCase.length());
            name += uCase.charAt(spot);
            if(name.compareTo(max) >= 0){
            	name = name.substring(0, name.length()-1);
            }
	    }
		return name;
	}
	
	
	
	
	/**
	 * This Method generates a Name with the standard convention of Individual names.
	 * 
	 * 
	 * @param length
	 * @return a new Rolename
	 */
	public static String generateIndivName(Integer length) {
	    final String dCase = "abcdefghijklmnopqxyz";
	    String name = "";
	    Random r = new Random();
	    while (name.length()<length){
            int spot = r.nextInt (20);
            name += dCase.charAt(spot);
	    }
		return name;
	}

	
	static String generateRoleName(Integer length) {
	    final String dCase = "rstuvw";
	    String name = "";
	    Random r = new Random();
	    while (name.length()<length){
            int spot = r.nextInt (6);
            name += dCase.charAt(spot);
	    }
		return name;
	}


	/**
	 * This Method generates a random Individualaxiom with a random Atomic Concept, then adds it to the Ontology incrementally and resolves all disjunctions that might be affected.
	 * 
	 * 
	 * @param o the Ontology this Axiom is added to.
	 */
	public static void generateIndivAx(Ontology o) {
		
		String name = generateIndivName(2);
		IndividualAxiom i = new IndividualAxiom(new Individual(name),(AtomicConcept) generateConcept(0));
		o.addAxiom(i);
		o.indivConceptMap.resolveDisjunction(o, i);

	}
	
	/**
	 * This Method is the Naive version of the Individual Generator.
	 * It creates an Axiom in the same way, but calls the naive Add Method.
	 * 
	 * 
	 * @param o
	 */
	public static void generateIndivAxNaive(Ontology o) {
		
		String name = generateIndivName(2);
		IndividualAxiom i = new IndividualAxiom(new Individual(name),(AtomicConcept) generateConcept(0));
		o.addAxiomNaive(i);
	}

	
	
	
	
}
