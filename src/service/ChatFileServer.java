package service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.google.gson.Gson;
import com.socket.chat.SocketUrls;

import bean.MessageBean;
import bean.MessageFileBean;

public class ChatFileServer {
	// socket服务
	private static ServerSocket server;
	// 文件保存路径
	private String fileSavePath = "C:\\Users\\Luhao\\Desktop\\索尼耳机";
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
		FileOutputStream fos = null;
		try {
			System.out.println("等待用户接入 : ");
			// 使用accept()阻塞等待客户请求
			Socket socket = server.accept();
			System.out.println("用户接入 : " + socket.getPort());
			// 开启一个子线程来等待另外的socket加入
			new Thread(new Runnable() {
				public void run() {
					// 再次创建一个socket服务等待其他用户接入
					createMessage();
				}
			}).start();
			// 用于服务器推送消息给用户
			getMessage(socket);
			// 从客户端获取信息
			InputStream is = socket.getInputStream();
			// 从客户端获取信息
			BufferedReader bff = new BufferedReader(new InputStreamReader(is));

			// 循环一直接收当前socket发来的消息
			while (true) {
				Thread.sleep(500);
				int length = 0;
				byte[] b = new byte[1024];
				String json = null;
				// 1、首先先得到实体类
				while ((json = bff.readLine()) != null) {
					// json = new String(b);
					MessageBean messageBean = gson.fromJson(json, MessageBean.class);
					if (messageBean.getChatFile() == null)
						continue;
					System.out.println("接受到的文件名为：" + messageBean.getChatFile().getFileName());
					String fileNewName = messageBean.getChatFile().getFileName() + "."
							+ messageBean.getChatFile().getFileTitle();
					System.out.println("新生成的文件名为:" + fileNewName);
					fos = new FileOutputStream("D:\\" + fileNewName);
					length = 0;
					int fileSzie = 0;
					// 2、把socket输入流写到文件输出流中去
					while ((length = is.read(b)) != -1) {
						fos.write(b, 0, length);
						fileSzie += length;
						System.out.println("当前大小：" + fileSzie);
						System.out.println("总大小：" + messageBean.getChatFile().getFileLength());
						if (fileSzie == messageBean.getChatFile().getFileLength()) {
							break;
						}
					}
					fos.close();
					System.out.println("文件:保存成功");
					// System.out.println("用户 : " + messageBean.getFriendId());
					// System.out.println("内容 : " + messageBean.getContent());
				}
			}
			// server.close();
		} catch (Exception e) {
			try {
				if (fos != null)
					fos.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
			System.out.println("错误 : " + e.getMessage());
		}

	}

	/**
	 * 接收文件方法
	 * 
	 * @param socket
	 * @throws IOException
	 */
	public MessageBean receiveFile(Socket socket) {
		byte[] inputByte = null;
		int length = 0;
		DataInputStream dis = null;
		MessageBean messageBean = null;
		double sumL = 0;
		try {
			dis = new DataInputStream(socket.getInputStream());
			inputByte = new byte[1024];
			while ((length = dis.read(inputByte, 0, inputByte.length)) > 0) {
				System.out.println("开始接收数据...");
				sumL += length;
				setFile(inputByte);
			}
			System.out.println("完成接收：");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return messageBean;
		}
	}

	/**
	 * 添加文件到本地
	 * 
	 * @param filePath
	 */
	@SuppressWarnings("resource")
	public boolean setFile(byte[] fileByte) {
		if (fileByte == null)
			return false;
		try {
			FileOutputStream fos = null;
			File file = new File(fileSavePath);
			if (!file.exists()) {
				file.mkdir();
			}
			/*
			 * 文件存储位置
			 */
			fos = new FileOutputStream(file);
			fos.write(fileByte);
			fos.flush();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}

	/**
	 * 将字节转换为对象
	 * 
	 * @param bytes
	 * @return
	 */
	public static Object byteToObject(byte[] bytes) {
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
	 * 发送消息
	 */
	private void getMessage(Socket socket) {
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
						OutputStream output = socket.getOutputStream();
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
}
