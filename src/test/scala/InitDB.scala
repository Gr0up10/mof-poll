import com.minute_of_fame.poll.actors.ctx
import com.minute_of_fame.poll.models.DbModels.{AppPollstat, AppStream, AuthUser}

trait InitDB {
  import ctx._

  ctx.run(ctx.quote {ctx.query[AppPollstat].delete})
  ctx.run(ctx.quote {ctx.query[AppStream].delete})
  ctx.run(ctx.quote {ctx.query[AuthUser].delete})

  ctx.run(ctx.quote {ctx.query[AuthUser].insert(ctx.lift(AuthUser()))})
  ctx.run(ctx.quote {ctx.query[AppStream].insert(ctx.lift(AppStream()))})
}
