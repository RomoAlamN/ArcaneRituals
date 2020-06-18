package com.romoalamn.amfbeta.magic.api.item;

import com.romoalamn.amfbeta.MagicMod;
import com.romoalamn.amfbeta.magic.api.MagicRegistries;
import com.romoalamn.amfbeta.magic.api.SpellUtilities;
import com.romoalamn.amfbeta.magic.api.spell.AbstractSpell;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.registries.ObjectHolder;

public class ItemMagicBook extends Item {
    public ItemMagicBook() {
        super(new Item.Properties().group(MagicMod.MAGIC).maxStackSize(1));
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     *
     * @param group
     * @param items
     */
    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        System.out.println("Filling Spellbook items. ");
//        if(this.isInGroup(group)) {
        for (AbstractSpell spell : MagicRegistries.INSTANCE.SPELLS.getValues()) {
            System.out.println(spell.toString());
            ItemStack toAdd = new ItemStack(this);
            SpellUtilities.addSpell(toAdd, spell);
            items.add(toAdd);
        }
//        }
    }

    @ObjectHolder(MagicMod.MODID + ":empty_book")
    public static final ItemMagicBook EMPTY = null;
}
