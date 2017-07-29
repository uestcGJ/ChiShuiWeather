package domain;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * 预警策略
 * ***/
@Entity
@Table(name="WarnStrategy")
public class WarnStrategy {
	@Id
	@GenericGenerator(name="key_increment",strategy="increment")
	@GeneratedValue(generator="key_increment")
	@Column(name="ID",nullable=false,unique=true,updatable=false)
	private Long id;
	
	@Column(name="NAME")
	private String name;//策略名称
	
	@Column(name="ITEM")
	private String item;//策略主题，即为预警要素 eg 温度、降雨量
	
	@Column(name="PARAM")
	private String param;//预警参数，eg整点、三小时、六小时
	
	@Column(name="THRESHOLD")
	private float threshold;//预警门限
	
	@Column(name="INFO_WAY",length=10)  //预警通知方式，三个字符分别表示短信、电话、邮件，为1表示采用，0表示不采用
	private String info_way;	      
	
	@Column(name="STATUS",columnDefinition="boolean default true")
	private boolean status;//启用状态  true表示启用、false表示未启用
	
	@Column(name="CREATE_USER")
	private String createUser;//创建人员
	
	@Column(name="CREATE_DATE")
	private String createDate;//创建日期
	//策略适用站点
	@ManyToMany(fetch = FetchType.EAGER,targetEntity=Stations.class)
	@JoinTable(name="warnstrategy_stations",
				joinColumns=@JoinColumn(name="warnstrategy_id",referencedColumnName="id"),
	            inverseJoinColumns=@JoinColumn(name="stations_id",referencedColumnName="id"))
	private Set<Stations> stations;
	public Set<Stations> getStations(){
		return this.stations;
	}
	public void setStations(Set<Stations> stations){
		this.stations=stations;
	}
	//报警联络人 
	@ManyToMany
	@JoinTable(name="warnstrategy_liaisons",
	joinColumns=@JoinColumn(name="warnstrategy_id",referencedColumnName="id"),
    inverseJoinColumns=@JoinColumn(name="liaisons_id",referencedColumnName="id"))
	private Set<Liaisons> liaisons;
	public Set<Liaisons> getLiaisons(){
		return this.liaisons;
	}
	public void setLiaisons(Set<Liaisons> liaisons){
		 this.liaisons=liaisons;
	}
	//id
	public long getId(){
		return this.id;
	}
    //name
	public void setName(String name){
		this.name=name;
	}
	public String getName(){
		return this.name;
	}
	//info_way
	public void setInfoWay(String info_way){
		this.info_way=info_way;
	}
	public String getInfoWay(){
		return this.info_way;
	}
	//item
	public void setItem(String item){
		this.item=item;
	}
	public String getItem(){
		return this.item;
	}
	//param
	public void setParam(String param){
		this.param=param;
	}
	public String getParam(){
		return this.param;
	}
	//threshold
	public void setThreshold(float threshold){
		 this.threshold=threshold;
	}
	public float getThreshold(){
		return this.threshold;
	}
	//status
	public void setStatus(boolean status){
		this.status=status;
	}
	public boolean getStatus(){
		return this.status;
	}
	//createUser
	public void setCreateUser(String createUser){
		this.createUser=createUser;
	}
	public String getCreateUser(){
		return this.createUser;
	}
	//createDate
	public void setCreateDate(String createDate){
		this.createDate=createDate;
	}
	public String getCreateDate(){
		return this.createDate;
	}
}
