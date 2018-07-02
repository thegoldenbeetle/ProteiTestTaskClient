package ru.proteiTestTask.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.cli.*;
import org.json.simple.JSONObject;

public class CommandLineInterface {

	private Options options;
	private HashMap<String, HashSet<String>> groupedOptions;
	private CommandLine cmd;
	private HelpFormatter formatter;
	private CommandLineParser parser;
	private final static String add = "For add new word use: --command=add --word=<arg> --definition=<arg>";
	private final static String getDefinition = "For get definition for word in dictionary use: --command=getDefinition --word=<arg>";
	private final static String remove = "For remove word from dictionary use: --command=remove --word=<arg>";
	private final static String change = "For change word use: --command=change --word=<arg> --newWord=<arg>";
	private final static String find = "For find words with mask use: --command=find --mask=<arg>";

	TCPClient client;

	private Options createOptions() {

		Options useOptions = new Options();

		Option command = new Option("c", "command", true, "Command names: getDescription, add, remove, change, find");
		useOptions.addOption(command);
		Option word = new Option("w", "word", true, "Word");
		useOptions.addOption(word);
		Option definition = new Option("d", "definition", true, "Definition");
		useOptions.addOption(definition);
		Option newWord = new Option("n", "newWord", true, "New word");
		useOptions.addOption(newWord);
		Option mask = new Option("m", "mask", true, "Mask: *, ?");
		useOptions.addOption(mask);

		groupedOptions = new HashMap<String, HashSet<String>>();
		fillGetDefinitionOrRemoveOptionsNames();
		fillAddOptionsNames();
		fillChangeOptionsNames();
		fillFindOptionsNames();

		return useOptions;
	}

	private void fillAddOptionsNames() {
		HashSet<String> addSet = new HashSet<String>();
		addSet.add("command");
		addSet.add("word");
		addSet.add("definition");
		groupedOptions.put("add", addSet);
	}

	private void fillChangeOptionsNames() {
		HashSet<String> changeSet = new HashSet<String>();
		changeSet.add("command");
		changeSet.add("word");
		changeSet.add("newWord");
		groupedOptions.put("change", changeSet);
	}

	private void fillGetDefinitionOrRemoveOptionsNames() {
		HashSet<String> definitionOrRemoveSet = new HashSet<String>();
		definitionOrRemoveSet.add("command");
		definitionOrRemoveSet.add("word");
		groupedOptions.put("getDefinitionOrRemove", definitionOrRemoveSet);
	}

	private void fillFindOptionsNames() {
		HashSet<String> findSet = new HashSet<String>();
		findSet.add("command");
		findSet.add("mask");
		groupedOptions.put("find", findSet);
	}

	public CommandLineInterface(TCPClient client) {

		this.client = client;
		options = createOptions();
		parser = new DefaultParser();
		formatter = new HelpFormatter();
	}

	private void printCliHelp() {
		formatter.printHelp("TCP Client", "Client for work with data base dictionary\n" + getDefinition + "\n" + add
				+ "\n" + remove + "\n" + change + "\n" + find, options, "Developed by Vasilieva Ludmila");
	}

	private void printResponse(JSONObject response, boolean definition, boolean words) {
		System.out.println("From server: " + response.get("ResultCode"));
		if (definition && response.get("ResultCode").equals("OK")) {
			System.out.println("Definition: " + response.get("definition"));
		}
		if (words && response.get("ResultCode").equals("OK")) {
			System.out.println("Founded words: " + response.get("words"));
		}

	}

	private void getDefinition(HashSet<String> inputOptionNames) {
		JSONObject response = new JSONObject();
		if (inputOptionNames.equals(groupedOptions.get("getDefinitionOrRemove"))) {
			printResponse(client.getDefinition(cmd.getOptionValue("word")), true, false);
		} else {
			System.out.println(getDefinition);
		}
	}

	private void addWord(HashSet<String> inputOptionNames) {
		JSONObject response = new JSONObject();
		if (inputOptionNames.equals(groupedOptions.get("add"))) {
			printResponse(client.addWord(cmd.getOptionValue("word"), cmd.getOptionValue("definition")), false, false);
		} else {
			System.out.println(add);
		}
	}

	private void removeWord(HashSet<String> inputOptionNames) {
		JSONObject response = new JSONObject();
		if (inputOptionNames.equals(groupedOptions.get("getDefinitionOrRemove"))) {
			printResponse(client.removeWord(cmd.getOptionValue("word")), false, false);
		} else {
			System.out.println(remove);
		}
	}

	private void changeWord(HashSet<String> inputOptionNames) {
		JSONObject response = new JSONObject();
		if (inputOptionNames.equals(groupedOptions.get("change"))) {
			printResponse(client.changeWord(cmd.getOptionValue("word"), cmd.getOptionValue("newWord"),
					cmd.getOptionValue("definition")), false, false);
		} else {
			System.out.println(change);
		}
	}

	private void findWords(HashSet<String> inputOptionNames) {
		JSONObject response = new JSONObject();
		if (inputOptionNames.equals(groupedOptions.get("find"))) {
			printResponse(client.findWords(cmd.getOptionValue("mask")), false, true);
		} else {
			System.out.println(find);
		}
	}

	public void run(String[] args) {

		try {

			cmd = parser.parse(options, args);

			String command = cmd.getOptionValue("command");

			HashSet<String> inputOptionsNames = new HashSet<String>();
			Option[] inputOptions = cmd.getOptions();
			for (Option inputOption : inputOptions) {
				inputOptionsNames.add(inputOption.getLongOpt());
			}

			switch (command) {
			case "getDefinition":
				getDefinition(inputOptionsNames);
				break;
			case "add":
				addWord(inputOptionsNames);
				break;
			case "remove":
				removeWord(inputOptionsNames);
				break;
			case "change":
				changeWord(inputOptionsNames);
				break;
			case "find":
				findWords(inputOptionsNames);
				break;
			default:
				System.out.println("Wrong command!");
				printCliHelp();
			}

		} catch (Exception e) {
			printCliHelp();
			return;
		}

	}
}
