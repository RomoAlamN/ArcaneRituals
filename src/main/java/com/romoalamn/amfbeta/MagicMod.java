package com.romoalamn.amfbeta;

import com.romoalamn.amfbeta.magic.api.MagicRegistries;
import com.romoalamn.amfbeta.magic.api.item.ItemMagicBook;
import com.romoalamn.amfbeta.magic.api.item.ItemRegisterer;
import com.romoalamn.amfbeta.magic.api.network.Channel;
import com.romoalamn.amfbeta.magic.api.ritual.RitualBlocks;
import com.romoalamn.amfbeta.magic.api.ritual.RitualSerializer;
import com.romoalamn.amfbeta.magic.api.ritual.block.MagicAshBlock;
import com.romoalamn.amfbeta.magic.api.spell.AbstractSpell;
import com.romoalamn.amfbeta.magic.api.spell.entity.DirectionSerializer;
import com.romoalamn.amfbeta.magic.api.spell.render.SpellEntityRenderer;
import com.romoalamn.amfbeta.magic.api.wand.Attunement;
import com.romoalamn.amfbeta.magic.impl.SpellRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MagicMod.MODID)
public class MagicMod {
    public static final String MODID = "arcanerituals";
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();
    public static ItemGroup MAGIC;

    public MagicMod() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        ItemRegisterer.registerItemRegistry();
        SpellRegister.registerSpellRegistry();
        RitualBlocks.registerBlockRegistry();
    }

    private void setup(final FMLCommonSetupEvent event) {
        // some preinit code
        Channel.registerChannel();
        MAGIC = new ItemGroup("itemgroup.arcanerituals.main") {
            @Override
            public ItemStack createIcon() {
                return new ItemStack(ItemMagicBook.EMPTY);
            }
        };
        DataSerializers.registerSerializer(DirectionSerializer.INSTANCE);

    }

    private void clientSetup(final FMLClientSetupEvent cse) {
        RenderingRegistry.registerEntityRenderingHandler(SpellRegister.FIREBALL_ENTITY.get(), SpellEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(SpellRegister.MAGIC_MISSILE_ENTITY.get(), SpellEntityRenderer::new);
        Minecraft.getInstance().getBlockColors().register(
                (state, t1, t2, t3) -> {
                    return MagicAshBlock.colorMultiplier(state.get(MagicAshBlock.POWER));
                }, RitualBlocks.ASH_WIRE.get()
        );
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onRegistryRegister(RegistryEvent.NewRegistry event) {
            IForgeRegistry<AbstractSpell> spells = new RegistryBuilder<AbstractSpell>()
                    .setType(AbstractSpell.class)
                    .setName(new ResourceLocation(MagicMod.MODID, "spell_registry"))
                    .create();
            IForgeRegistry<Attunement> attunements = new RegistryBuilder<Attunement>()
                    .setType(Attunement.class)
                    .setName(new ResourceLocation(MagicMod.MODID, "attunement_registry"))
                    .create();
            MagicRegistries.create(spells, attunements);
        }

        @SubscribeEvent
        public static void onRecipeSerializer(RegistryEvent.Register<IRecipeSerializer<?>> recipeSerializer) {
            recipeSerializer.getRegistry().register(new RitualSerializer().setRegistryName(MagicMod.MODID, "ritual"));
        }
    }
}
