package com.github.newk5.nodejspluginsetup;

import org.kamranzafar.jddl.DownloadListener;

public class EventListener implements DownloadListener {

    String fname;
    int size;

    private Runnable onComplete;

    public EventListener(Runnable onComplete) {
        this.onComplete = onComplete;
    }

    @Override
    public void onUpdate(int bytes, int totalDownloaded) {
        updateProgress((double) totalDownloaded / size);
    }

    @Override
    public void onStart(String fname, int size) {
        this.fname = fname;
        this.size = size;
        System.out.print("[ ");
    }

    @Override
    public void onComplete() {
        System.out.print(" ]");
        onComplete.run();
       // System.out.println("");
    }

    @Override
    public void onCancel() {
        System.out.println(fname + " cancelled");
    }
    Integer lastPercent = 0;

    void updateProgress(double progressPercentage) {
        Double v = (progressPercentage * 100);
        Integer iv = v.intValue();
        if (iv % 5 == 0 && !iv.equals(lastPercent)) {
            System.out.print(iv + "% " + (iv == 100 ? "" : ","));
        }
        lastPercent = iv;

    }

}
