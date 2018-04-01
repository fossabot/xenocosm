package economancy
package data

import java.time.{Duration, Instant, LocalDate, Period}
import java.time.temporal.{Temporal, TemporalAmount}

final case class PaymentSchedule[T <: TemporalAmount, U <: Temporal](compounding:T, on:T, start:U, end:U)

object PaymentSchedule {

  /** A period schedule where payments are due on the first day of the period
    *
    * @param compounding The compounding period
    * @param start       The start date of the schedule
    * @param end         The end date of the schedule
    * @return            The payment schedule
    */
  def period(compounding:Period, start:LocalDate, end:LocalDate):PaymentSchedule[Period, LocalDate] =
    PaymentSchedule(compounding, Period.ofDays(1), start, end)

  def period(compounding:Period, end:LocalDate):PaymentSchedule[Period, LocalDate] =
    period(compounding, LocalDate.now(), end)

  def period(compounding:Period):PaymentSchedule[Period, LocalDate] =
    period(compounding, LocalDate.MAX)

  /** A duration schedule where payments are due on the first nanosecond of the period
    *
    * @param compounding The compounding period
    * @param start       The start date of the schedule
    * @param end         The end date of the schedule
    * @return            The payment schedule
    */
  def duration(compounding:Duration, start:Instant, end:Instant):PaymentSchedule[Duration, Instant] =
    PaymentSchedule(compounding, Duration.ofNanos(1), start, end)

  def duration(compounding:Duration, end:Instant):PaymentSchedule[Duration, Instant] =
    duration(compounding, Instant.now(), end)

  def duration(compounding:Duration):PaymentSchedule[Duration, Instant] =
    duration(compounding, Instant.MAX)
}
