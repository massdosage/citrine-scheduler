/*
 * Copyright 2010 Last.fm
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

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.ObjectOutput;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.Channel;
import java.nio.channels.SocketChannel;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.management.remote.JMXConnector;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.junit.Test;

public class LastIoUtilsTest {

  @Test
  public void closeNullServerSocket() {
    LastIoUtils.closeQuietly((ServerSocket) null);
  }

  @Test
  public void closeServerSocket() throws IOException {
    ServerSocket mockSocket = mock(ServerSocket.class);
    LastIoUtils.closeQuietly(mockSocket);
    verify(mockSocket).close();
  }

  @Test
  public void closeServerSocketWithIOException() throws IOException {
    ServerSocket mockSocket = mock(ServerSocket.class);
    doThrow(new IOException()).when(mockSocket).close();
    LastIoUtils.closeQuietly(mockSocket);
    verify(mockSocket).close();
  }

  @Test
  public void closeNullSocket() {
    LastIoUtils.closeQuietly((Socket) null);
  }

  @Test
  public void closeSocket() throws IOException {
    Socket mockSocket = mock(Socket.class);
    LastIoUtils.closeQuietly(mockSocket);
    verify(mockSocket).close();
  }

  @Test
  public void closeSocketWithIOException() throws IOException {
    Socket mockSocket = mock(Socket.class);
    doThrow(new IOException()).when(mockSocket).close();
    LastIoUtils.closeQuietly(mockSocket);
    verify(mockSocket).close();
  }

  @Test
  public void closeNullObjectOutput() {
    LastIoUtils.closeQuietly((ObjectOutput) null);
  }

  @Test
  public void closeObjectOutput() throws IOException {
    ObjectOutput mockOutput = mock(ObjectOutput.class);
    LastIoUtils.closeQuietly(mockOutput);
    verify(mockOutput).close();
  }

  @Test
  public void closeObjectOutputWithIOException() throws IOException {
    ObjectOutput mockOutput = mock(ObjectOutput.class);
    doThrow(new IOException()).when(mockOutput).close();
    LastIoUtils.closeQuietly(mockOutput);
    verify(mockOutput).close();
  }

  @Test
  public void closeNullSocketChannel() {
    LastIoUtils.closeQuietly((SocketChannel) null);
  }

  @Test
  public void closeSocketChannel() throws IOException {
    Channel mockChannel = mock(Channel.class);
    LastIoUtils.closeQuietly(mockChannel);
    verify(mockChannel).close();
  }

  @Test
  public void closeSocketChannelWithIOException() throws IOException {
    Channel mockChannel = mock(Channel.class);
    doThrow(new IOException()).when(mockChannel).close();
    LastIoUtils.closeQuietly(mockChannel);
    verify(mockChannel).close();
  }

  @Test
  public void closeNullRandomAccessFile() {
    LastIoUtils.closeQuietly((RandomAccessFile) null);
  }

  @Test
  public void closeRandomAccessFile() throws IOException {
    RandomAccessFile mockFile = mock(RandomAccessFile.class);
    LastIoUtils.closeQuietly(mockFile);
    verify(mockFile).close();
  }

  @Test
  public void closeRandomAccessFileWithIOException() throws IOException {
    RandomAccessFile mockFile = mock(RandomAccessFile.class);
    doThrow(new IOException()).when(mockFile).close();
    LastIoUtils.closeQuietly(mockFile);
    verify(mockFile).close();
  }

  @Test
  public void closeNullStatement() {
    LastIoUtils.closeQuietly((Statement) null);
  }

  @Test
  public void closeStatement() throws SQLException {
    Statement mockStatement = mock(Statement.class);
    LastIoUtils.closeQuietly(mockStatement);
    verify(mockStatement).close();
  }

  @Test
  public void closeStatementWithSQLException() throws SQLException {
    Statement mockStatement = mock(Statement.class);
    doThrow(new SQLException()).when(mockStatement).close();
    LastIoUtils.closeQuietly(mockStatement);
    verify(mockStatement).close();
  }

  @Test
  public void closeNullConnection() {
    LastIoUtils.closeQuietly((Statement) null);
  }

  @Test
  public void closeConnection() throws SQLException {
    Connection mockConnection = mock(Connection.class);
    LastIoUtils.closeQuietly(mockConnection);
    verify(mockConnection).close();
  }

  @Test
  public void closeConnectionWithSQLException() throws SQLException {
    Connection mockConnection = mock(Connection.class);
    doThrow(new SQLException()).when(mockConnection).close();
    LastIoUtils.closeQuietly(mockConnection);
    verify(mockConnection).close();
  }

  @Test
  public void closeNullXMLStreamReader() {
    LastIoUtils.closeQuietly((XMLStreamReader) null);
  }

  @Test
  public void closeXMLStreamReader() throws XMLStreamException {
    XMLStreamReader mockReader = mock(XMLStreamReader.class);
    LastIoUtils.closeQuietly(mockReader);
    verify(mockReader).close();
  }

  @Test
  public void closeXMLStreamReaderWithXMLStreamException() throws XMLStreamException {
    XMLStreamReader mockReader = mock(XMLStreamReader.class);
    doThrow(new XMLStreamException()).when(mockReader).close();
    LastIoUtils.closeQuietly(mockReader);
    verify(mockReader).close();
  }

  @Test
  public void closeNullJMXConnector() {
    LastIoUtils.closeQuietly((JMXConnector) null);
  }

  @Test
  public void closeJMXConnector() throws IOException {
    JMXConnector mockConnector = mock(JMXConnector.class);
    LastIoUtils.closeQuietly(mockConnector);
    verify(mockConnector).close();
  }

  @Test
  public void closeJMXConnectorWithIOException() throws IOException {
    JMXConnector mockConnector = mock(JMXConnector.class);
    doThrow(new IOException()).when(mockConnector).close();
    LastIoUtils.closeQuietly(mockConnector);
    verify(mockConnector).close();
  }

}
