package com.beibeilian.me.model;
/**
 * 
 * �汾����ʵ����
 *
 */
public class Version {
	
	private String Code;//�汾��
	
	private String Size;//�ļ���С
	
	private String Url;//���µ�ַ
	
	private String content;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Version() {
	}

	public Version(String code, String size, String url) {
		Code = code;
		Size = size;
		Url = url;
	}

	public String getCode() {
		return Code;
	}

	public void setCode(String code) {
		Code = code;
	}


	public String getSize() {
		return Size;
	}

	public void setSize(String size) {
		Size = size;
	}

	public String getUrl() {
		return Url;
	}

	public void setUrl(String url) {
		Url = url;
	}	
}
