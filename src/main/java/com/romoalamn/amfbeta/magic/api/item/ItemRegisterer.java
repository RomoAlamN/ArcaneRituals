package com.romoalamn.amfbeta.magic.api.item;

import com.romoalamn.amfbeta.MagicMod;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemRegisterer {
    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, MagicMod.MODID);

    public static final RegistryObject<ItemMagicBook> ITEM_MAGIC_BOOK = ITEMS.register("magic_book", ItemMagicBook::new);
    public static final RegistryObject<ItemWand> ITEM_WAND = ITEMS.register("magic_wand", ()-> new ItemWand(128));
    public static final RegistryObject<ItemMagicAsh> ITEM_ASH = ITEMS.register("magic_ash", ()-> new ItemMagicAsh(
            new Item.Properties().group(MagicMod.MAGIC)
    ));

    public static final void registerItemRegistry(){
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
