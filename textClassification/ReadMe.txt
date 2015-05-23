To complie the files use the command： javac *.java

To execute use the command： java Main -arg0 (-arg1 -arg2 ). 
arg0 is the size of training set. 
arg1 is the name of corpus file. The default value is "tinyCorpus.txt".
arg2 is the name of stop word file. The default value is "stopwords.txt"


the command should be like: java Main 5, which will use "tinyCorpus.txt" as the corpus and "stopwords.txt" as set of stop words. The program will take first 5 biography as training set and rest of them as test set.

the complete command shoud be like: java Main "size" "file1" "file2"