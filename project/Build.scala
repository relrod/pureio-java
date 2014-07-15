import sbt._
import Keys._

object BuildSettings {
  val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := "me.elrod",
    version := "0.0.1-SNAPSHOT",
    publishTo := Some(Resolver.file("file", new File("releases")))
  )
}

object MainBuild extends Build {
  import BuildSettings._

  lazy val root: Project = Project(
    "pureio",
    file("."),
    settings = buildSettings ++ Seq(
      publishArtifact := false,
      run <<= run in Compile in core)
  ) aggregate(core, examples, test)

  lazy val core: Project = Project(
    "pureio-core",
    file("pureio-core"),
    settings = buildSettings
  )

  lazy val examples: Project = Project(
    "pureio-examples",
    file("pureio-examples"),
    settings = buildSettings ++ Seq(
      publishArtifact := false
    )
  ) dependsOn(core)


  lazy val test: Project = Project(
    "pure-test",
    file("pureio-test"),
    settings = buildSettings ++ Seq(
      publishArtifact := false
    )
  ) dependsOn(core)
}
