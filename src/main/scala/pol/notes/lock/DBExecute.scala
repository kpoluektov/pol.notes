package pol.notes.lock

import java.sql.{Connection, DriverManager, SQLException}
import java.util.Properties

abstract class DBExecute {
  val props = new Properties()
  def getConnection: Connection
  def isRowLockException(exception: SQLException): Boolean
  def getInsertSql: String
  def logIt(conn: Connection, objId: Integer, success: Boolean): Unit = {
    val ps = conn.prepareCall(getInsertSql)
    ps.setInt(1, objId)
    ps.setBoolean(2, success)
    ps.execute()
  }
}

class OraExecute extends DBExecute {
  override def isRowLockException(exception: SQLException): Boolean = exception.getErrorCode.equals(54)

  override def getConnection: Connection = {
    Class.forName("oracle.jdbc.driver.OracleDriver")
    val url = "jdbc:oracle:thin:pol/pol@localhost:1521:xe"
    DriverManager.getConnection(url, props)
  }

  def getInsertSql: String = "insert into log_table  values (?, ?, systimestamp)"
}

class PGExecute extends DBExecute {
  override def isRowLockException(exception: SQLException): Boolean = exception.getSQLState.equals("55P03")
  override def getConnection: Connection = {
    Class.forName("org.postgresql.Driver")
    val url = "jdbc:postgresql://localhost:5432/postgres?user=postgres&password=postgres&currentSchema=pol"
    //props.setProperty("autosave", "always")
    DriverManager.getConnection(url, props)
  }
  def getInsertSql: String = "insert into log_table  values (?, ?, now())"
}
