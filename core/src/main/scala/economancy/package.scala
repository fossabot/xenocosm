/**
  * <img src="/xenocosm/img/economancy-128.png" />
  * Provides data types and typeclasses for procedurally-generated markets
  *
  * ==Overview==
  * As much as possible, all data types have an instance of `spire.random.Dist`
  * in the companion object.
  */
package object economancy {
  object implicits
    extends TimeValue.Syntax
    with data.DepositAccount.Instances
}
