package org.codejudge.sb.model;

import lombok.Data;

@Data
public class Logs {

	private String exception;
	private int count;

	@Override
	public int hashCode() {

		return this.exception.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{

		// checking if both the object references are 
		// referring to the same object.
		if(this == obj)
			return true;

		if(obj == null || obj.getClass()!= this.getClass())
			return false;

		// type casting of the argument. 
		Logs logs = (Logs) obj;

		// comparing the state of argument with 
		// the state of 'this' Object.
		return (logs.exception.equalsIgnoreCase(this.exception));
	}
}
