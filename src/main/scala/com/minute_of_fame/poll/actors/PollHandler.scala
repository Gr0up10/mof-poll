package com.minute_of_fame.poll.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.minute_of_fame.poll.models.JsonPackets._
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

import scala.collection.mutable.ArrayBuffer

object PollHandler {
  def props(db: ActorRef) = Props(classOf[PollHandler], db)

  case class Connected()
  case class Disconnected()
}

class PollHandler(db: ActorRef) extends Actor with ActorLogging{
  import com.minute_of_fame.poll.actors.PollHandler._

  private val clients = ArrayBuffer[Int]()
  private var likes = 0
  private var dislikes = 0

  private def statePack = CommandPacket("update_stat", UpdateStat(likes, dislikes).asJson.noSpaces).asJson.noSpaces

  private def sendState(): Unit = {
    val state = statePack
    for(c <- clients) sender() ! packets.Packet(c, data = state)
  }

  override def receive: Receive = {
    case "connected" =>
      sender() ! packets.Register(name = "poll")
    case res: packets.Result =>
      if(res.status == packets.Result.Status.SUCCESS)
        log.info("Successfully connected")
      else
        log.error("Connection went unsuccessfully")
    case pack: packets.Packet =>
      pack.data match {
        case "connected" =>
          clients += pack.userId
          sender() ! packets.Packet(pack.userId, data = statePack)
        case "disconnected" =>
          clients -= pack.userId
        case str =>
          val cmdPack = decode[CommandPacket](str)
          cmdPack match {
            case Right(cmd) =>
              cmd.command match {
                case "like" =>
                  likes += 1
                  sendState()
                case "dislike" =>
                  dislikes += 1
                  sendState()
              }
            case Left(err) =>
              print(err)
          }
      }
  }
}
