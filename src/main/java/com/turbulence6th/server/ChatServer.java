package com.turbulence6th.server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ChatServer {

	private static List<Socket> sockets = Collections.synchronizedList(new LinkedList<>());

	public static void main(String[] args) {

		int port = Integer.parseInt(args[0]);

		System.out.println("Starting Chat Server at port " + port);

		try (ServerSocket serverSocket = new ServerSocket(port)) {

			System.out.println("Started Chat Server at port " + port);

			while (true) {
				Socket socket = serverSocket.accept();
				new Thread(new SocketListener(socket)).start();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void broadcastMessage(Socket except, String message) {
		sockets.stream().filter(socket -> socket != except).forEach(socket -> {

			try {
				PrintWriter writer = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()));
				writer.println(message);
				writer.flush();
			}

			catch (IOException e) {
				e.printStackTrace();
			}
			
		});

	}

	private static class SocketListener implements Runnable {

		private Socket socket;
		
		private String nickname;

		private SocketListener(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()))) {
				this.nickname = reader.readLine();

				System.out.println(this.nickname + " is connected to server");
				sockets.add(this.socket);
				broadcastMessage(this.socket, this.nickname + " is connected to server");

				String line;
				while ((line = reader.readLine()) != null) {
					System.out.println(this.nickname + ": " + line);
					broadcastMessage(this.socket, this.nickname + ": " + line);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
			
			System.out.println(this.nickname + " is disconnected");
			sockets.remove(this.socket);
			broadcastMessage(this.socket, this.nickname + " is disconnected");
			
		}

	}

}
