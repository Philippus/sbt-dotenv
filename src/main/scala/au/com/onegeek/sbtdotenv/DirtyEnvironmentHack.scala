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

import java.util.Collections
import scala.util.{Failure, Success, Try}
import scala.util.control.NonFatal

/** Rewrite the runtime Environment, embedding entries from the .env file.
  *
  * Taken from:
  * http://stackoverflow.com/questions/318239/how-do-i-set-environment-variables-from-java/7201825#7201825
  *
  * Created by mfellows on 20/07/2014.
  */
object DirtyEnvironmentHack {
  def setEnv(newEnv: java.util.Map[String, String]): Unit = {
    Try({
      val processEnvironmentClass =
        Class.forName("java.lang.ProcessEnvironment")

      val theEnvironmentField =
        processEnvironmentClass.getDeclaredField("theEnvironment")
      theEnvironmentField.setAccessible(true)
      val env = theEnvironmentField
        .get(null)
        .asInstanceOf[java.util.Map[String, String]] // scalastyle:off null
      env.putAll(newEnv)

      val theCaseInsensitiveEnvironmentField =
        processEnvironmentClass.getDeclaredField(
          "theCaseInsensitiveEnvironment"
        )
      theCaseInsensitiveEnvironmentField.setAccessible(true)
      val ciEnv = theCaseInsensitiveEnvironmentField
        .get(null)
        .asInstanceOf[java.util.Map[String, String]] // scalastyle:off null
      ciEnv.putAll(newEnv)
    }) match {
      case Failure(_: NoSuchFieldException) =>
        Try({
          val classes = classOf[Collections].getDeclaredClasses
          val env = System.getenv
          classes
            .filter(_.getName == "java.util.Collections$UnmodifiableMap")
            .foreach(cl => {
              val field = cl.getDeclaredField("m")
              field.setAccessible(true)
              val map =
                field.get(env).asInstanceOf[java.util.Map[String, String]]
              map.clear()
              map.putAll(newEnv)
            })
        }) match {
          case Failure(NonFatal(e2)) =>
            e2.printStackTrace()
          case Success(_) =>
        }
      case Failure(NonFatal(e1)) =>
        e1.printStackTrace()
      case Success(_) =>
    }
  }
}
