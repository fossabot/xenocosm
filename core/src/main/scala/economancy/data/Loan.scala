package economancy
package data

import java.time.Instant

final case class Loan[A](principal:Money[A], rate:BigDecimal, repayment:Instant)
