package domain;

import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="STATIONS")
/***
 * 监测站点
 * */
public class Stations {
	@Id
	@GenericGenerator(name="key_increment",strategy="increment")
	@GeneratedValue(generator="key_increment")
	@Column(name="ID",nullable=false,unique=true,updatable=false)
	private Long id;			//局站标识
	
	@Column(name="NAME",nullable=false)
	private String name;		//局站名
	
	@Column(name="DESCRIPTION")
	private String description;		//描述
	
	@Column(name="TYPE")//监测站类型，监测站分为雨量监测站、温度监测站和双要素监测站
	private String type;		
	
	@Column(name="CODE",unique=true)//监测站编号,在CIMISS系统中的编号,必须唯一
	private String code;		
	
	@Column(name="AREA_CODE")//地区编号,在CIMISS系统中的编号,必须唯一
	private String area_code;		
	
	@Column(name="CNTY")//监测站所在乡镇
	private String cnty;		
	
	@Column(name="LNG")	
	private String lng; 	//经度
	
	@Column(name="LAT")	
	private String lat; 	//纬度
	
//foreign key
	
	//Areas			为该监测站配置区域属性
	@ManyToOne(targetEntity=Area.class)
	@JoinColumn(name="AREA_ID",referencedColumnName="ID",nullable=true,updatable=false)
	private Area area;					//关联表：区域
	public void setArea(Area area){
		this.area = area;
	}
	public Area getArea(){		//查询该局站所属的区域
		return this.area;
	}
	@ManyToMany(fetch = FetchType.EAGER, targetEntity=WarnStrategy.class)//,cascade={CascadeType.ALL}
	@JoinTable(name="warnstrategy_stations",
	joinColumns=@JoinColumn(name="stations_id",referencedColumnName="id"),
    inverseJoinColumns=@JoinColumn(name="warnstrategy_id",referencedColumnName="id"))
	private List<WarnStrategy> warnStrategy;
	public List<WarnStrategy> getWarnStrategy(){
		return this.warnStrategy;
	}
	@ManyToMany(fetch = FetchType.EAGER,targetEntity=WarnStrategy.class)//,cascade={CascadeType.ALL}
	@JoinTable(name="warn_stations",
	joinColumns=@JoinColumn(name="stations_id",referencedColumnName="id"),
    inverseJoinColumns=@JoinColumn(name="warns_id",referencedColumnName="id"))
	private List<Warn> warns;
	public List<Warn> getWarns(){
		return this.warns;
	}
	
//Stations()
	public Stations(){}
	
//id
	public Long getId(){
		return this.id;
	}
//name
	public void setName(String name){
		this.name = name;
	}
	public String getName(){
		return this.name;
	}
//cnty
	public void setCnty(String cnty){
		this.cnty = cnty;
	}
	public String getCnty(){
		return this.cnty;
	}
//description
	public void setDescription(String description){
		this.description = description;
	}
	public String getDescription(){
		return this.description;
	}	
//longitude
	public void setLng(String longitude){
		this.lng = longitude;
	}
	public String getLng(){
		return this.lng;
	}
	
//latitude
	public void setLat(String latitude){
		this.lat = latitude;
	}
	public String getLat(){
		return this.lat;
	}
//code
	public void setCode(String code){
		this.code=code;
	}
	public String getCode(){
		return this.code;
	}
//area_code
	public void setAreaCode(String area_code){
		this.area_code=area_code;
	}
	public String getAreaCode(){
		return this.area_code;
	}	
//type
	public void setType(String type){
		this.type=type;
	}
	public String getType(){
		return this.type;
	}
}
