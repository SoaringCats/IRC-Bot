package tk.nekotech.dev.soaringcats.web;

public class GH_Commit {
    public class GHFile {
        public String filename;
        public int additions;
        public int deletions;
        public int changes;
        public String status;
    }

    public GHFile[] files;
}
