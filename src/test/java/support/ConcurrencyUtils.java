package support;

import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;


/**
 * Collection of concurrent utils.
 * 
 * @author Lars Hvile
 */
public final class ConcurrencyUtils {
    
    public static void await(CountDownLatch l) {
        try {
            assertTrue(l.await(2, SECONDS));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    public static void wait(Object o) {
        try {
            o.wait(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    public static void assertExecutionTimeLessThanMs(long millis, Runnable operation) {
        final long now = currentTimeMillis();
        operation.run();
        final long elapsed = currentTimeMillis() - now;
        
        assertTrue("expected operation to take < " + millis + "ms, was: " + elapsed,
            elapsed < millis);
    }
    
    
    private ConcurrencyUtils() {
    }
}
