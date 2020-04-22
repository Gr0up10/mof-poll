import java.time.LocalDate

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActors, TestKit, TestProbe}
import com.minute_of_fame.poll.actors.DataBase
import com.minute_of_fame.poll.actors.DataBase.SaveComplete
import com.minute_of_fame.poll.models.DbModels.{AppPollstat, AppStream, AuthUser}
import org.scalatest.{BeforeAndAfterAll, FunSuiteLike, Matchers}

//TODO: figure out the way to create test db
/*
class DBTest()
  extends TestKit(ActorSystem("MySpec"))
    with ImplicitSender
    with Matchers
    with FunSuiteLike
    with BeforeAndAfterAll
    with InitDB {

  import com.minute_of_fame.poll.actors.ctx
  import ctx._

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)

    ctx.run(ctx.quote {ctx.query[AppPollstat].delete})
    ctx.run(ctx.quote {ctx.query[AppStream].delete})
    ctx.run(ctx.quote {ctx.query[AuthUser].delete})
  }

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
    expectMsg(SaveComplete(0))
    val res = ctx.run(ctx.query[AppPollstat].filter(_.userId == 0))
    assert(res.size == 1)
    assert(res(0).vote == 0)
  }

}*/
