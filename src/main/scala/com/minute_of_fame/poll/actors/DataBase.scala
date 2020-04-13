package com.minute_of_fame.poll.actors

import java.time.LocalDate

import akka.actor.{Actor, ActorLogging, Props}
import com.minute_of_fame.poll.actors.DataBase.{SaveComplete, SaveVote}
import com.minute_of_fame.poll.models.DbModels.{AppPollstat, AppStream}
import io.getquill.context.jdbc.{Decoders, Encoders}
import io.getquill.{Literal, PostgresJdbcContext}
import io.getquill._

object DataBase {
  def props = Props(classOf[DataBase])

  case class SaveVote(id: Int, like: Boolean)
  case class SaveComplete(streamId: Int)
}

object ctx extends PostgresJdbcContext(SnakeCase, "db") with Encoders with Decoders

class DataBase extends Actor  with ActorLogging{
  import ctx._

  private def currentStream =
    run(query[AppStream].filter(_.active)).lift(0)

  override def receive: Receive = {
    case SaveVote(id, vote) =>
      currentStream match {
        case Some(curSt) =>
          val streamId = curSt.id
          def model(id: Int) = AppPollstat(id, if(vote) 1 else 0,streamId, id, LocalDate.now())

          run(query[AppPollstat].filter(m => m.userId==lift(id) && m.streamId == lift(streamId))).lift(0) match {
            case Some(m) =>
              run(query[AppPollstat].update(lift(model(m.id))))
            case None =>
              run(query[AppPollstat].insert(lift(model(0))))
          }

          sender() ! SaveComplete(streamId)
        case None =>
      }
  }
}
