package ru.proteiTestTask.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class TCPClient {
	
	int port;
	
	public TCPClient(int port) {
		this.port = port;
	}
	
	private JSONObject serverInteraction(JSONObject requestJSON) {
		JSONParser parser = new JSONParser();
		JSONObject response = new JSONObject();
		try {
			Socket clientSocket = new Socket("localhost", port);
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			outToServer.writeBytes(requestJSON.toJSONString() + "\n");
			String responseString = inFromServer.readLine();
			response = (JSONObject) parser.parse(responseString);
			clientSocket.close();
		} catch (IOException e) {
			System.out.println("No connection with server!");
			System.exit(1);
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		return response;
	}

	public JSONObject getDefinition(String word) {
		JSONObject requestJSON = new JSONObject();
		requestJSON.put("command", "getDefinition");
		requestJSON.put("word", word);
		return serverInteraction(requestJSON);
	}
	
	public JSONObject addWord(String word, String definition) {
		JSONObject requestJSON = new JSONObject();
		requestJSON.put("command", "add");
		requestJSON.put("word", word);
		requestJSON.put("definition", definition);		
		return serverInteraction(requestJSON);
	}
	
	public JSONObject removeWord(String word) {
		JSONObject requestJSON = new JSONObject();
		requestJSON.put("command", "remove");
		requestJSON.put("word", word);
		return serverInteraction(requestJSON);
	}
	
	public JSONObject changeWord(String word, String newWord, String newDefinition) {
		JSONObject requestJSON = new JSONObject();
		requestJSON.put("command", "change");
		requestJSON.put("word", word);
		requestJSON.put("newWord", newWord);
		requestJSON.put("newDefinition", newDefinition);
		return serverInteraction(requestJSON);		
	}
	
	public JSONObject findWords(String mask) {
		JSONObject requestJSON = new JSONObject();
		requestJSON.put("command", "find");
		requestJSON.put("mask", mask);
		return serverInteraction(requestJSON);		
	}
	
}
