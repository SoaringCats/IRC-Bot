package tk.nekotech.dev.soaringcats.github;

public class GH_Issue {
    public String message; // AKA error
    public String url;
    public String state;
    public String closed_at;
    public Label[] labels;
    public String closed_by;
    public String html_url;
    public String created_at;
    public int comments;
    public String body;
    public User user;
    public String title;
    public String assignee;
    public int number;
    public int id;
    public PR pull_request;
    public String updated_at;
    public String milestone;

    public class Label {
        public String url;
        public String color;
        public String name;
    }

    public class User {
        public String url;
        public String login;
        public String avatar_url;
        public String gravatar_id;
        public int id;
    }

    public class PR {
        public String html_url;
        public String patch_url;
        public String diff_url;
    }
}
