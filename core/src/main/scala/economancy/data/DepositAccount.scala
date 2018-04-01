package economancy
package data

final case class DepositAccount[A](money:Money[A], rate:BigDecimal)

object DepositAccount {
  trait Instances {
    implicit def depositAccountHasTimeValue:TimeValue[DepositAccount] =
      new TimeValue[DepositAccount] {
        def rate[A]:DepositAccount[A] => BigDecimal = _.rate
        def presentValue[A]:DepositAccount[A] => Money[A] = _.money
        def updatedValue[A]:(DepositAccount[A], Money[A]) => DepositAccount[A] =
          (account, money) => account.copy(money = money)
      }
  }
  object instances extends Instances
}
