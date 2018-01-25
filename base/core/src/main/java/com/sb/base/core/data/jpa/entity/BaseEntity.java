package com.sb.base.core.data.jpa.entity;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * 抽象实体基类，提供统一的ID，和相关的基本功能方法 如果ID是使用sequece策略请参考{@link BaseSeqEntity}
 * 
 * @author heshan
 * @param <ID>
 */
@MappedSuperclass
public class BaseEntity<ID extends Serializable> extends AbstractEntity<ID> {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected ID id;

	@Override
	public ID getId() {
		return id;
	}

	@Override
	public void setId(ID id) {
		this.id = id;
	}

}
