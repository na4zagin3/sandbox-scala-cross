
// val scala2Version = "2.13.8"
val scala3Version = "3.2.2"

val projectVersion = "0.1.0"

val sharedSettings = Seq(
)

lazy val parser =
  // select supported platforms
  crossProject(JSPlatform, JVMPlatform, NativePlatform)
    .crossType(CrossType.Full) // [Pure, Full, Dummy], default: CrossType.Full
    .settings(
      name := "scala3-cross",
      version := projectVersion,

      libraryDependencies ++= Seq(
        "org.scala-lang.modules" %%% "scala-parser-combinators" % "2.3.0",

        "org.scalameta" %%% "munit" % "1.0.0-M7" % Test,
        "org.scalameta" %% "munit-scalacheck" % "1.0.0-M7" % Test,
      ),

      // To make the default compiler and REPL use Dotty
      scalaVersion := scala3Version,

      // To cross compile with Scala 3 and Scala 2
      // crossScalaVersions := Seq(scala3Version, scala2Version)
    )
    .jsConfigure(_.enablePlugins(NpmPackagePlugin))
    .jsSettings(
      scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) },
      npmPackageKeywords := Seq("gettext"),
      // npmPackageStage := "FastOptJS",
    /* ... */
    ) // defined in sbt-scalajs-crossproject
    .jvmSettings(/* ... */)
    // configure Scala-Native settings
    .nativeSettings(/* ... */) // defined in sbt-scala-native

// // Optional in sbt 1.x (mandatory in sbt 0.13.x)
// lazy val parserJS     = parser.js
// lazy val parserJVM    = parser.jvm
// lazy val parserNative = parser.native

lazy val app =
  // select supported platforms
  crossProject(JSPlatform, JVMPlatform, NativePlatform)
    .crossType(CrossType.Full) // [Pure, Full, Dummy], default: CrossType.Full
    .settings(
      name := "scala3-cross-app",
      version := projectVersion,
      libraryDependencies += "org.scalameta" %%% "munit" % "1.0.0-M7" % Test,
      scalaVersion := scala3Version,
    )
    .dependsOn(parser)
    .jsConfigure(_.enablePlugins(NpmPackagePlugin))
    .jsSettings(
      // Tell Scala.js that this is an application with a main method
      scalaJSUseMainModuleInitializer := true,

      scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) },
      npmPackageKeywords := Seq("gettext"),
      // npmPackageStage := "FastOptJS",
    /* ... */
    ) // defined in sbt-scalajs-crossproject
    .jvmSettings(/* ... */)
    // configure Scala-Native settings
    .nativeSettings(/* ... */) // defined in sbt-scala-native
