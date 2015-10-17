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
  val timeout = Reg(init=Bool(false))
  when(count < io.speed) {
    count := count + UInt(1)
    timeout := Bool(false)
  }.otherwise {
    count := UInt(0)
    timeout := Bool(true)
  }

  /* state machine */
  val s_p1 :: s_p2 :: s_p3 :: s_p4 :: s_p5 :: s_p6 :: Nil = Enum(UInt(), 6)
  val state = Reg(init=s_p1)


  switch(state) {
      is(s_p1) {
          when(timeout) {
            when(io.dir) { state := s_p2 }
                .otherwise { state := s_p6 }
          }
      }
      is(s_p2) {
          when(timeout) {
            when(io.dir) { state := s_p3 }
                .otherwise { state := s_p1 }
          }
      }
      is(s_p3) {
          when(timeout) {
            when(io.dir) { state := s_p4 }
                .otherwise { state := s_p2 }
          }
      }
      is(s_p4) {
          when(timeout) {
            when(io.dir) { state := s_p5 }
                .otherwise { state := s_p3 }
          }
      }
      is(s_p5) {
          when(timeout) {
            when(io.dir) { state := s_p6 }
                .otherwise { state := s_p4 }
          }
      }
      is(s_p6) {
          when(timeout) {
            when(io.dir) { state := s_p1 }
                .otherwise { state := s_p5 }
          }
      }
  }

  io.phases(0).p := !(state===s_p2 && state===s_p3)
  io.phases(0).n := (state===s_p5 && state===s_p6)
  io.phases(1).p := !(state===s_p1 && state===s_p6)
  io.phases(1).n := (state===s_p3 && state===s_p4)
  io.phases(2).p := !(state===s_p4 && state===s_p5)
  io.phases(2).n := (state===s_p1 && state===s_p2)
}

class BrushlessTests(c: BrushlessModule) extends Tester(c) {
    poke(c.io.enable, 1)
    poke(c.io.speed, 5)
    step(50)
}

object brushless {
  def main(args: Array[String]): Unit = {
      chiselMainTest(Array[String]("--backend", "c", "--compile", "--test", "--genHarness", "--vcd"),
       () => Module(new BrushlessModule())){c => new BrushlessTests(c)}
  }
}
