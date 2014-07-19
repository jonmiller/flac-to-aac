// Jon Miller
// File: ArtistFolder.java
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

public class ArtistFolder
{
	private static Logger logger = Logger.getLogger(ArtistFolder.class.getName());
	
	private List<AlbumFolder> albumSubFolders;
	
	private Type type;
	private String path;
	private String name;
	private File file;
	private boolean exists;
	
	ArtistFolder(String path, Type type)
	{	
		albumSubFolders = new ArrayList<AlbumFolder>();
		
		this.path = path;
		this.type = type;
		
		name = path.substring(path.lastIndexOf(File.separatorChar) + 1);
		
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
	
	void populateChildren() throws NonExistenceException, MetaDataFileException
	{
		if(exists)
		{
			File[] children = file.listFiles();
			
			for(File f : children)
			{
				albumSubFolders.add(new AlbumFolder(f.getAbsolutePath(), type));
			}
			
			for(AlbumFolder a : albumSubFolders)
			{
				a.populateChildren();
			}
		}
		else
		{
			throw new NonExistenceException("Attempted to call populateChildren " +
					"on an ArtistFolder that does not exist");
		}
	}
	
	void populateChild(String album) throws NonExistenceException, MetaDataFileException
	{
		if(exists && file.exists())
		{
			AlbumFolder albumFolder = new AlbumFolder(path + File.separator + album, type);
			albumSubFolders.add(albumFolder);
			albumFolder.populateChildren();
		}
		else
		{
			throw new NonExistenceException("Attempted to call populateChild " +
					"on a AlbumFolder that does not exist");
		}
	}
	
	void copyStructureTo(ArtistFolder destinationArtistFolder) 
	throws NonExistenceException, MetaDataFileException, WrongTypeException, IOException
	{
		if(exists)
		{
			for(AlbumFolder a : albumSubFolders)
			{
				String destinationAlbumFolderPath = 
					destinationArtistFolder.getPath() + File.separator + a.getName();
				
				AlbumFolder destinationAlbumFolder = 
					new AlbumFolder(destinationAlbumFolderPath, destinationArtistFolder.getType());
				
				destinationAlbumFolder.createFolder();
				
				a.copyStructureTo(destinationAlbumFolder);
				
				destinationAlbumFolder.setExists(true);
				
				destinationArtistFolder.add(destinationAlbumFolder);
			}
		}
		else
		{
			throw new NonExistenceException("Attempted to call copyStructureTo " +
					"on a RootFolder that does not exist");
		}
	}
	
	void add(AlbumFolder albumFolder)
	{
		albumSubFolders.add(albumFolder);
	}
	
	void createFolder() throws IOException
	{
		if(!file.exists())
		{
			if(!file.mkdir())
			{
				throw new IOException("Failed to create a non-existent folder: " + file.getAbsolutePath());
			}
		}
	}

	String getPath()
	{
		return path;
	}

	String getName()
	{
		return name;
	}
	
	Type getType()
	{
		return type;
	}
	
	void setExists(boolean exists)
	{
		this.exists = exists;
	}

}
