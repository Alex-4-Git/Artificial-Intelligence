package post_Correspondence_Problem;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Main {
	public static void main(String[] args) throws CloneNotSupportedException{
		Domino[] dominos = null;
		String output_states = null;
		try{
            // Open the file that is the first 
            // command line parameter
			String fileName = args[0];
			output_states = args[1];
			
            FileInputStream fstream = new FileInputStream(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;
            //Read File Line By Line
            strLine = br.readLine();
            int maxQueueSize = Integer.parseInt(strLine.trim());
            strLine = br.readLine();
            int maxNumOfStates = Integer.parseInt(strLine.trim());
            ArrayList<Domino> domi_list = new ArrayList<Domino>();
            
            while ((strLine = br.readLine()) != null)   {
              StringTokenizer st = new StringTokenizer(strLine);
              String index = st.nextToken();
              String top = st.nextToken();
              String bottom = st.nextToken();
              Domino domi = new Domino(top, bottom);
              domi_list.add(domi);
            }
            
            dominos = new Domino[domi_list.size()];
            dominos = domi_list.toArray(dominos);
            
            //Close the input stream
            fstream.close();
            }catch (Exception e){//Catch exception if any
              System.err.println("Error: " + e.getMessage());
            }
	

		
		Solver solver = new Solver();
		solver.firstStage(dominos);
		if(solver.solution!=null){
			solver.printSolution();
			if(output_states.equals("1")){
				solver.generate_states_sequence(dominos);
				solver.print_states_sequence();
			}
		
			return;
		}
		if(solver.reachMaxNumOfStates) {
			System.out.println("out of research limit");
			return;
		}
		
		solver.secondStage(dominos);
		if(solver.solution!=null){
			solver.printSolution();
			if(output_states.equals("1")){
				solver.generate_states_sequence(dominos);
				solver.print_states_sequence();
			}

			return;
		}
		if(solver.noSolution) {
			System.out.println("there is no solution existing");
			return;
		}
		System.out.println("out of research limit");
	
	}

}
