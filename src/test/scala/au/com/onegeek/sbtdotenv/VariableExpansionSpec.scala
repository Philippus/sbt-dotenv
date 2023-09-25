/** The MIT License (MIT)
  *
  * Copyright (c) 2014 Matt Fellows (OneGeek) Copyright (c) 2018 Edd Steel (eddsteel)
  *
  * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
  * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
  * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
  * permit persons to whom the Software is furnished to do so, subject to the following conditions:
  *
  * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
  * Software.
  *
  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
  * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
  * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
  * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
  */
package au.com.onegeek.sbtdotenv

import java.io.File
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class VariableExpansionSpec extends AnyWordSpec with Matchers {
  "Variable expansion" should {

    "expand variables in values" in {
      VariableExpansion.expandAllVars(
        Map("USER_NAME" -> "alice"),
        Map("DB_USER"   -> "$USER_NAME-")
      ) should equal(Map("DB_USER" -> "alice-"))
      VariableExpansion.expandAllVars(
        Map("USER"           -> "bob"),
        Map("CONSUMER_GROUP" -> "consumer-${USER}")
      ) should equal(Map("CONSUMER_GROUP" -> "consumer-bob"))
    }

    "replace escaped $s with single $s" in {
      VariableExpansion.expandAllVars(
        Map.empty,
        Map("PATTERN" -> "$$OK_$${this is complicated}")
      ) should equal(Map("PATTERN" -> "$OK_${this is complicated}"))
    }

    "ignore incorrect variables" in {
      VariableExpansion.expandAllVars(
        Map.empty,
        Map("PATTERN" -> "lol $}{how does ${this work")
      ) should equal(Map("PATTERN" -> "lol $}{how does ${this work"))

      VariableExpansion.expandAllVars(
        Map("LEVEL" -> "ONE", "LEVEL_ONE" -> "LEVEL_TWO"),
        Map("NEST"  -> "${LEVEL_$LEVEL}")
      ) should equal(Map("NEST" -> "LEVEL_TWO"))
    }

    "ignore undefined variables" in {
      VariableExpansion.expandAllVars(
        Map.empty,
        Map("PATTERN" -> "forgetting $SOMETHING")
      ) should equal(Map("PATTERN" -> "forgetting $SOMETHING"))
    }

    "dereference up to 3 times" in {
      val defns = Map(
        "TOE_BONE"               -> "${FOOT_BONE}",
        "FOOT_BONE"              -> "${HEEL_BONE}",
        "HEEL_BONE"              -> "${ANKLE_BONE}",
        "ANKLE_BONE"             -> "SHIN_BONE",
        "LOOP"                   -> "${LOOP}",
        "PING"                   -> "${PONG}",
        "PONG"                   -> "$PING",
        "YOU"                    -> "I",
        "CAN_I_GO"               -> "IS_SUPPORTED",
        "DEEP_IS_SUPPORTED"      -> "DEEP_IS_YOUR_STACK",
        "HOW_DEEP_IS_YOUR_STACK" -> "3",
        "NEST"                   -> "${HOW_${DEEP_${CAN_${YOU}_GO}}}"
      )
      VariableExpansion.expandAllVars(
        defns,
        Map("ONE" -> "$FOOT_BONE", "TWO" -> "$TOE_BONE")
      ) should equal(Map("ONE" -> "SHIN_BONE", "TWO" -> "$TOE_BONE"))
      VariableExpansion.expandAllVars(
        defns,
        Map("NEST" -> "How ${DEEP_${CAN_${YOU}_GO}}")
      ) should equal(Map("NEST" -> "How DEEP_IS_YOUR_STACK"))
      // note the var is unexpanded, it doesn't expand 3 times then stop.
      VariableExpansion.expandAllVars(
        defns,
        Map("TEST" -> "$LOOP")
      ) should equal(Map("TEST" -> "$LOOP"))
      VariableExpansion.expandAllVars(
        defns,
        Map("TEST" -> "${PING}")
      ) should equal(Map("TEST" -> "${PING}"))
      VariableExpansion.expandAllVars(
        defns,
        Map("TEST" -> "$NEST")
      ) should equal(Map("TEST" -> "$NEST"))
    }
  }
}
