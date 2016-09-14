package com.socket.chat;

import service.ChatServer;

public class AppMain {
	
	public static void main(String[] args) throws Exception {
		new ChatServer().initServer();
	}
}
