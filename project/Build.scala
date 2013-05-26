import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "mtgox-play-2.0.4"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "io.backchat.hookup" % "hookup_2.9.2" % "0.2.2"
      // Add your project dependencies here,
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here      
    )

}
