package pl.bratosz.smartlockers.scraping;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OnlineConnection {
    final String startURL = "https://klsonline24.pl";
    final String loginURL = startURL + "/default.aspx";
    final String lockersViewURL = startURL + "/baza.aspx";
    final String rotationViewURL = startURL + "/rotacja.aspx";
    String referrer = "https://google.com/";
    final String userAgentChrome = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36";
    final String userAgentChrome2 = "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6";
    Map<String, String> cookies;
    Map<String, String> formParameters;
    Connection.Response actualResponse;
    Document actualPage;
    Elements elements;

    public OnlineConnection(String login, String password) {
        try {
            Connection.Response response = grabLoginPage(startURL, referrer);
            Document loginPage = response.parse();
            getFormParameters(loginPage);
            cookies = response.cookies();
            referrer = response.url().toString();
            actualResponse = logIn(login, password);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goToBoxesView() {
        try {
            actualPage = connectTo(lockersViewURL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goToClothesView() {
        try {
            actualPage = connectTo(rotationViewURL, 360);
            System.out.println("something");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Connection.Response grabLoginPage(String startURL, String referrer) {
        try {
            return Jsoup.connect(startURL)
                    .userAgent(userAgentChrome)
                    .referrer(referrer)
                    .timeout(10 * 1000)
                    .followRedirects(true)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    void getFormParameters(Document page) {
        formParameters = new HashMap<String, String>();
        Elements elements = page.select("input");
        for(Element e : elements) {
            String id = e.attr("name");
            String value = e.val();
            formParameters.put(id, value);
        }
        formParameters.remove("");
    }

    private Connection.Response logIn(String login, String password) {
        putLoginDataToForm(login, password);
        try {
            Connection.Response resp = Jsoup.connect(loginURL)
                    .userAgent(userAgentChrome)
                    .referrer(referrer)
                    .timeout(10 * 1000)
                    .cookies(cookies)
                    .data(formParameters)
                    .followRedirects(true)
                    .execute();
            cookies.putAll(resp.cookies());
            return resp;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Document connectTo(String url) throws IOException {
            actualResponse = Jsoup.connect(url)
                    .userAgent(userAgentChrome)
                    .referrer(referrer)
                    .timeout(10 * 1000)
                    .cookies(cookies)
                    .followRedirects(true)
                    .execute();
            return actualResponse.parse();
    }

    private Document connectTo(String url, int timeoutInSeconds) throws IOException {
        actualResponse = Jsoup.connect(url)
                .userAgent(userAgentChrome)
                .referrer(referrer)
                .timeout(timeoutInSeconds * 1000)
                .cookies(cookies)
                .followRedirects(true)
                .maxBodySize(0)
                .execute();
        return Jsoup.parse(actualResponse.body());
    }

    public Document checkConnection() throws IOException {
        actualPage = connectTo(lockersViewURL);
        String location = actualPage.location();
        if(location.equals(lockersViewURL)) {
            return actualPage;
        } else {
            throw new IOException();
        }
    }

    public void standardPost() {
        try {
        actualResponse = Jsoup.connect(lockersViewURL)
                .method(Connection.Method.POST)
                .userAgent(userAgentChrome)
                .referrer(referrer)
                .timeout(10 * 1000)
                .cookies(cookies)
                .data(formParameters)
                .followRedirects(true)
                .execute();
        actualPage = actualResponse.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void putLoginDataToForm(String login, String password) {
        formParameters.put("txtName", login);
        formParameters.put("txtPW", password);
    }


    public String getStartURL() {
        return startURL;
    }

    public String getLoginURL() {
        return loginURL;
    }

    public String getLockersViewURL() {
        return lockersViewURL;
    }

    public String getReferrer() {
        return referrer;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public String getUserAgentChrome() {
        return userAgentChrome;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public void setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public Map<String, String> getFormParameters() {
        return formParameters;
    }

    public void setFormParameters(Map<String, String> formParameters) {
        this.formParameters = formParameters;
    }

    public Connection.Response getActualResponse() {
        return actualResponse;
    }

    public void setActualResponse(Connection.Response actualResponse) {
        this.actualResponse = actualResponse;
    }

    public Document getActualPage() {
        return actualPage;
    }

    public void setActualPage(Document actualPage) {
        this.actualPage = actualPage;
    }


}



