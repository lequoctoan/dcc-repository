/*
 * Copyright (c) 2016 The Ontario Institute for Cancer Research. All rights reserved.                             
 *                                                                                                               
 * This program and the accompanying materials are made available under the terms of the GNU Public License v3.0.
 * You should have received a copy of the GNU General Public License along with                                  
 * this program. If not, see <http://www.gnu.org/licenses/>.                                                     
 *                                                                                                               
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY                           
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES                          
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT                           
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,                                
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED                          
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;                               
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER                              
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN                         
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.icgc.dcc.repository.ega.util;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.net.HttpHeaders.ACCEPT;
import static com.google.common.net.HttpHeaders.CONTENT_LENGTH;
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.icgc.dcc.common.core.json.Jackson.DEFAULT;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.icgc.dcc.common.core.security.SSLCertificateValidation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class EGAClient {

  /**
   * Constants
   */
  private static final String DEFAULT_API_URL = "https://ega.ebi.ac.uk/ega/rest/access/v2";

  public EGAClient(String userName, String password) {
    this(DEFAULT_API_URL, userName, password);
  }

  /**
   * Configuration.
   */
  @NonNull
  private final String url;
  @NonNull
  private final String userName;
  @NonNull
  private final String password;

  private final boolean reconnect = true;

  /**
   * State.
   */
  private String sessionId;

  @SneakyThrows
  public boolean login() {
    val connection = openConnection("/users/login");
    connection.setRequestMethod("POST");
    connection.setRequestProperty(ACCEPT, "application/json");
    connection.setRequestProperty(CONTENT_TYPE, "application/json");
    connection.setDoOutput(true);

    val request = createLoginRequest(userName, password);
    connection.setRequestProperty(CONTENT_LENGTH, Integer.toString(request.length()));
    connection.getOutputStream().write(request.getBytes(UTF_8));

    val response = readResponse(connection);
    this.sessionId = getSessionId(response);

    return sessionId != null;
  }

  public List<String> getDatasetIds() {
    return get("/datasets", new TypeReference<List<String>>() {});
  }

  public List<ObjectNode> getDataset(@NonNull String datasetId) {
    return get("/datasets/" + datasetId, new TypeReference<List<ObjectNode>>() {});
  }

  public List<ObjectNode> getFiles(@NonNull String datasetId) {
    return get("/datasets/" + datasetId + "/files", new TypeReference<List<ObjectNode>>() {});
  }

  public ObjectNode getFile(@NonNull String fileId) {
    return get("/files/" + fileId, new TypeReference<ObjectNode>() {});
  }

  private <T> T get(String path, TypeReference<T> responseType) {
    checkState(sessionId != null, "You must login first before calling API methods.");

    val connection = openConnection(path + "?session=" + sessionId);
    connection.setRequestProperty(ACCEPT, "application/json");

    val response = readResponse(connection);
    val code = getCode(response);
    if (isSessionExpired(code) && reconnect) {
      log.warn("Lost connection, reconnecting... {}", response);

      checkState(login(), "Could not reconnect");
      return get(path, responseType);
    }

    checkState(code == HTTP_OK, "Expected OK response, got %s: %s", code, response);

    return DEFAULT.convertValue(response.path("response").path("result"), responseType);
  }

  @SneakyThrows
  private HttpURLConnection openConnection(String path) {
    SSLCertificateValidation.disable();
    return (HttpURLConnection) new URL(url + path).openConnection();
  }

  @SneakyThrows
  private static JsonNode readResponse(URLConnection connection) {
    return DEFAULT.readTree(connection.getInputStream());
  }

  private static String createLoginRequest(String userName, String password) {
    val request = DEFAULT.createObjectNode();
    request.put("username", userName);
    request.put("password", password);
    return "loginrequest=" + request.toString();
  }

  private static String getSessionId(JsonNode response) {
    return response.path("response").path("result").path(1).textValue();
  }

  private static int getCode(JsonNode response) {
    return response.path("header").path("code").asInt();
  }

  private static boolean isSessionExpired(final int code) {
    return code == 991;
  }

}
