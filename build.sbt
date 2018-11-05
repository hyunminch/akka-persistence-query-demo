lazy val commonSettings = Seq(
  organization := "io.github.hyunminch",
  version := "0.0.1",
  scalaVersion := "2.12.6",
  scalacOptions ++= Seq(
    "-unchecked",
    "-deprecation",
    "-encoding",
    "utf8",
    "-feature",
    "-language:postfixOps",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-language:reflectiveCalls",
    "-Ypatmat-exhaust-depth",
    "off",
    "-Ypartial-unification"),
  Test / testOptions += Tests.Argument("-oD"),
  Test / fork := true,
  resolvers += Resolver.bintrayRepo("hseeberger", "maven"),
  resolvers += Resolver.sonatypeRepo("releases"),
  assembly / assemblyMergeStrategy := {
    case "application.conf" => MergeStrategy.first
    case PathList("org", "apache", "commons", "logging", xs @ _*) => MergeStrategy.first
    case netty if netty.endsWith("META-INF/io.netty.versions.properties") => MergeStrategy.first
    case manifest if manifest.contains("MANIFEST.MF") =>
      // We don't need manifest files since sbt-assembly will create
      // one with the given settings
      MergeStrategy.discard
    case referenceOverrides if referenceOverrides.contains("reference-overrides.conf") =>
      // Keep the content for all reference-overrides.conf files
      MergeStrategy.concat
    case x =>
      // For all the other files, use the default sbt-assembly merge strategy
      val oldStrategy = (assemblyMergeStrategy in assembly).value
      oldStrategy(x)
  },
  addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.7"),
  // if your project uses multiple Scala versions, use this for cross building
  addCompilerPlugin("org.spire-math" % "kind-projector" % "0.9.7" cross CrossVersion.binary),
  // if your project uses both 2.10 and polymorphic lambdas
  libraryDependencies ++= (scalaBinaryVersion.value match {
    case "2.10" =>
      compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full) :: Nil
    case _ =>
      Nil
  })
)

lazy val app = (project in file("."))
  .settings(commonSettings: _*)
  .settings(assembly / assemblyOutputPath := file("bin/app.jar"))
  .settings(name := "akka-persistence-query-demo")
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % "2.5.17",
      "com.typesafe.akka" %% "akka-testkit" % "2.5.17" % Test
    ),
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-stream" % "2.5.17",
      "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.17" % Test
    ),
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % "10.1.5",
      "com.typesafe.akka" %% "akka-http-testkit" % "10.1.5" % Test
    ),
    libraryDependencies += "com.typesafe.akka" %% "akka-cluster" % "2.5.17",
    libraryDependencies += "com.typesafe.akka" %% "akka-cluster-sharding" %  "2.5.17",
    libraryDependencies += "com.typesafe.akka" %% "akka-persistence" % "2.5.17"
  )
  .enablePlugins(AkkaGrpcPlugin)
  .enablePlugins(JavaAgent)
