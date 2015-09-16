/*
 * Copyright (c) 2015 The Ontario Institute for Cancer Research. All rights reserved.                             
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
package org.icgc.dcc.repository.cghub.core;

import static org.icgc.dcc.repository.cghub.util.CGHubAnalysisDetails.getAnalyteCode;

import java.util.List;

import org.icgc.dcc.repository.core.model.RepositoryFile.DataType;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableList;

import lombok.val;

public class CGHubDataTypeResolver {

  /**
   * Constants.
   */
  private static final List<String> DNA_SEQ_ANALYTE_CODES = ImmutableList.of("D", "G", "W", "X");
  private static final List<String> RNA_SEQ_ANALYTE_CODES = ImmutableList.of("R", "T", "H");

  public static String resolveDataType(JsonNode result) {
    val analyteCode = getAnalyteCode(result);
    if (DNA_SEQ_ANALYTE_CODES.contains(analyteCode)) {
      return DataType.DNA_SEQ;
    } else if (RNA_SEQ_ANALYTE_CODES.contains(analyteCode)) {
      return DataType.RNA_SEQ;
    }

    return null;
  }

}
