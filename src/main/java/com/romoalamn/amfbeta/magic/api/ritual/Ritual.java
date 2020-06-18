package com.romoalamn.amfbeta.magic.api.ritual;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class Ritual implements IRecipe<RitualInventory> {
    private Ingredient [] ingredients;
    private ItemStack result;
    int experienceGained;
    int manaCost;
    public Ritual(ItemStack result, Ingredient[] ingredients, int experienceGained, int manaCost){
        this.ingredients = ingredients;
        this.result = result;
        this.experienceGained = experienceGained;
        this.manaCost = manaCost;
    }
    /**
     * Used to check if a recipe matches current crafting inventory
     *
     * @param inv
     * @param worldIn
     */
    @Override
    public boolean matches(RitualInventory inv, World worldIn) {

        return false;
    }

    /**
     * Returns an Item that is the result of this recipe
     *
     * @param inv
     */
    @Override
    public ItemStack getCraftingResult(RitualInventory inv) {
        return null;
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     *
     * @param width
     * @param height
     */
    @Override
    public boolean canFit(int width, int height) {
        return false;
    }

    /**
     * Get the result of this recipe, usually for display purposes (e.g. recipe book). If your recipe has more than one
     * possible result (e.g. it's dynamic and depends on its inputs), then return an empty stack.
     */
    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }

    @Override
    public ResourceLocation getId() {
        return null;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return null;
    }

    @Override
    public IRecipeType<?> getType() {
        return null;
    }

    public int getManaCost() {
        return manaCost;
    }

    public int getExperience() {
        return experienceGained;
    }
}
