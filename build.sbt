val Http4sVersion = "0.23.6"
val CirceVersion = "0.14.1"
val MunitVersion = "0.7.29"
val LogbackVersion = "1.2.6"
val MunitCatsEffectVersion = "1.0.6"
val doobieVersion = "1.0.0-RC1"

lazy val root = (project in file("."))
  .settings(
    organization := "com.example",
    name := "template-project",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.13.6",
    resolvers += Resolver.sonatypeRepo("snapshots"),
    libraryDependencies ++= Seq(
      "org.http4s"      %% "http4s-ember-server" % Http4sVersion,
      "org.http4s"      %% "http4s-ember-client" % Http4sVersion,
      "org.http4s"      %% "http4s-circe"        % Http4sVersion,
      "org.http4s"      %% "http4s-dsl"          % Http4sVersion,
      "io.circe"        %% "circe-generic"       % CirceVersion,
      "org.scalameta"   %% "munit"               % MunitVersion           % Test,
      "org.typelevel"   %% "munit-cats-effect-3" % MunitCatsEffectVersion % Test,
      "ch.qos.logback"  %  "logback-classic"     % LogbackVersion,
      "org.scalameta"   %% "svm-subs"            % "20.2.0",
      "org.tpolecat" %% "doobie-core"      % doobieVersion,

      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s" %% "http4s-blaze-client" % Http4sVersion,

      // And add any of these as needed
      "org.tpolecat" %% "doobie-h2"        % doobieVersion,          // H2 driver 1.4.200 + type mappings.
      "org.tpolecat" %% "doobie-hikari"    % doobieVersion,          // HikariCP transactor.
      "org.tpolecat" %% "doobie-postgres"  % doobieVersion,          // Postgres driver 42.3.1 + type mappings.
      "org.tpolecat" %% "doobie-specs2"    % doobieVersion % "test", // Specs2 support for typechecking statements.
      "org.tpolecat" %% "doobie-scalatest" % doobieVersion % "test",  // ScalaTest support for typechecking statements.
      "com.github.pureconfig" %% "pureconfig" % "0.17.1",
      "com.github.cb372" %% "scalacache-caffeine" % "0.28.0"
    ),
    addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.13.0" cross CrossVersion.full),
    addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1"),
    testFrameworks += new TestFramework("munit.Framework")
  )
