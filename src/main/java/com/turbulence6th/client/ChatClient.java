package com.turbulence6th.client;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {

	public static void main(String[] args) {

		String host = args[0];

		int port = Integer.parseInt(args[1]);

		System.out.println("Connecting to host " + host + " at port " + port);

		try (Socket socket = new Socket(host, port);
				Scanner scan = new Scanner(System.in);
				PrintWriter writer = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()))) {

			System.out.println("Connected to host " + host + " at port " + port);
			System.out.println("Enter your nickname");
			
			String nickname = scan.nextLine();
			writer.println(nickname);
			writer.flush();

			new Thread(new SocketListener(socket)).start();

			while (true) {
				String input = scan.nextLine();
				writer.println(input);
				writer.flush();
			}

		} catch (NumberFormatException | IOException e) {
			System.out.println("Server is closed");
			System.exit(0);
		}

	}

	private static class SocketListener implements Runnable {

		private Socket socket;

		private SocketListener(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {

			try (BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()))) {

				String line;
				while((line = reader.readLine()) != null) {
					System.out.println(line);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
			
			System.out.println("Server is closed");
			System.exit(0);

		}

	}

}
