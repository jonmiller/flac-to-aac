//Jon Miller
//File: FlacToAacAlbum.java
//Project: FlacToAac


package com.tetyl.flactoaac.drivers;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tetyl.flactoaac.constants.Type;
import com.tetyl.flactoaac.corelogic.RootFolder;
import com.tetyl.flactoaac.exceptions.MetaDataFileException;
import com.tetyl.flactoaac.exceptions.NonExistenceException;
import com.tetyl.flactoaac.exceptions.WrongTypeException;

public class FlacToAacAlbum
{
	private static Logger logger = Logger.getLogger(FlacToAac.class.getName());
	 
	private static final String sourceRootFolderPath = "D:" + File.separator + "Users" + File.separator + "Jon" + File.separator + "Music" + File.separator + "Flac";
	private static final String intermediateRootFolderPath = "D:" + File.separator + "Users" + File.separator + "Jon" + File.separator + "Music" + File.separator + "Wav";
	private static final String destinationRootFolderPath = "D:" + File.separator + "Users" + File.separator + "Jon" + File.separator + "Music" + File.separator + "Aac";
	
	private static final String artistPath = "Finnders & Youngberg";
	private static final String albumPath = "I Don't Want Love You Won't Give Until I Cry";
	
	public static void main(String[] args)
	{
		FlacToAacAlbum flacToAacAlbum = new FlacToAacAlbum();
		flacToAacAlbum.convert(sourceRootFolderPath, intermediateRootFolderPath, destinationRootFolderPath, artistPath, albumPath);	
	}
	
	private void convert(String source, String intermediate, String destination, String artist, String album)
	{
		RootFolder sourceRootFolder = new RootFolder(source, Type.FLAC);
		try
		{
			sourceRootFolder.populateChild(artist, album);
			
			RootFolder intermediateRootFolder = new RootFolder(intermediate, Type.WAV);
			RootFolder destinationRootFolder = new RootFolder(destination, Type.AAC);
			
			try
			{
				intermediateRootFolder.createFolder();
				sourceRootFolder.copyStructureTo(intermediateRootFolder);
				intermediateRootFolder.setExists(true);
			}
			catch (NonExistenceException nee)
			{
				logger.log(Level.SEVERE, nee.getMessage(), nee);
			}
			catch (MetaDataFileException mdfe)
			{
				logger.log(Level.SEVERE, mdfe.getMessage(), mdfe);
			}
			catch (WrongTypeException wte)
			{
				logger.log(Level.SEVERE, wte.getMessage(), wte);
			}
			catch (IOException ioe)
			{
				logger.log(Level.SEVERE, ioe.getMessage(), ioe);
			}
			
			try
			{
				destinationRootFolder.createFolder();
				intermediateRootFolder.copyStructureTo(destinationRootFolder);
				destinationRootFolder.setExists(true);
			}
			catch (NonExistenceException nee)
			{
				logger.log(Level.SEVERE, nee.getMessage(), nee);
			}
			catch (MetaDataFileException mdfe)
			{
				logger.log(Level.SEVERE, mdfe.getMessage(), mdfe);
			}
			catch (WrongTypeException wte)
			{
				logger.log(Level.SEVERE, wte.getMessage(), wte);
			}
			catch (IOException ioe)
			{
				logger.log(Level.SEVERE, ioe.getMessage(), ioe);
			}
		}
		catch (NonExistenceException nee)
		{
			logger.log(Level.SEVERE, nee.getMessage(), nee);
		}
		catch (MetaDataFileException mdfe)
		{
			logger.log(Level.SEVERE, mdfe.getMessage(), mdfe);
		}
	}

}
