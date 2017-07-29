package domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * 联络人  包括各级领导和告警相关联络人员
 * ***/
@Entity
@Table(name="LIAISONS")
public class Liaisons {
	@Id
	@GenericGenerator(name="key_increment",strategy="increment")
	@GeneratedValue(generator="key_increment")
	@Column(name="ID",nullable=false,unique=true,updatable=false)
	private Long id;
	
	@Column(name="NAME")
	private String name;//联络人姓名
	
	@Column(name="PHONE")
	private String phone;//联络人手机号
	
	@Column(name="TITLE")
	private String title;//联络人头衔
	
	@Column(name="DESCRIPTION")
	private String description;//联络人描述
	
	@Column(name="EMAIL")
	private String email;//联络人邮箱
	
	//foreign key  LiaisonUnit		
	@ManyToOne(targetEntity=LiaisonUnit.class)
	@JoinColumn(name="UNIT_ID",referencedColumnName="ID",nullable=false)
	private LiaisonUnit LiaisonUnit;
	public LiaisonUnit getLiaisonUnit(){
		return this.LiaisonUnit;
	}
	public void setLiaisonUnit(LiaisonUnit LiaisonUnit){
		 this.LiaisonUnit=LiaisonUnit;
	}
	@ManyToMany(mappedBy="liaisons") 
	private List<WarnStrategy> warnStrategy;
	
	public List<WarnStrategy> getWarnStrategy(){
		return this.warnStrategy;
	}
	
	@ManyToMany(fetch = FetchType.EAGER,targetEntity=Warn.class)
	@JoinTable(name="warn_liaisons",
	joinColumns=@JoinColumn(name="liaisons_id",referencedColumnName="id"),
    inverseJoinColumns=@JoinColumn(name="warns_id",referencedColumnName="id"))
	private List<Warn> warns;
	public List<Warn> getWarns(){
		return this.warns;
	}
    //id
	public long getId(){
		return this.id;
	}
    //name
	public String getName(){
		return this.name;
	}
	public void setName(String name){
		this.name=name;
	}
   //phone
	public String getPhone(){
		return this.phone;
	}
	public void setPhone(String phone){
		this.phone=phone;
	}
	//title 
	public String getTitle(){
		return this.title;
	}
	public void setTitle(String title){
		this.title=title;
	}
	//email
	public String getEmail(){
		return this.email;
	}
	public void setEmail(String email){
		this.email=email;
	}
   //description	
	public String getDescription(){
		return this.description;
	}
	public void setDescription(String description){
		this.description=description;
	}
}
