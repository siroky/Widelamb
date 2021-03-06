package widelamb

sealed abstract class Term
sealed abstract class Constant(val tau: TypeConstant) extends Term

case class IntegerConstant(value: Int) extends Constant(Integer) {
    override def toString = value.toString
}

case class Variable(name: String) extends Term {
    override def toString = name
}

case class Abstraction(variable: Variable, body: Term) extends Term {
    override def toString = s"(\\$variable . $body)"
}

case class Application(function: Term, argument: Term) extends Term {
    override def toString = s"($function $argument)"
}

case class Let(variable: Variable, value: Term, body: Term) extends Term {
    override def toString = s"(let $variable = $value in $body)"
}

case class Fix(variable: Variable, body: Term) extends Term {
    override def toString = s"(fix $variable . $body)"
}
