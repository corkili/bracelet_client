package org.client.bracelet.entity;

import com.beardedhen.androidbootstrap.BootstrapButton;

import java.util.ArrayList;
import java.util.List;

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

    private List<FoodType> cacheFoodTypes;

    private ApplicationManager() {
        user = null;
        recipe = null;
        hasBandBracelet = false;
        cacheSteps = -1;
        cacheKilometre = -1;
        cacheSleep = -1;
        cacheFoodTypes = new ArrayList<>();
        String[] names = {"谷类", "酒类", "面食", "甜品", "饮料", "豆制品", "瘦肉", "肥肉"};
        for (int i = 0; i < 21; i++) {
            FoodType foodType = new FoodType();
            foodType.setId((long)i + 1);
            foodType.setName(String.valueOf(i + 1));
            cacheFoodTypes.add(foodType);
        }
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

    public List<FoodType> getCacheFoodTypes() {
        return cacheFoodTypes;
    }

    public void setCacheFoodTypes(List<FoodType> cacheFoodTypes) {
        this.cacheFoodTypes = cacheFoodTypes;
    }

    public boolean hasCacheFoodTypes() {
        return !cacheFoodTypes.isEmpty();
    }
}
