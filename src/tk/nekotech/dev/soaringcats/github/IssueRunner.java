package tk.nekotech.dev.soaringcats.github;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import tk.nekotech.dev.soaringcats.SoaringCats;
import com.google.gson.Gson;

public class IssueRunner extends Thread {
    private final SoaringCats sc;
    private final String repository;
    private final String issue;

    public IssueRunner(final SoaringCats sc, final String repository, final String issue) {
        this.sc = sc;
        this.repository = repository;
        this.issue = issue;
    }

    private String getMessage() {
        return "I/" + this.repository + "/" + this.issue + ": ";
    }

    @Override
    public void run() {
        HttpURLConnection con = null;
        BufferedReader in = null;
        try {
            con = (HttpURLConnection) new URL("https://api.github.com/repos/SoaringCats/" + this.repository + "/issues/" + this.issue + "?access_token=" + this.sc.oauth).openConnection();
            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            //in = new BufferedReader(new InputStreamReader(new URL("https://api.github.com/repos/SoaringCats/" + repository + "/issues/" + issue + "?access_token=" + sc.oauth).openStream()));
        } catch (final Exception e) {
            if (!(e instanceof FileNotFoundException)) {
                this.sc.sendMessage("#soaringcats", "Failed issue check for " + this.getMessage() + e.toString().split("\\?access_token=")[0] + "; full error in console.");
                e.printStackTrace();
                return;
            } else {
                in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
        }
        final StringBuilder sb = new StringBuilder();
        String nextLine;
        try {
            while ((nextLine = in.readLine()) != null) {
                sb.append(nextLine);
            }
        } catch (final IOException e) {
            this.sc.sendMessage("#soaringcats", "Failed issue check for " + this.getMessage() + e.toString().split("\\?access_token=")[0] + "; full error in console.");
            e.printStackTrace();
        }
        final GH_Issue iss = new Gson().fromJson(sb.toString(), GH_Issue.class);
        if (iss.message == null) {
            if (iss.title.length() > 20) {
                iss.title = iss.title.substring(0, 20);
                iss.title += " [...]";
            }
            this.sc.sendMessage("#soaringcats", this.getMessage() + "\"" + iss.title + "\" is currently " + iss.state.toUpperCase() + " with " + iss.comments + " comments and was opened by " + iss.user.login + ". View at " + iss.html_url);
        } else {
            this.sc.sendMessage("#soaringcats", this.getMessage() + iss.message);
        }
    }
}
