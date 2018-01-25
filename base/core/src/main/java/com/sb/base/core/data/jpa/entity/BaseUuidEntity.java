package com.sb.base.core.data.jpa.entity;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.GenericGenerator;

/**
 * uuid抽象实体基类，提供统一的ID，和相关的基本功能方法,
 * 
 * @author heshan
 * @param <ID>
 */
@MappedSuperclass
public class BaseUuidEntity<ID extends Serializable> extends AbstractEntity<ID> {
	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "uuidGenerator", strategy = "uuid")
	@GeneratedValue(generator = "uuidGenerator")
	protected ID id;

	public ID getId() {
		return id;
	}

	public void setId(ID id) {
		this.id = id;
	}

}
