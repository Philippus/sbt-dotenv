/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Matt Fellows (OneGeek)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package au.com.onegeek.sbtdotenv

import java.io.File
import org.scalatest.{ Matchers, WordSpec }

/**
 * Created by mfellows on 20/07/2014.
 */
class SbtDotenvSpec extends WordSpec with Matchers {
  "The plugin parser" should {
    "do nothing if no .env file exists" in {
      val file = new File("thisfilecannotexistunlessyoucreateit")
      val map = SbtDotenv.parseFile(file)
      map should equal(None)
    }

    "read .env file into an environment Map" in {
      val file = new File("./src/test/resources/.dotenv.valid")

      SbtDotenv.parseFile(file) should equal(Some(Map(
        "EMPTY_VARIABLE" -> "",
        "MONGO_PORT" -> "17017",
        "COVERALLS_REPO_TOKEN" -> "aoeucaPDc2rvkFugUGlNaCGu3EOeoaeu63WLo5",
        "MONGO_URL" -> "http://localhost:$MONGO_PORT/mongo#asdf"
      )))
    }

    "not accept empty lines" in {
      SbtDotenv.parse("") should equal(Map())
    }

    "not accept numeric variable names" in {
      SbtDotenv.parse("1234=5678") should equal(Map())
    }

    "not accept lines with no assignment" in {
      SbtDotenv.parse("F") should equal(Map())
    }

    "accept empty variables" in {
      SbtDotenv.parse("EMPTY=\nONE=TWO") should equal(Map("EMPTY" -> "","ONE" -> "TWO"))
    }

    "accept unquoted strings containing whitespace" in {
      SbtDotenv.parse("SOMETHING=I love kittens") should equal(Map("SOMETHING" -> "I love kittens"))
    }

    "accept lines with trailing comments" in {
      SbtDotenv.parse("WITHOUT_COMMENT=ThisIsValue # here is a comment") should equal(Map("WITHOUT_COMMENT" -> "ThisIsValue"))
    }

    "accept lines with URLs containing # characters" in {
      SbtDotenv.parse("WITH_HASH_URL='http://example.com#awesome-id'") should equal(Map("WITH_HASH_URL" -> "http://example.com#awesome-id"))
    }

    "accept lines with quoted variables and strips quotes" in {
      SbtDotenv.parse("FOO='a=b==ccddd'") should equal(Map("FOO" -> "a=b==ccddd"))
      SbtDotenv.parse("FOO=\"blah # blah \r blah \n blah \"") should equal(Map("FOO" -> "blah # blah \r blah \n blah "))
    }

    "accept lines with whitespace around assignment operator" in {
      SbtDotenv.parse("FOO   =   boo") should equal(Map("FOO" -> "boo"))
    }

    "accept lines with escaped characters and unescape them" in {
      SbtDotenv.parse("FOO=' \\\' \\\' '") should equal(Map("FOO" -> " \' \' "))
    }

    "accept lines with leading whitespace before variable name" in {
      SbtDotenv.parse("   FOO=noo") should equal(Map("FOO" -> "noo"))
    }

    "accept lines with leading export and ignore the export" in {
      SbtDotenv.parse(" export FOO=noo") should equal(Map("FOO" -> "noo"))
    }

    "accept lines with variables containing undescores, periods, and hyphens" in {
      SbtDotenv.parse(" export F.OO=period") should equal(Map("F.OO" -> "period"))
      SbtDotenv.parse("FO-O=hyphen") should equal(Map("FO-O" -> "hyphen"))
      SbtDotenv.parse("FOO__ = underscore") should equal(Map("FOO__" -> "underscore"))
    }

    "accept multi-line variables" in {
      val content = """MY_CERT="-----BEGIN CERTIFICATE-----
        |123456789qwertyuiopasdfghjklzxcvbnm
        |-----END CERTIFICATE-----
        |"
      """.stripMargin
      SbtDotenv.parse(content) should equal(Map("MY_CERT" -> """-----BEGIN CERTIFICATE-----
                                                             |123456789qwertyuiopasdfghjklzxcvbnm
                                                             |-----END CERTIFICATE-----
                                                             |""".stripMargin))
    }

    "validate correct lines in a .env file" in {
      SbtDotenv.parse("FOO=bar") should equal(Map("FOO" -> "bar"))

      SbtDotenv.parse("FOO=1234") should equal(Map("FOO" -> "1234"))

      SbtDotenv.parse("COVERALLS_REPO_TOKEN=NTHnTHSNthnTHSntNt09aoesNTH6") should equal(Map("COVERALLS_REPO_TOKEN" -> "NTHnTHSNthnTHSntNt09aoesNTH6"))
    }
  }
}
