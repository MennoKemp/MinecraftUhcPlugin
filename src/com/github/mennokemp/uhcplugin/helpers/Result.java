package com.github.mennokemp.uhcplugin.helpers;

public class Result 
{
	private final boolean isValid;
	private final String message;
	
	public Result(boolean isValid, String message) 
	{
		this.isValid = isValid;
		this.message = message;
	}
	
	public static Result failure(String message)
	{
		return new Result(false, message);
	}
	
	public static Result success(String message)
	{
		return new Result(true, message);
	}
	
	public boolean isSuccessful()
	{
		return isValid;
	}
	
	public String getMessage()
	{
		return message;
	}
}
