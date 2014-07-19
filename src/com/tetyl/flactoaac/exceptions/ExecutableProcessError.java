// Jon Miller
// File: ExecutableProcessError.java
// Project: FlacToAac

package com.tetyl.flactoaac.exceptions;

public class ExecutableProcessError extends Error
{
	private static final long serialVersionUID = 1L;
	
	public ExecutableProcessError(String message)
	{
		super(message);
	}
}
