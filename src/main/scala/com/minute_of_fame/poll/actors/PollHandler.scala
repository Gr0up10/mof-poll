package com.minute_of_fame.poll.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.minute_of_fame.poll.actors.DataBase.SaveComplete
import com.minute_of_fame.poll.models.JsonPackets._
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object PollHandler {
  def props(db: ActorRef) = Props(classOf[PollHandler], db)

  case class Connected()
  case class Disconnected()
}

class PollHandler(db: ActorRef) extends Actor with ActorLogging {
  import com.minute_of_fame.poll.actors.PollHandler._

  private val clients = mutable.HashMap[Int, Int]()
  private var likes = 0
  private var dislikes = 0
  private var stream = 0

  private def statePack = CommandPacket("update_stat", UpdateStat(likes, dislikes).asJson.noSpaces).asJson.noSpaces

  private def sendState(): Unit = {
    val state = statePack
    for(c <- clients.keys) sender() ! packets.Packet(c, data = state)
  }

  override def receive: Receive = {
    case "connected" =>
      sender() ! packets.Register(name = "poll")
    case res: packets.Result =>
      if(res.status == packets.Result.Status.SUCCESS)
        log.info("Successfully connected")
      else
        log.error("Connection went unsuccessfully")
    case SaveComplete(streamId) =>
      if(streamId != stream){
        stream = streamId
        likes = 0
        dislikes = 0
        sendState()
      }
    case pack: packets.Packet =>
      pack.data match {
        case "connected" =>
          clients += (pack.userId -> 0)
          sender() ! packets.Packet(pack.userId, data = statePack)
        case "disconnected" =>
          clients -= pack.userId
        case str =>
          val cmdPack = decode[CommandPacket](str)
          cmdPack match {
            case Right(cmd) =>
              log.info("{} set {}", pack.userId, cmd.command)
              cmd.command match {
                case "like" =>
                  if(clients(pack.userId) != 1) {
                    if(clients(pack.userId) == -1) dislikes -= 1
                    clients(pack.userId) = 1
                    likes += 1
                    db ! DataBase.SaveVote(pack.userId, like = true)
                    sendState()
                  }
                case "dislike" =>
                  if(clients(pack.userId) != -1) {
                    if(clients(pack.userId) == 1) likes -= 1
                    clients(pack.userId) = -1
                    dislikes += 1
                    db ! DataBase.SaveVote(pack.userId, like = false)
                    sendState()
                  }
              }
            case Left(err) =>
              print(err)
          }
      }
  }
}
