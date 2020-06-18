package com.romoalamn.amfbeta.magic.api;

import com.romoalamn.amfbeta.magic.api.spell.AbstractSpell;
import com.romoalamn.amfbeta.magic.api.wand.Attunement;
import net.minecraftforge.registries.IForgeRegistry;

public class MagicRegistries {
    public final IForgeRegistry<AbstractSpell> SPELLS;
    public final IForgeRegistry<Attunement> ATTUNEMENTS;

    public static MagicRegistries INSTANCE;

    public MagicRegistries(IForgeRegistry<AbstractSpell> spells, IForgeRegistry<Attunement> attunements) {
        SPELLS = spells;
        ATTUNEMENTS = attunements;
    }

    public static void create(IForgeRegistry<AbstractSpell> spells, IForgeRegistry<Attunement> attunements) {
        INSTANCE = new MagicRegistries(spells, attunements);
    }
}
