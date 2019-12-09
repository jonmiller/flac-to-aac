package com.jonamiller.flactoaac.drivers;

import com.jonamiller.flactoaac.constants.Type;
import com.jonamiller.flactoaac.corelogic.RootFolder;
import com.jonamiller.flactoaac.exceptions.NonExistenceException;
import com.jonamiller.flactoaac.exceptions.MetaDataFileException;
import com.jonamiller.flactoaac.exceptions.WrongTypeException;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FlacToAacAlbum {
  private static final String sourceRootFolderPath = "C:/Users/Jon/Music/Flac";
  private static final String intermediateRootFolderPath = "C:/Users/Jon/Music/Wav";
  private static final String destinationRootFolderPath = "C:/Users/Jon/Music/Aac";
  private static final String artistPath = "The Raconteurs";
  private static final String albumPath = "Help Us Stranger";
  private static Logger logger = Logger.getLogger(FlacToAac.class.getName());

  public static void main(String[] args) {
    FlacToAacAlbum flacToAacAlbum = new FlacToAacAlbum();
    flacToAacAlbum.convert(sourceRootFolderPath, intermediateRootFolderPath,
        destinationRootFolderPath, artistPath, albumPath);
  }

  private void convert(String source, String intermediate, String destination, String artist,
                       String album) {
    RootFolder sourceRootFolder = new RootFolder(source, Type.FLAC);
    try {
      sourceRootFolder.populateChild(artist, album);

      RootFolder intermediateRootFolder = new RootFolder(intermediate, Type.WAV);
      RootFolder destinationRootFolder = new RootFolder(destination, Type.AAC);

      try {
        intermediateRootFolder.createFolder();
        sourceRootFolder.copyStructureTo(intermediateRootFolder);
        intermediateRootFolder.setExists(true);
      } catch (NonExistenceException | MetaDataFileException | WrongTypeException | IOException e) {
        logger.log(Level.SEVERE, e.getMessage(), e);
      }

      try {
        destinationRootFolder.createFolder();
        intermediateRootFolder.copyStructureTo(destinationRootFolder);
        destinationRootFolder.setExists(true);
      } catch (NonExistenceException | IOException | WrongTypeException | MetaDataFileException e) {
        logger.log(Level.SEVERE, e.getMessage(), e);
      }
    } catch (NonExistenceException | MetaDataFileException e) {
      logger.log(Level.SEVERE, e.getMessage(), e);
    }
  }

}
