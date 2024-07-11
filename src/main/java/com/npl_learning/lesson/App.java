package com.npl_learning.lesson;

import java.util.Scanner;


import opennlp.tools.doccat.BagOfWordsFeatureGenerator;
import opennlp.tools.doccat.FeatureGenerator;
import opennlp.tools.lemmatizer.LemmatizerME;
import opennlp.tools.lemmatizer.LemmatizerModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.postag.POSModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.doccat.DoccatFactory;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.model.ModelUtil;

import java.util.Map;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * Hello world!
 *
 */
public class App 
{
	private static final String DEFAULT_ANSWER = "Sorry I did not understand your message, can you try again with a different word?";
	
	private static final Map<String, String> RESPONSE = new HashMap<String, String>();
	static {
		RESPONSE.put("greetings", "Hello, how can i help you?");
		RESPONSE.put("price", "The price of our product is 300$");
		RESPONSE.put("bye", "Thank you for contacting Inn Tech Resources. Do kindly stop by again next time");
	}
	
    public static void main( String[] args ) throws IOException
    {
    	DoccatModel categorizer = getCategorizerModel();
		    	
        Scanner scanner = new Scanner(System.in);
        while(true) {
        	System.out.println("Enter message to the Bot here:");
        	String userMsg = scanner.nextLine();
        	String[] brokenSentences = extractSentences(userMsg);
        	for(String sentence : brokenSentences) 
        	{
        		String[] token = extractToken(sentence);
        		
        		String[] pos = getPOSTag(token);

        		String[] lemmas = extractLemmas(token, pos);
        	
	        	String category = getCategory(categorizer, lemmas);
	        	
	        	String response;
	        	
	        	if(!RESPONSE.containsKey(category)) {
	        		response = DEFAULT_ANSWER;
	        		System.out.println(response);
	        		continue;
	        	}
	        	response = RESPONSE.get(category);
	        	
	        	System.out.println(response);
        	}
        }
    }
    
    private static DoccatModel getCategorizerModel() throws IOException
    {
    	InputStreamFactory inputStreamFactory = new MarkableFileInputStreamFactory(new File("categorizer.txt"));
		ObjectStream<String> lineObjectStream = new PlainTextByLineStream(inputStreamFactory, StandardCharsets.UTF_8);
		ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineObjectStream);
		
		DoccatFactory factory = new DoccatFactory(new FeatureGenerator[] {new BagOfWordsFeatureGenerator()});
		
		TrainingParameters trainingParam = ModelUtil.createDefaultTrainingParameters();
		trainingParam.put(TrainingParameters.CUTOFF_PARAM, 0);
		
		DoccatModel model = DocumentCategorizerME.train("en", sampleStream, trainingParam, factory);
			
		return model;
    }
    
    private static String[] extractSentences(String userMsg) 
    {
    	String[] sentences = {};
    	try(InputStream model = new FileInputStream("en-sent.bin")){
    		SentenceDetectorME sentenceDetectorME = new SentenceDetectorME(new SentenceModel(model));
    		sentences = sentenceDetectorME.sentDetect(userMsg);
    	
    	}catch(IOException ioe) {
    		ioe.printStackTrace();
    	}
    	return sentences;
    }
    
    
    private static String[] extractToken(String sentence)
    {
    	String[] tokens = {};
    	
    	try(InputStream model = new FileInputStream("en-token.bin")){
    		TokenizerME tokenizerME = new TokenizerME(new TokenizerModel(model));
    		tokens = tokenizerME.tokenize(sentence);    		
    	}catch(IOException ioe) {
    		ioe.printStackTrace();
    	}
    	return tokens;
    }
    
    private static String[] getPOSTag(String[] tokens)
    {
    	String[] posTags = {};
    	try(InputStream model = new FileInputStream("en-pos-maxent.bin")){
    		POSTaggerME posTaggerME = new POSTaggerME(new POSModel(model));
    		posTags = posTaggerME.tag(tokens);
    		
    	}catch(IOException ioe) {
    		ioe.printStackTrace();
    	}
    	return posTags;
    }
    
    private static String[] extractLemmas(String[] tokens, String[] posTags)
    {
    	String[] lemmas = {};
    	try(InputStream model = new FileInputStream("en-lemmatizer.bin")){
    		LemmatizerME lemmatizerME = new LemmatizerME(new LemmatizerModel(model));
    		lemmas = lemmatizerME.lemmatize(tokens, posTags);
    		
    	}catch(IOException ioe) {
    		ioe.printStackTrace();
    	}
    	return lemmas;
    }
    
    private static String getCategory(DoccatModel categorizerModel, String[] lemmas)
    {
    	DocumentCategorizerME documentCategorizerME = new DocumentCategorizerME(categorizerModel);
    	
    	double[] probabilities = documentCategorizerME.categorize(lemmas);
    	
    	return documentCategorizerME.getBestCategory(probabilities);
    }
}
