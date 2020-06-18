package com.romoalamn.amfbeta.magic.api.spell.render;

import com.romoalamn.amfbeta.magic.api.spell.entity.SpellEntity;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Renders a blank spell, for when you want to use particle effects to do what you want.
 * A close cousin would be the FlatRenderer, which renders a flat, possibly animated texture.
 * @param <T> The entity type we're rendering.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SpellEntityRenderer<T extends SpellEntity> extends EntityRenderer<T> {
    public SpellEntityRenderer(EntityRendererManager renderManager) {
        super(renderManager);
    }

    /**
     * Returns the location of an entity's texture.
     *
     * @param entity
     */
    @Override
    public ResourceLocation getEntityTexture(T entity) {
        return PlayerContainer.LOCATION_BLOCKS_TEXTURE;
    }
}
