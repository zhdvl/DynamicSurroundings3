/*
 * Dynamic Surroundings
 * Copyright (C) 2020  OreCruncher
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>
 */

package org.orecruncher.lib.tags;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.stream.Stream;

@OnlyIn(Dist.CLIENT)
public final class TagUtils {

    private TagUtils() {

    }

    private static RegistryAccess supplier;

    public static void setTagManager(@Nonnull final RegistryAccess manager) {
        supplier = manager;
    }

    public static void clearTagManager() {
        supplier = null;
    }

    public static RegistryAccess getAccess() {
        return supplier;
    }

    public static Biome getBiome(ResourceKey<Biome> key) {
        return getBiomeRegistry().getValue(key.location());
    }

    public static IForgeRegistry<Biome> getBiomeRegistry() {
        return ForgeRegistries.BIOMES;
    }

    @Nullable
    public static Registry<Block> getBlockTag(@Nonnull final String name) {
        return getBlockTag(new ResourceLocation(name));
    }

    @Nullable
    public static Registry<Block> getBlockTag(@Nonnull final ResourceLocation res) {
        if (supplier == null)
            return null;


        return supplier.registryOrThrow(Registry.BLOCK_REGISTRY);
    }

    public static Stream<String> dumpBlockTags() {
//        if (supplier == null)
            return ImmutableList.<String>of().stream();

//        final TagCollection<Block> collection = supplier.getOrEmpty(Registry.BLOCK.key());
//
//        return collection.getAvailableTags().stream().map(loc -> {
//            final StringBuilder builder = new StringBuilder();
//            builder.append(loc.toString()).append(" -> ");
//            final Tag<Block> tag = collection.getTag(loc);
//            final String text;
//            if (tag == null) {
//                text = "<NULL>";
//            } else {
//                text = tag.getValues().stream().map(l -> l.getRegistryName().toString()).collect(Collectors.joining(","));
//            }
//            builder.append(text);
//            return builder.toString();
//        }).sorted();
    }
}
