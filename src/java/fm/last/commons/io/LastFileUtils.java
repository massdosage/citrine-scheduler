/*
 * Copyright 2009 Last.fm
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package fm.last.commons.io;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.jar.Manifest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

/**
 * File related utilities.
 */
public class LastFileUtils {

  private static Logger log = Logger.getLogger(LastFileUtils.class);

  /**
   * Appends the passed string to the passed file.
   * 
   * @param file File to append string to (will be created if it does not exist).
   * @param string String to append.
   * @throws IOException If an error occurs appending the string to the file.
   */
  public static void appendStringToFile(File file, String string) throws IOException {
    BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
    writer.write(string);
    writer.close();
  }

  /**
   * Appends the passed files (in order) to the destination.
   * 
   * @param destination Destination file.
   * @param files Array of files to be appended to the destination file.
   * @throws IOException If an error occurs appending the files.
   */
  public static void appendFiles(File destination, File... files) throws IOException {
    appendFiles(destination, Arrays.asList(files));
  }

  /**
   * Appends the passed files (in list order) to the destination.
   * 
   * @param destination Destination file.
   * @param files List of files to be appended to the destination file.
   * @throws IOException If an error occurs appending the files.
   */
  public static void appendFiles(File destination, List<File> files) throws IOException {
    FileUtils.copyFile(files.get(0), destination);
    FileOutputStream fos = new FileOutputStream(destination, true);
    for (int i = 1; i < files.size(); i++) {
      FileInputStream fis = new FileInputStream(files.get(i));
      IOUtils.copy(fis, fos);
      IOUtils.closeQuietly(fis);
    }
    IOUtils.closeQuietly(fos);
  }

  /**
   * Gets the manifest associated with the passed class.
   * 
   * @param someClass Class to find manifest for.
   * @param removePath Path to remove from path used when searching for manifest.
   * @return The manifest.
   * @throws IOException If the manifest could not be found.
   */
  public static Manifest getManifest(Class<?> someClass, String removePath) throws IOException {
    String className = someClass.getSimpleName();
    String classFileName = className + ".class";
    String classFilePath = someClass.getPackage().toString().replace('.', '/') + "/" + className;
    String pathToThisClass = someClass.getResource(classFileName).toString();
    String pathToManifest = new StringBuilder().append(
        pathToThisClass.substring(0, pathToThisClass.length() + 2
            - new StringBuilder().append("/").append(classFilePath).toString().length() - removePath.length())).append(
        "/META-INF/MANIFEST.MF").toString();
    Manifest manifest = new Manifest(new URL(pathToManifest).openStream());
    return manifest;
  }

  /**
   * Gets the manifest associated with the passed class.
   * 
   * @param someClass Class to find manifest for.
   * @return The manifest.
   * @throws IOException If the manifest could not be found.
   */
  public static Manifest getManifest(Class<?> someClass) throws IOException {
    return getManifest(someClass, "");
  }

  /**
   * Gets the manifest associated with the passed class by looking in classes/WEB-INF/ for it.
   * 
   * @param someClass Class to find manifest for.
   * @return The manifest.
   * @throws IOException If the manifest could not be found.
   */
  public static Manifest getWebManifest(Class<?> someClass) throws IOException {
    return getManifest(someClass, "classes/WEB-INF/");
  }

  /**
   * Reads the last bytes from the end of the passed file as a String.
   * 
   * @param file File to read from.
   * @param bytes Number of bytes from end of file to read.
   * @return The end content of the file as a String.
   * @throws IOException If the file could not be opened or read.
   */
  public static String tail(File file, long bytes) throws IOException {
    RandomAccessFile raFile = null;
    StringBuffer tail = new StringBuffer();
    try {
      raFile = new RandomAccessFile(file, "r");
      long length = raFile.length();
      if (bytes >= length) {
        return FileUtils.readFileToString(file);
      } else {
        raFile.seek(length - bytes);
        tail = new StringBuffer((int) bytes);
        String line = raFile.readLine();
        while (line != null) {
          tail.append(line);
          line = raFile.readLine();
          if (line != null) { // there is another line coming, so add line break
            tail.append("\n");
          }
        }
      }
    } finally {
      LastIoUtils.closeQuietly(raFile);
    }
    return tail.toString();
  }

