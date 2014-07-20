SBT Plugin to load environment variables from .env into the JVM System Environment for local development.

Storing configuration in the environment is one of the tenets of a [twelve-factor app](http://www.12factor.net/). Anything that is likely to change between deployment environments–such as resource handles for databases or credentials for external services–should be extracted from the code into environment variables.

But it is not always practical to set environment variables on development machines or continuous integration servers where multiple projects are run. SBT dotenv loads variables from a .env file into ENV when the environment is bootstrapped.

SBT dotenv is intended to be used in development.

##Installation

Add the following line to your ```plugins.sbt``` file:

```scala
    addSbtPlugin("au.com.onegeek.sbtdotenv" %% "sbt-dotenv" % "0.1-SNAPSHOT")
```

That's it - as soon as you start using SBT the environment is prepared.

##Usage

Create a .env file in the root of your project with some environment specific settings. For example, you might want to set a Mongo DB port to 17017 if it's installed by Homebrew.

vi .env

```
MONGO_PORT=17017
I_BLOW_MY_NOSE=At you
```

## Should I commit my .env file?

It is recommended that you store development-only settings in your `.env` file, and commit it to your repository. Make sure that all your credentials for your development environment are different from your other deployments. This makes it easy for other developers to get started on your project, without compromising your credentials for other environments.

## Contributing

1. Fork it
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Added some feature'`)
4. Push to the branch (`git push origin my-new-feature`)
5. Create new Pull Request