package main;

import node.Axiomnode;
import node.Individual;
import node.IndividualAxiom;
import node.Role;
import node.SubsumptionAxiom;
import concepts.All;
import concepts.AtomicConcept;
import concepts.Concept;
import concepts.Conjunction;
import concepts.Disjunction;
import concepts.Exists;
import concepts.Negation;

/**
 * This Class contains all the Text Parser Methods. It processes all text input by the user and transforms it into actions and Axioms respectively.
 * 
 * @author Bjoern_Kreutz
 *
 */
public class Parser {
	
	static Boolean naive = false;
	
	
	static void parse(String input, Ontology o){
		
		Axiomnode c = null;


		//+Axiom
		if(input.substring(0, 1).equals("+")){
			c = parseAxiom(input.substring(1),o);
			System.out.println();
			if(naive){	
				o.addAxiomNaive(c);
				o.indivConceptMap.resolveDisjunction(o);
				c.printfollowers(0);
			}
			else{
				Axiomnode d = o.addAxiom(c);
				o.indivConceptMap.resolveDisjunction(o, c);
				d.printfollowers(0);
			}
			
			
			 
		//-Axiom		
		}else if(input.substring(0, 1).equals("-")){
			
			//parse the Axiom
			c = parseAxiom(input.substring(1),o);
			Axiomnode d = null;
			if(o.truthSet.contains(c)){
				//check the ontology for the original
				for(Axiomnode n : o.truthSet){
					if(n.equals(c)){
						d = n;
					}
				}
				assert(d!=null);
				if(naive)
					o.removeAxiomNaive(	d);
				else
					o.removeAxiom(		d);
			}else System.out.println("Axiom not Found!");
			
		//input "print"
		}else if( input.length()>=5 &&  input.substring(0,5).equals("print")){
			
			long runtimeprint = System.currentTimeMillis();

			System.out.println("Printing full Matrialization...");
			
			for(Axiomnode axiom : o){	
				if(axiom.isFact()){ 	
					axiom.printfollowers(0); 
				}
			}
			System.out.println("Done.");
			long runtimeprint2 = System.currentTimeMillis()-runtimeprint;
			System.out.println("time for printing was: "+runtimeprint2+"ms");
			
			
		//input "naive" toggles incremental and naive reasoning.	
		}else if( input.length()>=5 &&  input.substring(0,5).equals("naive")){
			
			naive^=true;
			System.out.println("Naive Reasoning Mode is now:"+naive);
			
			
		}else
		
		
		//the default input, it checks if the Axiom is in the Ontology. If yes, it applies the reasoner to it again, and then prints it's followers
		{
			c = parseAxiom(input,o);
			Axiomnode d = null;

			for(Axiomnode n : o){
				if(n.equals(c)){
					d = n;
				}
			}
			System.out.println();

			if(d!=null){
				d.applyTableau(o);
				d.printfollowers(0);
			}else{
				System.out.println("Axiom not found!");
				
				
			}
				
		}
		
		
		
		
	}
	
	
	static Axiomnode parseAxiom(String input, Ontology o){
		if(input.contains("<=")){
			return parseTBoxAxiom(input, o);
			
		}else if(input.contains(":")){
			return parseABoxAxiom(input, o);

			
			
		}
		return null;

		
		
	}
	
	
	
	/**
	 * Read a SubsumptionAxiom
	 * 
	 * 
	 * @param input String in the Form c1 <= c2
	 * @param o Ontology
	 */
	static SubsumptionAxiom parseTBoxAxiom(String input, Ontology o){
		String[] arr = input.split(" ");
		assert(arr[1].equals("<=")):"Parse Error";
		Concept c1 = parseConcept(arr[0]);
		Concept c2 = parseConcept(arr[2]);
		if(((AtomicConcept) c1).value.compareTo(utility.getLowestConcept(c2.findAtoms())) >= 0){
			System.out.println("Warning: Invalid Concept names. Consistency not Guarantueed.");
		}
		
		SubsumptionAxiom c = new SubsumptionAxiom( c1, c2 );
		System.out.print("read SubsumptionAxiom: ");
		c.printvalue();
		System.out.println();
		return c;
		
			
			
		
		//return false;
	}
	
