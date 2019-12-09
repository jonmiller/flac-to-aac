//Jon Miller
//File: TrackFile.java
//Project: FlacToAac


package com.jonamiller.flactoaac.corelogic;

import com.jonamiller.flactoaac.constants.Type;
import com.jonamiller.flactoaac.exceptions.ExecutableProcessError;
import com.jonamiller.flactoaac.exceptions.MetaDataFileException;
import com.jonamiller.flactoaac.exceptions.NonExistenceException;
import com.jonamiller.flactoaac.exceptions.WrongTypeException;
import com.jonamiller.flactoaac.utilities.ProcessStreamRedirector;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

public class TrackFile {
  private static final String metaflacLocation = "C:/Users/Jon/Programs/Flac/metaflac.exe";
  private static final String flacLocation = "C:/Users/Jon/Programs/Flac/flac.exe";
  private static final String neroEncoderLocation = "C:/Users/Jon/Programs/NeroAAC/win32/neroAacEnc.exe";
  private static final String atomicParsleyLocation = "C:/Users/Jon/Programs/Atomic Parsley/AtomicParsley.exe";
  private static final String caseIrrelevantTitleTagKey = "title";
  private static final String caseIrrelevantTrackTagKey = "tracknumber";
  private static final String caseIrrelevantAlbumTagKey = "album";
  private static final String caseIrrelevantArtistTagKey = "artist";
  private static final String caseIrrelevantGenreTagKey = "genre";
  private static final String caseIrrelevantYearTagKey = "date";
  private static final String quality = ".5";
  private static Logger logger = Logger.getLogger(TrackFile.class.getName());
  private String caseAccurateTitleTagKey;
  private String caseAccurateTrackTagKey;
  private String caseAccurateAlbumTagKey;
  private String caseAccurateArtistTagKey;
  private String caseAccurateGenreTagKey;
  private String caseAccurateYearTagKey;

  private Type type;
  private String path;
  private String name;
  private File file;
  private boolean exists;

  private String title;
  private String track;
  private String album;
  private String artist;
  private String genre;
  private String year;
  private String numTracks;

  private String metadataPath;
  private File metadataFile;

  TrackFile(String path, Type type) {
    this.path = path;
    this.type = type;

    name = path.substring(path.lastIndexOf(File.separatorChar) + 1);

    exists = type.equals(Type.FLAC);

    file = new File(path);

    metadataPath = path.substring(0, path.lastIndexOf('.')) + ".meta";
  }

  TrackFile(String path, Type type, boolean exists) {
    this(path, type);
    this.exists = exists;
  }

  void populateMetadata(boolean deleteMetadataFileWhenDone,
                        int numTracks) throws NonExistenceException, MetaDataFileException {
    if (exists) {
      Runtime runtime = Runtime.getRuntime();

      String[] commandArray = new String[]{metaflacLocation,
          "--no-utf8-convert",
          "--export-tags-to=" + metadataPath,
          file.getAbsolutePath()};


      try {
        logger.info("In populateMetadata executing: " + concatenateCommandArray(commandArray));
        Process process = runtime.exec(commandArray);
        ProcessStreamRedirector errorProcessStreamRedirector =
            new ProcessStreamRedirector(process.getErrorStream(), logger);
        ProcessStreamRedirector outputProcessStreamRedirector =
            new ProcessStreamRedirector(process.getInputStream(), logger);
        errorProcessStreamRedirector.start();
        outputProcessStreamRedirector.start();
        int returnStatus = process.waitFor();
        if (returnStatus != 0) {
          throw new ExecutableProcessError("Metaflac returned with an error status code " + "when" + " called with:" + concatenateCommandArray(commandArray));
        }
      } catch (IOException ioe) {
        throw new IOError(ioe);
      } catch (InterruptedException ie) {
        throw new Error(ie);
      }

      metadataFile = new File(metadataPath);

      try {
        BufferedReader metadataReader =
            new BufferedReader(new InputStreamReader(new FileInputStream(metadataFile), StandardCharsets.UTF_8));
        Properties properties = new Properties();
        properties.load(metadataReader);
        Set<String> propertyNamesSet = properties.stringPropertyNames();
        for (String property : propertyNamesSet) {
          if (property.equalsIgnoreCase(caseIrrelevantTitleTagKey)) {
            caseAccurateTitleTagKey = property;
          } else if (property.equalsIgnoreCase(caseIrrelevantTrackTagKey)) {
            caseAccurateTrackTagKey = property;
          } else if (property.equalsIgnoreCase(caseIrrelevantAlbumTagKey)) {
            caseAccurateAlbumTagKey = property;
          } else if (property.equalsIgnoreCase(caseIrrelevantArtistTagKey)) {
            caseAccurateArtistTagKey = property;
          } else if (property.equalsIgnoreCase(caseIrrelevantGenreTagKey)) {
            caseAccurateGenreTagKey = property;
          } else if (property.equalsIgnoreCase(caseIrrelevantYearTagKey)) {
            caseAccurateYearTagKey = property;
          }
        }
        title = caseAccurateTitleTagKey != null ? properties.getProperty(caseAccurateTitleTagKey) : "";
        track = caseAccurateTrackTagKey != null ? properties.getProperty(caseAccurateTrackTagKey) : "";
        album = caseAccurateAlbumTagKey != null ? properties.getProperty(caseAccurateAlbumTagKey) : "";
        artist = caseAccurateArtistTagKey != null ? properties.getProperty(caseAccurateArtistTagKey) : "";
        genre = caseAccurateGenreTagKey != null ? properties.getProperty(caseAccurateGenreTagKey) : "";
        year = caseAccurateYearTagKey != null ? properties.getProperty(caseAccurateYearTagKey) : "";
        this.numTracks = String.valueOf(numTracks);
        metadataReader.close();
      } catch (IOException ioe) {
        throw new IOError(ioe);
      }

      if (deleteMetadataFileWhenDone) {
        cleanUpMetadataFile();
      }
    } else {
      throw new NonExistenceException("Attempted to call populateMetadata " + "on a TrackFile " + "that does not " +
          "exist");
    }
  }

