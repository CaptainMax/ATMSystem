import java.util.concurrent.atomic.AtomicInteger;

public class RateLimiter {
    public static void main(String[] args) throws InterruptedException {
        FixedWindowRateLimiter rateLimiter = new FixedWindowRateLimiter(5, 1);
        for(int i = 0; i < 10; i++){
            System.out.println("Request " + (i + 1) + ": " + (rateLimiter.isAllowed() ? "Allowed" : "Blocked"));
            Thread.sleep(10);
        }
    }
}

//单机实现
class FixedWindowRateLimiter{
    private final int maxRequests;
    private final long windowSize;
    private AtomicInteger counter;
    private long windowStart;

    public FixedWindowRateLimiter(int maxRequests, long windowSizeInSeconds){
        this.maxRequests = maxRequests;
        this.windowSize = windowSizeInSeconds * 1000;
        this.counter = new AtomicInteger(0);
        this.windowStart = System.currentTimeMillis();
    }

    public synchronized boolean isAllowed(){
        long currentTime = System.currentTimeMillis();

        if(currentTime - windowStart >= windowSize){
            counter.set(0);
            windowStart = currentTime;
        }

        if(counter.incrementAndGet() <= maxRequests){
            return true;
        } else {
            return false;
        }
    }
}
// //redis 实现
// class RedisFixedWindowRatelimiter{
//     private static final String REDIS_HOST = "localhost";
//     private static final int REDIS_PORT = 6379;
//     private static final int MAX_REQUEST = 5;
//     private static final int WINDOW_SIZE = 1;

//     private Jedis jedis;
//     public RedisFixedWindowRatelimiter() {
//         this.jedis = new Jedis(REDIS_HOST, REDIS_PORT);
//     }

//     public boolean isAllowed(String userId){
//         String key = "rate_limit: "+ userId;
//         long currentCount = jedis.incr(key);

//         if(currentCount == 1){
//             jedis.expire(key, WINDOW_SIZE);
//         }

//         return currentCount <= MAX_REQUEST;
//     }

// }