package service;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.Socket;

public class ChatFileServer {
	private String fileSavePath = "C:\\Users\\Luhao\\Desktop\\索尼耳机";

	/**
	 * 接收文件方法
	 * 
	 * @param socket
	 * @throws IOException
	 */
	public void receiveFile(Socket socket, String filePath) {
		try {
			byte[] inputByte = null;
			int length = 0;
			DataInputStream dis = null;
			FileOutputStream fos = null;
			dis = new DataInputStream(socket.getInputStream());
			File f = new File(fileSavePath);
			if (!f.exists()) {
				f.mkdir();
			}
			/*
			 * 文件存储位置
			 */
			fos = new FileOutputStream(new File(filePath));
			inputByte = new byte[1024];
			double sumL = 0;
			System.out.println("开始接收数据...");
			while ((length = dis.read(inputByte, 0, inputByte.length)) > 0) {
				sumL += length;
				System.out.println("已接收：" + ((sumL / messageFileBean.getFileLength()) * 100) + "%");
				
				fos.write(inputByte, 0, length);
				fos.flush();
			}
			System.out.println("完成接收：" + filePath);
		} finally {
			if (fos != null)
				fos.close();
			if (dis != null)
				dis.close();
			if (socket != null)
				socket.close();
		}
	}
}
