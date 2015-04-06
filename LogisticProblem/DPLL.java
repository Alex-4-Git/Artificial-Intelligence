package logistic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DPLL {
	private HashSet<String> atoms=new HashSet<String>();
	private ArrayList<String> additionalInfo=new ArrayList<String>();
	private ArrayList<ArrayList<String>> clauses=new ArrayList<ArrayList<String>>();
	private Map<String, String> resultMap;
	private ArrayList<String> solution=new ArrayList<String>();
	private boolean hasSolution = false;
	
	// read atoms into private data member-atoms and set-clauses
	public void readInformation(String fileName) throws IOException  {
		
		FileInputStream fStream = new FileInputStream(fileName);
		
		BufferedReader br= new BufferedReader(new InputStreamReader(fStream));
		String line;

		while(!(line=br.readLine()).equals("0")){
			addInfo(line);
		}
		
		while((line=br.readLine())!=null){
			additionalInfo.add(line);
		}
		
//		File file=new File("DPLL_readInfo");
//		PrintStream ps=new PrintStream(file);
//		for(ArrayList<String> arr:clauses){
//			ps.println(arr.toString());
//		}
//		ps.println(0);
//		for(String s:additionalInfo){
//			ps.println(s);
//		}
//		
//		ps.flush();
//		ps.close();
		
	}
	
	// add atoms and clauses into corresponding data structure
	private void addInfo(String line){
		String[] literals = line.split(" ");
		ArrayList<String> clause=new ArrayList<String>();
		for(String s:literals){
			
			//atom is positive symbol
			if(s.matches("^-.*")){
				String s1=s.substring(1);
				atoms.add(s1);
			}else{
				atoms.add(s);
			}
			
			clause.add(s);
		}
		clauses.add(clause);
	}
	
	public void findSolution() throws Exception{
		resultMap = dp(clauses);
		if(resultMap==null){
			solution.add("Nothing");
		}
		else{
			for(int i=1;i<=resultMap.keySet().size();i++){
				for(String s: resultMap.keySet()){
					if(i==Integer.parseInt(s)){
						String s1=resultMap.get(s);
						String s2=s+" "+s1;
						solution.add(s2);
					}
				}
			}
		}
		solution.add("0");
		solution.addAll(additionalInfo);
	}
	
	public void printSolution(String fileName) throws FileNotFoundException{
		File file = new File(fileName);
		PrintStream ps = new PrintStream(file);
		for(String s:solution){
			ps.println(s);
		}
		ps.flush();
		ps.close();
	}
	
	
	// deep copy of ArrayList
	private ArrayList<ArrayList<String>> copyOf(ArrayList<ArrayList<String>> lists){
		ArrayList<ArrayList<String>> newLists = (ArrayList<ArrayList<String>>) lists.clone();
		for(int i=0;i<lists.size();i++){
			newLists.set(i, (ArrayList<String>) lists.get(i).clone());
		}
		return newLists;
	}
	
	
	
	private Map<String, String>	dp(ArrayList<ArrayList<String>> S) throws Exception{
		HashMap<String, String>  valueMap = new HashMap<String, String>();
		return dp1(S,valueMap);
	}
	
	private HashMap<String, String> dp1(ArrayList<ArrayList<String>> S0, HashMap<String, String> V0) throws Exception{
		// make copy of clause lists: S0 and Value Map: V0.
		ArrayList<ArrayList<String>> S=copyOf(S0);
		HashMap<String, String> V=(HashMap<String, String>) V0.clone();
		// loop as long as there are easy cases to cherry pick
		while(true){
			String l=null;
			if(S.size()==0){
				fillMap(atoms,V);
				return V;
			}
			else if(clauseIsEmpty(S)){
				return null;
			}
			else if((l=findPureLiteral(S))!=null){
				obvious_assign(l,V);
				deletePureLiteral(l,S);
				continue;
			}
			else if((l=findSingleLiteral(S))!=null){
				obvious_assign(l,V);
				S=propagate(l,S,V);
				continue;
			}
			break;
		}
		
		//pick some atom and try each assignment in turn
		String l=findUnboundAtom(S,V);
		assignTrue(l,V);
		ArrayList<ArrayList<String>> S1=propagate(l,S,V);
		HashMap<String, String> vNew=dp1(S1,V);
		if(vNew!=null) return vNew;
		
		assignFalse(l,V);
		S1=propagate(l,S,V);
		return dp1(S1,V);
	}
	
	private boolean clauseIsEmpty(ArrayList<ArrayList<String>> S){
		for(ArrayList<String> arr:S ){
			if(arr.size()==0) return true;
		}
		return false;
	}
	
	
	// assign false to the atoms not in value map
	private void fillMap(HashSet<String> atoms, HashMap<String, String> V){
		for(String s: atoms){
			if(!V.containsKey(s)){
				assignFalse(s, V);
			}
		}
	}
	
	private String findPureLiteral(ArrayList<ArrayList<String>> S){
		HashSet<String> literals = new HashSet<String>();
		for(ArrayList<String> arr: S ){
			for(String s:arr){
				literals.add(s);
			}
		}
		
		for(String s: literals){
			String s1;
			if(s.matches("^-.*")){
				s1=s.substring(1);
			}
			else{
				s1="-"+s;
			}
			if(!literals.contains(s1)){
				return s;
			}
		}
		return null;
	}
	
	
	
	// delete all clauses containing pure literal
	private void deletePureLiteral(String l, ArrayList<ArrayList<String>> S){
			Iterator<ArrayList<String>> iterator= S.iterator();
			while(iterator.hasNext()){
				ArrayList<String> clause=iterator.next();
				if(clause.contains(l)){
					iterator.remove();
				}
			}
			
	}
	
	private String findSingleLiteral(ArrayList<ArrayList<String>> S){
		for(ArrayList<String> arr: S){
			if(arr.size()==1){
				return arr.get(0);
			}
		}
		return null;
	}
	
	private String findUnboundAtom(ArrayList<ArrayList<String>> S, HashMap<String, String> V){
		for(ArrayList<String> clause: S){
			for(String s:clause){
				if(s.matches("[0-9]*")&&!V.containsKey(s)){
					return s;
				}
			}
		}
		return null;
	}
	
	
	// assign atom true or false.
	private void obvious_assign(String l,HashMap<String, String> V){
			if(l.matches("^-.*")) {
				String l1=l.substring(1);
				assignFalse(l1, V);
			}
			else{
				assignTrue(l,V);
			}
		}
		
	// String is in form l, V[l]=true;
	private void assignTrue(String l,HashMap<String, String> V){
		V.put(l, "T");
	}
	
	// String is in form l,  V[l]=false;
	private void assignFalse(String l, HashMap<String, String> V){
		V.put(l, "F");
		
	}
	
	// change clauses contains l or -l to create new set of clauses
	private ArrayList<ArrayList<String>> propagate(String l, ArrayList<ArrayList<String>> S0, 
										HashMap<String,String> V) throws Exception{
		// make copy of S0 and make sure SO is not changed.
		ArrayList<ArrayList<String>> S=copyOf(S0);
		
		String positiveValue;
		String negativeValue;
		
		if(l.matches("^-.*")){
			String l1=l.substring(1);
			if(ValueisTrue(l1, V)){
				positiveValue=l1;
				negativeValue=l;
			}
			else{
				positiveValue=l;
				negativeValue=l1;
			}
		}
		else {
			String l1="-"+l;
			if(ValueisTrue(l, V)){
				positiveValue=l;
				negativeValue=l1;
			}else{
				positiveValue=l1;
				negativeValue=l;
			}
		}
		
//		for(int i=0;i<S.size();i++){
//			ArrayList<String> clause=S.get(i);
//			if(clause.contains(positiveValue)){
//				S.remove(i);
//				i--;
//			}
//			if(clause.contains(negativeValue)){
//				clause.remove(negativeValue);
//			}
//		}
		
		Iterator<ArrayList<String>>  it=S.iterator();
		while(it.hasNext()){
			ArrayList<String> clause = it.next();
			if(clause.contains(positiveValue)){
				it.remove();
			}
			else if(clause.contains(negativeValue)){
				clause.remove(negativeValue);
			}
		}
		return S;
	}
	
	private boolean ValueisTrue(String l, HashMap<String, String> V) throws Exception{
		if(!V.containsKey(l)){
			throw new Exception("value map does not contain this key");
		}
		String bString=V.get(l);
		if(bString.equals("T")) return true;
		else return false;
	}

}
