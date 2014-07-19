// Jon Miller
// File: RootFolder.java
// Project: FlacToAac


package com.tetyl.flactoaac.corelogic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.tetyl.flactoaac.constants.Type;
import com.tetyl.flactoaac.exceptions.MetaDataFileException;
import com.tetyl.flactoaac.exceptions.NonExistenceException;
import com.tetyl.flactoaac.exceptions.WrongTypeException;

public class RootFolder
{
	private static Logger logger = Logger.getLogger(RootFolder.class.getName());
	
	private List<ArtistFolder> artistSubFolders;
	
	private Type type;
	private String path;
	private File file;
	private boolean exists;
	
	public RootFolder(String path, Type type)
	{
		artistSubFolders = new ArrayList<ArtistFolder>();
		
		this.path = path;
		this.type = type;
		
		if(type.equals(Type.FLAC))
		{
			exists = true;
		}
		else
		{
			exists = false;
		}
		
		file = new File(path);	
	}
	
	public void populateChildren() throws NonExistenceException, MetaDataFileException
	{
		if(exists)
		{
			File[] children = file.listFiles();
			
			for(File f : children)
			{
				artistSubFolders.add(new ArtistFolder(f.getAbsolutePath(), type));
			}
			
			for(ArtistFolder a : artistSubFolders)
			{
				a.populateChildren();
			}
		}
		else
		{
			throw new NonExistenceException("Attempted to call populateChildren " +
					"on a RootFolder that does not exist");
		}
	}
	
	public void populateChild(String artist, String album) throws NonExistenceException, MetaDataFileException
	{
		if(exists)
		{
			ArtistFolder artistFolder = new ArtistFolder(path + File.separator + artist, type);
			artistSubFolders.add(artistFolder);
			artistFolder.populateChild(album);
		}
		else
		{
			throw new NonExistenceException("Attempted to call populateChild " +
					"on a RootFolder that does not exist");
		}
	}

	public void copyStructureTo(RootFolder destinationRootFolder) 
	throws NonExistenceException, MetaDataFileException, WrongTypeException, IOException
	{
		if(exists)
		{		
			for(ArtistFolder a : artistSubFolders)
			{
				String destinationArtistFolderPath = 
					destinationRootFolder.getPath() + File.separator + a.getName();
				
				ArtistFolder destinationArtistFolder = 
					new ArtistFolder(destinationArtistFolderPath, destinationRootFolder.getType());
				
				destinationArtistFolder.createFolder();
				
				a.copyStructureTo(destinationArtistFolder);
				
				destinationArtistFolder.setExists(true);
				
				destinationRootFolder.add(destinationArtistFolder);
			}
		}
		else
		{
			throw new NonExistenceException("Attempted to call copyStructureTo " +
					"on a RootFolder that does not exist");
		}
	}
	
	public void add(ArtistFolder artistFolder)
	{
		artistSubFolders.add(artistFolder);
	}

	public void createFolder() throws IOException
	{
		if(!file.exists())
		{
			if(!file.mkdir())
			{
				throw new IOException("Failed to create a non-existent folder: " + file.getAbsolutePath());
			}
		}
	}

	public String getPath()
	{
		return path;
	}
	
	public Type getType()
	{
		return type;
	}
	
	public void setExists(boolean exists)
	{
		this.exists = exists;
	}	
}
