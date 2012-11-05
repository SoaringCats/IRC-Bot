package tk.nekotech.dev.soaringcats;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Factoid {
    private final SoaringCats sc;
    private final String channel = "#SoaringCats";

    public Factoid(final SoaringCats sc) {
        this.sc = sc;
        final File file = new File("factoids");
        if (file.isFile()) {
            file.delete();
        } else if (!file.exists()) {
            file.mkdir();
        }
    }

    public String combineSplit(final int startIndex, final String[] string, final String seperator) {
        final StringBuilder builder = new StringBuilder();
        for (int i = startIndex; i < string.length; i++) {
            builder.append(string[i]);
            builder.append(seperator);
        }
        builder.deleteCharAt(builder.length() - seperator.length());
        return builder.toString();
    }

    public void handle(final String username, final String message) {
        final String[] args = message.split(" ");
        if (this.sc.isOp(username, this.channel)) {
            if (args[1].equals("add")) {
                if (args.length < 4) {
                    this.sc.sendNotice("@" + this.channel, username + ": failed to add as the message and/or factoid wasn't specified.");
                } else {
                    final File file = new File("factoids/" + args[2].replace('/', '~') + ".ftd");
                    if (file.exists()) {
                        this.sc.sendNotice("@" + this.channel, username + ": failed to add as the factoid already exists. Try '?? remove " + message + "'");
                    } else {
                        try {
                            final BufferedWriter out = new BufferedWriter(new FileWriter(file));
                            out.write(this.combineSplit(3, args, " "));
                            out.flush();
                            out.close();
                            this.sc.sendNotice("@" + this.channel, "Factoid added!");
                        } catch (final IOException e) {
                            e.printStackTrace();
                            this.sc.sendNotice("@" + this.channel, "Failed to add factoid: " + e.toString());
                        }
                    }
                }
                return;
            } else if (args[1].equals("remove")) {
                final File file = new File("factoids/" + args[2].replace('/', '~') + ".ftd");
                if (file.exists()) {
                    file.delete();
                    this.sc.sendNotice("@" + this.channel, "Factoid removed!");
                } else {
                    this.sc.sendNotice("@" + this.channel, username + ": that factoid doesn't exist.");
                }
                return;
            } else if (args[1].equals("update")) {
                final File file = new File("factoids/" + args[2].replace('/', '~') + ".ftd");
                if (file.exists()) {
                    try {
                        final BufferedWriter out = new BufferedWriter(new FileWriter(file));
                        out.write(this.combineSplit(3, args, " "));
                        out.flush();
                        out.close();
                        this.sc.sendNotice("@" + this.channel, "Factoid added!");
                    } catch (final IOException e) {
                        e.printStackTrace();
                        this.sc.sendNotice("@" + this.channel, "Failed to add factoid: " + e.toString());
                    }
                } else {
                    this.sc.sendNotice("@" + this.channel, username + ": that factoid doesn't exist.");
                }
            }
        }
        if (args.length != 2) {
            return;
        }
        final File fact = new File("factoids/" + args[1].replace('/', '~') + ".ftd");
        if (fact.exists()) {
            try {
                final BufferedReader in = new BufferedReader(new FileReader(fact));
                this.sc.sendMessage("#SoaringCats", username + ": ?? " + args[1] + ": " + in.readLine());
                in.close();
            } catch (final FileNotFoundException e) {
            } catch (final IOException e) {
                e.printStackTrace();
                this.sc.sendMessage("#SoaringCats", username + ": Failed to read factoid: " + e.toString() + "; full error in console.");
            }
        }
    }
}
