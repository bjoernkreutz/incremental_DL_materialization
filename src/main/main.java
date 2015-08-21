package main;



import java.util.Random;
import java.util.Scanner;

import node.Axiomnode;


/**
 * The main class. 
 * @author Bjoern_Kreutz
 *
 */
public class main {
	
	
	/**
	 * This Ontology contains all Axioms, it makes sure they're syntactically unique.
	 */
	public static Ontology axiomSet = new Ontology();




	public static void main(String[] args) {
		
		

		
		
		Scanner scan = new Scanner(System.in);
		
		// Tracks the running time
		long runtime1 = System.currentTimeMillis();
		
		boolean naive = false;
		
		System.out.println("Enter number of facts to generate:");
		String ax = scan.nextLine();
	
		//the amount of loops to calculate on the Ontology
		//for runtime analysis urposes
		int loops = 000;
		
		//the initial amount of Axioms in the Ontology
		//int axioms = 12000;
		int axioms = Integer.parseInt(ax);;
		
		double amountAxioms[] = new double[loops];
		double runtimesAdd[] = new double[loops];
		double runtimesRemove[] = new double[loops];
		//double totalruntimes[] = new double[loops];
		
		
		//long total = 0;
		//double xSquare = 0;
		
		
		int t = (int) (axioms*0.1);
		int indiv = (int) (axioms*0.45);
		int role = (int) (axioms*0.45);
		
		
		axiomSet.fillOntology(t, indiv, role);
		
		//adds and removes random Axioms
		for (int i = 0; i<loops; i++) {

			
			
//add---------------------------------------------------------------------
			//tracks the running time of the generator.
			long runtimegenerate = System.nanoTime();

			
			if(naive){
				if(utility.randomZeroPointOne())			{	
					utility.generateTBoxAxiomNaive(4,axiomSet);
				}else
					if(utility.coinflip()){
						utility.generateIndivAxNaive(axiomSet);
					}else{
						utility.generateRoleAxiomNaive(axiomSet);
					}
			}else{
				if(utility.randomZeroPointOne())			{	
					utility.generateTBoxAxiom(4,axiomSet);
				}else
					if(utility.coinflip()){
						utility.generateIndivAx(axiomSet);
					}else{
						utility.generateRoleAxiom(axiomSet);
					}
				

			}
			
			
			
			
			
			double runtimeAdd = (System.nanoTime()-runtimegenerate)/1000000.0;
			runtimesAdd[i] = runtimeAdd;

			
			
//add^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
			
			
	
//remove-----------------------------------------------------------------------------------------------------------
			//take time
			long runtimegenerate2 = System.nanoTime();
			
		
			//get a random spot in the truth set
			Random rnd = new Random();
			int r = rnd.nextInt(axiomSet.truthSet.size());
			int j = 0;


			//the break is used because it's the most efficient way to get a random Axiomnode out of the Set.
			if(naive){
				for(Axiomnode a : axiomSet.truthSet){
					if(j>=r){
						axiomSet.removeAxiomNaive(a);
						break;
					}
					j++;	
				}
			}else{
				for(Axiomnode a : axiomSet.truthSet){
					if(j>=r){
						axiomSet.removeAxiom(a);
						break;
					}
					j++;	
				}
			
			}

			//take time, part two
			double runtimeremove = (System.nanoTime()-runtimegenerate2)/1000000.0;
			runtimesRemove[i] = runtimeremove;
				
//remove^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
			
		
			
			
			
			//track amount of axioms
			amountAxioms[i] = axiomSet.size();
			
			//a nice output that tracks the amount of axioms, generation progress, and runtimes.
			/*
			System.out.print(axiomSet.truthSet.size());
			System.out.println(+" Facts \t");
					
			System.out.print(axiomSet.size());
			System.out.print(" Axioms \t");
			*/

			float p = ((float)i/(float)loops)*100;
			System.out.print("%"+p+"     \t ");
			
			
			
			//xSquare+=runtimeAdd*runtimeAdd;
			//System.out.print("  "+runtimegenerate2);
			//System.out.print(+"ms   ");
			
			//System.out.print(" "+runtimegenerate2/axiomSet.truthSet.size());
			//System.out.print(" ");
			//total+=runtimegenerate2;
			//totalruntimes[i]=total;
			//double e = (double)total/(double)axiomSet.truthSet.size();
			//System.out.print("  "+e);
			
			//System.out.print("  "+axiomSet.indivConceptMap.disjunctionSet.size()+"  ");

			
			/*
			//double eOfXSquare = xSquare/axiomSet.truthSet.size();
			double Q = xSquare - (e*e)/axiomSet.truthSet.size();
			//System.out.print(" Q is:" +Q);
			double v = (1.0/(axiomSet.truthSet.size()-1.0))*Q;
			System.out.print("  "+v);
			double st_dev = Math.sqrt(v);
			System.out.print("  "+st_dev);
			*/

			//System.out.print("\t \t"+total+"ms");
			System.out.println();
		
		
		}
		
		//generate averages and variance
		double avAx = utility.calculateAverage(amountAxioms);
		double avAdd = utility.calculateAverage(runtimesAdd);
		double avRem = utility.calculateAverage(runtimesRemove);
		double vAdd = utility.calculateVariance(runtimesAdd);
		double vRem = utility.calculateVariance(runtimesRemove);
		double stDevAdd = Math.sqrt(vAdd);
		double stDevRem = Math.sqrt(vRem);
		
		
		//output
		System.out.println("Facts \t Axioms\t Add\t Remove");
		for (int i = 1; i<loops; i++) {
			System.out.print(axioms);
			//System.out.print(i+axioms);
			System.out.print("\t ");
			System.out.print(amountAxioms[i]);
			System.out.print("\t ");
			System.out.print(runtimesAdd[i]);
			System.out.print("\t ");
			System.out.print(runtimesRemove[i]);
			System.out.print("\t ");
			System.out.println();
			
		}
		
		System.out.println("Average Axioms: "+ avAx);
		System.out.println("Average for Add: "+ avAdd);
		System.out.println("Average for Rem: "+ avRem);
		//System.out.println("Variance for Add: "+ vAdd);
		//System.out.println("Variance for Rem: "+ vRem);
		System.out.println("StDev for Add: "+ stDevAdd);
		System.out.println("StDev for Rem: "+ stDevRem);

		
		//an attempt at putting out the data into a .dat file.
		//OutputStream out = new FileOutputStream("C:\\Eigene Dateien\\Uni\\Bachelor Arbeit\\test.dat");

				/*
				DataOutputStream out = null;
				try {
					out = new DataOutputStream(new BufferedOutputStream(
					          new FileOutputStream("C:\\Eigene Dateien\\Uni\\Bachelor Arbeit\\test.dat")));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 */
		
				
		
		long runtime = System.currentTimeMillis() - runtime1;
		System.out.println("Total runtime was: "+runtime+"ms");
		
		
		
		
		
		
		
//Bonus Round Test-----------------------------------------------------------------------------------------------------
		
		for (int i = 0; i < 20; i++) {
			
		System.out.println();
		System.out.println("'+' and an Axiom, to add");
		System.out.println("'-' and an Axiom, to remove");
		System.out.println("'print', to print the materialization");
		System.out.println("'naive', to toggle the naive reasoning mode");

		System.out.println("Enter Command:");
		String input = scan.nextLine();
		
		long runtimeadd = System.currentTimeMillis();

		
		
		System.out.println("----------------------------------------------------------------------------------------------------------------");
		
		
		//Clear the console somehow?
		//System.out.println('\f');	
		
		//System.out.println(axiomSet.indivConceptMap.disjunctionSet.size());
		
		Parser.parse(input,axiomSet);
		
		
		long runtimeadd2 = System.currentTimeMillis() - runtimeadd;
		System.out.println("Total runtime was: "+runtimeadd2+"ms");
		
		}
	}
	
}
