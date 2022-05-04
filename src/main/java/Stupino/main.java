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

        FileWriter mainPageFile = new FileWriter("main-site.html");
        mainPageFile.write(mainPage.body());
        mainPageFile.close();
        System.out.println("Успешное сохранение в файл главной страницы." + lineBreak);

        System.out.println("Чтение файла главной страницы." + lineBreak);
        File mainPageOpen = new File("main-site.html");
        System.out.println("Парс новостей с файла главной страницы." + lineBreak);
        Document mainPageOpenParce = Jsoup.parse(mainPageOpen, "UTF-8");
        Elements newsPostElements = mainPageOpenParce.getElementsByClass("teaser-tile");
        final int[] foreachkey = {1};
        newsPostElements.forEach(newsPostElement -> {
            Elements newsURL = newsPostElement.getElementsByTag("a");
            String newsLink = "https://stupinoadm.ru" + newsURL.attr("href");
            try {
                System.out.println("Подключение к странице новости " + foreachkey[0] + ", ее парс в файл");
                var pageNews = Jsoup.connect(newsLink).execute();

                FileWriter pageNewsFile = new FileWriter("news-site-" + foreachkey[0] + ".html");
                pageNewsFile.write(pageNews.body());
                pageNewsFile.close();
                System.out.println("Успешное сохранение в файл страницы новости " + foreachkey[0] + ".");

                System.out.println("Чтение файла новости "+ foreachkey[0] + ".");
                File pageNewsFileOpen = new File("news-site-" + foreachkey[0] + ".html");
                System.out.println("Парс файла новости "+ foreachkey[0] + ".");
                foreachkey[0] = foreachkey[0] +1;
                Document newsPage = Jsoup.parse(pageNewsFileOpen, "UTF-8");

                Elements titleNewsPage = newsPage.getElementsByClass("headline");
                String titleNews = "Заголовок: " + titleNewsPage.text();

                Elements textNewsPage = newsPage.getElementsByClass("text");
                String textNews = "Текст: " + textNewsPage.text();

                Elements dateNewsPage = newsPage.getElementsByClass("date");
                String dateNews = "Дата: " + dateNewsPage.text().split(" ")[0];

                String urlNews = "Ссылка: " + newsLink;

                System.out.println("Полученная информация из файла новости:");
                System.out.println(titleNews); // Заголовок
                System.out.println(textNews); // Текст
                System.out.println(dateNews); // Дата
                System.out.println(urlNews); // Ссылка
                System.out.println(lineBreak); // Разделение

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}