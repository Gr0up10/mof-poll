package com.minute_of_fame.poll

import java.net.InetSocketAddress

import akka.actor.{ActorRef, ActorSystem}
import com.minute_of_fame.poll.actors.{Client, DataBase, PollHandler}
import com.minute_of_fame.stream.actors.Session
import com.typesafe.config.ConfigFactory
import io.getquill.util.LoadConfig.getClass

object Poll extends App {
  implicit val actorSystem: ActorSystem = ActorSystem()
  var db = actorSystem.actorOf(DataBase.props)
  var handler = actorSystem.actorOf(PollHandler.props(db), "poll_handler")
  val session = actorSystem.actorOf(Session.props(handler), "session")
  val host = scala.util.Properties.envOrElse("HANDLER_HOST", "localhost")
  val port = scala.util.Properties.envOrElse("HANDLER_PORT", "58008").toInt
  println(s"Connecting to $host:$port")
  val mainActor = actorSystem.actorOf(Client.props(new InetSocketAddress(host, port), session), "client")
}
