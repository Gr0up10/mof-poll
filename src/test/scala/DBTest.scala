import java.time.LocalDate

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActors, TestKit, TestProbe}
import com.minute_of_fame.poll.actors.DataBase
import com.minute_of_fame.poll.actors.DataBase.SaveComplete
import com.minute_of_fame.poll.models.DbModels.{AppPollstat, AppStream, AuthUser}
import org.scalatest.{BeforeAndAfterAll, FunSuiteLike, Matchers}

class DBTest()
  extends TestKit(ActorSystem("MySpec"))
    with ImplicitSender
    with Matchers
    with FunSuiteLike
    with BeforeAndAfterAll
    with InitDB {

  import com.minute_of_fame.poll.actors.ctx

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  import ctx._

  test("DB actor test"){
    val poll = system.actorOf(DataBase.props)
    test("Save model") {
      poll ! DataBase.SaveVote(0, like = true)
      expectMsg(SaveComplete(0))
      val res = ctx.run(ctx.query[AppPollstat].filter(_.userId == 0))
      assert(res.size == 1)
      assert(res(0).streamId == 0)
    }
    test("Change vote") {
      poll ! DataBase.SaveVote(0, like = false)
      val res = ctx.run(ctx.query[AppPollstat].filter(_.userId == 0))
      assert(res.size == 1)
      assert(res(0).vote == 0)
    }
   }
}
