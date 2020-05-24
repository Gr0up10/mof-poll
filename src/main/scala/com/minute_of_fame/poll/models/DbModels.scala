package com.minute_of_fame.poll.models

import java.time.LocalDate

object DbModels {
  case class AppPollstat(id: Int, vote: Int, streamId: Int, userId: Int, date: LocalDate)
  case class AppStream(id: Int = 0, streamId: String = "", active: Boolean = true, publisher_id: Int = 0,
                       date: LocalDate = LocalDate.now(), pending: Boolean = false)
  case class AuthUser(id: Int = 0, password: String = "", lastLogin: LocalDate = LocalDate.now(),
                      isSuperuser: Boolean = false, username: String = "", firstName: String = "",
                      lastName: String = "", email: String = "", isStaff: Boolean = false, isActive: Boolean = true,
                      dateJoined: LocalDate = LocalDate.now())
}
