package org.codejudge.sb;

import static org.junit.Assert.assertTrue;

import org.codejudge.sb.model.RequestLogModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTests {
    Logger logger = LoggerFactory.getLogger(ApplicationTests.class);


	@LocalServerPort
	private int port;

	TestRestTemplate restTemplate = new TestRestTemplate();

	HttpHeaders headers = new HttpHeaders();

	@Test
	public void testRetrieveStudentCourse() {

		RequestLogModel requestLogModel = new RequestLogModel();
		
		String[] logFiles= {"https://codejudge-question-artifacts.s3.ap-south-1.amazonaws.com/q-120/log1.txt"};
		int parallelFileProcessingCount=1;
		
		requestLogModel.setLogFiles(logFiles);
		requestLogModel.setParallelFileProcessingCount(parallelFileProcessingCount);
		HttpHeaders headers = new HttpHeaders();
		

		HttpEntity<RequestLogModel> req = new HttpEntity<RequestLogModel>(requestLogModel, headers);

		ResponseEntity<String> response = restTemplate.exchange(
				createURLWithPort("/api/process-logs/"),
				HttpMethod.POST, req, String.class);
		logger.debug(response.getBody());
		assertTrue(response.getBody() != null);
	}

	private String createURLWithPort(String uri) {
		return "http://localhost:" + port + uri;
	}
}