  /**
   * Searches for a file on local filesytem, classpath etc.
   * 
   * @param fileName Name of file to find.
   * @param classToLoadFrom Class to use as a base for finding the file via it's classloader, if necessary.
   * @return The file if found on the file system.
   * @throws FileNotFoundException If the File could not be found.
   */
  public static File getFile(String fileName, Class<?> classToLoadFrom) throws FileNotFoundException {
    File file = new File(fileName); // first try the path directly
    if (!file.exists()) {
      URL fileURL = classToLoadFrom.getResource(fileName);// next try the class's classpath
      if (fileURL == null) {
        fileURL = classToLoadFrom.getClassLoader().getResource(fileName);// next try the class' classloader's classpath
        if (fileURL == null) {
          fileURL = ClassLoader.getSystemClassLoader().getResource(fileName); // finally try the system classloader's
          // classpath
          if (fileURL == null) {
            throw new FileNotFoundException("Could not find " + fileName + " on path, classpath "
                + "or system classpath");
          }
        }
      }
      file = new File(fileURL.getFile());
    }
    log.debug("Path to file located is " + file.getAbsolutePath());
    return file;
  }

  /**
   * Writes the contents of the passed input stream to the passed file and closes the InputStream when done.
   * 
   * @param inputStream Input stream to write to file.
   * @param file The file to write to.
   * @throws IOException If an error occurs writing the InputStream to the file.
   */
  public static void writeToFile(InputStream inputStream, File file) throws IOException {
    writeToFile(inputStream, file, true);
  }

  /**
   * Writes the contents of the passed input stream to the passed file and closes the InputStream when done.
   * 
   * @param inputStream Input stream to write to file.
   * @param file The file to write to.
   * @param makeDirs Whether to create the files parent dir(s) or not.
   * @throws IOException If an error occurs writing the InputStream to the file.
   */
  public static void writeToFile(InputStream inputStream, File file, boolean makeDirs) throws IOException {
    if (makeDirs && file.getParentFile() != null && !file.getParentFile().exists()) {
      if (!file.getParentFile().mkdirs()) {
        throw new IOException("Error creating '" + file.getParentFile().getAbsolutePath() + "'");
      }
    }
    BufferedOutputStream outputStream = null;
    try {
      outputStream = new BufferedOutputStream(new FileOutputStream(file));
      byte[] inBuffer = new byte[32 * 1024];
      int bytesRead = 0;
      while ((bytesRead = inputStream.read(inBuffer)) != -1) {
        outputStream.write(inBuffer, 0, bytesRead);
      }
    } finally {
      IOUtils.closeQuietly(inputStream);
      IOUtils.closeQuietly(outputStream);
    }
  }

  /**
   * Moves a file "safely" - first copies it to the destination with ".part" appended to the filename, and then renames
   * it. This is useful for copying files to locations where files with certain extensions are processed. The rename
   * step should be a lot quicker than the copying step, preventing the file from being processed before it is fully
   * copied.
   * 
   * @param srcFile Source file.
   * @param destFile Destination file.
   * @throws IOException If an error occurrs moving the file.
   */
  public static void moveFileSafely(File srcFile, File destFile) throws IOException {
    File partFile = new File(destFile.getParentFile(), destFile.getName() + ".part");
    FileUtils.moveFile(srcFile, partFile);
    if (!partFile.renameTo(destFile)) {
      throw new IOException("Error renaming " + partFile.getAbsolutePath() + " to " + destFile.getAbsolutePath());
    }
  }

  /**
   * Moves a file "safely" to a directory - first copies it to the destination with ".part" appended to the filename,
   * and then renames it. This is useful for copying files to locations where files with certain extensions are
   * processed. The rename step should be a lot quicker than the copying step, preventing the file from being processed
   * before it is fully copied.
   * 
   * @param srcFile Source file.
   * @param destDir Destination directory.
   * @throws IOException If an error occurs moving the file.
   */
  public static void moveFileToDirectorySafely(File srcFile, File destDir, boolean createDestDir) throws IOException {
    if (destDir == null) {
      throw new NullPointerException("Destdir must not be null");
    }
    if (!destDir.exists() && createDestDir) {
      if (!destDir.mkdirs()) {
        throw new IOException("Could not create " + destDir.getAbsolutePath());
      }
    }
    if (!destDir.exists()) {
      throw new FileNotFoundException("Destination directory '" + destDir + "' does not exist");
    }
    moveFileSafely(srcFile, new File(destDir, srcFile.getName()));
  }

  public static boolean canRead(File file) {
    return file != null && file.exists() && file.canRead();
  }

}
