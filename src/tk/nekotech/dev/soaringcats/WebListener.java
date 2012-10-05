package tk.nekotech.dev.soaringcats;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import tk.nekotech.dev.soaringcats.web.GitHubRunner;
import fi.iki.elonen.NanoHTTPD;

public class WebListener extends NanoHTTPD {
    private SoaringCats sc;

    public WebListener(SoaringCats sc) throws IOException {
        super(8080, new File("."));
        this.sc = sc;
    }
    
    private NanoHTTPD.Response getResponse() {
        return new NanoHTTPD.Response(NanoHTTPD.HTTP_FORBIDDEN, NanoHTTPD.MIME_HTML, "<html><body><h1>Forbidden.</h1><p>Access forbidden.</p></html>");
    }
    
    @Override
    public Response serve(final String uri, final String method, final Properties header, final Properties parms, final Properties files) {
        if (!uri.equals("/github")) {
            return this.getResponse();
        }
        if (parms.getProperty("payload") == null) {
            return this.getResponse();
        }
        final String payload = parms.getProperty("payload");
        (new GitHubRunner(payload, this)).start();
        return new NanoHTTPD.Response(NanoHTTPD.HTTP_OK, NanoHTTPD.MIME_HTML, "");
    }
    
    public void send(String message) {
        this.sc.sendMessage("#SoaringCats", message);
    }
}
