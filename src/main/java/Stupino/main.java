package Stupino;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class main {

    public static String Queue1 = "parser_link";
    static ConnectionFactory factory;
    public static Document download_html(String url) throws IOException {
        var mainPage = Jsoup.connect(url).execute();
        String main_site = mainPage.body();
        Document Page_local = Jsoup.parse(main_site, "UTF-8");
        return Page_local;
    }

    public static void create_URL_news(String URL) throws IOException, TimeoutException {
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        Elements newsPostElements = download_html(URL).getElementsByClass("teaser-tile");

        newsPostElements.forEach(newsPostElement -> {
            Elements newsURL = newsPostElement.getElementsByTag("a");
            String newsLink = URL + newsURL.attr("href");


            //newsLink помещяем Рэбит
            try {
                channel.basicPublish("", Queue1, null, newsLink.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void parsing() throws IOException, TimeoutException, InterruptedException {
        String lineBreak = "\n";

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        while (true) {
//            synchronized (this) {
                try {
                    if (channel.messageCount(Queue1) == 0) continue;
                    String url = new String(channel.basicGet(Queue1, true).getBody(), StandardCharsets.UTF_8);
                    if (url!=null){
                        try {

                            System.out.println("Подключение к странице новости, ее парс");
                            Document newsPage = download_html(url);

                            // Заголовок новости
                            Elements titleNewsPage = newsPage.getElementsByClass("headline");
                            String titleNews = titleNewsPage.text();

                            // Текст
                            Elements textNewsPage = newsPage.getElementsByClass("text");
                            String textNews = textNewsPage.text();

                            // Дата
                            Elements dateNewsPage = newsPage.getElementsByClass("date");
                            String dateNews = dateNewsPage.text().split(" ")[0];

                            System.out.println("Полученная информация из файла новости:");
                            System.out.println("Заголовок: " + titleNews); // Заголовок
                            System.out.println("Текст: " + textNews); // Текст
                            System.out.println("Дата: " + dateNews); // Дата
                            System.out.println("Ссылка: " + url); // Ссылка
                            System.out.println(lineBreak); // Разделение

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    //notify();
                }
                catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                    //wait();
                }
            //}
        }



    }

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.99.100");
        factory.setPort(5672);
        factory.setVirtualHost("/");
        factory.setUsername("rabbitmq");
        factory.setPassword("rabbitmq");

        Connection connection = factory.newConnection();

        Channel channel = connection.createChannel();
        channel.queueDeclare(Queue1, false, false, false, null);
        channel.close();
        connection.close();

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run()
            {
                try {
                    create_URL_news("https://stupinoadm.ru");
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
                    parsing();
                }
                catch (InterruptedException | IOException | TimeoutException e) {
                    e.printStackTrace();
                }
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

    }
}