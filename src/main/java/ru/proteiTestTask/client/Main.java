package ru.proteiTestTask.client;
import java.io.*;
import java.net.*;

public class Main {

	public static void main(String[] args) {
		TCPClient client = new TCPClient(6895);
		CommandLineInterface cli = new CommandLineInterface(client);
		cli.run(args);
	}

}
