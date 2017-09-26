package com.beibeilian.privateletter.model;

public class MessageList {

	private String outuser;
	
	public String getOutuser() {
		return outuser;
	}

	public void setOutuser(String outuser) {
		this.outuser = outuser;
	}

	private String fromuser;
	
	private String fromname;
	
	private String fromcontent;
	
	private String fromtime;
	
	private String fromnoreadcount;
	
	private String fromphotourl;

	public String getFromuser() {
		return fromuser;
	}

	public void setFromuser(String fromuser) {
		this.fromuser = fromuser;
	}

	public String getFromname() {
		return fromname;
	}

	public void setFromname(String fromname) {
		this.fromname = fromname;
	}

	public String getFromcontent() {
		return fromcontent;
	}

	public void setFromcontent(String fromcontent) {
		this.fromcontent = fromcontent;
	}

	public String getFromtime() {
		return fromtime;
	}

	public void setFromtime(String fromtime) {
		this.fromtime = fromtime;
	}

	public String getFromnoreadcount() {
		return fromnoreadcount;
	}

	public void setFromnoreadcount(String fromnoreadcount) {
		this.fromnoreadcount = fromnoreadcount;
	}

	public String getFromphotourl() {
		return fromphotourl;
	}

	public void setFromphotourl(String fromphotourl) {
		this.fromphotourl = fromphotourl;
	}

	@Override 
    public boolean equals(Object obj) { 
 
        // TODO Auto-generated method stub  
        //������Լ�  
        if(this==obj){ 
            return true ; 
 
        } 
        //����ǿ�  
        if(obj==null ){ 
            return false; 
        } 
        //�Ƚ�����People�������Ƿ���ͬ  
        if(obj!=null && obj instanceof MessageList){ 
            if((((MessageList)obj).outuser.equals(this.outuser))||(((MessageList)obj).fromuser.equals(this.fromuser))) 
                return  true ; 
 
        } 
        return false; 
 
    } 
    @Override 
    public int hashCode() { 
 
        return outuser.hashCode()*fromuser.hashCode(); 
 
    } 
	
	
	
}
