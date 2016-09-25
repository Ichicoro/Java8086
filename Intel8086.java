public class Intel8086() {
	
	/* Let's define registers */

	/* AX */
	private int ah, al;

	/* BX */
	private int bh, bl;

	/* CX */
	private int ch, cl;

	/* DX */
	private int dh, dl;




	/* Let's define pointers */

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


    

}