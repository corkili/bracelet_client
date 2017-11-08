package org.client.bracelet.entity;

import com.beardedhen.androidbootstrap.BootstrapButton;

/**
 * Created by 李浩然 on 2017/11/8.
 */

public class ApplicationManager {
    private static ApplicationManager applicationManager;

    private boolean isLogin;
    private User user;
    private boolean hasCacheRecipe;
    private Recipe recipe;

    private ApplicationManager() {
        isLogin = false;
        user = null;
        hasCacheRecipe = false;
        recipe = null;
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
        return hasCacheRecipe;
    }

    public void hasCacheRecipe(boolean hasCacheRecipe) {
        this.hasCacheRecipe = hasCacheRecipe;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }
}
