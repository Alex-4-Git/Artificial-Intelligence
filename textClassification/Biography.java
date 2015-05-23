package textClassification;

import java.util.HashSet;

public class Biography {
	private String name;
	private  String category;
	private String prediction;
	private HashSet<String> keywords;
	
	public Biography(String n, String c, HashSet<String> words){
		name = n;
		category = c;
		keywords = words;
	}
	
	public boolean predictRight(){
		return category.equals(prediction);
	}
	
	public String name(){
		return name;
	}
	
	public String category(){
		return category;
	}
	
	public HashSet<String> keywords(){
		return new HashSet<String>(keywords);
	}

	public void setPrediction(String p){
		prediction = p;
	}

}
