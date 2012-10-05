package tk.nekotech.dev.soaringcats.web;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import org.jibble.pircbot.Colors;
import tk.nekotech.dev.soaringcats.WebListener;
import tk.nekotech.dev.soaringcats.web.GH_Payload.Commit;
import com.google.gson.Gson;

public class GitHubRunner extends Thread {
    private String json;
    private WebListener wl;

    public GitHubRunner(String json, WebListener wl) {
        this.json = json;
        this.wl = wl;
    }

    public void run() {
        GH_Payload ret = (new Gson()).fromJson(json, GH_Payload.class);
        try {
            for (Commit commit : ret.commits) {
                StringBuilder sb = new StringBuilder();
                String user = ret.repository.url.replace("https://github.com/", "").replace("/" + ret.repository.name, "");
                final URL url = new URL("https://api.github.com/repos/" + user  + "/" + ret.repository.name + "/commits/" + commit.id);
                final BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    sb.append(inputLine);
                }
                in.close();
                final GH_Commit sc = new Gson().fromJson(sb.toString(), GH_Commit.class);
                String msg = "";
                msg += Colors.DARK_GREEN + commit.author.name;
                msg += Colors.BROWN + " " + ret.ref.replace("refs/heads/", "");
                msg += Colors.NORMAL + Colors.BOLD + " * " + Colors.NORMAL;
                msg += "r" + Colors.BOLD + commit.id.substring(5, commit.id.length()) + Colors.NORMAL;
                msg += " / ";
                if (sc.files.length == 1) {
                    msg += sc.files[0].filename;
                } else {
                    msg += sc.files[0].filename + "(+" + sc.files.length + " more)";
                }
                msg += " : " + commit.message;
                msg += " - " + commit.url;
                this.wl.send(msg);
            }
        } catch (final Exception e) {
            this.wl.send("Exception in commit read: " + e.toString() + "; full print in console.");
            e.printStackTrace();
        }
    }

}
