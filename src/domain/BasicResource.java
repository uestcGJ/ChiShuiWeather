package domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="BASIC_RESOURCE")
public class BasicResource {
	@Id
	@GenericGenerator(name="key_increment",strategy="increment")
	@GeneratedValue(generator="key_increment")
	@Column(name="ID",nullable=false,unique=true,updatable=false)
	private Long id;			//基信标识
	@Column(name="TITLE")
	private String title;	   //基信标题
	@Lob
	@Column(name="CONTEXT",columnDefinition="LONGTEXT")
	private String context;	   //文件内容	
	@Column(name="DESCRIPTION")
	private String description;	//基信描述
	
	//id
	public Long getId(){
		return this.id;
	}
	//title
	public String getTitle(){
		return this.title;
	}
	public void setTitle(String title){
		this.title=title;
	}
	//context
	public String getContext(){
		return this.context;
	}
	public void setContext(String context){
		this.context=context;
	}
	//description
	public String getDescription(){
		return this.description;
	}
	public void setDescription(String description){
		this.description=description;
	}
}
