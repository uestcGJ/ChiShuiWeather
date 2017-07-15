package domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/***
 * 联络组
 * ***/
@Entity
@Table(name="LIAISONUNIT")
public class LiaisonUnit {
	@Id
	@GenericGenerator(name="key_increment",strategy="increment")
	@GeneratedValue(generator="key_increment")
	@Column(name="ID",nullable=false,unique=true,updatable=false)
	private Long id;
	@Column(name="NAME",unique=true,length=150)
	private String name;
	
	@Column(name="CREATE_USER",length=100)
	private String create_user;
	
	@Column(name="CREATE_DATE",length=50)
	private String create_date;
	
	@Column(name="DESCRIPTION")
	private String description;
	
	//联络人
	@OneToMany(targetEntity=Liaisons.class,cascade=CascadeType.ALL,mappedBy="LiaisonUnit")
	private Set<Liaisons> liaisons = new HashSet<Liaisons>();
	public Set<Liaisons>  getLiaisons(){
		return this.liaisons;
	}
	//id
	public Long getId(){
		return this.id;
	}
	//name
	public String getName(){
		return this.name;
	}
	public void setName(String name){
		this.name=name;
	}
	//description
	public String getDescription(){
		return this.description;
	}
	public void setDescription(String description){
		this.description=description;
	}
	//create_user
	public String getCreateUser(){
		return this.create_user;
	}
	public void setCreateUser(String createUser){
		this.create_user=createUser;
	}
	//create_date
	public String getCreateDate(){
		return this.create_date;
	}
	public void setCreateDate(String createDate){
		this.create_date=createDate;
	}	
}
