package Stupino;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class main {

    public static Document get_URL(String url) throws IOException {
        var mainPage = Jsoup.connect(url).execute();
        String main_site = mainPage.body();
        Document Page_local = Jsoup.parse(main_site, "UTF-8");
        return Page_local;
    }

    public static void parsing(String URL) throws IOException {
        String lineBreak = "\n";
        System.out.println("Подключение к главной страницы, ее парс" + lineBreak);

        Elements newsPostElements = get_URL(URL).getElementsByClass("teaser-tile");

        newsPostElements.forEach(newsPostElement -> {
            Elements newsURL = newsPostElement.getElementsByTag("a");
            String newsLink = URL + newsURL.attr("href");

            try {
                System.out.println("Подключение к странице новости, ее парс");
                Document newsPage = get_URL(newsLink);

                // Заголовок новости
                Elements titleNewsPage = newsPage.getElementsByClass("headline");
                String titleNews = titleNewsPage.text();

                // Текст
                Elements textNewsPage = newsPage.getElementsByClass("text");
                String textNews = textNewsPage.text();

                // Дата
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

    public static void main(String[] args) throws IOException {
        parsing("https://stupinoadm.ru");
    }
}