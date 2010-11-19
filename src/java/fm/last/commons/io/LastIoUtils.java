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

import java.io.IOException;
import java.io.ObjectOutput;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.Channel;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.management.remote.JMXConnector;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.log4j.Logger;

/**
 * Utility class for performing IO-related activities.
 */
public class LastIoUtils {

  private static Logger log = Logger.getLogger(LastIoUtils.class);

  /**
   * Close the passed socket, logging an IOExceptions but not re-throwing them.
   * 
   * @param socket Socket to be closed (can be null).
   */
  public static void closeQuietly(ServerSocket socket) {
    if (socket != null) {
      try {
        socket.close();
      } catch (IOException e) {
        log.error(e);
      }
    }
  }

  /**
   * Close the passed socket, logging an IOExceptions but not re-throwing them.
   * 
   * @param socket Socket to be closed (can be null).
   */
  public static void closeQuietly(Socket socket) {
    if (socket != null) {
      try {
        socket.close();
      } catch (IOException e) {
        log.error(e);
      }
    }
  }

  /**
   * Close the passed ObjectOutput, logging an IOExceptions but not re-throwing them.
   * 
   * @param objectOutput ObjectOutput to be closed (can be null).
   */
  public static void closeQuietly(ObjectOutput objectOutput) {
    if (objectOutput != null) {
      try {
        objectOutput.close();
      } catch (IOException e) {
        log.error(e);
      }
    }
  }

  /**
   * Close the passed Channel, logging any IOExceptions but not re-throwing them.
   * 
   * @param channel Channel to be closed (can be null).
   */
  public static void closeQuietly(Channel channel) {
    if (channel != null) {
      try {
        channel.close();
      } catch (IOException e) {
        log.error(e);
      }
    }
  }

  /**
   * Close the passed RandomAccessFile, logging any IOExceptions but not re-throwing them.
   * 
   * @param file RandomAccessFile to be closed (can be null).
   */
  public static void closeQuietly(RandomAccessFile file) {
    if (file != null) {
      try {
        file.close();
      } catch (IOException e) {
        log.error(e);
      }
    }
  }

  /**
   * Close the passed Statement, logging any SQLExceptions but not re-throwing them.
   * 
   * @param statement Statement to be closed (can be null).
   */
  public static void closeQuietly(Statement statement) {
    if (statement != null) {
      try {
        statement.close();
      } catch (SQLException e) {
        log.error(e);
      }
    }
  }

  /**
   * Close the passed Connection, logging any SQLExceptions but not re-throwing them.
   * 
   * @param connection Connection to be closed (can be null).
   */
  public static void closeQuietly(Connection connection) {
    if (connection != null) {
      try {
        connection.close();
      } catch (SQLException e) {
        log.error(e);
      }
    }
  }

  /**
   * Close the passed XMLStreamReader, logging any XMLStreamExceptions but not re-throwing them.
   * 
   * @param streamReader XMLStreamReader to be closed (can be null).
   */
  public static void closeQuietly(XMLStreamReader streamReader) {
    if (streamReader != null) {
      try {
        streamReader.close();
      } catch (XMLStreamException e) {
        log.error(e);
      }
    }
  }

  /**
   * Close the passed JMXConnector, logging any XMLIOExceptions but not re-throwing them.
   * 
   * @param connector JMXConnector to be closed (can be null).
   */
  public static void closeQuietly(JMXConnector connector) {
    if (connector != null) {
      try {
        connector.close();
      } catch (IOException e) {
        log.error(e);
      }
    }
  }

}
