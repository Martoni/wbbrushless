import Chisel._

class Phase extends Bundle {
    val p = Bool(OUTPUT)
    val n = Bool(OUTPUT)
}

class BrushlessModule(c: Clock = null, freq: Int = 1250000) extends Module(c) {
  val io = new Bundle {
        /* parameters */
        val enable = Bool(INPUT)
        val speed = SInt(INPUT, width=16) 
        /* output */
        val phases = Vec.fill(3){new Phase()}
  }


  val count = Reg(init=UInt(0, width=6))

  count := count + UInt(1)

  io.phases(0).p := Bool(true)

}

class BrushlessTests(c: BrushlessModule) extends Tester(c) {
    poke(c.io.enable, 1)
    step(10)
}

object brushless {
  def main(args: Array[String]): Unit = {
      chiselMainTest(Array[String]("--backend", "c", "--compile", "--test", "--genHarness", "--vcd"),
       () => Module(new BrushlessModule())){c => new BrushlessTests(c)}
  }
}
