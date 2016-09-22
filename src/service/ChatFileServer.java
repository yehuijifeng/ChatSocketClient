package service;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import bean.MessageBean;

public class ChatFileServer {
	private String fileSavePath = "C:\\Users\\Luhao\\Desktop\\索尼耳机";

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
		ObjectInputStream oi = null;
		ByteArrayInputStream bi = null;
		MessageBean messageBean = null;
		double sumL = 0;
		try {
			dis = new DataInputStream(socket.getInputStream());
			inputByte = new byte[1024];
			System.out.println("开始接收数据...");
			while ((length = dis.read(inputByte, 0, inputByte.length)) > 0) {
				sumL += length;
				bi = new ByteArrayInputStream(inputByte, 0, length);
				oi = new ObjectInputStream(bi);
  
				// System.out.println("已接收：" + ((sumL /
				// messageFileBean.getFileLength()) * 100) + "%");
				// fos.write(inputByte, 0, length);
				// fos.flush();
			}
			messageBean = (MessageBean) oi.readObject();
			System.out.println("完成接收：");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (oi != null)
					oi.close();
				if (bi != null)
					bi.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
}
