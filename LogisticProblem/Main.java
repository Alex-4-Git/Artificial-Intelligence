package logistic;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
	public static void main(String[] args) throws Exception{
		String input="input.txt";
		String frontEnd_output="frontEnd_output.txt";
		String dpll_output="DPLL_output.txt";
		String backEnd_output="backEnd_output.txt";
		int n=args.length;
		if(n>0){
			 input = args[0];
			 if(n>1){
				 frontEnd_output=args[1];
			 }
			 if(n>2){
				 dpll_output=args[2];
			 }
			 if(n>3){
				 backEnd_output=args[3];
			 }
		}
		
		FrontEnd frontEnd = new FrontEnd();
		frontEnd.readInformation(input);
		frontEnd.generateAtoms();
		frontEnd.generateClause(frontEnd_output);
		
		DPLL dpll = new DPLL();
		dpll.readInformation(frontEnd_output);
		dpll.findSolution();
		dpll.printSolution("DPLL_output.txt");
		
		BackEnd backEnd = new BackEnd(frontEnd.getStep());
		backEnd.readInfo(dpll_output);
		backEnd.outputSolution(backEnd_output);
		
	}

}