  void convertTo(TrackFile destinationTrackFile) throws NonExistenceException, WrongTypeException {
    if (exists) {
      if (destinationTrackFile.getType().equals(Type.WAV)) {
        if (type.equals(Type.FLAC)) {
          Runtime runtime = Runtime.getRuntime();
          String[] commandArray = new String[]{"\"" + flacLocation + "\"",
              "-d ",
              "-f ",
              "-o " + "\"" + destinationTrackFile.getPath() + "\"",
              "\"" + file.getAbsolutePath() + "\""};

          try {
            logger.info("In convertTo executing: " + concatenateCommandArray(commandArray));
            Process process = runtime.exec(concatenateCommandArray(commandArray));
            ProcessStreamRedirector errorProcessStreamRedirector =
                new ProcessStreamRedirector(process.getErrorStream(), logger);
            ProcessStreamRedirector outputProcessStreamRedirector =
                new ProcessStreamRedirector(process.getInputStream(), logger);
            errorProcessStreamRedirector.start();
            outputProcessStreamRedirector.start();
            process.waitFor();
          } catch (IOException ioe) {
            throw new IOError(ioe);
          } catch (InterruptedException ie) {
            throw new Error(ie);
          }

          destinationTrackFile.setTitle(title);
          destinationTrackFile.setTrack(track);
          destinationTrackFile.setAlbum(album);
          destinationTrackFile.setArtist(artist);
          destinationTrackFile.setGenre(genre);
          destinationTrackFile.setYear(year);
          destinationTrackFile.setNumTracks(numTracks);
        } else {
          throw new WrongTypeException("Attempted to convert something other " + "than a FLAC " + "file to a WAV file");
        }

      } else if (destinationTrackFile.getType().equals(Type.AAC)) {
        if (type.equals(Type.WAV)) {
          Runtime runtime = Runtime.getRuntime();
          String[] commandArray = new String[]{"\"" + neroEncoderLocation + "\"",
              "-q " + quality,
              "-if " + "\"" + file.getAbsolutePath() + "\"",
              "-of " + "\"" + destinationTrackFile.getPath() + "\""};

          try {
            logger.info("In convertTo executing: " + concatenateCommandArray(commandArray));
            Process process = runtime.exec(concatenateCommandArray(commandArray));
            ProcessStreamRedirector errorProcessStreamRedirector =
                new ProcessStreamRedirector(process.getErrorStream(), logger);
            ProcessStreamRedirector outputProcessStreamRedirector =
                new ProcessStreamRedirector(process.getInputStream(), logger);
            errorProcessStreamRedirector.start();
            outputProcessStreamRedirector.start();
            process.waitFor();
          } catch (IOException ioe) {
            throw new IOError(ioe);
          } catch (InterruptedException ie) {
            throw new Error(ie);
          }

          destinationTrackFile.setTitle(title);
          destinationTrackFile.setTrack(track);
          destinationTrackFile.setAlbum(album);
          destinationTrackFile.setArtist(artist);
          destinationTrackFile.setGenre(genre);
          destinationTrackFile.setYear(year);
          destinationTrackFile.setNumTracks(numTracks);
        } else {
          throw new WrongTypeException("Attempted to convert something other " + "than a WAV file" + " to an AAC file");
        }
      }
    } else {
      throw new NonExistenceException("Attempted to call convert " + "on a TrackFile that does " + "not exist");
    }
  }

