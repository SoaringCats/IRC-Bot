package tk.nekotech.dev.soaringcats;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.jibble.pircbot.Colors;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

public class SoaringCats extends PircBot {
    private String version = "0.1";
    private String ident = "default:default";
    private ArrayList<String> prefixes;
    private SimpleDateFormat sdf;

    public static void main(String[] args) {
        new SoaringCats();
    }

    public SoaringCats() {
        System.out.println("Starting...");
        sdf = new SimpleDateFormat("E, dd yyyy; kk:mm");
        File file = new File("notes");
        if (!file.exists()) {
            file.mkdir();
        } else if (file.exists() && !file.isDirectory()) {
            file.delete();
            file.mkdir();
        }
        file = new File("prefixes.cfg");
        if (!file.exists()) {
            try {
                file.createNewFile();
                BufferedWriter out = new BufferedWriter(new FileWriter(file));
                out.write("# Enter prefixes seperated by new lines to be welcomed by the bot");
                out.flush();
                out.close();
            } catch (IOException exception) {
                System.err.println("Failed first write of config!");
                exception.printStackTrace(System.err);
            }
        } else {
            try {
                BufferedReader in = new BufferedReader(new FileReader(file));
                prefixes = new ArrayList<String>();
                String nextLine;
                while ((nextLine = in.readLine()) != null) {
                    if (!nextLine.startsWith("#"))
                        prefixes.add(nextLine);
                }
                in.close();
            } catch (IOException exception) {
                System.err.println("Failed read of config!");
                exception.printStackTrace(System.err);
            }
        }
        file = new File("config.cfg");
        if (!file.exists()) {
            try {
                file.createNewFile();
                BufferedWriter out = new BufferedWriter(new FileWriter(file));
                out.write("# Enter nickserv ident in format account:pass");
                out.flush();
                out.close();
            } catch (IOException exception) {
                System.err.println("Failed first write of config!");
                exception.printStackTrace(System.err);
            }
        } else {
            try {
                BufferedReader in = new BufferedReader(new FileReader(file));
                String nextLine;
                while ((nextLine = in.readLine()) != null) {
                    if (!nextLine.startsWith("#"))
                        ident = nextLine;
                }
                in.close();
            } catch (IOException exception) {
                System.err.println("Failed read of config!");
                exception.printStackTrace(System.err);
            }
        }
        file = null;

        this.setAutoNickChange(true);
        this.setFinger("SoaringCats bot v" + version + " | Report issues in #SoaringCats");
        this.setLogin("meow");
        this.setName("SoaringCats");
        this.setVerbose(true);
        this.setVersion(Colors.BOLD + Colors.RED + "Report issues in #SoaringCats");
        System.out.println("Started! Connecting...");
        try {
            this.connect("irc.esper.net", 5555, ident);
        } catch (Exception exception) {
            System.err.println("Failed to connect!");
            exception.printStackTrace(System.err);
            System.exit(1);
        }
    }

    @Override
    public void onConnect() {
        System.out.println("Connected!");
        this.setVersion("SoaringCats bot v" + version + " | Report issues in #SoaringCats");
        if (!this.getNick().equals("SoaringCats")) {
            this.sendMessage("NickServ", "ghost SoaringCats");
            this.sendMessage("NickServ", "release");
            this.changeNick("SoaringCats");
            System.out.println("Attempted nickname regain.");
        }
        this.joinChannel("#SoaringCats");
        this.sendMessage("#SoaringCats", "Meow!");
    }

    @Override
    public void onDisconnect() {
        try {
            this.reconnect();
        } catch (Exception exception) {
            System.err.println("Failed to reconnect!");
            exception.printStackTrace(System.err);
            System.exit(1);
        }
    }
    
    @Override
    public void onJoin(String channel, String sender, String login, String hostname) {
        if (!channel.equalsIgnoreCase("#SoaringCats"))
            return;
        for (String prefix : prefixes) {
            if (sender.startsWith(prefix)) {
                this.sendMessage(channel, "Welcome to " + channel + ", " + sender + "! "
                        + "If you are here to request help, please state your question and "
                        + Colors.BOLD + "do not ask to ask" + Colors.NORMAL + ". After "
                        + "asking your question, please wait patiently for a reply. Not all "
                        + "users are currently active. You're also able to change your name "
                        + "with " + Colors.BOLD + "/nick <new name>" + Colors.NORMAL);
            }
        }
    }

