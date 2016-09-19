package service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.socket.chat.SocketUrls;

import bean.MessageBean;
import bean.UserInfoBean;
import data.UserData;
import net.sf.json.JSONObject;

/**
 * 聊天服务
 * 
 * @author Luhao
 *
 */
public class ChatServer {
	// socket服务
	private static ServerSocket server;

	// 使用ArrayList存储所有的Socket
	public List<Socket> socketList = new ArrayList<>();

	// 模仿保存在内存中的socket
	public Map<Integer, Socket> socketMap = new HashMap();
	// 模仿保存在数据库中的用户信息
	public Map<Integer, UserInfoBean> userMap = new HashMap();
	public Gson gson = new Gson();

	/**
	 * 初始化socket服务
	 */
	public void initServer() {
		try {
			// 创建一个ServerSocket在端口8080监听客户请求
			server = new ServerSocket(SocketUrls.PORT);
			createMessage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 创建消息管理，一直接收消息
	 */
	private void createMessage() {
		try {
			System.out.println("等待用户接入 : ");
			// 使用accept()阻塞等待客户请求
			Socket socket = server.accept();
			// 将链接进来的socket保存到集合中
			socketList.add(socket);
			System.out.println("用户接入 : " + socket.getPort());
			// 开启一个子线程来等待另外的socket加入
			new Thread(new Runnable() {
				public void run() {
					// 再次创建一个socket服务等待其他用户接入
					createMessage();
				}
			}).start();
			// 用于服务器推送消息给用户
			 getMessage();
			// 从客户端获取信息
			BufferedReader bff = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// 读取发来服务器信息
			String line = null;
			// 循环一直接收当前socket发来的消息
			while (true) {
				Thread.sleep(500);
				// System.out.println("内容 : " + bff.readLine());
				// 获取客户端的信息
				while ((line = bff.readLine()) != null) {
					// 解析实体类
					MessageBean messageBean = gson.fromJson(line, MessageBean.class);
					// 将用户信息添加进入map中，模仿添加进数据库和内存
					// 实体类存入数据库，socket存入内存中，都以用户id作为参照
					setChatMap(messageBean, socket);
					// 将用户发送进来的消息转发给目标好友
					getFriend(messageBean);
					System.out.println("用户 : " + userMap.get(messageBean.getUserId()).getUserName());
					System.out.println("内容 : " + messageBean.getContent());
				}
			}
			// server.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("错误 : " + e.getMessage());
		}
	}

	/**
	 * 发送消息
	 */
	private void getMessage() {
		new Thread(new Runnable() {
			public void run() {
				try {
					String buffer;
					while (true) {
						// 从控制台输入
						BufferedReader strin = new BufferedReader(new InputStreamReader(System.in));
						buffer = strin.readLine();
						// 因为readLine以换行符为结束点所以，结尾加入换行
						buffer += "\n";
						// 这里修改成向全部连接到服务器的用户推送消息
						for (Socket socket : socketMap.values()) {
							OutputStream output = socket.getOutputStream();
							output.write(buffer.getBytes("utf-8"));
							// 发送数据
							output.flush();
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 模拟添加信息进入数据库和内存
	 * 
	 * @param messageBean
	 * @param scoket
	 */
	private void setChatMap(MessageBean messageBean, Socket scoket) {
		// 将用户信息存起来
		if (userMap != null && userMap.get(messageBean.getUserId()) == null) {
			userMap.put(messageBean.getUserId(), UserData.getUserInfoBean(messageBean.getUserId()));
		}
		// 将对应的链接进来的socket存起来
		if (socketMap != null && socketMap.get(messageBean.getUserId()) == null) {
			socketMap.put(messageBean.getUserId(), scoket);
		}
	}

	

	/**
	 * 将消息转发给目标好友
	 * 
	 * @param messageBean
	 */
	private void getFriend(MessageBean messageBean) {
		if (socketMap != null && socketMap.get(messageBean.getFriendId()) != null) {
			Socket socket = socketMap.get(messageBean.getFriendId());
			String buffer = gson.toJson(messageBean);
			// 因为readLine以换行符为结束点所以，结尾加入换行
			buffer += "\n";
			try {
				// 向客户端发送信息
				OutputStream output = socket.getOutputStream();
				output.write(buffer.getBytes("utf-8"));
				// 发送数据
				output.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
