package com.npl_learning.lesson;

import java.io.FileNotFoundException;
import java.util.Scanner;


/**
 * Hello world!
 *
 */
public class App 
{
	public static void main( String[] args )
    {
		
    	Scanner scanner = new Scanner(System.in);
        while(true) {
        	System.out.println("Enter message to the Bot here:");
        	String userMsg = scanner.nextLine();
        	
        	/*//FOR CHATBOT
        	new Chatbot(userMsg);
        	*/
        	
        	//FOR LANGUAGE DETECTOR
        	try {
				new LangDetector(userMsg);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
		
		
		
    }
    
    
}
