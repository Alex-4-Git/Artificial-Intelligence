package logistic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.HashMap;

public class BackEnd {
	private HashMap<String, String> valueMap = new HashMap<String,String>();
	private HashMap<String, String> contentMap = new HashMap<String,String>();
	private ArrayList<String>  trueAtoms= new ArrayList<String>();
	private ArrayList<String>  actionAtoms= new ArrayList<String>();
	private HashMap<String, String> move=new HashMap<String,String>();
	private HashMap<String, String> load=new HashMap<String,String>();
	private HashMap<String, String> unload=new HashMap<String,String>();
	private int step;
	
	public BackEnd(int s){
		step=s;
	}
	
	public void readInfo(String fileName) throws IOException{
		FileInputStream fStream= new FileInputStream(fileName);
		BufferedReader bf = new BufferedReader(new InputStreamReader(fStream));
		String line;
		
		while(!(line=bf.readLine()).equals("0")){
			String[] state=line.split(" ");
			valueMap.put(state[0], state[1]);
		}
		while((line=bf.readLine())!=null){
			String[] state=line.split("\\:");
			contentMap.put(state[0].trim(), state[1].trim());
		}
		
		for(String s:valueMap.keySet()){
			if(valueMap.get(s).equals("T")){
				String content=contentMap.get(s);
				trueAtoms.add(content);
			}
		}
	}
	
	public void outputSolution(String fileName) throws FileNotFoundException{
		for(String s:trueAtoms){
			if(s.matches("^Move.*")){
				String[] words=s.split(" ");
				String time=words[4];
				String desc="Move "+words[1]+" from "+words[2]+" to "+words[3]+".";
				move.put(time, desc);
			}
			
			if(s.matches("^Loads.*")){
				String[] words=s.split(" ");
				String time=words[4];
				String desc="Load "+words[1]+" into "+words[2]+ " at "+words[3]+".";
				load.put(time, desc);
			}
			if(s.matches("^Unloads.*")){
				String[] words=s.split(" ");
				String time=words[4];
				String desc="Unload "+words[1]+" from "+words[2]+ " at "+words[3]+".";
				unload.put(time, desc);
			}
		}
		File file=new File(fileName);
		PrintStream ps=new PrintStream(file);
		
		for(int i=0;i<=step;i++){
			String time= "Time "+i+" : ";
			String desc="";
			String timestamp=String.valueOf(i);
			if(unload.containsKey(timestamp)){
				desc=desc+unload.get(timestamp)+" ";
			}
			if(load.containsKey(timestamp)){
				desc=desc+load.get(timestamp)+" ";
			}
			if(move.containsKey(timestamp)){
				desc=desc+move.get(timestamp)+" ";
			}
			if(desc!=""){
				ps.println(time+desc);
			}
		}
		ps.flush();
		ps.close();
	}
	
	

}
