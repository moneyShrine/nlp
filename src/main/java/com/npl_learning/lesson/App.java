package com.npl_learning.lesson;

import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;

/**
 * Hello world!
 *
 */
public class App 
{
	private static final String DEFAULT_ANSWER = "Sorry I did not understand your message, can you try again with a different word?";
	
	private static final Map<String, String> RESPONSE = new HashMap<>();
	static {
		RESPONSE.put("greetings", "Hello, how can i help you?");
		RESPONSE.put("price", "The price of our product is 300$");
		RESPONSE.put("bye", "Thank you for contacting Inn Tech Resources. Do kindly stop by again next time");
	}
	
    public static void main( String[] args )
    {
        Scanner scanner = new Scanner(System.in);
        while(true) {
        	System.out.println("Enter message to the Bot here:");
        	String userMsg = scanner.nextLine();
        	
        	//TODO: Implement the Open NLP here
        	String category = "";
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
