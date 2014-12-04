/*
 * Copyright (c) 2014 Azavea.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package geotrellis.proj4

import geotrellis.proj4.io.Proj4FileReader
import geotrellis.proj4.parser.Proj4Parser

object CRSFactory {
  private val csReader = new Proj4FileReader()
}

class CRSFactory {

  def createFromName(name: String): CoordinateReferenceSystem = {
    val params = CRSFactory.csReader.getParameters(name)
    if (params == null) throw new UnknownAuthorityException(name)

    createFromParameters(name, params)
  }

  def createFromParameters(
    name: String,
    params: String): CoordinateReferenceSystem =
    createFromParameters(name, splitParameters(params))

  def createFromParameters(
    name: String,
    params: Array[String]): CoordinateReferenceSystem =
    if (params == null) null
    else Proj4Parser.parse(name, params)

  private def splitParameters(params: String) = params.split("\\s+")
}