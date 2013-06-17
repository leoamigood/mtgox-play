import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

  val appName         = "mtgox-play"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm,
    "io.backchat.hookup" % "hookup_2.10" % "0.2.3",
    "org.reactivemongo" %% "play2-reactivemongo" % "0.9"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
  )

}
