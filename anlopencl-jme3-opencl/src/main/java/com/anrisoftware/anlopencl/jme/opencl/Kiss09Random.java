package com.anrisoftware.anlopencl.jme.opencl;

/**
 * Implements KISS (Keep It Simple, Stupid) generator, proposed in 2009.
 *
 * G. Marsaglia, 64-bit kiss rngs,
 * https://www.thecodingforums.com/threads/64-bit-kiss-rngs.673657.
 *
 * @author RandomCL
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class Kiss09Random {

    /**
     * Seeds kiss09 RNG.
     *
     * @param state Variable, that holds state of the generator to be seeded.
     * @param seed  Value used for seeding. Should be randomly generated for each
     *              instance of generator (thread).
     */
    public static void kiss09_seed(Kiss09State state, long j) {
        long xx = Long.parseUnsignedLong("1234567890987654321");
        state.setX(xx ^ j);
        long cc = Long.parseUnsignedLong("123456123456123456");
        state.setC(cc ^ j);
        long yy = Long.parseUnsignedLong("362436362436362436");
        long y = yy ^ j;
        if (y == 0) {
            y = 1;
        }
        state.setY(y);
        long zz = Long.parseUnsignedLong("1066149217761810");
        state.setZ(zz ^ j);
    }

}
