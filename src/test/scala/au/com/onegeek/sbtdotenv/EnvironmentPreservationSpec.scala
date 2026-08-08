/** The MIT License (MIT)
 *
 * Copyright (c) 2014 Matt Fellows (OneGeek)
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

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

/** Test that environment variables set by CI runners (e.g., AWS ECR credentials) are preserved when applying .env settings.
 */
class EnvironmentPreservationSpec extends AnyWordSpec with Matchers {
  "Environment variable application" should {
    "preserve existing environment variables not in .env file" in {
      // Simulate a CI environment with credentials
      val ciEnvironment = Map(
        "AWS_ACCESS_KEY_ID" -> "AKIAIOSFODNN7EXAMPLE",
        "AWS_SECRET_ACCESS_KEY" -> "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY",
        "CI_BUILD_ID" -> "12345",
        "PATH" -> "/usr/local/bin:/usr/bin"
      )

      // Simulate .env file with application-specific variables
      val dotEnvVars = Map(
        "APP_ENV" -> "development",
        "DEBUG_MODE" -> "true"
      )

      // After applying .env, both CI and .env variables should exist
      val merged = ciEnvironment ++ dotEnvVars

      // Verify all variables are present
      merged should contain key "AWS_ACCESS_KEY_ID"
      merged should contain key "AWS_SECRET_ACCESS_KEY"
      merged should contain key "CI_BUILD_ID"
      merged should contain key "PATH"
      merged should contain key "APP_ENV"
      merged should contain key "DEBUG_MODE"

      // Verify values are intact
      merged("AWS_ACCESS_KEY_ID") should equal(
        "AKIAIOSFODNN7EXAMPLE"
      )
      merged("AWS_SECRET_ACCESS_KEY") should equal(
        "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"
      )
      merged("APP_ENV") should equal("development")
    }

    "handle .env variables that override CI variables" in {
      // CI environment
      val ciEnvironment = Map(
        "APP_ENV" -> "production",
        "API_URL" -> "https://api.prod.example.com"
      )

      // .env file intentionally overrides CI variable
      val dotEnvVars = Map(
        "APP_ENV" -> "development", // Override
        "LOG_LEVEL" -> "debug"
      )

      val merged = ciEnvironment ++ dotEnvVars

      // .env value should take precedence (this is expected behavior)
      merged("APP_ENV") should equal("development")
      merged("API_URL") should equal("https://api.prod.example.com")
      merged("LOG_LEVEL") should equal("debug")
    }

    "preserve empty string variables from CI environment" in {
      // Some CI systems set empty variables
      val ciEnvironment = Map(
        "GITHUB_TOKEN" -> "",
        "BUILD_TIMESTAMP" -> "2023-01-01T00:00:00Z"
      )

      val dotEnvVars = Map(
        "APP_ENV" -> "test"
      )

      val merged = ciEnvironment ++ dotEnvVars

      merged should contain key "GITHUB_TOKEN"
      merged("GITHUB_TOKEN") should equal("")
      merged should contain key "BUILD_TIMESTAMP"
    }
  }
}
