package com.sb.base.core.data.jpa.entity;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * sequence抽象实体基类，提供统一的ID，和相关的基本功能方法, <br/>
 * 如果是如mysql这种自动生成主键的，请参考{@link BaseEntity} <br/>
 * 子类只需要在类头上加 @SequenceGenerator(name="seq", sequenceName="你的sequence名字")
 * 
 * @author heshan
 * @param <ID>
 */
@MappedSuperclass
public class BaseSeqEntity<ID extends Serializable> extends AbstractEntity<ID> {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
	protected ID id;

	public ID getId() {
		return id;
	}

	public void setId(ID id) {
		this.id = id;
	}

}
