package com.romoalamn.amfbeta.magic.api.ritual;

import com.romoalamn.amfbeta.MagicMod;
import com.romoalamn.amfbeta.magic.api.ritual.block.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RitualBlocks {
    public static DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, MagicMod.MODID);
    public static DeferredRegister<Item> ITEM_BLOCKS = new DeferredRegister<>(ForgeRegistries.ITEMS, MagicMod.MODID);
    private static DeferredRegister<TileEntityType<?>> TILE_ENTITIES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, MagicMod.MODID);

    public static RegistryObject<AltarBlock> ALTAR = BLOCKS.register("ritual_altar", () -> new AltarBlock(
            Block.Properties.create(Material.ROCK)
                    .hardnessAndResistance(1.0f)
                    .notSolid()
                    .harvestLevel(1)
                    .harvestTool(ToolType.PICKAXE)
    ));
    public static RegistryObject<MagicAshBlock> ASH_WIRE = BLOCKS.register("ash_wire", () -> new MagicAshBlock(
                    Block.Properties.create(Material.MISCELLANEOUS)
                            .hardnessAndResistance(0)
                            .doesNotBlockMovement()
            )
    );
    public static RegistryObject<RitualFocusBlock> RITUAL_FOCUS = BLOCKS.register("ritual_focus", () -> new RitualFocusBlock(
            Block.Properties.create(Material.ROCK)
                    .hardnessAndResistance(1)
                    .notSolid()
                    .harvestLevel(1)
                    .harvestTool(ToolType.PICKAXE)
    ));


    public static RegistryObject<BlockItem> ALTAR_ITEM = ITEM_BLOCKS.register("ritual_altar_item", () -> new BlockItem(ALTAR.get(),
            new Item.Properties().group(MagicMod.MAGIC)
    ));
    public static RegistryObject<BlockItem> RITUAL_FOCUS_ITEM = ITEM_BLOCKS.register("ritual_focus_item", () -> new BlockItem(RITUAL_FOCUS.get(),
            new Item.Properties().group(MagicMod.MAGIC)
    ));

    public static RegistryObject<TileEntityType<AltarTileEntity>> ALTAR_TILE = TILE_ENTITIES.register("altar_tile",
            () -> TileEntityType.Builder.create(AltarTileEntity::new, ALTAR.get()).build(null)
    );
    public static RegistryObject<TileEntityType<RitualFocusTileEntity>> RITUAL_TILE = TILE_ENTITIES.register("ritual_tile",
            () -> TileEntityType.Builder.create(RitualFocusTileEntity::new, RITUAL_FOCUS.get()).build(null)
    );

    public static void registerBlockRegistry() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEM_BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
