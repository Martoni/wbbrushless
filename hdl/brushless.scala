import Chisel._

class BrushlessModule(c: Clock = null) extends Module(c) {
  val io = new Bundle {
        /* parameters */
        val enable = Bool(INPUT)
        /* output */
        val pwmout = Bool(OUTPUT)
  }

  io.pwmout := io.enable
}

class BrushlessTests(c: BrushlessModule) extends Tester(c) {
  step(1)
}

object brushless {
  def main(args: Array[String]): Unit = {
    chiselMainTest(Array[String]("--backend", "c", "--compile", "--test", "--genHarness"),
       () => Module(new BrushlessModule())){c => new BrushlessTests(c)}
  }
}
