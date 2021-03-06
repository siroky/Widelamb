package widelamb

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FlatSpec

class ParserSpec extends Parser with FlatSpec with ShouldMatchers
{
    val List(x, y, z) = List("x", "y", "z").map(Variable)

    "The Parser" should "parser integer literals" in {
        parsing("""0""") should equal(IntegerConstant(0))
        parsing("""123""") should equal(IntegerConstant(123))
        parsing("""-456""") should equal(IntegerConstant(-456))
    }

    it should "parser variables" in {
        parsing("""x""") should equal(x)
        parsing("""foo""") should equal(Variable("foo"))
        parsing("""FoOBaR""") should equal(Variable("FoOBaR"))
    }

    it should "parse lamda application" in {
        parsing("""x y""") should equal(Application(x, y))
        parsing("""x         y""") should equal(Application(x, y))
        parsing("""x y z""") should equal(Application(Application(x, y), Variable("z")))
    }

    it should "parse lamda abstraction" in {
        parsing("""\x.y""") should equal(Abstraction(x, y))
        parsing("""\x.\y.y""") should equal(Abstraction(x, Abstraction(y, y)))
        parsing("""\x  .  \y  .  y""") should equal(Abstraction(x, Abstraction(y, y)))
    }

    it should "parse let expression" in {
        parsing("""let x = y in y""") should equal(Let(x, y, y))
        parsing("""let x = y in z x""") should equal(Let(x, y, Application(z, x)))
    }

    it should "parse parenthesized terms" in {
        parsing("""((x))""") should equal(x)
        parsing("""(x y) (y x)""") should equal(Application(Application(x, y), Application(y, x)))
    }

    it should "parse fix expression" in {
        parsing("""fix x . y""") should equal(Fix(x, y))
        parsing("""fix x . \y . x x""") should equal(Fix(x, Abstraction(y, Application(x, x))))
    }

    it should "parse example program" in {
        parsing("""(fix fact . \x . ifzero x 1 (mul x (fact (minus x 1)))) 10""") should equal(
            Application(
                Fix(
                    Variable("fact"),
                    Abstraction(
                        x,
                        Application(
                            Application(
                                Application(Variable("ifzero"), x),
                                IntegerConstant(1)
                            ),
                            Application(
                                Application(Variable("mul"), x),
                                Application(
                                    Variable("fact"),
                                    Application(
                                        Application(Variable("minus"), x),
                                        IntegerConstant(1)
                                    )
                                )
                            )
                        )
                    )
                ),
                IntegerConstant(10)
            )
        )
    }

    def parsing(s: String): Term = {
        parse(s) match {
            case Right(result) => result
            case Left(message) => fail(message)
        }
    }
}
