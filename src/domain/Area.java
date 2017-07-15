package domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;

/***
 * 
 * 监测区域
 * 系统中一共具有三个监测区域:赤水、习水、仁怀
 * **/
@Entity
@Table(name="AREA")
public class Area {
    @Id
    @GenericGenerator(name="key_increment",strategy="increment")
    @GeneratedValue(generator="key_increment")
    @Column(name="ID",nullable=false,unique=true,updatable=false)
	private Long id;//区域标识
    
    @Column(name="NAME",nullable=false,unique=true,updatable=false)
   	private String name;//区域名称
    
    @Column(name="CODE",nullable=false,unique=true,updatable=false)
   	private String code;//区域代号 在CIMISS中的代号
       
//foreign key
	//Stations			获取该区域下的所有监测站
	@OneToMany(targetEntity=Stations.class,mappedBy="area")
	@Cascade({CascadeType.DELETE})
	private List<Stations> stations = new ArrayList<>();
	//获取当前区域下的所有监测站
	public List<Stations> getStations(){
		return this.stations; 
	} 
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
//code
	public void setCode(String code){
		this.code=code;
	}
	public String getCode(){
		return this.code;
	}	
}
