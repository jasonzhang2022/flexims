package com.flexdms.flexims.report.rs;

import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;

public class ReportActionContext {

	FleximsDynamicEntityImpl fxReport;

	public FleximsDynamicEntityImpl getFxReport() {
		return fxReport;
	}

	public void setFxReport(FleximsDynamicEntityImpl fxReport) {
		this.fxReport = fxReport;
	}

}
