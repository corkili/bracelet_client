package org.client.bracelet.entity;

import com.beardedhen.androidbootstrap.BootstrapButton;

/**
 * Created by 李浩然 on 2017/11/8.
 */

public class ApplicationManager {
    private static ApplicationManager applicationManager;

    private User user;
    private Recipe recipe;
    private boolean hasBandBracelet;
    private int cacheSteps;
    private double cacheKilometre;
    private double cacheSleep;

    private ApplicationManager() {
        user = null;
        recipe = null;
        hasBandBracelet = false;
        cacheSteps = -1;
        cacheKilometre = -1;
        cacheSleep = -1;
    }

    public static ApplicationManager getInstance() {
        if (applicationManager == null) {
            applicationManager = new ApplicationManager();
        }
        return applicationManager;
    }

    public boolean isLogin() {
        return user != null;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean hasCacheRecipe() {
        return recipe != null;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public int getCacheSteps() {
        return cacheSteps;
    }

    public void setCacheSteps(int cacheSteps) {
        this.cacheSteps = cacheSteps;
    }

    public double getCacheKilometre() {
        return cacheKilometre;
    }

    public void setCacheKilometre(double cacheKilometre) {
        this.cacheKilometre = cacheKilometre;
    }

    public double getCacheSleep() {
        return cacheSleep;
    }

    public void setCacheSleep(double cacheSleep) {
        this.cacheSleep = cacheSleep;
    }

    public boolean hasBandBracelet() {
        return hasBandBracelet;
    }

    public void hasBandBracelet(boolean hasBandBracelet) {
        this.hasBandBracelet = hasBandBracelet;
    }

    public boolean hasCacheSteps() {
        return cacheSteps >= 0;
    }

    public boolean hasCacheKilometre() {
        return cacheKilometre >= 0;
    }

    public boolean hasCacheSleep() {
        return cacheSleep > 0;
    }
}
