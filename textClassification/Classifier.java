package textClassification;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class Classifier {
	public final double log2 =  Math.log(2);
	private int trainingSize;
	private HashMap<String, Integer> categoryCount = new HashMap<>();
	private HashMap<String, Double>	categoryProbability= new HashMap<>();
	private HashMap<String, HashMap<String, Integer>> wordCount = new HashMap<>();
	private HashMap<String, HashMap<String, Double>> wordProbability = new HashMap<>();
	private HashSet<String> stopWords = new HashSet<>();
	private Biography[] trainingSet;
	private int testTotalNum=0;
	private int testRigthNum=0;
	private double e = 0.1; // Laplacian correction efficiency
	
	public Classifier(int n){
		trainingSize = n;
		trainingSet = new Biography[n];
	}
	
	public void go(String fileName,String fileName2) throws IOException{
		readStopwords(fileName2);
		process(fileName);
		summary();
	}
	
	private void summary() {
		double accuracy = (double)testRigthNum/testTotalNum;
		System.out.printf("Overall accuracy: %d out of %d = %.2f.",testRigthNum,
				testTotalNum,accuracy);
	}

	private void process(String fileName) throws IOException{
		File file = new File(fileName);
		BufferedReader bf = new BufferedReader(new FileReader(file));
		String line=bf.readLine();
		// skip empty line
		while(line.length()==0){
			line=bf.readLine();
		}
		
		// generate training set.
		for(int i=0;i<trainingSize;i++){
			String name = line;
			String category = bf.readLine().trim();
			HashSet<String> words = new HashSet<>();
			line = bf.readLine();
			while(line.length()!=0){
				addToSet(line,words);
				line=bf.readLine();
			}
			trainingSet[i] = new Biography(name, category, words);
	
			while(line.length()==0){
				line=bf.readLine();
			}
		}
		training(trainingSet);
		
		calculateProbability(e);
		
//		printCategoryProb();
		
		// test
		while(line!=null){
			String name = line;
			String category = bf.readLine().trim();
			HashSet<String> words = new HashSet<>();
			line = bf.readLine();
			while(line.length()!=0){
				addToSet(line,words);
				line=bf.readLine();
				if(line==null){
					break;
				}
			}
			Biography bio = new Biography(name, category, words);
			testTotalNum++;
			test(bio);
			
			if(line==null){
				break;
			}
			while(line.length()==0){
				line=bf.readLine();
				if(line==null){
					break;
				}
			}
		}
	}
	
	
	private void calculateProbability(double e) {
		caculateCategoryPro(e);
		caculateWordPro(e);
	}
	
	
	private void printCategoryProb(){
		for(String s: categoryCount.keySet()){
			System.out.println(s+" "+categoryCount.get(s));
		}
		for(String s: categoryProbability.keySet()){
			System.out.println(s+" "+categoryProbability.get(s));
		}
		System.out.println("*************************");
		System.out.println();
	}

	private void caculateWordPro(double e) {
		for(String cat: categoryCount.keySet()){
			int categoryTotalNum = categoryCount.get(cat);
			HashMap<String, Integer> wordMap = wordCount.get(cat);
			HashMap<String, Double> proMap = new HashMap<>();
			wordProbability.put(cat,proMap);
			for(String word: wordMap.keySet()){
				double wordFreq = (double)wordMap.get(word)/categoryTotalNum;
				double wordProb = (wordFreq+e)/(1+2*e);
				wordProb = negativeLog(wordProb);
				proMap.put(word, wordProb);
			}
		}
	}

	private void caculateCategoryPro(double e) {
		int sum=0;
		for(String cat: categoryCount.keySet()){
			sum+=categoryCount.get(cat);
		}
		for(String cat: categoryCount.keySet()){
			double freq=(double)categoryCount.get(cat)/sum;
			double proCat = (freq+e)/(1+categoryCount.size()*e);
			proCat = negativeLog(proCat);
			categoryProbability.put(cat, proCat);
		}
		
	}

	private double negativeLog(double x) {
		return -Math.log(x)/log2;
	}

	private void readStopwords(String fileName) throws IOException {
		File file = new File(fileName);
		BufferedReader bf = new BufferedReader(new FileReader(file));
		String line=null;
		while((line=bf.readLine())!=null){
			if(line.length()!=0){
				String[] words = line.split(" ");
				for(String word: words){
						stopWords.add(word);
				}
			}
		}	
	}
	


	private void addToSet(String line, HashSet<String> words) {
		String[] elements = line.split(" ");
		for(String s: elements){
			String s1 = s.replaceAll("[,.]","");
			if(s1.length()>2&&!stopWords.contains(s1)){
				words.add(s1);
			}
		}
	
	}

	private void test(Biography bio) {
		HashSet<String> keywords = bio.keywords();
		HashMap<String, Double> categoryScore = new HashMap<>(categoryProbability);
		for(String word: keywords){
			for(String cat: wordProbability.keySet()){
				HashMap<String, Double> curMap = wordProbability.get(cat);
				if(!curMap.containsKey(word)){
					continue;
				}else{
					categoryScore.put(cat, categoryScore.get(cat)+curMap.get(word));
				}
			}
		}
		
		Double min = Double.MAX_VALUE;
		String prediction = null;
		

		for(String cat: categoryScore.keySet()){
//			System.out.println(cat+": "+categoryScore.get(cat));
			if(categoryScore.get(cat)<min){
				min=categoryScore.get(cat);
				prediction = cat;
			}
		}
		bio.setPrediction(prediction);
		if(bio.predictRight()){
			testRigthNum++;
		}
		
//		System.out.println(bio.category());
		System.out.print(bio.name()+".\t");
		String result = bio.predictRight()?"Right":"Wrong";
		System.out.println("Predition: "+prediction+". "+result+".");
		// recover and display recovered probability
		recover(categoryScore);
		
		
		System.out.println();
	}

	private void recover(HashMap<String, Double> scores) {
		double min = Double.MAX_VALUE;
		for(String cat: scores.keySet()){
			double d = scores.get(cat);
			if(d<min){
				min=d;
			}
		}
		
		double sum=0;
		Iterator<String> it = scores.keySet().iterator();
		while(it.hasNext()){
			String cat = it.next();
			double score = scores.get(cat);
			double recoveredScore =0;
			if(score-min<7){
				recoveredScore = Math.pow(2, (min-score));
			}else{
				recoveredScore = 0;
			}
			scores.put(cat, recoveredScore);
			sum+=recoveredScore;
		}
		
		for(String cat: scores.keySet()){
			double result = scores.get(cat)/sum;
//			System.out.print(cat+": "+result+"\t");
			System.out.printf("%s: %.2f\t",cat,result);
		}
		System.out.println();
	}

	public void training(Biography[] trainingSet){
		//generate all categories.
		for(int i=0;i<trainingSet.length;i++){
			String category = trainingSet[i].category();
			if(!categoryCount.containsKey(category)){
				categoryCount.put(category,1);
				wordCount.put(category, new HashMap<String,Integer>());
			}else{
				categoryCount.put(category, categoryCount.get(category)+1);
			}
		}
		
		
		//count word occurrence.
		for(Biography bio:trainingSet){
			String category = bio.category();
			HashSet<String>	keywords = bio.keywords();
			HashMap<String, Integer> categoryMap = wordCount.get(category);
			for(String word: keywords){
				if(categoryMap.containsKey(word)){
					categoryMap.put(word, categoryMap.get(word)+1);
				}else{
					for(String cat: wordCount.keySet()){
						HashMap<String, Integer> catMap = wordCount.get(cat);
						if(cat.equals(category)){
							catMap.put(word, 1);
						}else{
							catMap.put(word,0);
						}
					}
				}
			}
		}	
	}
}
