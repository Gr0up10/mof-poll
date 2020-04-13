import akka.actor.ActorSystem
import akka.io.Tcp.Write
import akka.testkit.{ImplicitSender, TestActors, TestKit, TestProbe}
import akka.util.ByteString
import com.minute_of_fame.poll.actors.{PollHandler, Session}
import com.minute_of_fame.poll.models.JsonPackets.{CommandPacket, UpdateStat}
import org.scalatest.{BeforeAndAfterAll, FunSuiteLike, Matchers}
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import io.circe._

class PollHandlerTest()
  extends TestKit(ActorSystem("MySpec"))
    with ImplicitSender
    with Matchers
    with FunSuiteLike
    with BeforeAndAfterAll {

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  test("Poll handler test"){
    val echo = system.actorOf(TestActors.blackholeProps)
    val poll = system.actorOf(PollHandler.props(echo))
    poll ! "connected"
    expectMsg(packets.Register(name = "poll"))
    poll ! packets.Packet(userId = 1, data = "connected")
    expectMsg(packets.Packet(userId = 1, data = CommandPacket("update_stat", UpdateStat(0, 0).asJson.noSpaces).asJson.noSpaces))
    poll ! packets.Packet(userId = 1, data = CommandPacket("like", "").asJson.noSpaces)
    expectMsg(packets.Packet(userId = 1, data = CommandPacket("update_stat", UpdateStat(1, 0).asJson.noSpaces).asJson.noSpaces))
    poll ! packets.Packet(userId = 1, data = CommandPacket("dislike", "").asJson.noSpaces)
    expectMsg(packets.Packet(userId = 1, data = CommandPacket("update_stat", UpdateStat(0, 1).asJson.noSpaces).asJson.noSpaces))
    poll ! packets.Packet(userId = 2, data = "connected")
    expectMsg(packets.Packet(userId = 2, data = CommandPacket("update_stat", UpdateStat(0, 1).asJson.noSpaces).asJson.noSpaces))
  }
}
