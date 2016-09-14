package bean;

import java.io.Serializable;

public class MessageFileBean implements Serializable {
	private static final long serialVersionUID = 2L;
	private int fileId;//文件id
	private String fileName;//文件�?
	private long fileLength;//文件长度
	private Byte[] fileByte;//文件内容
	private String fileType;//文件类型
	private String fileTitle;//文件头名�?
	public int getFileId() {
		return fileId;
	}
	public void setFileId(int fileId) {
		this.fileId = fileId;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public long getFileLength() {
		return fileLength;
	}
	public void setFileLength(long fileLength) {
		this.fileLength = fileLength;
	}
	public Byte[] getFileByte() {
		return fileByte;
	}
	public void setFileByte(Byte[] fileByte) {
		this.fileByte = fileByte;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getFileTitle() {
		return fileTitle;
	}
	public void setFileTitle(String fileTitle) {
		this.fileTitle = fileTitle;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
