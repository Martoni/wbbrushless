import Chisel._

class Phase extends Bundle {
    val p = Bool(OUTPUT)
    val n = Bool(OUTPUT)
}

class BrushlessModule(c: Clock = null, freq: Int = 1250000) extends Module(c) {
  val io = new Bundle {
        /* parameters */
        val enable = Bool(INPUT)
        val dir = Bool(INPUT)
        val speed = UInt(INPUT, width=15)
        /* output */
        val phases = Vec.fill(3){new Phase()}
  }

  /* counter */
  val count = Reg(init=UInt(0, width=16))
  val timeout = Bool(false)
  when(count < speed) {
    count := count + UInt(1)
    timeout := Bool(false)
  }.otherwise {
    count := UInt(0)
    timeout := Bool(true)
  }

  /* state machine */
  val s_p1 :: s_p2 :: s_p3 :: s_p4 :: s_p5 :: s_p6 :: Nil = Enum(UInt(), 6)
  val state = Reg(init=s_p1)


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
