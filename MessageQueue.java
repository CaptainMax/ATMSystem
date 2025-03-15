import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;


public class MessageQueue {
    public static void main(String[] args) {
        SimpleMessageQueue mq = new SimpleMessageQueue();
        new Thread(() -> {
            for(int i = 1; i <= 5; i++){
                mq.send("message " + i);
                try {
                    Thread.sleep(500);
                } catch(InterruptedException igException){}
            }
        }).start();

        for(int i = 1; i <= 3; i++){
            int consumerId = i;
            new Thread(() -> {
                while(true){
                    String message = mq.receive();
                    System.out.println("consumer: " + consumerId + " processing: " + message);
                }
            }).start();
        }
        
    }
}

class SimpleMessageQueue {
    private final BlockingQueue<String> queue = new LinkedBlockingDeque<>();

    //puducer
    public void send(String message){
        try{
            queue.put(message);
            System.out.println("Send the message " + message);
        } catch(InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }

    //receiver
    public String receive(){
        try{
            return queue.take();
        } catch (InterruptedException e){
            Thread.currentThread().interrupt();
            return null;
        }
    }

}
