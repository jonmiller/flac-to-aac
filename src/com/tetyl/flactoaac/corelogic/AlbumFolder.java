// Jon Miller
// File: AlbumFolder.java
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

public class AlbumFolder
{
	private static Logger logger = Logger.getLogger(AlbumFolder.class.getName());
	
	private List<TrackFile> trackFiles;
	
	private Type type;
	private String path;
	private String name;
	private File file;
	private boolean exists;
	
	AlbumFolder(String path, Type type)
	{
		trackFiles = new ArrayList<TrackFile>();
		
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
				if(f.getAbsolutePath().indexOf(".flac") != -1)
				{
					trackFiles.add(new TrackFile(f.getAbsolutePath(), type));
				}
			}
			
			for(TrackFile t : trackFiles)
			{
				t.populateMetadata(true, trackFiles.size());
			}
		}
		else
		{
			throw new NonExistenceException("Attempted to call populateChildren " +
					"on an AlbumFolder that does not exist");
		}
	}

	void copyStructureTo(AlbumFolder destinationAlbumFolder) 
	throws NonExistenceException, MetaDataFileException, WrongTypeException
	{
		if(exists)
		{
			for(TrackFile t : trackFiles)
			{
				String destinationTrackName = null;
			
				if(destinationAlbumFolder.getType().equals(Type.WAV))
				{
					destinationTrackName = t.getName().substring(0,	t.getName().lastIndexOf('.')) + ".wav";
				}
				else if(destinationAlbumFolder.getType().equals(Type.AAC))
				{
					destinationTrackName = t.getName().substring(0,	t.getName().lastIndexOf('.')) + ".m4a";
				}
				else
				{
					throw new WrongTypeException("Attempted to convert to a type other than WAV or AAC");
				}
				
				String destinationTrackFilePath = 
					destinationAlbumFolder.getPath() + File.separator + destinationTrackName;
				
				TrackFile destinationTrackFile = 
					new TrackFile(destinationTrackFilePath, destinationAlbumFolder.getType());
				
				t.convertTo(destinationTrackFile);
				
				destinationTrackFile.setExists(true);
				
				destinationAlbumFolder.add(destinationTrackFile);
				
				if(destinationAlbumFolder.getType().equals(Type.AAC))
				{
					destinationTrackFile.writeMetadataToFile();
				}
			}
		}
		else
		{
			throw new NonExistenceException("Attempted to call copyStructureTo " +
					"on a AlbumFolder that does not exist");
		}
	}

	void add(TrackFile trackFile)
	{
		trackFiles.add(trackFile);
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