	/**
	 * 
	 * 
	 * 
	 * @param input String in the form a:C
	 * @param o the Ontology
	 * @return 
	 */
	static IndividualAxiom parseABoxAxiom(String input, Ontology o){
		String[] arr = input.split(":");
		
		Concept c1 = parseConcept(arr[1]);
		IndividualAxiom c = new IndividualAxiom( new Individual(arr[0]),c1);
		System.out.print("Read IndividualAxiom: ");
		c.printvalue();
		System.out.println();
		return c;
		
	}
	
	
	/**
	 * this method takes a concept String in the shape of (____&____) or (____|____), and returns the position of the most top level logic operator.
	 * 
	 * 
	 * @param logic
	 * @return the index of the topmost logic operator.
	 */
	static int locateSplitLogicString(String logic){
		Integer brackets= 0;
		for (int i = 0; i < logic.length(); i++) {
			if(logic.substring(i,i+1).equals("(")){ brackets++;}
			if(logic.substring(i,i+1).equals(")")){ brackets--;}
			if((logic.substring(i,i+1).equals("&") || logic.substring(i,i+1).equals("|")) && brackets==1){ 
				//System.out.println(i);
				return i;
			};

			
		}
		
		
		return 1;
		
		
	}

	
	/**
	 * This method takes a String of a Nested Concept and return it as a compiled concepts.Concept
	 * 
	 * 
	 * 
	 * @param input input String of the Concept
	 * @return Concept c that is represented by the string
	 */
	static Concept parseConcept(String input){
		
		
		
		//TODO Parse an input string into a Concept
		
		
		String s = input;
		/*
		if (input.startsWith("(") && input.endsWith(")") ){
			s = input.substring(1, input.length()-1);
		}
		*/
		String next=null;
		if(s.length()>1){ 
			next = s.substring(0,1);
			} else {next = s;}
		switch (next){
			// !___
			case "!":
				//return a negation of that concept, parse recursively.
				return new Negation( parseConcept(s.substring(1, s.length())));
			
			
			// (___&___) or (___|___)
			case "(":
				
				if(s.contains("&") || s.contains("|")){
			
					Integer split = locateSplitLogicString(s);
					Concept c1 = parseConcept( s.substring(1,	split));
					Concept c2 = parseConcept( s.substring(split+1, s.length()-1));
					if(s.substring(split,split+1).equals("|")){
						return new Disjunction(c1,c2);
					}else
						return new Conjunction(c1,c2);
				}
				//(____)
				else {	
					//Atom. Remove the brackets.
					do{ 
						s = s.substring(1, s.length()-1);
					} while (s.startsWith("(") && s.endsWith(")"));
						
					
					//System.out.println("New Atom "+s);
					return new AtomicConcept(s);
				}
				
			//€r_____	
			case "€":
				s= s.substring(1);
				String roleEx = s.split("\\.")[0];
				String cEx = s.split("\\.")[1];
				//return new Exists( new Role(s.substring(1, 2))   , parseConcept(s.substring(2, s.length())));
				return new Exists( new Role(roleEx)   , parseConcept(cEx));
				
			//$r_____
			case "$":
				s= s.substring(1);
				String roleAll = s.split("\\.")[0];
				String cAll = s.split("\\.")[1];
				//return new All( new Role(s.substring(1, 2))   , parseConcept(s.substring(2, s.length())));
				return new All( new Role(roleAll)   , parseConcept(cAll));
					
			
			default:
				//System.out.println("New Atom "+s);
				return new AtomicConcept(s);
				
				
		}
		
		
		

		
	}

}
