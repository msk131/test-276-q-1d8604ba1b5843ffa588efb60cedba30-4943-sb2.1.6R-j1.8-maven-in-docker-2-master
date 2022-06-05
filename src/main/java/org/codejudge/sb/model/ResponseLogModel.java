package org.codejudge.sb.model;

import java.util.List;

import lombok.Data;

@Data
public class ResponseLogModel implements Comparable<ResponseLogModel> {

	private String tiestamp;
	private List<Logs> logs;


	@Override
	public int compareTo(ResponseLogModel o) throws NumberFormatException, ArrayIndexOutOfBoundsException{

		String substr =this.tiestamp.substring(0, this.tiestamp.indexOf("-"));
		String[] hoursMinutes=substr.split(":");
		substr =o.tiestamp.substring(0, o.tiestamp.indexOf("-"));
		String[] hoursMinutesO=substr.split(":"); 
		 
		if(Integer.valueOf(hoursMinutes[0]) == Integer.valueOf(hoursMinutesO[0])) {
			
			if (Integer.valueOf(hoursMinutes[1]) > Integer.valueOf(hoursMinutesO[1])) {
				return 1;
			} else {
				return -1;
			}
		}else if (Integer.valueOf(hoursMinutes[0]) > Integer.valueOf(hoursMinutesO[0])) {
			return 1;
		} else {
			return -1;
		}
	}
}
