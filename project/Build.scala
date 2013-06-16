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
    "io.backchat.hookup" % "hookup_2.10" % "0.3.0-SNAPSHOT",
    "org.reactivemongo" %% "play2-reactivemongo" % "0.9",
    "com.roundeights" %% "hasher" % "0.3"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
  )

}
