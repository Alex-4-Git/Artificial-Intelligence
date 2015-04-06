package logistic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.PseudoColumnUsage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.swing.event.ListSelectionEvent;
import javax.swing.text.StyledEditorKit.ForegroundAction;

//A B C D 
//Milk Eggs
//Truck1 
//3
//START
//Truck1 A
//Eggs A
//Milk B
//A B 
//B A C 
//C A
//GOAL
//Eggs C
//Milk A


public class FrontEnd {
	private  String[] locations;
	private  String[] objects;
	private  String[] trucks;
	private  ArrayList<String> start = new ArrayList<String>();
	private  ArrayList<String> goal = new ArrayList<String>();
	private  int step=0;
	
	// some location may have no neighbors, so neighbors may return a null pointer.
	private  HashMap<String, ArrayList<String>> neighbors = new HashMap<String, ArrayList<String>>();
	private  HashMap<String, Integer> atoms=new HashMap<String, Integer>();
	private  int counter=0;
	
	public int getStep(){
		return step;
	}
	
	public void readInformation(String fileName){
		try{
			FileInputStream fStream = new FileInputStream(fileName);
			BufferedReader br= new BufferedReader(new InputStreamReader(fStream));
			//read locations
			String line = br.readLine();
			locations = line.split(" ");
			
			//read objects
			line = br.readLine();
			objects = line.split(" ");
			
			//read trucks
			line = br.readLine();
			trucks = line.split(" ");
			
			//read steps
			line = br.readLine();
			step = Integer.parseInt(line);
			
			//read START
			line = br.readLine();
			if(!line.equals("START")) throw new IllegalStateException();
			for(int i=0;i<objects.length+trucks.length;i++){
				line = br.readLine();
//				String s = "AT "+line+" 0";
				String s=line;
				start.add(s);
			}
			
			// read arcs
			line = br.readLine();
			while(!line.equals("GOAL")){
				String[] place = line.split(" ");
				String startPoint = place[0];
				ArrayList<String> neighb = new ArrayList<String>();
				for(int i=1;i<place.length;i++){
					neighb.add(place[i]);
				}
				if(neighb.size()>0){
					neighbors.put(startPoint, neighb);
				}	
				line = br.readLine();
			}
			
			// read GOAL
			if(!line.equals("GOAL")) throw new IllegalStateException();
			while((line = br.readLine())!=null){
//				String s = "AT "+line+" "+step;
				String s = line;
				goal.add(s);
			}
		

		}catch(Exception e){
			System.out.println("Read File Error");
		}
	}
	
	public void generateAtoms(){
		for(int x=0;x<objects.length;x++){
			for(int l=0;l<locations.length;l++){
				for(int i=0;i<=step;i++){
					String s=generateAt(objects[x], locations[l], i);
					atoms.put(s, ++counter);
				}
			}
		}
		
		
		for(int x=0;x<trucks.length;x++){
			for(int l=0;l<locations.length;l++){
				for(int i=0;i<=step;i++){
					String s=generateAt(trucks[x], locations[l], i);
					atoms.put(s, ++counter);
				}
			}
		}

		
		for(int x=0;x<objects.length;x++){
			for(int y=0;y<trucks.length;y++){
				for(int i=0;i<=step;i++){
					String s=generateLoaded(objects[x], trucks[y], i);
					atoms.put(s, ++counter);
				}
			}
		}
		
		for(int x=0;x<objects.length;x++){
			for(int y=0;y<trucks.length;y++){
				for(int l=0;l<locations.length;l++){
					for(int i=0;i<=step;i++){
						String s=generateUnloads(objects[x], trucks[y], locations[l], i);
						atoms.put(s, ++counter);
					}
				}
			}
		}
		
		for(int x=0;x<objects.length;x++){
			for(int y=0;y<trucks.length;y++){
				for(int l=0;l<locations.length;l++){
					for(int i=0;i<step;i++){
						String s=generateLoads(objects[x], trucks[y], locations[l], i);
						atoms.put(s, ++counter);
					}
				}
			}
		}

		
		for(int x=0;x<trucks.length;x++){
			for(int l1=0;l1<locations.length;l1++){
				ArrayList<String> destination = neighbors.get(locations[l1]);
				if(destination==null) continue;
				for(String dest:destination){
					for(int i=0;i<step;i++){
						String s=generateMove(trucks[x], locations[l1], dest, i);
						atoms.put(s, ++counter);
					}
				}
			}
		}
		
	}
	
	
	private String generateAt(String x, String l, int i){
		String s="At "+x+" "+l+" "+i;
		return s;
	}
	
