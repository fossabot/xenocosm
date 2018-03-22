package xenocosm
package data

import java.util.UUID
import economancy.data.{DepositAccount, Loan}

final case class Trader(uuid:UUID, ship:Ship, loans:Set[Loan[Currency]], deposits:Set[DepositAccount[Currency]])
