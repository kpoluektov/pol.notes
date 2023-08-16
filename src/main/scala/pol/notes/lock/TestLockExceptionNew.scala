package pol.notes.lock

import java.sql.{Connection, SQLException, Types}
import scala.util.{Try, Using}

object TestLockExceptionNew extends App {
  val updateSql = "select 1 from type_test_table where id = ? for update nowait";
  def execLockTest(exec: DBExecute, id: Integer) {
    val check: Try[Unit] = Using(exec.getConnection) { conn =>
      def LockAndLog(conn: Connection): Unit = {
        var isLockSuccess = true
        val updStatement = conn.prepareCall(updateSql)
        try {
          updStatement.setInt(1, id /*potentially locked row ID*/)
          updStatement.execute()
        } catch {
          case e: SQLException if exec.isRowLockException(e) => conn.rollback(); println("Row is already locked"); isLockSuccess = false
        } finally {
          exec.logIt(conn, id, isLockSuccess)
        }
      }
      conn.setAutoCommit(false)
      // place for some prep-code including savepoint
      LockAndLog(conn)
      // place for some after-code including overall transaction control - commit or rollback
    }
    println(exec.getClass.getName + " test is " + check)
  }
  execLockTest(new OraExecute(), 1)
  execLockTest(new PGExecute, 1)
}