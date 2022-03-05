/** The MIT License (MIT)
  *
  * Copyright (c) 2014 Matt Fellows (OneGeek)
  *
  * Permission is hereby granted, free of charge, to any person obtaining a copy
  * of this software and associated documentation files (the "Software"), to
  * deal in the Software without restriction, including without limitation the
  * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
  * sell copies of the Software, and to permit persons to whom the Software is
  * furnished to do so, subject to the following conditions:
  *
  * The above copyright notice and this permission notice shall be included in
  * all copies or substantial portions of the Software.
  *
  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
  * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
  * IN THE SOFTWARE.
  */
package au.com.onegeek.sbtdotenv

import java.util.regex.Matcher
import scala.annotation.tailrec
import scala.util.matching.Regex

object VariableExpansion {
  type Env = Map[String, String]

  /** Expand variables in the second map based on values in the first.
    *
    * This recognises variables of the form "$$FOO" and "$${FOO}", and supports
    * multiple levels of reference and nesting (up to 3 times).
    * @param defns
    *   Key/value map that will be used to expand any variable references
    * @param vars
    *   Map of variables that may contain variable references for expansion
    */
  def expandAllVars(defns: Env, vars: Env): Env =
    expandAllVarsNTimes(defns, vars, vars, 3)

  @tailrec
  private def expandAllVarsNTimes(
      defns: Env,
      vars: Env,
      originals: Env,
      n: Int
  ): Env =
    if (n <= 0) {
      escapeAndRestoreFailedOriginals(vars, originals)
    } else {
      val reduced = expandAllVarsOnce(defns, vars)
      expandAllVarsNTimes(defns ++ reduced, reduced, originals, n - 1)
    }

  private def escapeAndRestoreFailedOriginals(vars: Env, originals: Env): Env =
    vars.map { case (key, value) =>
      // lookup failed, restore original
      val restoredValue = if (containsVars(value)) originals(key) else value
      key -> restoredValue.replaceAll("\\$\\$", "\\$")
    }

  private def expandAllVarsOnce(defns: Env, vars: Env): Env =
    vars.map {
      case (key, value) if containsVars(value) =>
        key -> expandVarsInValue(defns, value)

      case pair =>
        pair
    }

  // 0-length look behind for either nothing, or any character that's not a '$'
  // followed by
  //   $ and any non-boundary chars (_ is ok), reluctantly so the first \b stops or
  //   $ and {} pair containing at least one character of anything but }
  //
  private val variableRegex: Regex = {
    val start = "(?<=^|[^$])"
    val braceless = "\\$([^{\\$]+?)\\b"
    val braces = "\\$\\{([^\\}\\$]+)\\}"
    s"$start(?:$braceless|$braces)".r
  }

  private def variableName(m: Regex.Match) =
    Option(m.group(1)).getOrElse(m.group(2))

  private def containsVars(s: String): Boolean =
    variableRegex.findFirstIn(s).nonEmpty

  private def expandVarsInValue(defns: Env, value: String): String = {
    val replaceMatch: (Regex.Match => String) = m => {
      val lookup = defns.get(variableName(m))
      val fallback = m.matched
      val replacement = lookup.getOrElse(fallback)

      Matcher.quoteReplacement(replacement)
    }

    variableRegex.replaceAllIn(value, replaceMatch)
  }
}
