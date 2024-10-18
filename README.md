# sbt-dotenv

[![build](https://github.com/Philippus/sbt-dotenv/workflows/build/badge.svg)](https://github.com/Philippus/sbt-dotenv/actions/workflows/scala.yml?query=workflow%3Abuild+branch%3Amain)
![Current Version](https://img.shields.io/badge/version-3.0.0-brightgreen.svg?style=flat "3.0.0")
[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)
[![License](https://img.shields.io/badge/license-MIT-blue.svg?style=flat "MIT")](LICENSE)

sbt plugin to load environment variables from .env into the JVM System Environment for local development.

Storing configuration in the environment is one of the tenets of a [twelve-factor app](http://www.12factor.net/). Anything that is likely to change between deployment environments–such as resource handles for databases or credentials for external services–should be extracted from the code into environment variables.

But it is not always practical to set environment variables on development machines or continuous integration servers where multiple projects are run. sbt-dotenv loads variables from a .env file into ENV when the environment is bootstrapped.

sbt-dotenv is intended to be used in development.

## Installation

sbt-dotenv is published for sbt 1.3.9 and above. To start using it add the following to your plugins.sbt:
```
addSbtPlugin("nl.gn0s1s" % "sbt-dotenv" % "3.0.0")
```
That's it - as soon as you start using sbt the environment is prepared.

Note that the group id has changed from `au.com.onegeek` to `nl.gn0s1s`.

### Apple Silicon (M series chips)

When on Apple Silicon (M series), make sure to use sbt 1.6.0 or higher.

## Usage

Create a .env file in the root of your project with some environment specific settings. For example, you might want to set a Mongo DB port to 17017 if it's installed by Homebrew.

vi .env

```
# this is an example .env file, comments like this will be ignored
URL_HOST=my-service.example.org # trailing comments are ignored too!

# you can export variables like a regular shell script
export URL_PORT=1234

# variables can be quoted
URL_PATH="/my-content#body"

# variable expansion is supported
URL=http://$SERVICE_HOST:${SERVICE_PORT}${URL_PATH}

# these will work with sbt-dotenv, but won't work with `source .env`
MY.VARIABLE=1
MY-OTHER-VARIABLE=2

# multiline variables work too
MY_CERT="-----BEGIN CERTIFICATE-----
123456789qwertyuiopasdfghjklzxcvbnm
-----END CERTIFICATE-----
"

# heredocs aren't supported!
```

Variable expansion of the form `$FOO` and `${FOO}` is supported based on the values in `.env` or the system environment.

### Change file name
It is possible to override the default file name `.env`. It will be treated as an absolute path if it starts with a `/`, and as a relative path to base directory of this project otherwise.
```
ThisBuild  / envFileName := "dotenv"
```

### Use file to define environment for tests
It is possible to use same of alternative file to provide an environment for tests:
```
Test / envFileName := "test.env" // optional

Test / envVars := (Test / envFromFile).value
```

and integration tests:
```
IntegrationTest / envFileName := "test.env" // optional

IntegrationTest / envVars := (IntegrationTest / envFromFile).value
```

### "Illegal reflective access" warnings and exceptions

On java versions 9 and up this plugin will give "illegal reflective access"-warnings.
Java versions 16 and up turns the warnings into exceptions.

These can be avoided by starting sbt with these extra java options:

```--illegal-access=deny --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED```

The options can also be added to the `.sbtopts`-file:
```
-J--add-opens=java.base/java.util=ALL-UNNAMED
-J--add-opens=java.base/java.lang=ALL-UNNAMED
```
or in the `.jvmopts`-file:
```
--add-opens=java.base/java.util=ALL-UNNAMED
--add-opens=java.base/java.lang=ALL-UNNAMED
```

## Should I commit my .env file?

It is recommended that you store development-only settings in your `.env` file, and commit it to your repository. Make sure that all your credentials for your development environment are different from your other deployments. This makes it easy for other developers to get started on your project, without compromising your credentials for other environments.

## License
The code is available under the [MIT license](LICENSE).
