package domain;

import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="WARN")
/***
 * 预警表
 * **/
public class Warn {

	@Id
	@GenericGenerator(name="key_increment",strategy="increment")
	@GeneratedValue(generator="key_increment")
	@Column(name="ID",nullable=false,unique=true,updatable=false)
	private Long id;			//告警标识
	
	@Column(name="STRATEGY_ID")
	private Long strategy_id;	   //策略标识
	
	@Column(name="TITLE")
	private String title;	       //预警标题
	
	@Column(name="ITEM")
	private String item;	       //预警要素 rainfall temp 降雨量、温度

	@Column(name="PARAM")
	private String param;			//告警要素 1h low、ave、high等
	
	@Column(name="TIME",length=50)
	private String time;		    //告警时间
	
	@Column(name="SOURCE",length=50)
	private String source;	       //预警来源，预警策略的名称或发布预警的用户
	@Lob
	@Column(name="CONTEXT",columnDefinition="LONGTEXT")
	private String context;	       //预警内容
	
	@Column(name="STATUS")
	private Boolean status;			//是否处理
	
	@Column(name="HANDLE_USER",length=25)
	private String handle_user;			//处理用户
	
	@Column(name="HANDLE_DATE",length=25)
	private String handle_date;			//处理时间
	
	@Column(name="INFO_WAY",length=10)  //预警通知方式三个字符分别表示短信、电话、邮件，为1表示采用，0表示不采用
	private String info_way;	       //预警内容
	
	//foreign key		
	@ManyToMany(fetch = FetchType.EAGER,targetEntity=Stations.class)
	@JoinTable(name="warn_stations",
	joinColumns=@JoinColumn(name="warns_id",referencedColumnName="id"),
    inverseJoinColumns=@JoinColumn(name="stations_id",referencedColumnName="id"))
	private Set<Stations> stations;//监测站
	public void setStations(Set<Stations> stations){
		this.stations = stations;
	}
	public Set<Stations> getStations(){
		return this.stations;
	}
	@ManyToMany(fetch = FetchType.EAGER,targetEntity=Liaisons.class)
	@JoinTable(name="warn_liaisons",
	joinColumns=@JoinColumn(name="warns_id",referencedColumnName="id"),
    inverseJoinColumns=@JoinColumn(name="liaisons_id",referencedColumnName="id"))
	private Set<Liaisons> liaisons;//告警消息发送的人员
	public void setLiaisons(Set<Liaisons> liaisons){
		this.liaisons = liaisons;
	}
	public Set<Liaisons> getLiaisons(){
		return this.liaisons;
	}
//id
	public Long getId(){
		return this.id;
	}
//strategy_id
	public void setStrategyId(Long strategyId){
		 this.strategy_id=strategyId;
	}
	public Long getStrategyId(){
		return this.strategy_id;
	}
	//info_way
	public void setInfoWay(String info_way){
		this.info_way=info_way;
	}
	public String getInfoWay(){
		return this.info_way;
	}	
//	item
	public void setItem(String item){
		 this.item=item;
	}
	public String getItem(){
		return this.item;
	}	
//time
	public void setTime(String time){
		this.time =time;
	}
	public String getTime(){
		return this.time;
	}
	
//param
	public void setParam(String param){
		this.param = param;
	}
	public String getParam(){
		return this.param;
	}
//title
	public void setTitle(String title){
		this.title = title;
	}
	public String getTitle(){
		return this.title;
	}
//status
	public void setStatus(boolean status){
		this.status = status;
	}
	public boolean getStatus(){
		return this.status;
	}
//handle_user
	public void setHandleUser(String handle_user){
		this.handle_user = handle_user;
	}
	public String getHandleUser(){
		return this.handle_user;
	}
//handle_date
	public void setHandleDate(String handle_date){
		this.handle_date = handle_date;
	}
	public String getHandleDate(){
		return this.handle_date;
	}
//context
	public void setContext(String context){
		this.context = context;
	}
	public String getContext(){
		return this.context;
	}
//resource	
	public void setSource(String source){
		this.source = source;
	}
	public String getSource(){
		return this.source;
	}
}
