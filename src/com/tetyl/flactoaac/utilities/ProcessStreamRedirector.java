// Jon Miller
// File: ProcessStreamRedirector.java
// Project: FlacToAac


package com.tetyl.flactoaac.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProcessStreamRedirector extends Thread
{
	InputStream inputStream;
	Logger logger;

	public ProcessStreamRedirector(InputStream inputStream, Logger logger)
	{
		this.inputStream = inputStream;
		this.logger = logger;
	}

	public void run()
	{
		try
		{
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			String line = null;
			while((line = bufferedReader.readLine()) != null)
			{
				logger.info(line);
			}
		} 
		catch (IOException ioe)
		{
			logger.log(Level.SEVERE, ioe.getMessage(), ioe);  
		}
	}
}
