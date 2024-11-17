package io.github.thatpreston.warppads;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.thatpreston.warppads.block.InterdimensionalWarpPadBlock;
import io.github.thatpreston.warppads.block.WarpPadBlock;
import io.github.thatpreston.warppads.block.WarpPadBlockEntity;
import io.github.thatpreston.warppads.menu.WarpConfigMenu;
import io.github.thatpreston.warppads.menu.WarpSelectionMenu;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class RegistryHandler {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(WarpPads.MOD_ID, Registries.BLOCK);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(WarpPads.MOD_ID, Registries.ITEM);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(WarpPads.MOD_ID, Registries.BLOCK_ENTITY_TYPE);
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(WarpPads.MOD_ID, Registries.SOUND_EVENT);
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(WarpPads.MOD_ID, Registries.PARTICLE_TYPE);
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(WarpPads.MOD_ID, Registries.MENU);
    public static final RegistrySupplier<Block> WARP_PAD_BLOCK = BLOCKS.register("warp_pad", WarpPadBlock::new);
    public static final RegistrySupplier<Item> WARP_PAD_ITEM = ITEMS.register("warp_pad", () -> new BlockItem(WARP_PAD_BLOCK.get(), new Item.Properties().arch$tab(CreativeModeTabs.FUNCTIONAL_BLOCKS)));
    public static final RegistrySupplier<Block> INTERDIMENSIONAL_WARP_PAD_BLOCK = BLOCKS.register("interdimensional_warp_pad", InterdimensionalWarpPadBlock::new);
    public static final RegistrySupplier<Item> INTERDIMENSIONAL_WARP_PAD_ITEM = ITEMS.register("interdimensional_warp_pad", () -> new BlockItem(INTERDIMENSIONAL_WARP_PAD_BLOCK.get(), new Item.Properties().arch$tab(CreativeModeTabs.FUNCTIONAL_BLOCKS)));
    public static final RegistrySupplier<BlockEntityType<WarpPadBlockEntity>> WARP_PAD = BLOCK_ENTITY_TYPES.register("warp_pad", () -> BlockEntityType.Builder.of(WarpPadBlockEntity::new, WARP_PAD_BLOCK.get(), INTERDIMENSIONAL_WARP_PAD_BLOCK.get()).build(null));
    public static final RegistrySupplier<SoundEvent> WARP_OUT_SOUND = SOUND_EVENTS.register("warp_out", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(WarpPads.MOD_ID, "warp_out")));
    public static final RegistrySupplier<SoundEvent> WARP_IN_SOUND = SOUND_EVENTS.register("warp_in", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(WarpPads.MOD_ID, "warp_in")));
    public static final RegistrySupplier<SimpleParticleType> WARP_PARTICLE = PARTICLE_TYPES.register("warp_particle", RegistryHandler::createParticleType);
    public static final RegistrySupplier<MenuType<WarpSelectionMenu>> WARP_SELECTION = MENU_TYPES.register("warp_selection", () -> MenuRegistry.ofExtended(WarpSelectionMenu::new));
    public static final RegistrySupplier<MenuType<WarpConfigMenu>> WARP_CONFIG = MENU_TYPES.register("warp_config", () -> MenuRegistry.ofExtended(WarpConfigMenu::new));
    public static void register() {
        BLOCKS.register();
        ITEMS.register();
        BLOCK_ENTITY_TYPES.register();
        SOUND_EVENTS.register();
        PARTICLE_TYPES.register();
        MENU_TYPES.register();
    }
    @ExpectPlatform
    public static SimpleParticleType createParticleType() {
        throw new AssertionError();
    }
}