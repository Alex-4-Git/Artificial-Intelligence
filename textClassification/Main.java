package textClassification;

import java.io.IOException;


public class Main {
	public static void main(String[] args) throws Exception{
		if(args.length==0){
			throw new Exception("Please input legal argument");
		}
		int trainingSize = 0;
		String corpus="tinyCorpus.txt";
		String stopWord="stopwords.txt";
		trainingSize = Integer.parseInt(args[0]);
		if(args.length>=2){
			corpus = args[1];
		}
		if(args.length>=3){
			stopWord = args[2];
		}
		Classifier cla = new Classifier(trainingSize);
		cla.go(corpus,stopWord);
	}

}
