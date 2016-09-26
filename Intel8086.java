
/** :D
    Intel 8086 Emulator
*/

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

public class Intel8086 {

    private static int signconv(final int w, final int x) {
        return x << 32 - BITS[w] >> 32 - BITS[w];
    }


    /* used to count clock cycles */
    private long clocks;



    /** 8086 Istruction Encoding
            http://aturing.umcs.maine.edu/~meadow/courses/cos335/8086-instformat.pdf
        
        [byte][7][6][5][4][3][2][1]
          [1] [   opcode    ][d][w]     opcode byte
          [2] [mode][  reg  ][r/m ]     addressing mode byte
          [3] [      optional     ]     low disp, addr ,or data
          [4] [      optional     ]     high disp, addr, or data
          [5] [      optional     ]     low data
          [6] [      optional     ]     high data

        [prefix] OPCODE [addr mode] [low disp] [high disp] [low data] [high data]

        d specifies the direction of data movement

        if d=1 then data moves from r/m to reg
        if d=0 then data moves from reg to r/m

        
        w (word/byte) specifies operand size

        if w=1 then data is word    (16 bits)
        if w=0 then data is byte     (8 bits)



        [E X A M P L E]

        Instruction     16-bit code
        mov ax,[bx]     8B 07
        mov eax,[bx]    66 8B 07
        mov ax,[ebx]    67 8B 03
        mov eax,[ebx]   67 66

    */



    private int op;     // op (instruction) code

    private int d;      // direction from/to register

    private int w;      // word/byte op

    private int mod;    // displacement length 

    private int reg;    //

    private int rm;     //

    private int ea;     // effective address




	
	/* 8086 REGISTERS */

	/* AX */
	private int ah, al;

	/* BX */
	private int bh, bl;

	/* CX */
	private int ch, cl;

	/* DX */
	private int dh, dl;




	/* 8086 POINTERS */

	/* Source Index - SI */
	private int si;

	/* Destination Index - DI */
	private int di;

	/* Base Pointer - BP */
	private int bp;

	/* Stack Pointer - SP */
	private int sp;







	/* Defining Segments */

	/* Code Segment - CS */
	private int cs;

	/* Data Segment - DS */
	private int ds;

	/* Extra Segment - ES */
	private int es;

	/* Stack Segment - SS*/
	private int ss;




	/* 8086 FLAGS */


    private int flags;


	/**
     * CF (carry flag)
     *
     * If an addition results in a carry out of the high-order bit of the
     * result, then CF is set; otherwise CF is cleared. If a subtraction
     * results in a borrow into the high-order bit of the result, then CF is
     * set; otherwise CF is cleared. Note that a signed carry is indicated by
     * CF ≠ OF. CF can be used to detect an unsigned overflow. Two
     * instructions, ADC (add with carry) and SBB (subtract with borrow),
     * incorporate the carry flag in their operations and can be used to
     * perform multibyte (e.g., 32-bit, 64-bit) addition and subtraction.
     */
    private static final int   CF     = 1 << 0;

    /**
     * PF (parity flag)
     *
     * If the low-order eight bits of an arithmetic or logical operation is
     * zero contain an even number of 1-bits, then the parity flag is set,
     * otherwise it is cleared. PF is provided for 8080/8085 compatibility; it
     * can also be used to check ASCII characters for correct parity.
     */
    private static final int   PF     = 1 << 2;

    /**
     * AF (auxiliary carry flag)
     *
     * If an addition results in a carry out of the low-order half-byte of the
     * result, then AF is set; otherwise AF is cleared. If a subtraction
     * results in a borrow into the low-order half-byte of the result, then AF
     * is set; otherwise AF is cleared. The auxiliary carry flag is provided
     * for the decimal adjust instructions and ordinarily is not used for any
     * other purpose.
     */
    private static final int   AF     = 1 << 4;

    /**
     * ZF (zero flag)
     *
     * If the result of an arithmetic or logical operation is zero, then ZF is
     * set; otherwise ZF is cleared. A conditional jump instruction can be used
     * to alter the flow of the program if the result is or is not zero.
     */
    private static final int   ZF     = 1 << 6;

    /**
     * SF (sign flag)
     *
     * Arithmetic and logical instructions set the sign flag equal to the
     * high-order bit (bit 7 or 15) of the result. For signed binary numbers,
     * the sign flag will be 0 for positive results and 1 for negative results
     * (so long as overflow does not occur). A conditional jump instruction can
     * be used following addition or subtraction to alter the flow of the
     * program depending on the sign of the result. Programs performing
     * unsigned operations typically ignore SF since the high-order bit of the
     * result is interpreted as a digit rather than a sign.
     */
    private static final int   SF     = 1 << 7;

    /**
     * TF (trap flag)
     *
     * Settings TF puts the processor into single-step mode for debugging. In
     * this mode, the CPU automatically generates an internal interrupt after
     * each instruction, allowing a program to be inspected as it executes
     * instruction by instruction.
     */
    private static final int   TF     = 1 << 8;

    /**
     * IF (interrupt-enable flag)
     *
     * Setting IF allows the CPU to recognize external (maskable) interrupt
     * requests. Clearing IF disables these interrupts. IF has no affect on
     * either non-maskable external or internally generated interrupts.
     */
    private static final int   IF     = 1 << 9;

    /**
     * DF (direction flag)
     *
     * Setting DF causes string instructions to auto-decrement; that is, to
     * process strings from the high addresses to low addresses, or from "right
     * to left". Clearing DF causes string instructions to auto-increment, or
     * to process strings from "left to right."
     */
    private static final int   DF     = 1 << 10;

    /**
     * OF (overflow flag)
     *
     * If the result of an operation is too large a positive number, or too
     * small a negative number to fit in the destination operand (excluding the
     * sign bit), then OF is set; otherwise OF is cleared. OF thus indicates
     * signed arithmetic overflow; it can be tested with a conditional jump or
     * the INFO (interrupt on overflow) instruction. OF may be ignored when
     * performing unsigned arithmetic.
     */
    private static final int   OF     = 1 << 11;


    
    /* Instruction queue */
    private final int[]     queue       = new int[6];



    /* Definition of the memory */
    protected final int[]   memory      = new int[0x100000];


    private int getAddr(final int seg, final int off) {
        return (seg << 4) + off;
    }



    // reset() resets the CPU to its default state
    public void reset() {
        flags = 0;
        ip = 0x0000;
        cs = 0xffff;
        ds = 0x0000;
        ss = 0x0000;
        es = 0x0000;
        for (int z=1; z<6; z++) {
            queue[z] = 0;
        }
        clocks = 0;
    }


    private void setFlag(final int flag, final boolean set) {
        if (set) {
            flags |= flag;
        } else {
            flags &= ~flag;
        }
    }



    /*
        load() loads code from a given file (String fileName), placing each
        byte starting from a given address (int addr)
    */
    public void load(final int addr, final String fileName) throws IOException {
        InputStream inputStream = this.getClass().getClassLoader.getResourceAsStream(fileName);
        final byte[] bin = new byte[inputStream.available()];
        DataInputStream dataIS = null;
        try {
            dataIS = new DataInputStream(inputStream);
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            if (dataIS != null)
                dataIS.close();
            is.close();
        }
        /* loading the code into memory */
        for (int i=1; i < bin.length; i++) {
            memory[addr+i] = bin[i] & 0xff;
        }
    }

}
