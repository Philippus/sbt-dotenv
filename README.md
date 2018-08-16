# sbt-dotenv

[![Build Status](https://travis-ci.org/mefellows/sbt-dotenv.svg?branch=master)](https://travis-ci.org/mefellows/sbt-dotenv)
![Current Version](https://img.shields.io/badge/version-1.2.88-brightgreen.svg?style=flat "1.2.88")
[![License](https://img.shields.io/badge/license-MIT-blue.svg?style=flat "MIT")](LICENSE)

sbt plugin to load environment variables from .env into the JVM System Environment for local development.

Storing configuration in the environment is one of the tenets of a [twelve-factor app](http://www.12factor.net/). Anything that is likely to change between deployment environments–such as resource handles for databases or credentials for external services–should be extracted from the code into environment variables.

But it is not always practical to set environment variables on development machines or continuous integration servers where multiple projects are run. sbt-dotenv loads variables from a .env file into ENV when the environment is bootstrapped.

sbt-dotenv is intended to be used in development.

## Installation

Add the following to your sbt `project/plugins.sbt` file:

    addSbtPlugin("au.com.onegeek" %% "sbt-dotenv" % "1.2.88")

That's it - as soon as you start using sbt the environment is prepared.

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

## Should I commit my .env file?

It is recommended that you store development-only settings in your `.env` file, and commit it to your repository. Make sure that all your credentials for your development environment are different from your other deployments. This makes it easy for other developers to get started on your project, without compromising your credentials for other environments.

## sbt version

Please note that this plugin takes advantage of sbt [Auto Plugins](http://www.scala-sbt.org/0.13/docs/Plugins.html) and therefore only works in sbt v0.13.5+

## Contributing

1. Fork it
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Added some feature'`) and, optionally, squash history (`git rebase -i <previous commit hash>`)
4. Push to the branch (`git push origin my-new-feature`)
5. Create new Pull Request
