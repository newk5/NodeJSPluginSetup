package com.github.newk5.nodejspluginsetup.entities;

import java.util.ArrayList;
import java.util.List;

public class Response {

    private List<Asset> assets = new ArrayList<>();

    public Response() {
    }

    public List<Asset> getAssets() {
        return assets;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }

    @Override
    public String toString() {
        return "Response{" + "assets=" + assets + '}';
    }

}
