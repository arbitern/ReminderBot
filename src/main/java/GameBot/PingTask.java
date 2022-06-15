package GameBot;

import java.net.HttpURLConnection;
import java.net.URL;

public class PingTask {

    private String url = "https://www.google.com";

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void pingMe() {
        try {
            URL url = new URL(getUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            connection.getResponseCode();
            connection.disconnect();
        } catch (Exception ignored) {}
    }
}
