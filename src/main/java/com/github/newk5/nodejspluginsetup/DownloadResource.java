package com.github.newk5.nodejspluginsetup;

import com.github.newk5.nodejspluginsetup.entities.Asset;
import com.github.newk5.nodejspluginsetup.entities.Response;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import org.kamranzafar.jddl.DirectDownloader;
import org.kamranzafar.jddl.DownloadTask;

public class DownloadResource {

    private String filename;
    private String url;
    private Gson gson;
    private Runnable onComplete;
    private String saveDir;

    public DownloadResource(Gson gson) {
        this.gson = gson;
    }

    public String getFilename() {
        return filename;
    }

    public DownloadResource setFilename(String filename) {
        this.filename = filename;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public Runnable getOnComplete() {
        return onComplete;
    }

    public DownloadResource onComplete(Runnable onComplete) {
        this.onComplete = onComplete;
        return this;
    }

    public DownloadResource setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getSaveDir() {
        return saveDir;
    }

    public DownloadResource setSaveDir(String saveDir) {
        this.saveDir = saveDir;
        return this;
    }

    public void start() {
        try {
            System.out.println("Downloading: " + filename);
            String response = HttpRequest.get(url).body();
            Response obj = gson.fromJson(response, Response.class);

            Asset asset = obj.getAssets().stream().filter(a -> a.getName().equals(filename)).findFirst().orElse(null);
            if (saveDir != null) {
                new File(saveDir).mkdirs();
            }
            String dir = saveDir == null ? filename : saveDir + File.separator + filename;
            DirectDownloader dd = new DirectDownloader();

            dd.download(new DownloadTask(
                    new URL(asset.getBrowser_download_url()),
                    new FileOutputStream(dir),
                    new EventListener(onComplete)));
            dd.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
