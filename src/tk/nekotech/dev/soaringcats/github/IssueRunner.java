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
    private SoaringCats sc;
    private String repository;
    private String issue;

    public IssueRunner(SoaringCats sc, String repository, String issue) {
        this.sc = sc;
        this.repository = repository;
        this.issue = issue;
    }

    public void run() {
        HttpURLConnection con = null;
        BufferedReader in = null;
        try {
            con = (HttpURLConnection) new URL("https://api.github.com/repos/SoaringCats/" + repository + "/issues/" + issue + "?access_token=" + sc.oauth).openConnection();
            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            //in = new BufferedReader(new InputStreamReader(new URL("https://api.github.com/repos/SoaringCats/" + repository + "/issues/" + issue + "?access_token=" + sc.oauth).openStream()));
        } catch (Exception e) {
            if (!(e instanceof FileNotFoundException)) {
                sc.sendMessage("#soaringcats", "Failed issue check for " + this.getMessage() + e.toString().split("\\?access_token=")[0] + "; full error in console.");
                e.printStackTrace();
                return;
            } else {
                in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
        }
        StringBuilder sb = new StringBuilder();
        String nextLine;
        try {
            while ((nextLine = in.readLine()) != null) {
                sb.append(nextLine);
            }
        } catch (IOException e) {
            sc.sendMessage("#soaringcats", "Failed issue check for " + this.getMessage() + e.toString().split("\\?access_token=")[0] + "; full error in console.");
            e.printStackTrace();
        }
        GH_Issue iss = (new Gson()).fromJson(sb.toString(), GH_Issue.class);
        if (iss.message == null) {
            if (iss.title.length() > 20) {
                iss.title = iss.title.substring(0, 20);
                iss.title += " [...]";
            }
            sc.sendMessage("#soaringcats", this.getMessage() + "\"" + iss.title + "\" is currently " + iss.state.toUpperCase() + " with " + iss.comments + " comments and was opened by " + iss.user.login + ". View at " + iss.html_url);
        } else {
            sc.sendMessage("#soaringcats", this.getMessage() + iss.message);
        }
    }

    private String getMessage() {
        return "I/" + this.repository + "/" + this.issue + ": ";
    }
}
