package org.client.bracelet.entity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class Recipe {
    private Set<Food> foods;

    /**
     * 热量（千卡/100g）
     */
    private double heatContent;

    /**
     * 硫胺素（毫克/100g）
     */
    private double thiamine;

    /**
     * 钙（毫克/100g）
     */
    private double calcium;

    /**
     * 蛋白质（克/100g）
     */
    private double protein;

    /**
     * 核黄素（毫克/100g）
     */
    private double riboflavin;

    /**
     * 镁（毫克/100g）
     */
    private double magnesium;

    /**
     * 脂肪（克/100g）
     */
    private double fat;

    /**
     * 烟酸（毫克/100g）
     */
    private double niacin;

    /**
     * 铁（毫克/100g）
     */
    private double iron;

    /**
     * 碳水化合物（克/100g）
     */
    private double carbohydrate;

    /**
     * 维生素C（毫克/100g）
     */
    private double vitaminC;

    /**
     * 猛（毫克/100g）
     */
    private double manganese;

    /**
     * 膳食纤维（克/100g）
     */
    private double dietaryFibre;

    /**
     * 维生素E（毫克/100g）
     */
    private double vitaminE;

    /**
     * 锌（毫克/100g）
     */
    private double zinc;

    /**
     * 维生素A（微克/100g）
     */
    private double vitaminA;

    /**
     * 胆固醇（毫克/100g）
     */
    private double cholesterol;

    /**
     * 铜（毫克/100g）
     */
    private double copper;

    /**
     * 胡萝卜素（微克/100g）
     */
    private double carotene;

    /**
     * 钾（毫克/100g）
     */
    private double potassium;

    /**
     * 磷（毫克/100g）
     */
    private double phosphorus;

    /**
     * 视黄醇当量（微克/100g）
     */
    private double retinolEquivalent;


    /**
     * 钠（毫克/100g）
     */
    private double sodium;

    /**
     * 硒（微克/100g）
     */
    private double selenium;

    private double water;

    public Recipe() {
        this.heatContent = 0.0;
        this.thiamine = 0.0;
        this.calcium = 0.0;
        this.protein = 0.0;
        this.riboflavin = 0.0;
        this.magnesium = 0.0;
        this.fat = 0.0;
        this.niacin = 0.0;
        this.iron = 0.0;
        this.carbohydrate = 0.0;
        this.vitaminC = 0.0;
        this.manganese = 0.0;
        this.dietaryFibre = 0.0;
        this.vitaminE = 0.0;
        this.zinc = 0.0;
        this.vitaminA = 0.0;
        this.cholesterol = 0.0;
        this.copper = 0.0;
        this.carotene = 0.0;
        this.potassium = 0.0;
        this.phosphorus = 0.0;
        this.retinolEquivalent = 0.0;
        this.sodium = 0.0;
        this.selenium = 0.0;
        foods = new LinkedHashSet<>();
    }

    public Recipe(String jsonString) {
        this();
        try {
            JSONObject json = new JSONObject(jsonString);
            water = json.getDouble("water");
            JSONArray foodArray = json.getJSONArray("foods");
            for (int i = 0; i < foodArray.length(); i++) {
                addFood(new Food((foodArray.getJSONObject(i)).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addFood(Food food) {
        if (foods.add(food)) {
            this.heatContent += food.getHeatContent();
            this.thiamine += food.getThiamine();
            this.calcium += food.getCalcium();
            this.protein += food.getProtein();
            this.riboflavin += food.getRiboflavin();
            this.magnesium += food.getMagnesium();
            this.fat += food.getFat();
            this.niacin += food.getNiacin();
            this.iron += food.getIron();
            this.carbohydrate += food.getCarbohydrate();
            this.vitaminC += food.getVitaminC();
            this.manganese += food.getManganese();
            this.dietaryFibre += food.getDietaryFibre();
            this.vitaminE += food.getVitaminE();
            this.zinc += food.getZinc();
            this.vitaminA += food.getVitaminA();
            this.cholesterol += food.getCholesterol();
            this.copper += food.getCopper();
            this.carotene += food.getCarotene();
            this.potassium += food.getPotassium();
            this.phosphorus += food.getPhosphorus();
            this.retinolEquivalent += food.getRetinolEquivalent();
            this.sodium += food.getSodium();
            this.selenium += food.getSelenium();
        }
    }

    public void delFood(Food food) {
        if (foods.remove(food)) {
            this.heatContent -= food.getHeatContent();
            this.thiamine -= food.getThiamine();
            this.calcium -= food.getCalcium();
            this.protein -= food.getProtein();
            this.riboflavin -= food.getRiboflavin();
            this.magnesium -= food.getMagnesium();
            this.fat -= food.getFat();
            this.niacin -= food.getNiacin();
            this.iron -= food.getIron();
            this.carbohydrate -= food.getCarbohydrate();
            this.vitaminC -= food.getVitaminC();
            this.manganese -= food.getManganese();
            this.dietaryFibre -= food.getDietaryFibre();
            this.vitaminE -= food.getVitaminE();
            this.zinc -= food.getZinc();
            this.vitaminA -= food.getVitaminA();
            this.cholesterol -= food.getCholesterol();
            this.copper -= food.getCopper();
            this.carotene -= food.getCarotene();
            this.potassium -= food.getPotassium();
            this.phosphorus -= food.getPhosphorus();
            this.retinolEquivalent -= food.getRetinolEquivalent();
            this.sodium -= food.getSodium();
            this.selenium -= food.getSelenium();
        }
    }

    public void fix() {
        this.heatContent = 0.0;
        this.thiamine = 0.0;
        this.calcium = 0.0;
        this.protein = 0.0;
        this.riboflavin = 0.0;
        this.magnesium = 0.0;
        this.fat = 0.0;
        this.niacin = 0.0;
        this.iron = 0.0;
        this.carbohydrate = 0.0;
        this.vitaminC = 0.0;
        this.manganese = 0.0;
        this.dietaryFibre = 0.0;
        this.vitaminE = 0.0;
        this.zinc = 0.0;
        this.vitaminA = 0.0;
        this.cholesterol = 0.0;
        this.copper = 0.0;
        this.carotene = 0.0;
        this.potassium = 0.0;
        this.phosphorus = 0.0;
        this.retinolEquivalent = 0.0;
        this.sodium = 0.0;
        this.selenium = 0.0;
        for (Food food : foods) {
            this.heatContent += food.getHeatContent();
            this.thiamine += food.getThiamine();
            this.calcium += food.getCalcium();
            this.protein += food.getProtein();
            this.riboflavin += food.getRiboflavin();
            this.magnesium += food.getMagnesium();
            this.fat += food.getFat();
            this.niacin += food.getNiacin();
            this.iron += food.getIron();
            this.carbohydrate += food.getCarbohydrate();
            this.vitaminC += food.getVitaminC();
            this.manganese += food.getManganese();
            this.dietaryFibre += food.getDietaryFibre();
            this.vitaminE += food.getVitaminE();
            this.zinc += food.getZinc();
            this.vitaminA += food.getVitaminA();
            this.cholesterol += food.getCholesterol();
            this.copper += food.getCopper();
            this.carotene += food.getCarotene();
            this.potassium += food.getPotassium();
            this.phosphorus += food.getPhosphorus();
            this.retinolEquivalent += food.getRetinolEquivalent();
            this.sodium += food.getSodium();
            this.selenium += food.getSelenium();
        }
    }

    public int size() {
        return foods.size();
    }

    public boolean isExist(Food food) {
        return foods.contains(food);
    }

    public int getHeatContent() {
        return Double.valueOf(heatContent).intValue();
    }

    public double getThiamine() {
        return thiamine;
    }

    public double getCalcium() {
        return calcium;
    }

    public double getProtein() {
        return protein;
    }

    public double getRiboflavin() {
        return riboflavin;
    }

    public double getMagnesium() {
        return magnesium;
    }

    public double getFat() {
        return fat;
    }

    public double getNiacin() {
        return niacin;
    }

    public double getIron() {
        return iron;
    }

    public double getCarbohydrate() {
        return carbohydrate;
    }

    public double getVitaminC() {
        return vitaminC;
    }

    public double getManganese() {
        return manganese;
    }

    public double getDietaryFibre() {
        return dietaryFibre;
    }

    public double getVitaminE() {
        return vitaminE;
    }

    public double getZinc() {
        return zinc;
    }

    public double getVitaminA() {
        return vitaminA;
    }

    public double getCholesterol() {
        return cholesterol;
    }

    public double getCopper() {
        return copper;
    }

    public double getCarotene() {
        return carotene;
    }

    public double getPotassium() {
        return potassium;
    }

    public double getPhosphorus() {
        return phosphorus;
    }

    public double getRetinolEquivalent() {
        return retinolEquivalent;
    }

    public double getSodium() {
        return sodium;
    }

    public double getSelenium() {
        return selenium;
    }

    public List<Food> getFoods() {
        List<Food> foodList = new ArrayList<>();
        foodList.addAll(foods);
        return foodList;
    }

    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        JSONArray foodArray = new JSONArray();
        for (Food food : foods) {
            foodArray.put(food.toString());
        }
        try {
            json.put("foods", foodArray);
            json.put("water", water);
            json.put("size", foods.size());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    public double getWater() {
        return water;
    }

    public void setWater(double water) {
        this.water = water;
    }
}
