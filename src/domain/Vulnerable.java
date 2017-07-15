package domain;
/***
 * 易受灾站点
 * 
 * **/

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="VULNERABLE")
public class Vulnerable {
	@Id
	@GenericGenerator(name="key_increment",strategy="increment")
	@GeneratedValue(generator="key_increment")
	@Column(name="ID",nullable=false,unique=true,updatable=false)
    private Long id;//标识
	
	@Column(name="NAME")	//名称
	private String name;
	
	@Column(name="LNG")	
	private String lng; 	//经度
	
	@Column(name="LAT")	
	private String lat; 	//纬度
	
	@Column(name="CNTY")	
	private String cnty; 	//乡镇
	
	@Column(name="ECP_NAME")	//紧急联系人姓名
	private String ECP_name;
	
	@Column(name="ECP_EMAIL")	//紧急联系人邮箱
	private String ECP_email;
	
	@Column(name="ECP_PHONE")	//紧急联系人手机号
	private String ECP_phone;
	
	@Column(name="RES_POP")	//常住人口
	private String res_pop;
	
	@Column(name="TYPE")	//易受灾类型
	private String type;
	@Lob
	@Column(name="EVA_ROUTE",columnDefinition="LONGTEXT")	//疏散路线
	private String eva_route;
	@Lob
	@Column(name="DESCRIPTION",columnDefinition="LONGTEXT")	//易受灾点描述
	private String description;

//id	
	public Long getId(){
		return this.id;
	}
//name 
	public void setName(String name){
		this.name=name;
	}
	public String getName(){
		return this.name;
	}
//cnty
	public void setCnty(String cnty){
		this.cnty=cnty;
	}
	public String getCnty(){
		return this.cnty;
	}
//type
	public void setType(String type){
		this.type=type;
	}
	public String getType(){
		return this.type;
	}	
//lng  经度
	public void setLng(String lng){
		this.lng=lng;
	}
	public String getLng(){
		return this.lng;
	}
//lat 纬度
	public void setLat(String lat){
		this.lat=lat;
	}
	public String getLat(){
		return this.lat;
	}
//ECP_name 紧急联系人名称
	public void setECPName(String ECP_name){
		this.ECP_name=ECP_name;
	}
	public String getECPName(){
		return this.ECP_name;
	}
//ECP_Email	紧急联系人邮箱
	public void setECPEmail(String ECP_email){
		this.ECP_email=ECP_email;
	}
	public String getECPEmail(){
		return this.ECP_email;
	}
//ECP_Phone	紧急联系人手机号
	public void setECPPhone(String ECP_phone){
		this.ECP_phone=ECP_phone;
	}
	public String getECPPhone(){
		return this.ECP_phone;
	}	
//res_pop
	public void setResPop(String resPop){
		this.res_pop=resPop;
	}
	public String getResPop(){
		return this.res_pop;
	}
//eva_route	疏散路线
	public void setEvaRoute(String eva_route){
		this.eva_route=eva_route;
	}
	public String getEvaRoute(){
		return this.eva_route;
	}
//description
	public void setDescription(String description){
		this.description=description;
	}
	public String getDescription(){
		return this.description;
	}
}
