package org.codejudge.sb.model;

import lombok.Data;

@Data
public class RequestLogModel {

	private String[] logFiles;
	
	private int parallelFileProcessingCount;
}
