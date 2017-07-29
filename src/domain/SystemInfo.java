package domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;


@Entity
@Table(name="SYSTEMINFO")
/**系统参数**/
public class SystemInfo {
	@Id
	@GenericGenerator(name="key_increment",strategy="increment")
	@GeneratedValue(generator="key_increment")
	@Column(name="ID",nullable=false,unique=true,updatable=false)
	private Long id;			//标识
	@Column(name="IP")
	private String ip;			//服务器IP地址
	
	@Column(name="EMAIL_ADDR")
	private String email_addr;			//邮件服务器地址
	
	@Column(name="EMAIL_PORT")
	private int email_port;			//邮件服务器端口
	
	@Column(name="EMAIL_ACCOUNT")
	private String email_account;	    //邮件账户
	
	@Column(name="EMAIL_PWORD")
	private String email_pword;			//邮件密码
	
	@Column(name="SMS_PORT")
	private int sms_port;			//短信模块端口
	
	@Column(name="ACCOUNT_SID")
	private String account_sid;			//语音平台账户id
	
	@Column(name="AUTH_TOKEN")
	private String auth_token;			//语音平台auth_token
	
	@Column(name="APP_ID")
	private String app_id;			//语音平台app_id
	//id
	public Long getId(){
		return this.id;
	}
	//ip
	public void setIp(String ip){
		this.ip=ip;
	}
	public String getIp(){
		return this.ip;
	}
	//email_addr
	public void setEmailAddr(String addr){
		this.email_addr=addr;
	}
	public String getEmailAddr(){
		return this.email_addr;
	}
	//email_port
	public void setEmailPort(int port){
		this.email_port=port;
	}
	public int getEmailPort(){
		return this.email_port;
	}
	//email_account
	public void setEmailAccount(String account){
		this.email_account=account;
	}
	public String getEmailAccount(){
		return this.email_account;
	}
	//email_pword
	public void setEmailPword(String pword){
		this.email_pword=pword;
	}
	public String getEmailPword(){
		return this.email_pword;
	}
	//sms_port
	public void setSmsPort(int sms_port){
		this.sms_port=sms_port;
	}
	public int getSmsPort(){
		return this.sms_port;
	}
    //account_sid
	public void setAccountSid(String account_sid){
		this.account_sid=account_sid;
	}
	public String getAccountSid(){
		return this.account_sid;
	}
    //auth_token
	public void setAuthToken(String auth_token){
		this.auth_token=auth_token;
	}
	public String getAuthToken(){
		return this.auth_token;
	}
    //app_id
	public void setAppId(String app_id){
		this.app_id=app_id;
	}
	public String getAppId(){
		return this.app_id;
	}	
}
