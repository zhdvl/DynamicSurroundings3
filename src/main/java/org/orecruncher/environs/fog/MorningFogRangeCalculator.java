/*
 *  Dynamic Surroundings
 *  Copyright (C) 2020  OreCruncher
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
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>
 */

package org.orecruncher.environs.fog;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import org.orecruncher.environs.config.Config;
import org.orecruncher.environs.handlers.CommonState;
import org.orecruncher.lib.GameUtils;
import org.orecruncher.lib.math.MathStuff;
import org.orecruncher.lib.random.XorShiftRandom;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class MorningFogRangeCalculator extends VanillaFogRangeCalculator {

    private static final float START = 0.630F;
    private static final float END = 0.830F;
    private static final float RESERVE = 10F;

    protected final FogResult cache = new FogResult();
    protected int fogDay = -1;
    protected FogType type = FogType.NORMAL;

    public MorningFogRangeCalculator() {
        super("MorningFogRangeCalculator");
    }

    @Override
    public boolean enabled() {
        return Config.CLIENT.fog.enableMorningFog.get();
    }

    @Override
    @Nonnull
    public FogResult calculate(@Nonnull final EntityViewRenderEvent.RenderFogEvent event) {
        this.cache.set(event);
        if (this.type != FogType.NONE && this.cache.getStart() > this.type.getReserve()) {
            final float ca = GameUtils.getWorld().getTimeOfDay((float) event.getPartialTicks());
            if (ca >= this.type.getStart() && ca <= this.type.getEnd()) {
                final float mid = (this.type.getStart() + this.type.getEnd()) / 2F;
                final float factor = 1F - MathStuff.abs(ca - mid) / (mid - this.type.getStart());
                final float shift = this.cache.getStart() * factor;
                final float newEnd = this.cache.getEnd() - shift;
                final float newStart = MathStuff.clamp(this.cache.getStart() - shift * 2, this.type.getReserve() + 1,
                        newEnd);
                this.cache.set(newStart, newEnd);
            }
        }
        return this.cache;
    }

    @Override
    public void tick() {
        // Determine if fog is going to be done this Minecraft day
        final int day = CommonState.getClock().getDay();
        if (this.fogDay != day) {
            final int dim = CommonState.getDimensionId();
            final int morningFogChance = Config.CLIENT.fog.morningFogChance.get();
            this.fogDay = day;
            final boolean doFog = (dim != -1 && dim != 1) && (morningFogChance == 100 || XorShiftRandom.current().nextInt(100) <= morningFogChance);
            this.type = doFog ? getFogType() : FogType.NONE;
        }
    }

    @Nonnull
    protected FogType getFogType() {
        return FogType.NORMAL;
    }

    public enum FogType {
        NONE(0F, 0F, 0F),
        NORMAL(START, END, RESERVE),
        LIGHT(START + 0.1F, END - 0.1F, RESERVE + 5F),
        MEDIUM(START - 0.1F, END + 0.1F, RESERVE),
        HEAVY(START - 0.1F, END + 0.2F, RESERVE - 5F);

        private final float start;
        private final float end;
        private final float reserve;

        FogType(final float start, final float end, final float reserve) {
            this.start = start;
            this.end = end;
            this.reserve = reserve;
        }

        public float getStart() {
            return this.start;
        }

        public float getEnd() {
            return this.end;
        }

        public float getReserve() {
            return this.reserve;
        }
    }

}
