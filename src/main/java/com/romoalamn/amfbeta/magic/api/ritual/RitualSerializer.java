package com.romoalamn.amfbeta.magic.api.ritual;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RitualSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<Ritual> {
    @Override
    public Ritual read(ResourceLocation recipeId, JsonObject json) {
        JsonArray ingredients = json.getAsJsonArray("ingredients");
        Ingredient[] ingr = new Ingredient[ingredients.size()];
        for (int i = 0; i < ingr.length; i++) {
            ingr[i] = Ingredient.deserialize(ingredients.get(i));
        }
        JsonObject result = json.getAsJsonObject("result");
        ItemStack resItem = ShapedRecipe.deserializeItem(result);
        int experience = json.get("experience").getAsInt();
        int manaCost = json.get("mana").getAsInt();
        return new Ritual(resItem, ingr, experience, manaCost);
    }

    @Nullable
    @Override
    public Ritual read(ResourceLocation recipeId, PacketBuffer buffer) {
        int ingredientAmount = buffer.readIntLE();
        Ingredient[] ingr = new Ingredient[ingredientAmount];
        for(int i = 0; i < ingredientAmount; i ++){
            ingr[i] = Ingredient.read(buffer);
        }
        ItemStack resItem = buffer.readItemStack();
        int experience = buffer.readIntLE();
        int manaCost = buffer.readIntLE();
        return new Ritual(resItem, ingr, experience, manaCost);
    }

    @Override
    public void write(PacketBuffer buffer, Ritual recipe) {
        buffer.writeIntLE(recipe.getIngredients().size());
        for(Ingredient ingr : recipe.getIngredients()){
            ingr.write(buffer);
        }
        buffer.writeItemStack(recipe.getRecipeOutput());
        buffer.writeIntLE(recipe.getExperience());
        buffer.writeIntLE(recipe.getManaCost());
    }
}
