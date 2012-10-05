package tk.nekotech.dev.soaringcats.web;

public class GH_Payload {
    
    public class Author {
        public String email;
        public String name;
    }

    public class Commit {
        public String id;
        public String url;
        public Author author;
        public String message;
        public String timestamp;
        public String[] added;
    }

    public class Repository {
        public String url;
        public String name;
        public String description;
        public int watchers;
        public int forks;
    }

    public String before;
    public Repository repository;
    public Commit[] commits;
    public String after;
    public String ref;
    
}
