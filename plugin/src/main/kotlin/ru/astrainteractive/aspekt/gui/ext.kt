package ru.astrainteractive.aspekt.gui

import org.bukkit.Material
import org.bukkit.entity.EntityType

fun EntityType.toMaterial() = when (this) {
    EntityType.DROPPED_ITEM -> Material.DIRT
    EntityType.EXPERIENCE_ORB -> Material.EXPERIENCE_BOTTLE
    EntityType.AREA_EFFECT_CLOUD -> Material.DIRT
    EntityType.ELDER_GUARDIAN -> Material.ELDER_GUARDIAN_SPAWN_EGG
    EntityType.WITHER_SKELETON -> Material.WITHER_SKELETON_SPAWN_EGG
    EntityType.STRAY -> Material.STRAY_SPAWN_EGG
    EntityType.EGG -> Material.EGG
    EntityType.LEASH_HITCH -> Material.LEAD
    EntityType.PAINTING -> Material.PAINTING
    EntityType.ARROW -> Material.ARROW
    EntityType.SNOWBALL -> Material.SNOWBALL
    EntityType.FIREBALL -> Material.FIRE_CHARGE
    EntityType.SMALL_FIREBALL -> Material.FIRE_CHARGE
    EntityType.ENDER_PEARL -> Material.ENDER_PEARL
    EntityType.ENDER_SIGNAL -> Material.BARRIER
    EntityType.SPLASH_POTION -> Material.SPLASH_POTION
    EntityType.THROWN_EXP_BOTTLE -> Material.EXPERIENCE_BOTTLE
    EntityType.ITEM_FRAME -> Material.ITEM_FRAME
    EntityType.WITHER_SKULL -> Material.WITHER_SKELETON_SKULL
    EntityType.PRIMED_TNT -> Material.TNT
    EntityType.FALLING_BLOCK -> Material.SAND
    EntityType.FIREWORK -> Material.FIREWORK_ROCKET
    EntityType.HUSK -> Material.HUSK_SPAWN_EGG
    EntityType.SPECTRAL_ARROW -> Material.SPECTRAL_ARROW
    EntityType.SHULKER_BULLET -> Material.SHULKER_BOX
    EntityType.DRAGON_FIREBALL -> Material.BARRIER
    EntityType.ZOMBIE_VILLAGER -> Material.ZOMBIE_VILLAGER_SPAWN_EGG
    EntityType.SKELETON_HORSE -> Material.SKELETON_HORSE_SPAWN_EGG
    EntityType.ZOMBIE_HORSE -> Material.ZOMBIE_HORSE_SPAWN_EGG
    EntityType.ARMOR_STAND -> Material.ARMOR_STAND
    EntityType.DONKEY -> Material.DONKEY_SPAWN_EGG
    EntityType.MULE -> Material.MULE_SPAWN_EGG
    EntityType.EVOKER_FANGS -> Material.EVOKER_SPAWN_EGG
    EntityType.EVOKER -> Material.EVOKER_SPAWN_EGG
    EntityType.VEX -> Material.VEX_SPAWN_EGG
    EntityType.VINDICATOR -> Material.VINDICATOR_SPAWN_EGG
    EntityType.ILLUSIONER -> Material.BARRIER
    EntityType.MINECART_COMMAND -> Material.COMMAND_BLOCK_MINECART
    EntityType.BOAT -> Material.BIRCH_BOAT
    EntityType.MINECART -> Material.MINECART
    EntityType.MINECART_CHEST -> Material.CHEST_MINECART
    EntityType.MINECART_FURNACE -> Material.FURNACE_MINECART
    EntityType.MINECART_TNT -> Material.TNT
    EntityType.MINECART_HOPPER -> Material.HOPPER_MINECART
    EntityType.MINECART_MOB_SPAWNER -> Material.SPAWNER
    EntityType.CREEPER -> Material.CREEPER_SPAWN_EGG
    EntityType.SKELETON -> Material.SKELETON_SPAWN_EGG
    EntityType.SPIDER -> Material.SPIDER_SPAWN_EGG
    EntityType.GIANT -> Material.BARRIER
    EntityType.ZOMBIE -> Material.ZOMBIE_SPAWN_EGG
    EntityType.SLIME -> Material.SLIME_SPAWN_EGG
    EntityType.GHAST -> Material.GHAST_SPAWN_EGG
    EntityType.ZOMBIFIED_PIGLIN -> Material.ZOMBIFIED_PIGLIN_SPAWN_EGG
    EntityType.ENDERMAN -> Material.ENDERMAN_SPAWN_EGG
    EntityType.CAVE_SPIDER -> Material.CAVE_SPIDER_SPAWN_EGG
    EntityType.SILVERFISH -> Material.SILVERFISH_SPAWN_EGG
    EntityType.BLAZE -> Material.BLAZE_SPAWN_EGG
    EntityType.MAGMA_CUBE -> Material.MAGMA_CUBE_SPAWN_EGG
    EntityType.ENDER_DRAGON -> Material.DRAGON_HEAD
    EntityType.WITHER -> Material.WITHER_ROSE
    EntityType.BAT -> Material.BAT_SPAWN_EGG
    EntityType.WITCH -> Material.WITCH_SPAWN_EGG
    EntityType.ENDERMITE -> Material.ENDERMITE_SPAWN_EGG
    EntityType.GUARDIAN -> Material.GUARDIAN_SPAWN_EGG
    EntityType.SHULKER -> Material.SHULKER_SPAWN_EGG
    EntityType.PIG -> Material.PIG_SPAWN_EGG
    EntityType.SHEEP -> Material.SHEEP_SPAWN_EGG
    EntityType.COW -> Material.COW_SPAWN_EGG
    EntityType.CHICKEN -> Material.CHICKEN_SPAWN_EGG
    EntityType.SQUID -> Material.SQUID_SPAWN_EGG
    EntityType.WOLF -> Material.WOLF_SPAWN_EGG
    EntityType.MUSHROOM_COW -> Material.COW_SPAWN_EGG
    EntityType.SNOWMAN -> Material.BARRIER
    EntityType.OCELOT -> Material.OCELOT_SPAWN_EGG
    EntityType.IRON_GOLEM -> Material.IRON_BLOCK
    EntityType.HORSE -> Material.HORSE_SPAWN_EGG
    EntityType.RABBIT -> Material.RABBIT_SPAWN_EGG
    EntityType.POLAR_BEAR -> Material.POLAR_BEAR_SPAWN_EGG
    EntityType.LLAMA -> Material.LLAMA_SPAWN_EGG
    EntityType.LLAMA_SPIT -> Material.BARRIER
    EntityType.PARROT -> Material.PARROT_SPAWN_EGG
    EntityType.VILLAGER -> Material.VILLAGER_SPAWN_EGG
    EntityType.ENDER_CRYSTAL -> Material.END_CRYSTAL
    EntityType.TURTLE -> Material.TURTLE_SPAWN_EGG
    EntityType.PHANTOM -> Material.PHANTOM_SPAWN_EGG
    EntityType.TRIDENT -> Material.TRIDENT
    EntityType.COD -> Material.COD
    EntityType.SALMON -> Material.SALMON
    EntityType.PUFFERFISH -> Material.PUFFERFISH
    EntityType.TROPICAL_FISH -> Material.TROPICAL_FISH
    EntityType.DROWNED -> Material.DROWNED_SPAWN_EGG
    EntityType.DOLPHIN -> Material.DOLPHIN_SPAWN_EGG
    EntityType.CAT -> Material.CAT_SPAWN_EGG
    EntityType.PANDA -> Material.PANDA_SPAWN_EGG
    EntityType.PILLAGER -> Material.PILLAGER_SPAWN_EGG
    EntityType.RAVAGER -> Material.RAVAGER_SPAWN_EGG
    EntityType.TRADER_LLAMA -> Material.TRADER_LLAMA_SPAWN_EGG
    EntityType.WANDERING_TRADER -> Material.WANDERING_TRADER_SPAWN_EGG
    EntityType.FOX -> Material.FOX_SPAWN_EGG
    EntityType.BEE -> Material.BEE_SPAWN_EGG
    EntityType.HOGLIN -> Material.HOGLIN_SPAWN_EGG
    EntityType.PIGLIN -> Material.PIGLIN_SPAWN_EGG
    EntityType.STRIDER -> Material.STRIDER_SPAWN_EGG
    EntityType.ZOGLIN -> Material.ZOGLIN_SPAWN_EGG
    EntityType.PIGLIN_BRUTE -> Material.PIGLIN_BRUTE_SPAWN_EGG
    EntityType.AXOLOTL -> Material.AXOLOTL_SPAWN_EGG
    EntityType.GLOW_ITEM_FRAME -> Material.GLOW_ITEM_FRAME
    EntityType.GLOW_SQUID -> Material.GLOW_SQUID_SPAWN_EGG
    EntityType.GOAT -> Material.GOAT_SPAWN_EGG
    EntityType.MARKER -> Material.BARRIER
    EntityType.ALLAY -> Material.ALLAY_SPAWN_EGG
    EntityType.CHEST_BOAT -> Material.BIRCH_CHEST_BOAT
    EntityType.FROG -> Material.FROG_SPAWN_EGG
    EntityType.TADPOLE -> Material.TADPOLE_SPAWN_EGG
    EntityType.WARDEN -> Material.WARDEN_SPAWN_EGG
    EntityType.FISHING_HOOK -> Material.FISHING_ROD
    EntityType.LIGHTNING -> Material.BARRIER
    EntityType.PLAYER -> Material.PLAYER_HEAD
    EntityType.UNKNOWN -> Material.BARRIER
}