	private String generateLoaded(String x, String y, int i){
		String s="Loaded "+x+" "+y+" "+i;
		return s;
	}
	
	private String generateLoads(String x, String y, String l, int i){
		String s="Loads "+x+" "+y+" "+l+" "+i;
		return s;
	}
	
	private String generateUnloads(String x, String y, String l, int i){
		String s="Unloads "+x+" "+y+" "+l+" "+i;
		return s;
	}
	
	private String generateMove(String x, String l1, String l2, int i){
		String s="Move "+x+" "+l1+" "+l2+" "+i;
		return s;
	}
	
	
	// print frontEnd result into file: fileName
	public void generateClause(String fileName) throws FileNotFoundException{
		File file = new File(fileName);
		PrintStream ps = new PrintStream(file);
		ArrayList<String> clauses = new ArrayList<String>();
		rule1(clauses);
		rule2(clauses);
		rule3(clauses);
		rule4(clauses);
		rule5(clauses);
		rule6(clauses);
		rule7(clauses);
		rule8(clauses);
		rule9(clauses);
		rule10(clauses);
		rule11(clauses);
		rule12(clauses);
		rule13(clauses);
		rule14(clauses);
		rule15(clauses);
		rule16(clauses);
		rule17(clauses);
		rule18(clauses);
		
		for(String s:clauses){
//			System.out.println(s);
			ps.println(s);
		}
		ps.println(0);
		for(int i=1;i<=atoms.size();i++){
			for(String s:atoms.keySet()){
				if(atoms.get(s)==i){
					ps.println(i+" : "+s);
				}
			}
		}
		
		ps.flush();
		ps.close();
		
	}
	
//	1. If Move(X,L1,L2,I) then At(X,L2,I+1).
	private void rule1(ArrayList<String> lists){
		for(int x=0;x<trucks.length;x++){
			for(int l1=0;l1<locations.length;l1++){
				ArrayList<String> destination = neighbors.get(locations[l1]);
				if(destination==null) continue;
				for(String dest:destination){
					for(int i=0;i<step;i++){
						int a1=Move(trucks[x], locations[l1], dest, i);
						int a2=At(trucks[x], dest, i+1);
						String s="-"+a1+" "+a2;
						lists.add(s);
					}
				}
			}
		}
	}
	
//	2. If Load(X,Y,L,I) then Loaded(X,Y,I+1).
	private void rule2(ArrayList<String> lists){
		for(int x=0;x<objects.length;x++){
			for(int y=0;y<trucks.length;y++){
				for(int l=0;l<locations.length;l++){
					for(int i=0;i<step;i++){
						int a1=Loads(objects[x], trucks[y], locations[i], i);
						int a2=Loaded(objects[x], trucks[y], i+1);
						String s="-"+a1+" "+a2;
						lists.add(s);
					}
				}
			}
		}
	}
	
//	3. If Unload(X,Y,L,I) then At(X,L,I).
	private void rule3(ArrayList<String> lists){
		for(int x=0;x<objects.length;x++){
			for(int y=0;y<trucks.length;y++){
				for(int l=0;l<locations.length;l++){
					for(int i=0;i<=step;i++){
						int a1=Unloads(objects[x], trucks[y], locations[l], i);
						int a2=At(objects[x], locations[l], i);
						String s="-"+a1+" "+a2;
						lists.add(s);
					}
				}
			}
		}
	}
	
//	4. If Move(X,L1,L2,I) then not At(X,L1,I+1).
	private void rule4(ArrayList<String> lists){
		for(int x=0;x<trucks.length;x++){
			for(int l1=0;l1<locations.length;l1++){
				ArrayList<String> destination = neighbors.get(locations[l1]);
				if(destination==null) continue;
				for(String dest:destination){
					for(int i=0;i<step;i++){
						int a1=Move(trucks[x], locations[l1], dest, i);
						int a2=At(trucks[x], locations[l1], i+1);
						String s="-"+a1+" -"+a2;
						lists.add(s);
					}
				}
			}
		}
	}
	
//	5. If Load(X,Y,L,I) then not At(X,L,I+1)
	private void rule5(ArrayList<String> lists){
		for(int x=0;x<objects.length;x++){
			for(int y=0;y<trucks.length;y++){
				for(int l=0;l<locations.length;l++){
					for(int i=0;i<step;i++){
						int a1=Loads(objects[x], trucks[y], locations[l], i);
						int a2=At(objects[x], locations[l], i+1);
						String s="-"+a1+" -"+a2;
						lists.add(s);
					}
				}
			}
		}
	}
	
//	6. If Unload(X,Y,L,I) then not Loaded(X,Y,I+1).
	private void rule6(ArrayList<String> lists){
		for(int x=0;x<objects.length;x++){
			for(int y=0;y<trucks.length;y++){
				for(int l=0;l<locations.length;l++){
					for(int i=0;i<step;i++){
						int a1=Unloads(objects[x], trucks[y], locations[l], i);
						int a2=Loaded(objects[x], trucks[y], i+1);
						String s="-"+a1+" -"+a2;
						lists.add(s);
					}
				}
			}
		}
	}
	
//	7. If Move(X,L1,L2,I) then At(X,L1,I).
	private void rule7(ArrayList<String> lists){
		for(int x=0;x<trucks.length;x++){
			for(int l1=0;l1<locations.length;l1++){
				ArrayList<String> destination = neighbors.get(locations[l1]);
				if(destination==null) continue;
				for(String dest:destination){
					for(int i=0;i<step;i++){
						int a1=Move(trucks[x], locations[l1], dest, i);
						int a2=At(trucks[x], locations[l1], i);
						String s="-"+a1+" "+a2;
						lists.add(s);
					}
				}
			}
		}
	}
	
//	8. If Load(X,Y,L,I) then At(X,L,I) and At(Y,L,I).
	private void rule8(ArrayList<String> lists){
		for(int x=0;x<objects.length;x++){
			for(int y=0;y<trucks.length;y++){
				for(int l=0;l<locations.length;l++){
					for(int i=0;i<step;i++){
						int a1=Loads(objects[x], trucks[y], locations[l], i);
						int a2=At(objects[x], locations[l], i);
						int a3=At(trucks[y], locations[l], i);
						String s="-"+a1+" "+a2;
						String s1="-"+a1+" "+a3;
						lists.add(s);
						lists.add(s1);
					}
				}
			}
		}
	}
	
	
//	9. If Unload(X,Y,L,I) then At(Y,L,I) and Loaded(X,Y,I).
	private void rule9(ArrayList<String> lists){
		for(int x=0;x<objects.length;x++){
			for(int y=0;y<trucks.length;y++){
				for(int l=0;l<locations.length;l++){
					for(int i=0;i<=step;i++){
						int a1=Unloads(objects[x], trucks[y], locations[l], i);
						int a2=At(trucks[y], locations[l],i);
						int a3=Loaded(objects[x], trucks[y], i);
						String s="-"+a1+" "+a2;
						String s1="-"+a1+" "+a3;
						lists.add(s);
						lists.add(s1);
					}
				}
			}
		}
	}
	
//	10. For any object X, if At(X,L,I) and not At(X,L,I+1) then 
//	[Load(X,Truck1,L,I) or Load(X,Truck2,L,I) or ... or Load(X,TruckM,L,I)].
	private void rule10(ArrayList<String> lists){
		for(int x=0;x<objects.length;x++){
			for(int l=0;l<locations.length;l++){
				for(int i=0;i<step;i++){
					int a1=At(objects[x], locations[l], i);
					int a2=At(objects[x], locations[l], i+1);
					String s="-"+a1+" "+a2;
					for(int y=0;y<trucks.length;y++){
						int a3=Loads(objects[x], trucks[y], locations[l], i);
						s+=" " +a3;
					}
					lists.add(s);
				}
			}
		}
	}
	
//	11. For any object X, if not At(X,L,I-1) and At(X,L,I) then 
//	[Unload(X,Truck1,L,I) or Unload(X,Truck2,L,I) or ... or Unload(X,TruckM,L,I)].
	private void rule11(ArrayList<String> lists){
		for(int x=0;x<objects.length;x++){
			for(int l=0;l<locations.length;l++){
				for(int i=1;i<=step;i++){
					int a1=At(objects[x], locations[l], i-1);
					int a2=At(objects[x], locations[l], i);
					String s=a1+" -"+a2;
					for(int y=0;y<trucks.length;y++){
						int a3=Unloads(objects[x], trucks[y], locations[l], i);
						s+=" " +a3;
					}
					lists.add(s);
				}
			}
		}
	}
	
//	12. For any truck X, if At(X,L,I) and not At(X,L,I+1) then [Move(X,L,L1,I) 
//	or Move(X,L,L2,I) or ... or Move(X,L,Lq,I) where L1 ... Lq are the heads of all the outarcs from L.
	private void rule12(ArrayList<String> lists){
		for(int y=0;y<trucks.length;y++){
			for(int l=0;l<locations.length;l++){
				for(int i=0;i<step;i++){
					ArrayList<String> destination = neighbors.get(locations[l]);
					if(destination==null) continue;
					int a1=At(trucks[y], locations[l], i);
					int a2=At(trucks[y], locations[l], i+1);
					String s="-"+a1+" "+a2;
					for(String dest:destination){
						int a3=Move(trucks[y], locations[l], dest, i);
						s+=" "+a3;
					}
					lists.add(s);
				}
				
			}
		}
	}
	
	
//	13. For any truck X, if not At(X,L,I) and At(X,L,I+1) then [Move(X,L1,L,I) 
//	or Move(X,L2,L,I) or ... or Move(X,Lq,L,I) where L1 ... Lq are the tails of all the inarcs into L.
	private void rule13(ArrayList<String> lists){
		for(int y=0;y<trucks.length;y++){
			for(int l=0;l<locations.length;l++){
				for(int i=0;i<step;i++){
					ArrayList<String> startPoint = new ArrayList<String>();
					for(int l1=0;l1<locations.length;l1++){
						ArrayList<String> arr=neighbors.get(locations[l1]);
						if(arr==null) continue;
						if(arr.contains(locations[l])){
							startPoint.add(locations[l1]);
						}
					}
					if(startPoint.size()==0) continue;
					int a1=At(trucks[y], locations[l], i);
					int a2=At(trucks[y], locations[l], i+1);
					String s=a1+" -"+a2;
					for(String start: startPoint){
						int a3=Move(trucks[y], start, locations[l],i);
						s+=" "+a3;
					}
					lists.add(s);
				}
			}
		}
		
	}
	
	
//	14. If Loaded(X,Y,I) and At(Y,L,I) and not Loaded(X,Y,I+1) then Unload(X,Y,L,I)
	private void rule14(ArrayList<String> lists){
		for(int x=0;x<objects.length;x++){
			for(int y=0;y<trucks.length;y++){
				for(int l=0;l<locations.length;l++){
					for(int i=0;i<step;i++){
						int a1=Loaded(objects[x], trucks[y], i);
						int a2=At(trucks[y],locations[l],i);
						int a3=Loaded(objects[x], trucks[y], i+1);
						int a4=Unloads(objects[x], trucks[y], locations[l], i);
						String s="-"+a1+" -"+a2+" "+a3+" "+a4;
						lists.add(s);
					}
				}
			}
		}
	}
	
	
//	15. If not Loaded(X,Y,I) and At(Y,L,I) and Loaded(X,Y,I+1) then Load(X,Y,L,I).
	private void rule15(ArrayList<String> lists){
		for(int x=0;x<objects.length;x++){
			for(int y=0;y<trucks.length;y++){
				for(int l=0;l<locations.length;l++){
					for(int i=0;i<step;i++){
						int a1=Loaded(objects[x], trucks[y], i);
						int a2=At(trucks[y],locations[l],i);
						int a3=Loaded(objects[x], trucks[y], i+1);
						int a4=Loads(objects[x], trucks[y], locations[l], i);
						String s=a1+" -"+a2+" -"+a3+" "+a4;
						lists.add(s);
					}
				}
			}
		}
	}
	
//	16. For each pair of outarcs L -> L1 and L -> L2 with the same tail L, 
//	not [Move(X,L,L1,I) and Move(X,L,L2,I)].
	private void rule16(ArrayList<String> lists){
		for(int x=0;x<trucks.length;x++){
			for(int i=0;i<step;i++){
				for(int l=0;l<locations.length;l++){
					ArrayList<String> neighb= neighbors.get(locations[l]);
					if(neighb==null) continue;
					if(neighb.size()>1){
						for(int l1=0;l1<neighb.size()-1;l1++){
							for(int l2=l1+1;l2<neighb.size();l2++){
								int a1=Move(trucks[x], locations[l], neighb.get(l1), i);
								int a2=Move(trucks[x], locations[l], neighb.get(l2), i);
								String s="-"+a1+" -"+a2;
								lists.add(s);
							}
						}
					}
				}
			}
		}
		
	}
	
//	17. Atoms characterizing the start state. This should include both all the 
//	"At" and "Loaded" statements that are true at time 0 and those that are false at time 0.
	private void rule17(ArrayList<String> lists){
		// add all the "At" statements
		for(String s:start){
			String[] info=s.split(" ");
			String ob=info[0];
			String lo=info[1];
			
			for(int l=0;l<locations.length;l++){
				int a1=At(ob, locations[l], 0);
				String s1;
				if(locations[l].equals(lo)){
					s1=String.valueOf(a1);
				}
				else{
					s1="-"+a1;
				}
				lists.add(s1);
			}
		}
		
		// add all the "Loaded" statements
		for(int x=0;x<objects.length;x++){
			for(int y=0;y<trucks.length;y++){
				int a1=Loaded(objects[x],trucks[y], 0);
				String s="-"+a1;
				lists.add(s);
			}
		}

	}
	
//	18. Atoms characterizing the goal.
	private void rule18(ArrayList<String> lists){
		for(String s:goal){
			String[] info=s.split(" ");
			String ob=info[0];
			String lo=info[1];
			int a=At(ob, lo, 0);
			String s1=String.valueOf(a);
			for(int i=1;i<=step;i++){
				int a1=At(ob, lo, i);
				s1=s1+" "+a1;
			}
			lists.add(s1);
		}
	}
	
//	no truck will appear at two locations at the same time
	private void rule19(ArrayList<String> lists){
		for(int y=0;y<trucks.length;y++){
			for(int i=0;i<=step;i++){
				for(int l1=0;l1<locations.length-1;l1++){
					for(int l2=l1+1;l2<locations.length;l2++){
						int a1=At(trucks[y], locations[l1], i);
						int a2=At(trucks[y], locations[l2], i);
						String s="-"+a1+" -"+a2;
						lists.add(s);
					}
				}
			}
		}
	}
	
	

	// return the corresponding value of String in hash map
	private int At(String x, String l, int i){
		String s=generateAt(x, l, i);
		return atoms.get(s);
	}
	// return the corresponding value of String in hash map
	private int Loaded(String x, String y, int i){
		String string = generateLoaded(x, y, i);
		return atoms.get(string);
	}
	// return the corresponding value of String in hash map
	private int Loads(String x, String y, String l, int i){
		String s=generateLoads(x, y, l, i);
		return atoms.get(s);
	}
	// return the corresponding value of String in hash map
	private int Unloads(String x, String y, String l, int i){
		String string=generateUnloads(x, y, l, i);
		return atoms.get(string);
	}
	// return the corresponding value of String in hash map
	private int Move(String x, String l1, String l2, int i){
		String s=generateMove(x, l1, l2, i);
		return atoms.get(s);
	}
}
