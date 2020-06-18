package com.romoalamn.amfbeta.magic.impl;

import com.romoalamn.amfbeta.MagicMod;
import com.romoalamn.amfbeta.magic.api.spell.AbstractSpell;
import com.romoalamn.amfbeta.magic.api.wand.Attunement;
import com.romoalamn.amfbeta.magic.impl.spells.SpellFireball;
import com.romoalamn.amfbeta.magic.impl.spells.SpellMagicMissile;
import com.romoalamn.amfbeta.magic.impl.spells.entity.SpellFireballEntity;
import com.romoalamn.amfbeta.magic.impl.spells.entity.SpellMagicMissileEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SpellRegister {
    public static final DeferredRegister<EntityType<?>> SPELL_ENTITIES = new DeferredRegister<>(ForgeRegistries.ENTITIES, MagicMod.MODID);
    //spells
    @ObjectHolder(MagicMod.MODID + ":spell_fireball")
    public static final AbstractSpell FIREBALL = null;
    @ObjectHolder(MagicMod.MODID + ":spell_magic_missile")
    public static final AbstractSpell MAGIC_MISSLE = null;

    //spell entities
    public static final RegistryObject<EntityType<SpellFireballEntity>> FIREBALL_ENTITY = SPELL_ENTITIES.register("entity_fireball",
            () -> EntityType.Builder.<SpellFireballEntity>create(SpellFireballEntity::new, EntityClassification.MISC)
                    .setUpdateInterval(1)
                    .build("entity_fireball")
    );
    public static final RegistryObject<EntityType<SpellMagicMissileEntity>> MAGIC_MISSILE_ENTITY = SPELL_ENTITIES.register("entity_magic_missile",
            () -> EntityType.Builder.<SpellMagicMissileEntity>create(SpellMagicMissileEntity::new, EntityClassification.MISC)
                    .setUpdateInterval(1)
                    .setShouldReceiveVelocityUpdates(true)
                    .size(0.1f,0.1f)
                    .build("entity_magic_missile")
    );


    public static void registerSpellRegistry() {
        SPELL_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @SubscribeEvent
    public static void registerSpells(RegistryEvent.Register<AbstractSpell> spells) {
        spells.getRegistry().register(new SpellFireball().setRegistryName("spell_fireball"));
        spells.getRegistry().register(new SpellMagicMissile().setRegistryName("spell_magic_missile"));
    }

    @SubscribeEvent
    public static void registerAttunements(RegistryEvent.Register<Attunement> attunements) {
        attunements.getRegistry().register(new Attunement("base").setRegistryName("base"));
    }
}
