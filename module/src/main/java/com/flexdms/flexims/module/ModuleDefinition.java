package com.flexdms.flexims.module;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ModuleDefinition {
	String module;

	String types;

	boolean withdata=true;
	String reporttype="typeonly";
	long startid=1000;
	long queryStartid=1000;
	long reportStartid=1000;
}
