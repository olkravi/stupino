package Stupino;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class main {
    public static void main(String[] args) throws IOException {
        String lineBreak = "\n";
        System.out.println("Подключение к главной страницы, ее парс" + lineBreak);
        var mainPage = Jsoup.connect("https://stupinoadm.ru").execute();



        String main_site = mainPage.body();

        System.out.println("Успешное сохранение главной страницы." + lineBreak);

        System.out.println("Парс новостей главной страницы." + lineBreak);
        Document mainPageOpenParce = Jsoup.parse(main_site, "UTF-8");

        Elements newsPostElements = mainPageOpenParce.getElementsByClass("teaser-tile");


        newsPostElements.forEach(newsPostElement -> {
            Elements newsURL = newsPostElement.getElementsByTag("a");
            String newsLink = "https://stupinoadm.ru" + newsURL.attr("href");
            try {
                System.out.println("Подключение к странице новости, ее парс");
                var pageNews = Jsoup.connect(newsLink).execute();

                String news_site = pageNews.body();


                Document newsPage = Jsoup.parse(news_site, "UTF-8");

                Elements titleNewsPage = newsPage.getElementsByClass("headline");
                String titleNews = titleNewsPage.text();

                Elements textNewsPage = newsPage.getElementsByClass("text");
                String textNews = textNewsPage.text();

                Elements dateNewsPage = newsPage.getElementsByClass("date");
                String dateNews = dateNewsPage.text().split(" ")[0];

                String urlNews = newsLink;

                System.out.println("Полученная информация из файла новости:");
                System.out.println("Заголовок: " + titleNews); // Заголовок
                System.out.println("Текст: " + textNews); // Текст
                System.out.println("Дата: " + dateNews); // Дата
                System.out.println("Ссылка: " + urlNews); // Ссылка
                System.out.println(lineBreak); // Разделение

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}