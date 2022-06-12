package Stupino;

public class Json {
    public Json()
    {
        super();
    }

    public Json(String header, String text, String url, String date) {
        this.HEADER=header;
        this.TEXT=text;
        this.URL=url;
        this.DATE=date;
    }

    public String HEADER;
    public String TEXT;
    public String URL;
    public String DATE;
}
