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
package org.icgc.dcc.repository.ega.pcawg.reader;

import static org.icgc.dcc.repository.ega.pcawg.model.EGAStudyFile.studyFile;

import java.io.File;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.icgc.dcc.repository.ega.pcawg.model.EGAStudyFile;

public class EGAStudyFileReader extends EGAFileReader<EGAStudyFile> {

  /**
   * Constants.
   */
  private static final Pattern STUDY_FILE_PATTERN = Pattern.compile(""
      // Template: study/study.[study].xml
      // Example : study/study.PCAWG.xml
      + "study/study"
      + "\\."
      + "([^.]+)" // [study]
      + "\\.xml");

  public EGAStudyFileReader(File repoDir) {
    super(repoDir, STUDY_FILE_PATTERN);
  }

  @Override
  protected EGAStudyFile createFile(Path path, Matcher matcher) {
    return studyFile()
        .study(matcher.group(1))
        .contents(readFile(path))
        .build();
  }

}