  void writeMetadataToFile() throws WrongTypeException, NonExistenceException {
    if (exists) {
      if (type.equals(Type.AAC)) {
        Runtime runtime = Runtime.getRuntime();

        String[] commandArray = new String[]{atomicParsleyLocation,
            path,
            "--title=" + title,
            "--tracknum=" + track + "/" + numTracks,
            "--album=" + album,
            "--artist=" + artist,
            "--genre=" + genre,
            "--year=" + year};

        try {
          logger.info("In writeMetadataToFile executing: " + concatenateCommandArray(commandArray));
          Process process = runtime.exec(commandArray);
          ProcessStreamRedirector errorProcessStreamRedirector =
              new ProcessStreamRedirector(process.getErrorStream(), logger);
          ProcessStreamRedirector outputProcessStreamRedirector =
              new ProcessStreamRedirector(process.getInputStream(), logger);
          errorProcessStreamRedirector.start();
          outputProcessStreamRedirector.start();
          process.waitFor();
        } catch (IOException ioe) {
          throw new IOError(ioe);
        } catch (InterruptedException ie) {
          throw new Error(ie);
        }

        File parentFolder = file.getParentFile();
        String[] localTrackFiles = parentFolder.list();
        for (String localTrackFile : localTrackFiles) {
          logger.info("localTrackFile = " + localTrackFile);
          logger.info("localTrackFile.contains(name) = " + localTrackFile.contains(name));
          logger.info("!localTrackFile.equals(name) = " + !localTrackFile.equals(name));
          if (localTrackFile.contains(name.substring(0, name.lastIndexOf('.'))) && !localTrackFile.equals(name)) {
            logger.info("found match = " + localTrackFile);

            File newFile = new File(parentFolder, localTrackFile);

            logger.info("Deleting " + file.toString());
            boolean deleteResult = file.delete();
            if (!deleteResult)
              ;
            {
              logger.severe("In writeMetadataToFile deleting the original and returned false");
            }

            logger.info("Renaming " + newFile.toString() + " to " + file.toString());
            boolean renameResult = newFile.renameTo(file);
            if (!renameResult) {
              logger.severe("In writeMetadataToFile renaming the new file to the original and " + "failed");
            }

            file = newFile;
          }
        }

      } else {
        throw new WrongTypeException("Attempted to write metadata out to a non-AAC file");
      }

    } else {
      throw new NonExistenceException("Attempted to call writeMetadataToFile " + "on a TrackFile " + "that does not " +
          "exist");
    }
  }

  String getName() {
    return name;
  }

  Type getType() {
    return type;
  }

  String getPath() {
    return path;
  }

  void setTitle(String title) {
    this.title = title;
  }

  void setTrack(String track) {
    this.track = track;
  }

  void setAlbum(String album) {
    this.album = album;
  }

  void setArtist(String artist) {
    this.artist = artist;
  }

  void setGenre(String genre) {
    this.genre = genre;
  }

  void setYear(String year) {
    this.year = year;
  }

  void setNumTracks(String numTracks) {
    this.numTracks = numTracks;
  }

  void setExists(boolean exists) {
    this.exists = exists;
  }

  private String concatenateCommandArray(String[] commandArray) {
    String command = "";
    for (String s : commandArray) {
      command += (s + " ");
    }
    return command;
  }

  private void cleanUpMetadataFile() throws MetaDataFileException {
    if (metadataFile.exists()) {
      if (!metadataFile.delete()) {
        throw new MetaDataFileException("Failed to delete a metadata file");
      }
    }
  }
}
