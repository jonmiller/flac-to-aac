// Jon Miller
// File: WrongTypeException.java
// Project: FlacToAac


package com.tetyl.flactoaac.exceptions;

public class WrongTypeException extends Exception
{
	private static final long serialVersionUID = 1L;
	
	public WrongTypeException(String message)
	{
		super(message);
	}
}