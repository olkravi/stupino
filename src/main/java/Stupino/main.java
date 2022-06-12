package Stupino;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class main {



    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {

        func func_crawler = new func();

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        factory.setVirtualHost("/");
        factory.setUsername("rabbitmq");
        factory.setPassword("rabbitmq");

        Connection connection = factory.newConnection();

        Channel channel = connection.createChannel();
        channel.queueDeclare(func_crawler.Queue1, false, false, false, null);
        channel.close();
        connection.close();

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run()
            {
                try {
                    func_crawler.create_URL_news("https://stupinoadm.ru");
                }
                catch (IOException | TimeoutException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run()
            {
                try {
                    func_crawler.parsing();
                }
                catch (InterruptedException | IOException | TimeoutException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread t3 = new Thread(new Runnable() {
            @Override
            public void run()
            {
                try {
                    func_crawler.agrigation();
                }
                catch (UnknownHostException | ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

    }
}