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
					createMessage();
				}
			}).start();
			// 向客户端发送信息
			OutputStream output = socket.getOutputStream();
			// 用于服务器推送消息
			getTouch(output);
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
					MessageBean messageBean = gson.fromJson(line, MessageBean.class);
					setUserMap(messageBean, socket);
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

	private void getTouch(OutputStream output) {
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
						output.write(buffer.getBytes("utf-8"));
						// 发送数据
						output.flush();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void setUserMap(MessageBean messageBean, Socket scoket) {
		if (userMap != null && userMap.get(messageBean.getUserId()) == null) {
			userMap.put(messageBean.getUserId(), getUserInfoBean(messageBean.getUserId()));
		}
		if (socketMap != null && socketMap.get(messageBean.getUserId()) == null) {
			socketMap.put(messageBean.getUserId(), scoket);
		}
	}

	private UserInfoBean getUserInfoBean(int userId) {
		UserInfoBean userInfoBean = new UserInfoBean();
		userInfoBean.setUserIcon("用户头像");
		userInfoBean.setUserId(userId);
		userInfoBean.setUserName("admin");
		userInfoBean.setUserPwd("123123132a");
		return userInfoBean;
	}

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