    @Override
    public void onMessage(String channel, String sender, String login, String hostname, String message) {
        File file = new File("notes/" + sender);
        if (file.exists()) {
            try {
                BufferedReader in = new BufferedReader(new FileReader(file));
                String nextLine;
                StringBuilder sb = new StringBuilder();
                while ((nextLine = in.readLine()) != null) {
                    sb.append(nextLine + " ");
                }
                in.close();
                sb.setLength(sb.length() - 1);
                file.delete();
                String mess = sender + ", " + Colors.BOLD + "you have notes!" + sb.toString();
                String send = "";
                int max = 512 - ("PRIVMSG " + channel + " :").length();
                for (char c : mess.toCharArray()) {
                    send += c;
                    if (send.length() == max) {
                        this.sendMessage(channel, send);
                        send = "";
                    }
                }
                this.sendMessage(channel, send);
            } catch (FileNotFoundException exception) {
                exception.printStackTrace();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        if (message.startsWith("!note ")) {
            String[] args = message.split(" ");
            if (args.length == 2) {
                this.sendMessage(channel, "Cowardly discarding empty note.");
            } else if (args.length < 3) {
                this.sendMessage(channel, "Sepcify user and note.");
            } else {
                if (args[1].length() > 30) {
                    this.sendMessage(channel, "Cowardly discarding bad note.");
                } else {
                    try {
                        file = new File("notes/" + args[1]);
                        BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
                        if (file.exists()) {
                            out.newLine();
                        }
                        out.write(Colors.NORMAL + "[" + sdf.format(new Date(System.currentTimeMillis())) + "] <" + Colors.UNDERLINE + sender + Colors.NORMAL + "> " + message.replace("!note " + args[1] + " ", ""));
                        out.flush();
                        out.close();
                        this.sendMessage(channel, "done!");
                    } catch (IOException exception) {
                        exception.printStackTrace();
                        this.sendMessage(channel, "Failed to save note.");
                    }
                }
            }
        }
        if (message.startsWith("!prefix")) {
            for (User user : this.getUsers(channel)) {
                if (user.getNick().equals(sender)) {
                    if (!user.isOp()) {
                        return;
                    }
                }
            }
            String[] args = message.split(" ");
            String reply = "!prefix <list/add/rem>";
            if (args.length == 1) {
                this.sendMessage(channel, reply);
            } else if (args.length == 2) {
                if (args[1].equals("list")) {
                    reply = Colors.BOLD + "Prefixes: " + Colors.NORMAL;
                    for (String prefix : prefixes) {
                        reply += prefix;
                    }
                    String mess = "";
                    int max = 512 - ("PRIVMSG " + channel + " :").length();
                    for (char c : reply.toCharArray()) {
                        mess += c;
                        if (mess.length() == max) {
                            this.sendMessage(channel, mess);
                            mess = "";
                        }
                    }
                    this.sendMessage(channel, mess);
                } else {
                    this.sendMessage(channel, reply);
                }
            } else if (args.length == 3) {
                if (args[1].equals("add")) {
                    this.sendMessage(channel, this.prefixes.add(args[2]) ? "Added " + args[2] : "Couldn't add " + args[2]);
                    try {
                        BufferedWriter out = new BufferedWriter(new FileWriter(new File("prefixes.cfg"), true));
                        out.newLine();
                        out.write(args[2]);
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                } else if (args[1].equals("rem")) {
                    this.sendMessage(channel, this.prefixes.remove(args[2]) ? "Removed " + args[2] : "Couldn't remove " + args[2]);
                    try {
                        BufferedWriter out = new BufferedWriter(new FileWriter(new File("prefixes.cfg"), true));
                        for (String prefix : prefixes) {
                            out.newLine();
                            out.write(prefix);
                        }
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                } else {
                    this.sendMessage(channel, reply);
                }
            } else {
                this.sendMessage(channel, reply);
            }
        }
        if (message.startsWith("!clear")) {
            for (User user : this.getUsers(channel)) {
                if (user.getNick().equals(sender)) {
                    if (!user.isOp()) {
                        return;
                    }
                }
            }
            String[] args = message.split(" ");
            String reply = "!clear <user>";
            if (args.length != 2) {
                this.sendMessage(channel, reply);
            } else {
                file = new File("notes/" + args[1]);
                if (file.exists()) {
                    this.sendMessage(channel, file.delete() ? "Cleared notes for " + args[1] : "Failed to clear notes for " + args[1]);
                } else {
                    this.sendMessage(channel, "There are no recorded notes for " + args[1]);
                }
            }
        }
    }

}