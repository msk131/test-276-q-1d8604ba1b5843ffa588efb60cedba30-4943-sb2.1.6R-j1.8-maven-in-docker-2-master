package org.codejudge.sb.controller;

import io.swagger.annotations.ApiOperation;
import lombok.Data;

import org.codejudge.sb.model.RequestLogModel;
import org.codejudge.sb.services.LogParserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class AppController {
	
    Logger logger = LoggerFactory.getLogger(AppController.class);

	@Autowired
	LogParserService logParserService;

    @ApiOperation("The API will take a list of log files")
    @PostMapping("/api/process-logs/")
    public ResponseEntity<?> processLogFiles(@RequestBody RequestLogModel logDetail) {
    	
    	logger.debug(logDetail.toString());
    		if(logDetail.getParallelFileProcessingCount() ==0 || 
    				logDetail.getLogFiles().length >30 || logDetail.getParallelFileProcessingCount() >15) {
    			
    			ErrorResponse errorResponse = new ErrorResponse();
    	          errorResponse.setStatus("failure");
    	          errorResponse.setReason("Parallel File Processing count must be greater than zero!");
    	          return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    		}
    	 return new ResponseEntity<>(
    			 logParserService.parseLogFiles(logDetail), HttpStatus.OK);
    }

}

@Data
class ErrorResponse {
	
	private String status;
    private String reason;
    
}
