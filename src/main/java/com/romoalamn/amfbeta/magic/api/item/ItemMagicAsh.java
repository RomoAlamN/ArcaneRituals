package com.romoalamn.amfbeta.magic.api.item;

import com.romoalamn.amfbeta.magic.api.ritual.RitualBlocks;
import net.minecraft.item.BlockItem;

public class ItemMagicAsh extends BlockItem {

    public ItemMagicAsh(Properties properties) {
        super(RitualBlocks.ASH_WIRE.get(), properties);
    }
}
