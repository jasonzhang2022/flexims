package com.flexdms.flexims.rsutil;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("rawtypes")
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Entities {

	List items = new LinkedList();

	@XmlAnyElement(lax = true)
	public List getItems() {
		return items;
	}

	public void setItems(List items) {
		this.items = items;
	}

	@SuppressWarnings("unchecked")
	public Entities addItem(Object... objs) {
		if (items == null) {
			items = new LinkedList<>();
		}
		for (Object obj : objs) {
			items.add(obj);
		}
		return this;
	}

}
