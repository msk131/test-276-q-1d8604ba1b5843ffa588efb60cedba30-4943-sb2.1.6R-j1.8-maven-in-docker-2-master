package org.codejudge.sb.services.Impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.codejudge.sb.model.Logs;
import org.codejudge.sb.model.RequestLogModel;
import org.codejudge.sb.model.ResponseLogModel;
import org.codejudge.sb.services.LogParserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LogParserServiceImpl implements LogParserService{

	Logger logger = LoggerFactory.getLogger(LogParserServiceImpl.class);

	@Override
	public Set<ResponseLogModel> parseLogFiles(RequestLogModel logDetail) {

		logger.debug("logDetail {}", logDetail.toString() );

		ExecutorService fixedPool = Executors.newFixedThreadPool(logDetail.getParallelFileProcessingCount());
		Map<String, Map<String, Logs>> responseMap = new HashMap<>();
		for(int i=0; i< 	logDetail.getLogFiles().length; i++) {

			final int j=i;


			FutureTask<String> task= new FutureTask<>(new  Runnable() {

				@Override
				public void run() {
					BufferedReader in = null;

					URL oracle;
					try {
						oracle = new URL(logDetail.getLogFiles()[j]);
						in = new BufferedReader(new InputStreamReader(oracle.openStream()));

						String inputLine;
						while ((inputLine = in.readLine()) != null) {

							logger.debug("inputLine {}", inputLine );

							String[] str =inputLine.split(" ");
							String exception = str[2];
							long milliSeconds= Long.parseLong(str[1]);

							Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
							calendar.setTimeInMillis(milliSeconds);
							logger.debug("calendar {}", calendar.getTime() );

							int hour =calendar.get(Calendar.HOUR_OF_DAY);
							int minute =calendar.get(Calendar.MINUTE);  
							logger.debug("calendar hour {}, minute{}", hour, minute );
							if(minute >0 && minute <= 15) {

								minute =0;
							}else if(minute >15 && minute <= 30) {

								minute =15;
							}else if(minute >30 && minute <= 45) {

								minute =30;
							}else if(minute >45 && minute <= 60) {

								minute =45;
							}

							StringBuilder key = new StringBuilder();
							key.append(hour).append(":").append(minute)
							.append("-").append(hour).append(":").append(minute+15);
							logger.debug("key {}, minute{}", key );

							if(responseMap.containsKey(key.toString())) {

								Logs logs = new Logs();
								logs.setException(exception);
								Map<String, Logs> map =responseMap.get(key.toString());
								Logs update;
								if(map.containsKey(exception)) {

									update = map.get(key.toString());
									update.setCount(update.getCount()+1);


								}else {

									update = new Logs();
									update.setException(exception);
									update.setCount(1);
								}
								map.put(exception, update);
								responseMap.put(key.toString(), map);
							}else {

								Map<String, Logs> map  = new HashMap<>();
								Logs update = new Logs();
								update.setException(exception);
								update.setCount(1);
								map.put(key.toString(), update);
								responseMap.put(key.toString(), map);
							}

						}
					} catch (MalformedURLException e) {

						e.printStackTrace();
					} catch (IOException e) {

						e.printStackTrace();
					}finally {

						try {
							in.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

				}
			}, "");
			fixedPool.submit(task);
		}

		fixedPool.shutdown();
		try {
			fixedPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {

		}
		logger.debug("responseMap {}, minute{}", responseMap.toString() );

		Set<ResponseLogModel> response = new TreeSet<>();
		for(Map.Entry<String, Map<String, Logs>> entry : responseMap.entrySet()) {

			ResponseLogModel responseLogModel = new ResponseLogModel();
			responseLogModel.setTiestamp(entry.getKey());

			List<Logs> logs = new ArrayList<>();;
			for(Map.Entry<String,Logs> logsListMap : entry.getValue().entrySet()) {

				logs.add(logsListMap.getValue());
			}
			responseLogModel.setLogs(logs);

			response.add(responseLogModel);
		}
		logger.debug("response {}, minute{}", response.toString() );
		
		return response;
	}
}