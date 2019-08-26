package com.bridgelabz.fundoo.response;

import lombok.Data;

@Data
public class ResponseToken {

	private String statusMessage;	
	private int statusCode;
	private String token;
	private String emailId;
	
	
	
	
}
