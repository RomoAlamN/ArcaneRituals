package com.romoalamn.amfbeta.magic.api.item;

import com.romoalamn.amfbeta.MagicMod;
import com.romoalamn.amfbeta.magic.api.MagicRegistries;
import com.romoalamn.amfbeta.magic.api.SpellUtilities;
import com.romoalamn.amfbeta.magic.api.spell.AbstractSpell;
import com.romoalamn.amfbeta.magic.api.wand.Attunement;
import com.romoalamn.amfbeta.magic.api.wand.IWand;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemWand extends Item implements IWand {
    public ItemWand(int damage) {
        super(new Item.Properties().group(MagicMod.MAGIC).maxStackSize(1).maxDamage(damage));
    }

    @Override
    public Attunement getAttunement(ItemStack stack) {
        return SpellUtilities.getAttunement(stack);
    }

    @Override
    public ResourceLocation getItemRegistryName() {
        return Objects.requireNonNull(this.getRegistryName());
    }

    @Override
    public void castEffect(IWorld worldIn, LivingEntity caster, AbstractSpell spell, Vec3d direction) {
        spell.cast(worldIn, caster, direction, caster.getActiveItemStack(), caster.getActiveHand());
    }
    Hand otherHand(Hand h){
        return h == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     *
     * @param group
     * @param items
     */
    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        for(Attunement att : MagicRegistries.INSTANCE.ATTUNEMENTS.getValues()){
            ItemStack wand = new ItemStack(this);
            SpellUtilities.addAttunement(wand, att);
            items.add(wand);
        }
        super.fillItemGroup(group, items);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if(playerIn.getHeldItem(handIn).getItem() instanceof IWand){
            // held item is a wand.
            playerIn.setActiveHand(handIn);
            Hand other = otherHand(handIn);
            if(playerIn.getHeldItem(other).getItem() instanceof ItemMagicBook){
                ItemStack book = playerIn.getHeldItem(other);
                castEffect(worldIn, playerIn, SpellUtilities.getSpell(book), playerIn.getLookVec());
                // success!
                return ActionResult.resultSuccess(playerIn.getHeldItem(handIn));
            }
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}
