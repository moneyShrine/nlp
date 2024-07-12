package com.npl_learning.lesson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import opennlp.tools.langdetect.Language;
import opennlp.tools.langdetect.LanguageDetector;
import opennlp.tools.langdetect.LanguageDetectorFactory;
import opennlp.tools.langdetect.LanguageDetectorME;
import opennlp.tools.langdetect.LanguageDetectorModel;
import opennlp.tools.langdetect.LanguageDetectorSampleStream;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

public class LangDetector 
{
	private final String TRAINING_DATA_FILE_NAME = "DoccatSample.txt";
	

	public LangDetector(String userMsg) throws FileNotFoundException
	{
		LanguageDetectorModel languageDetectorModel = null;
        
        //1- Load the training data into the stream
        
        LanguageDetectorSampleStream languageDetectorSampleStream;
        InputStreamFactory inputStreamFactory = new MarkableFileInputStreamFactory(new File("TrainingData"+File.separator+TRAINING_DATA_FILE_NAME));
        try(ObjectStream<String> stream = new PlainTextByLineStream(inputStreamFactory, StandardCharsets.UTF_8)){
        	languageDetectorSampleStream = new LanguageDetectorSampleStream(stream);
        	TrainingParameters trainingParam = new TrainingParameters();
        	trainingParam.put(TrainingParameters.ITERATIONS_PARAM, 100);
        	trainingParam.put(TrainingParameters.CUTOFF_PARAM, 5);
        	trainingParam.put("DataIndexer", "TwoPass");
        	trainingParam.put(TrainingParameters.ALGORITHM_PARAM, "NAIVEBAYES");
        	
        //2- Train the model
        	languageDetectorModel = LanguageDetectorME.train(languageDetectorSampleStream, trainingParam, new LanguageDetectorFactory());
                	
        }catch(IOException ioe) {
        	ioe.printStackTrace();
        }
        
        if(languageDetectorModel == null) {
        	throw new IllegalStateException("Language detector is null hence could not be initialized!");
        }
        //3- Load the trained model
        LanguageDetector languageDetector = new LanguageDetectorME(languageDetectorModel);
        
        //4- Predict the Language
        Language[] languages = languageDetector.predictLanguages(userMsg);
        
        for(Language language : languages) {
        	System.out.println(language.getLang()+" with confidence of "+language.getConfidence()*100);
        }
		
	}
}
