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

class SessionTest()
  extends TestKit(ActorSystem("MySpec"))
    with ImplicitSender
    with Matchers
    with FunSuiteLike
    with BeforeAndAfterAll {

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  test("Poll handler test"){
    val probe = TestProbe()
    val poll = system.actorOf(Session.props(probe.ref))
    poll ! "connected"
    probe.expectMsg("connected")
    poll ! packets.PacketWrapper(message = Some(com.google.protobuf.any.Any.pack(packets.Packet()))).toByteArray
    probe.expectMsg(packets.Packet())
    poll ! packets.Packet()
    expectMsg(ByteString(packets.PacketWrapper(message = Some(com.google.protobuf.any.Any.pack(packets.Packet()))).toByteArray))
  }
}
