package org.client.bracelet.entity;

import android.util.JsonReader;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

/**
 * 食物
 */
public class Food {

    /**
     * 食物ID
     */
    private Long id;

    /**
     * 食物名称
     */
    private String name;

    /**
     * 热量（千卡/100g）
     */
    private Double heatContent;

    /**
     * 硫胺素（毫克/100g）
     */
    private Double thiamine;

    /**
     * 钙（毫克/100g）
     */
    private Double calcium;

    /**
     * 蛋白质（克/100g）
     */
    private Double protein;

    /**
     * 核黄素（毫克/100g）
     */
    private Double riboflavin;

    /**
     * 镁（毫克/100g）
     */
    private Double magnesium;

    /**
     * 脂肪（克/100g）
     */
    private Double fat;

    /**
     * 烟酸（毫克/100g）
     */
    private Double niacin;

    /**
     * 铁（毫克/100g）
     */
    private Double iron;

    /**
     * 碳水化合物（克/100g）
     */
    private Double carbohydrate;

    /**
     * 维生素C（毫克/100g）
     */
    private Double vitaminC;

    /**
     * 猛（毫克/100g）
     */
    private Double manganese;

    /**
     * 膳食纤维（克/100g）
     */
    private Double dietaryFibre;

    /**
     * 维生素E（毫克/100g）
     */
    private Double vitaminE;

    /**
     * 锌（毫克/100g）
     */
    private Double zinc;

    /**
     * 维生素A（微克/100g）
     */
    private Double vitaminA;

    /**
     * 胆固醇（毫克/100g）
     */
    private Double cholesterol;

    /**
     * 铜（毫克/100g）
     */
    private Double copper;

    /**
     * 胡萝卜素（微克/100g）
     */
    private Double carotene;

    /**
     * 钾（毫克/100g）
     */
    private Double potassium;

    /**
     * 磷（毫克/100g）
     */
    private Double phosphorus;

    /**
     * 视黄醇当量（微克/100g）
     */
    private Double retinolEquivalent;


    /**
     * 钠（毫克/100g）
     */
    private Double sodium;

    /**
     * 硒（微克/100g）
     */
    private Double selenium;

    /**
     * 食物所属类型
     */
    private FoodType foodType;

    public Food() {
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
    }

