package tk.nekotech.dev.soaringcats;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Factoid {
    private SoaringCats sc;
    private String channel = "#SoaringCats";

    public Factoid(SoaringCats sc) {
        this.sc = sc;
        File file = new File("factoids");
        if (file.isFile()) {
            file.delete();
        } else if (!file.exists()) {
            file.mkdir();
        }
    }

    public void handle(String username, String message) {
        String[] args = message.split(" ");
        if (this.sc.isOp(username, channel)) {
            if (args[1].equals("add")) {
                if (args.length < 4) {
                    this.sc.sendNotice("@" + channel, username + ": failed to add as the message and/or factoid wasn't specified.");
                } else {
                    File file = new File("factoids/" + args[2].replace('/', '~') + ".ftd");
                    if (file.exists()) {
                        this.sc.sendNotice("@" + channel, username + ": failed to add as the factoid already exists. Try '?? remove " + message + "'");
                    } else {
                        try {
                            BufferedWriter out = new BufferedWriter(new FileWriter(file));
                            out.write(combineSplit(3, args, " "));
                            out.flush();
                            out.close();
                            this.sc.sendNotice("@" + channel, "Factoid added!");
                        } catch (IOException e) {
                            e.printStackTrace();
                            this.sc.sendNotice("@" + channel, "Failed to add factoid: " + e.toString());
                        }
                    }
                }
                return;
            } else if (args[1].equals("remove")) {
                File file = new File("factoids/" + args[2].replace('/', '~') + ".ftd");
                if (file.exists()) {
                    file.delete();
                    this.sc.sendNotice("@" + channel, "Factoid removed!");
                } else {
                    this.sc.sendNotice("@" + channel, username + ": that factoid doesn't exist.");
                }
                return;
            } else if (args[1].equals("update")) {
                File file = new File("factoids/" + args[2].replace('/', '~') + ".ftd");
                if (file.exists()) {
                    try {
                        BufferedWriter out = new BufferedWriter(new FileWriter(file));
                        out.write(combineSplit(3, args, " "));
                        out.flush();
                        out.close();
                        this.sc.sendNotice("@" + channel, "Factoid added!");
                    } catch (IOException e) {
                        e.printStackTrace();
                        this.sc.sendNotice("@" + channel, "Failed to add factoid: " + e.toString());
                    }
                } else {
                    this.sc.sendNotice("@" + channel, username + ": that factoid doesn't exist.");
                }
            }
        }
        if (args.length != 2) {
            return;
        }
        File fact = new File("factoids/" + args[1].replace('/', '~') + ".ftd");
        if (fact.exists()) {
            try {
                BufferedReader in = new BufferedReader(new FileReader(fact));
                this.sc.sendMessage("#SoaringCats", username + ": ?? " + args[1] + ": " + in.readLine());
                in.close();
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
                e.printStackTrace();
                this.sc.sendMessage("#SoaringCats", username + ": Failed to read factoid: " + e.toString() + "; full error in console.");
            }
        }
    }

    public String combineSplit(int startIndex, String[] string, String seperator) {
        final StringBuilder builder = new StringBuilder();
        for (int i = startIndex; i < string.length; i++) {
            builder.append(string[i]);
            builder.append(seperator);
        }
        builder.deleteCharAt(builder.length() - seperator.length());
        return builder.toString();
    }
}
