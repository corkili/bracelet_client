package org.client.bracelet.entity;

import android.bluetooth.BluetoothDevice;
import android.util.Pair;

import com.beardedhen.androidbootstrap.BootstrapButton;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 李浩然 on 2017/11/8.
 */

public class ApplicationManager {
    private static ApplicationManager applicationManager;

    private boolean isLogin;
    private User user;
    private Recipe recipe;
    private boolean hasBandBracelet;
    private int cacheSteps;
    private double cacheKilometre;
    private double cacheSleep;
    private Date lastCacheRecipeTime;
    private boolean recipeReasonHasModified;
    private int steps;
    private double kilometres, sleepTime;
    private java.util.Date lastUpdateStateTime;
    private Pair<Long, Long> timePair;
    private long lastSleepTimes;

    private List<FoodType> cacheFoodTypes;

    private ApplicationManager() {
        isLogin = false;
        user = null;
        recipe = null;
        hasBandBracelet = false;
        cacheSteps = -1;
        cacheKilometre = -1;
        cacheSleep = -1;
        recipeReasonHasModified = false;
        steps = 0;
        kilometres = 0;
        sleepTime = 0;
        cacheFoodTypes = new ArrayList<>();
    }

    public static ApplicationManager getInstance() {
        if (applicationManager == null) {
            applicationManager = new ApplicationManager();
        }
        return applicationManager;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void isLogin(boolean login) {
        isLogin = login;
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

    public List<FoodType> getCacheFoodTypes() {
        return cacheFoodTypes;
    }

    public void setCacheFoodTypes(List<FoodType> cacheFoodTypes) {
        this.cacheFoodTypes = cacheFoodTypes;
    }

    public boolean hasCacheFoodTypes() {
        return !cacheFoodTypes.isEmpty();
    }

    public void updateLastCacheRecipeTime() {
        this.lastCacheRecipeTime = new Date(System.currentTimeMillis());
    }

    public boolean needRefreshRecipe() {
        Date now = new Date(System.currentTimeMillis());
        return lastCacheRecipeTime.before(now) || recipeReasonHasModified;
    }

    public Date getLastCacheRecipeTime() {
        return lastCacheRecipeTime;
    }

    public void setLastCacheRecipeTime(Date lastCacheRecipeTime) {
        this.lastCacheRecipeTime = lastCacheRecipeTime;
    }

    public void recipeReasonHasModified(boolean recipeReasonHasModified) {
        this.recipeReasonHasModified = recipeReasonHasModified;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public double getKilometres() {
        return kilometres;
    }

    public void setKilometres(double kilometres) {
        this.kilometres = kilometres;
    }

    public java.util.Date getLastUpdateStateTime() {
        return lastUpdateStateTime;
    }

    public void setLastUpdateStateTime(java.util.Date lastUpdateStateTime) {
        this.lastUpdateStateTime = lastUpdateStateTime;
    }

    public Pair<Long, Long> getTimePair() {
        return timePair;
    }

    public void setTimePair(Pair<Long, Long> timePair) {
        this.timePair = timePair;
    }

    public long getLastSleepTimes() {
        return lastSleepTimes;
    }

    public void setLastSleepTimes(long lastSleepTimes) {
        this.lastSleepTimes = lastSleepTimes;
    }

    public double getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(double sleepTime) {
        this.sleepTime = sleepTime;
    }
}
