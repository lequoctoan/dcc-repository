/*
 * Copyright (c) 2015 The Ontario Institute for Cancer Research. All rights reserved.                             
 *                                                                                                               
 * This program and the accompanying materials are made available under the terms of the GNU Public License v3.0.
 * You should have received a copy of the GNU General Public License along with                                  
 * this program. If not, see <http://www.gnu.org/licenses/>.                                                     
 *                                                                                                               
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS"AS IS" AND ANY                           
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES                          
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT                           
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,                                
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED                          
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;                               
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER                              
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN                         
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.icgc.dcc.repository.core.model;

import static com.google.common.collect.Iterables.tryFind;
import static lombok.AccessLevel.PRIVATE;
import static org.icgc.dcc.repository.core.model.RepositorySource.AWS;
import static org.icgc.dcc.repository.core.model.RepositorySource.CGHUB;
import static org.icgc.dcc.repository.core.model.RepositorySource.PCAWG;
import static org.icgc.dcc.repository.core.model.RepositorySource.TCGA;
import static org.icgc.dcc.repository.core.model.RepositoryType.GNOS;
import static org.icgc.dcc.repository.core.model.RepositoryType.S3;
import static org.icgc.dcc.repository.core.model.RepositoryType.WEB_ARCHIVE;

import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@NoArgsConstructor(access = PRIVATE)
public final class RepositoryServers {

  // @formatter:off
  public static final List<RepositoryServer> SERVERS = ImmutableList.of(
      server().source(CGHUB).type(GNOS)       .name("CGHub - Santa Cruz")    .code("cghub")             .country("US").baseUrl("https://cghub.ucsc.edu/").build(),
      server().source(TCGA) .type(WEB_ARCHIVE).name("TCGA DCC - Bethesda")   .code("tcga")              .country("US").baseUrl("https://tcga-data.nci.nih.gov/").build(),
      server().source(PCAWG).type(GNOS)       .name("PCAWG - Barcelona")     .code("pcawg-barcelona")   .country("ES").baseUrl("https://gtrepo-bsc.annailabs.com/").build(),
      server().source(PCAWG).type(GNOS)       .name("PCAWG - Santa Cruz")    .code("pcawg-cghub")       .country("US").baseUrl("https://cghub.ucsc.edu/").build(),
      server().source(PCAWG).type(GNOS)       .name("PCAWG - Tokyo")         .code("pcawg-tokyo")       .country("JP").baseUrl("https://gtrepo-riken.annailabs.com/").build(),
      server().source(PCAWG).type(GNOS)       .name("PCAWG - Seoul")         .code("pcawg-seoul")       .country("KR").baseUrl("https://gtrepo-etri.annailabs.com/").build(),
      server().source(PCAWG).type(GNOS)       .name("PCAWG - London")        .code("pcawg-london")      .country("UK").baseUrl("https://gtrepo-ebi.annailabs.com/").build(),
      server().source(PCAWG).type(GNOS)       .name("PCAWG - Heidelberg")    .code("pcawg-heidelberg")  .country("DE").baseUrl("https://gtrepo-dkfz.annailabs.com/").build(),
      server().source(PCAWG).type(GNOS)       .name("PCAWG - Chicago (ICGC)").code("pcawg-chicago-icgc").country("US").baseUrl("https://gtrepo-osdc-icgc.annailabs.com/").build(),
      server().source(PCAWG).type(GNOS)       .name("PCAWG - Chicago (TCGA)").code("pcawg-chicago-tcga").country("US").baseUrl("https://gtrepo-osdc-tcga.annailabs.com/").build(),
      server().source(AWS)  .type(S3)         .name("AWS - Virginia")        .code("aws-virginia")      .country("US").baseUrl("https://s3-external-1.amazonaws.com/").build()
      );
  // @formatter:on

  public static Iterable<RepositoryServer> getServers() {
    return SERVERS;
  }

  public static RepositoryServer getCGHubServer() {
    return findServer(server -> server.getSource() == CGHUB);
  }

  public static RepositoryServer getTCGAServer() {
    return findServer(server -> server.getSource() == TCGA);
  }

  public static RepositoryServer getPCAWGServer(String genosRepo) {
    return findServer(server -> server.getSource() == PCAWG && server.getBaseUrl().equals(genosRepo));
  }

  public static RepositoryServer getAWSServer() {
    return findServer(server -> server.getSource() == AWS);
  }

  private static RepositoryServer findServer(Predicate<RepositoryServer> predicate) {
    return tryFind(getServers(), predicate).orNull();
  }

  @Value
  @Builder
  public static class RepositoryServer {

    RepositoryType type;
    RepositorySource source;
    String name;
    String code;
    String country;
    String baseUrl;

  }

  private static RepositoryServer.RepositoryServerBuilder server() {
    return RepositoryServer.builder();
  }

}