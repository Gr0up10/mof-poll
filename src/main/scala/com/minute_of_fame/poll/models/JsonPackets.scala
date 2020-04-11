package com.minute_of_fame.poll.models

object JsonPackets {
  case class CommandPacket(command: String, data: String)
  case class UpdateStat(likes: Int, dislikes: Int)
}