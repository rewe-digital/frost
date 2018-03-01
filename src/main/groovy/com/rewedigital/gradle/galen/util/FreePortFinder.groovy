package com.rewedigital.gradle.galen.util

class FreePortFinder {

    private static final int DYNAMIC_PORT_RANGE_LB = 49152
    private static final int DYNAMIC_PORT_RANGE_UB = 65535

    /**
     * Not 100% bullet-proof, as two callers may get the same port number. But should be sufficient for us.
     */
    static int getFreePort() throws BindException {
        while (true) {
            int port = provideRandomPortNumber()
            ServerSocket ss
            DatagramSocket ds
            try {
                ss = new ServerSocket(port)
                ss.setReuseAddress(true)
                ds = new DatagramSocket(port)
                ds.setReuseAddress(true)
                return port
            } catch (IOException e) {
                // obviously already occupied, try the next one
            } finally {
                closeQuietly(ss)
                closeQuietly(ds)
            }
        }
    }

    private static int provideRandomPortNumber() {
        int range = DYNAMIC_PORT_RANGE_UB - DYNAMIC_PORT_RANGE_LB
        new Random().nextInt(range) + DYNAMIC_PORT_RANGE_LB
    }

    private static void closeQuietly(final Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close()
            } catch (final IOException e) {
                // ignore
            }
        }
    }
}
