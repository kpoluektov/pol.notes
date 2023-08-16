ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.11"

lazy val root = (project in file("."))
  .settings(
    name := "pol.notes.PessimisticLock"
  )

libraryDependencies ++= {
  Seq(
    "com.oracle.database.jdbc" % "ojdbc8" % "12.2.0.1",
    "org.postgresql" % "postgresql" % "42.2.23"
  )
}
