package monocle.function

import monocle.{Iso, Optional}

import scala.util.Try
import cats.data.{Validated => Validation}

/**
 * Typeclass that defines an [[Optional]] from a monomorphic container `S` to a possible value `A`.
 * There must be at most one `A` in `S`.
 * @tparam S source of the [[Optional]]
 * @tparam A target of the [[Optional]], `A` is supposed to be unique for a given `S`
 */
abstract class Possible[S, A] extends Serializable {
  def possible: Optional[S, A]
}

trait PossibleFunctions {
  def possible[S, A](implicit ev: Possible[S, A]): Optional[S, A] = ev.possible
}

object Possible extends PossibleFunctions {
  /** lift an instance of [[monocle.Optional]] using an [[Iso]] */
  def fromIso[S, A, B](iso: Iso[S, A])(implicit ev: Possible[A, B]): Possible[S, B] = new Possible[S, B] {
    val possible: Optional[S, B] =
      iso composeOptional ev.possible
  }

  /************************************************************************************************/
  /** Std instances                                                                               */
  /************************************************************************************************/

  implicit def optionPossible[A]: Possible[Option[A], A] = 
    new Possible[Option[A], A] { 
      def possible = monocle.std.option.some.asOptional
    }

  implicit def eitherPossible[A,B]: Possible[Either[A,B], B] =
    new Possible[Either[A,B], B] { 
      def possible = monocle.std.either.stdRight.asOptional
    }

  implicit def validationPossible[A,B]: Possible[Validation[A,B], B] =
    new Possible[Validation[A,B], B] { 
      def possible = monocle.std.validation.success.asOptional
    }

  implicit def tryPossible[A]: Possible[Try[A], A] =
    new Possible[Try[A], A] { 
      def possible = monocle.std.utilTry.trySuccess.asOptional
    }
}