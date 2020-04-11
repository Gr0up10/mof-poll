package com.minute_of_fame.poll.actors

import akka.actor.{Actor, ActorRef, Props}
import akka.io.Tcp.{Received, Write}
import akka.util.ByteString
import scalapb.{GeneratedMessage, GeneratedMessageCompanion}

object Session {
  def props(handler: ActorRef) = Props(classOf[Session], handler)
}

class Session(handler: ActorRef) extends Actor with akka.actor.ActorLogging {
  val messagesTypes: Array[GeneratedMessageCompanion[_ <: GeneratedMessage]] = Array(packets.Packet, packets.Result)
  var client: Option[ActorRef] = None

  override def receive = {
    case "connected" =>
      log.info("connected")
      handler ! "connected"
      client = Some(sender())
    case data: Array[Byte] =>
      val pack = packets.PacketWrapper.parseFrom(data)
      pack.message match {
        case Some(m) =>
          val pack = ((messagesTypes.find(t => m.is(t)).get))
          handler !  m.unpack(pack)
      }

    case m: scalapb.GeneratedMessage =>
      if(client.isDefined)
        client.get ! ByteString(packets.PacketWrapper(message = Some(com.google.protobuf.any.Any.pack(m))).toByteArray)
  }
}