    public Food(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);
            this.id = json.getLong("id");
            this.name = json.getString("name");
            this.heatContent = json.getDouble("heatContent");
            this.thiamine = json.getDouble("thiamine");
            this.calcium = json.getDouble("calcium");
            this.protein = json.getDouble("protein");
            this.riboflavin = json.getDouble("riboflavin");
            this.magnesium = json.getDouble("magnesium");
            this.fat = json.getDouble("fat");
            this.niacin = json.getDouble("niacin");
            this.iron = json.getDouble("iron");
            this.carbohydrate = json.getDouble("carbohydrate");
            this.vitaminC = json.getDouble("vitaminC");
            this.manganese = json.getDouble("manganese");
            this.dietaryFibre = json.getDouble("dietaryFibre");
            this.vitaminE = json.getDouble("vitaminE");
            this.zinc = json.getDouble("zinc");
            this.vitaminA = json.getDouble("vitaminA");
            this.cholesterol = json.getDouble("cholesterol");
            this.copper = json.getDouble("copper");
            this.carotene = json.getDouble("carotene");
            this.potassium = json.getDouble("potassium");
            this.phosphorus = json.getDouble("phosphorus");
            this.retinolEquivalent = json.getDouble("retinolEquivalent");
            this.sodium = json.getDouble("sodium");
            this.selenium = json.getDouble("selenium");
            this.foodType = new FoodType(json.getJSONObject("foodType").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getHeatContent() {
        return heatContent;
    }

    public void setHeatContent(Double heatContent) {
        this.heatContent = heatContent;
    }

    public Double getThiamine() {
        return thiamine;
    }

    public void setThiamine(Double thiamine) {
        this.thiamine = thiamine;
    }

    public Double getCalcium() {
        return calcium;
    }

    public void setCalcium(Double calcium) {
        this.calcium = calcium;
    }

    public Double getProtein() {
        return protein;
    }

    public void setProtein(Double protein) {
        this.protein = protein;
    }

    public Double getRiboflavin() {
        return riboflavin;
    }

    public void setRiboflavin(Double riboflavin) {
        this.riboflavin = riboflavin;
    }

    public Double getMagnesium() {
        return magnesium;
    }

    public void setMagnesium(Double magnesium) {
        this.magnesium = magnesium;
    }

    public Double getFat() {
        return fat;
    }

    public void setFat(Double fat) {
        this.fat = fat;
    }

    public Double getNiacin() {
        return niacin;
    }

    public void setNiacin(Double niacin) {
        this.niacin = niacin;
    }

    public Double getIron() {
        return iron;
    }

    public void setIron(Double iron) {
        this.iron = iron;
    }

    public Double getCarbohydrate() {
        return carbohydrate;
    }

    public void setCarbohydrate(Double carbohydrate) {
        this.carbohydrate = carbohydrate;
    }

    public Double getVitaminC() {
        return vitaminC;
    }

    public void setVitaminC(Double vitaminC) {
        this.vitaminC = vitaminC;
    }

    public Double getManganese() {
        return manganese;
    }

    public void setManganese(Double manganese) {
        this.manganese = manganese;
    }

    public Double getDietaryFibre() {
        return dietaryFibre;
    }

    public void setDietaryFibre(Double dietaryFibre) {
        this.dietaryFibre = dietaryFibre;
    }

    public Double getVitaminE() {
        return vitaminE;
    }

    public void setVitaminE(Double vitaminE) {
        this.vitaminE = vitaminE;
    }

    public Double getZinc() {
        return zinc;
    }

    public void setZinc(Double zinc) {
        this.zinc = zinc;
    }

    public Double getVitaminA() {
        return vitaminA;
    }

    public void setVitaminA(Double vitaminA) {
        this.vitaminA = vitaminA;
    }

    public Double getCholesterol() {
        return cholesterol;
    }

    public void setCholesterol(Double cholesterol) {
        this.cholesterol = cholesterol;
    }

    public Double getCopper() {
        return copper;
    }

    public void setCopper(Double copper) {
        this.copper = copper;
    }

    public Double getCarotene() {
        return carotene;
    }

    public void setCarotene(Double carotene) {
        this.carotene = carotene;
    }

    public Double getPotassium() {
        return potassium;
    }

    public void setPotassium(Double potassium) {
        this.potassium = potassium;
    }

    public Double getPhosphorus() {
        return phosphorus;
    }

    public void setPhosphorus(Double phosphorus) {
        this.phosphorus = phosphorus;
    }

    public Double getRetinolEquivalent() {
        return retinolEquivalent;
    }

    public void setRetinolEquivalent(Double retinolEquivalent) {
        this.retinolEquivalent = retinolEquivalent;
    }

    public Double getSodium() {
        return sodium;
    }

    public void setSodium(Double sodium) {
        this.sodium = sodium;
    }

    public Double getSelenium() {
        return selenium;
    }

    public void setSelenium(Double selenium) {
        this.selenium = selenium;
    }

    public FoodType getFoodType() {
        return foodType;
    }

    public void setFoodType(FoodType foodType) {
        this.foodType = foodType;
    }

    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        try {
            json.put("id", id)
                    .put("name", name)
                    .put("heatContent", heatContent)
                    .put("thiamine", thiamine)
                    .put("calcium", calcium)
                    .put("protein", protein)
                    .put("riboflavin", riboflavin)
                    .put("magnesium", magnesium)
                    .put("fat", fat)
                    .put("niacin", niacin)
                    .put("iron", iron)
                    .put("carbohydrate", carbohydrate)
                    .put("vitaminC", vitaminC)
                    .put("manganese", manganese)
                    .put("dietaryFibre", dietaryFibre)
                    .put("vitaminE", vitaminE)
                    .put("zinc", zinc)
                    .put("vitaminA", vitaminA)
                    .put("cholesterol", cholesterol)
                    .put("copper", copper)
                    .put("carotene", carotene)
                    .put("potassium", potassium)
                    .put("phosphorus", phosphorus)
                    .put("retinolEquivalent", retinolEquivalent)
                    .put("sodium", sodium)
                    .put("selenium", selenium)
                    .put("foodType", new JSONObject(foodType.toString()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Food)) return false;

        Food food = (Food) o;

        if (id != null ? !id.equals(food.id) : food.id != null) return false;
        if (name != null ? !name.equals(food.name) : food.name != null) return false;
        if (heatContent != null ? !heatContent.equals(food.heatContent) : food.heatContent != null) return false;
        if (thiamine != null ? !thiamine.equals(food.thiamine) : food.thiamine != null) return false;
        if (calcium != null ? !calcium.equals(food.calcium) : food.calcium != null) return false;
        if (protein != null ? !protein.equals(food.protein) : food.protein != null) return false;
        if (riboflavin != null ? !riboflavin.equals(food.riboflavin) : food.riboflavin != null) return false;
        if (magnesium != null ? !magnesium.equals(food.magnesium) : food.magnesium != null) return false;
        if (fat != null ? !fat.equals(food.fat) : food.fat != null) return false;
        if (niacin != null ? !niacin.equals(food.niacin) : food.niacin != null) return false;
        if (iron != null ? !iron.equals(food.iron) : food.iron != null) return false;
        if (carbohydrate != null ? !carbohydrate.equals(food.carbohydrate) : food.carbohydrate != null) return false;
        if (vitaminC != null ? !vitaminC.equals(food.vitaminC) : food.vitaminC != null) return false;
        if (manganese != null ? !manganese.equals(food.manganese) : food.manganese != null) return false;
        if (dietaryFibre != null ? !dietaryFibre.equals(food.dietaryFibre) : food.dietaryFibre != null) return false;
        if (vitaminE != null ? !vitaminE.equals(food.vitaminE) : food.vitaminE != null) return false;
        if (zinc != null ? !zinc.equals(food.zinc) : food.zinc != null) return false;
        if (vitaminA != null ? !vitaminA.equals(food.vitaminA) : food.vitaminA != null) return false;
        if (cholesterol != null ? !cholesterol.equals(food.cholesterol) : food.cholesterol != null) return false;
        if (copper != null ? !copper.equals(food.copper) : food.copper != null) return false;
        if (carotene != null ? !carotene.equals(food.carotene) : food.carotene != null) return false;
        if (potassium != null ? !potassium.equals(food.potassium) : food.potassium != null) return false;
        if (phosphorus != null ? !phosphorus.equals(food.phosphorus) : food.phosphorus != null) return false;
        if (retinolEquivalent != null ? !retinolEquivalent.equals(food.retinolEquivalent) : food.retinolEquivalent != null)
            return false;
        if (sodium != null ? !sodium.equals(food.sodium) : food.sodium != null) return false;
        if (selenium != null ? !selenium.equals(food.selenium) : food.selenium != null) return false;
        if (foodType != null ? !foodType.equals(food.foodType) : food.foodType != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (heatContent != null ? heatContent.hashCode() : 0);
        result = 31 * result + (thiamine != null ? thiamine.hashCode() : 0);
        result = 31 * result + (calcium != null ? calcium.hashCode() : 0);
        result = 31 * result + (protein != null ? protein.hashCode() : 0);
        result = 31 * result + (riboflavin != null ? riboflavin.hashCode() : 0);
        result = 31 * result + (magnesium != null ? magnesium.hashCode() : 0);
        result = 31 * result + (fat != null ? fat.hashCode() : 0);
        result = 31 * result + (niacin != null ? niacin.hashCode() : 0);
        result = 31 * result + (iron != null ? iron.hashCode() : 0);
        result = 31 * result + (carbohydrate != null ? carbohydrate.hashCode() : 0);
        result = 31 * result + (vitaminC != null ? vitaminC.hashCode() : 0);
        result = 31 * result + (manganese != null ? manganese.hashCode() : 0);
        result = 31 * result + (dietaryFibre != null ? dietaryFibre.hashCode() : 0);
        result = 31 * result + (vitaminE != null ? vitaminE.hashCode() : 0);
        result = 31 * result + (zinc != null ? zinc.hashCode() : 0);
        result = 31 * result + (vitaminA != null ? vitaminA.hashCode() : 0);
        result = 31 * result + (cholesterol != null ? cholesterol.hashCode() : 0);
        result = 31 * result + (copper != null ? copper.hashCode() : 0);
        result = 31 * result + (carotene != null ? carotene.hashCode() : 0);
        result = 31 * result + (potassium != null ? potassium.hashCode() : 0);
        result = 31 * result + (phosphorus != null ? phosphorus.hashCode() : 0);
        result = 31 * result + (retinolEquivalent != null ? retinolEquivalent.hashCode() : 0);
        result = 31 * result + (sodium != null ? sodium.hashCode() : 0);
        result = 31 * result + (selenium != null ? selenium.hashCode() : 0);
        result = 31 * result + (foodType != null ? foodType.hashCode() : 0);
        return result;
    }
}
