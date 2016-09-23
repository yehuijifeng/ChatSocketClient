package com.socket.chat;

import service.ChatFileServer;
import service.ChatServer;

public class Main {
		public static void main(String[] args) throws Exception {
		//new ChatServer().initServer();
			new ChatFileServer().initServer();
	}
}
