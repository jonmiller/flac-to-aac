package com.jonamiller.flactoaac.drivers;

import com.jonamiller.flactoaac.constants.Type;
import com.jonamiller.flactoaac.corelogic.RootFolder;
import com.jonamiller.flactoaac.exceptions.MetaDataFileException;
import com.jonamiller.flactoaac.exceptions.NonExistenceException;
import com.jonamiller.flactoaac.exceptions.WrongTypeException;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FlacToAac {
  private static final String sourceRootFolderPath =
      "F:" + File.separator + "Music" + File.separator + "Flac";
  private static final String intermediateRootFolderPath =
      "F:" + File.separator + "Music" + File.separator + "Wav";
  private static final String destinationRootFolderPath =
      "F:" + File.separator + "Music" + File.separator + "Aac";
  private static Logger logger = Logger.getLogger(FlacToAac.class.getName());

  public static void main(String[] args) {
    FlacToAac flacToAac = new FlacToAac();
    flacToAac.convert(sourceRootFolderPath, intermediateRootFolderPath, destinationRootFolderPath);
  }

  private void convert(String source, String intermediate, String destination) {
    RootFolder sourceRootFolder = new RootFolder(source, Type.FLAC);
    try {
      sourceRootFolder.populateChildren();

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
      } catch (NonExistenceException | MetaDataFileException | WrongTypeException | IOException e) {
        logger.log(Level.SEVERE, e.getMessage(), e);
      }
    } catch (NonExistenceException | MetaDataFileException e) {
      logger.log(Level.SEVERE, e.getMessage(), e);
    }
  }
}
