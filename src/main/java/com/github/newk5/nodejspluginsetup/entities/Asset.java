
package com.github.newk5.nodejspluginsetup.entities;


public class Asset {

    private String name;
    private String browser_download_url;

    public Asset() {
    }

    public Asset(String name, String browser_download_url) {
        this.name = name;
        this.browser_download_url = browser_download_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrowser_download_url() {
        return browser_download_url;
    }

    public void setBrowser_download_url(String browser_download_url) {
        this.browser_download_url = browser_download_url;
    }

    @Override
    public String toString() {
        return "Asset{" + "name=" + name + ", browser_download_url=" + browser_download_url + '}';
    }
    
    

}
