package service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.socket.chat.SocketUrls;

import bean.MessageBean;

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

	// 使用map将用户信息和相对应的socket存起来
	// public Map<UserInfoBean, Socket> socketMap = new HashMap<>();

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
			// 开启一个子线程来等待另外的socket加入
			new Thread(new Runnable() {
				public void run() {
					createMessage();
				}
			}).start();
			// 循环一直接收当前socket发来的消息
			while (true) {
				Thread.sleep(500);
				MessageBean messageBean = (MessageBean) byteToObject(readBytes(socket.getInputStream()));
				// 在标准输出上打印从Server读入的字符串
				System.out.println("用户 : " + messageBean.getUserName());
				System.out.println("内容 : " + messageBean.getContent());
			}
			// server.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 将字节数组转换成对象
	 * 
	 * @param bytes
	 * @return
	 */
	private Object byteToObject(byte[] bytes) {
		if (bytes == null)
			return null;
		Object obj = null;
		try {
			// bytearray to object
			ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
			ObjectInputStream oi = new ObjectInputStream(bi);
			obj = oi.readObject();
			bi.close();
			oi.close();
		} catch (Exception e) {
			System.out.println("translation" + e.getMessage());
			e.printStackTrace();
		}
		return obj;
	}

	/**
	 * 将输入流穿换成字节数组
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static byte[] readBytes(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int n = 0;
		while ((n = in.read(buffer)) != -1) {
			out.write(buffer, 0, n);
		}
		return out.toByteArray();
	}
}
