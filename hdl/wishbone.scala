import Chisel._

package object Wishbone {

    class WbSlave(dataWidth: Int, addrWidth: Int) extends Bundle {
        val adr_i = UInt(INPUT, width=addrWidth)
        val dat_o = UInt(OUTPUT, width=dataWidth)
        val dat_i = UInt(INPUT, width=dataWidth)
        val we_i = Bool(INPUT)
        val sel_i = UInt(INPUT, width=dataWidth/8)
        val stb_i = Bool(INPUT)
        val ack_o = Bool(OUTPUT)
        val cyc_i = Bool(INPUT)
    }
}
