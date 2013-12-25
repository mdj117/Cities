/*
 * Copyright 2013 MovingBlocks
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.terasology.cities.model;

import java.awt.Rectangle;

/**
 * A hip roof
 * @author Martin Steiger
 */
public class HipRoof extends RectangularRoof {

    private final int maxHeight;
    private final double pitch;
    
    /**
     * @param rc the roof area
     * @param baseHeight the base height of the roof
     * @param maxHeight the maximum height of the roof
     * @param pitch the roof pitch
     */
    public HipRoof(Rectangle rc, int baseHeight, double pitch, int maxHeight) {
        super(rc, baseHeight);
        
        this.maxHeight = maxHeight;
        this.pitch = pitch;
    }

    /**
     * @param rc the roof area
     * @param baseHeight the base height of the roof
     * @param pitch the roof pitch
     */
    public HipRoof(Rectangle rc, int baseHeight, double pitch) {
        this(rc, baseHeight, pitch, Integer.MAX_VALUE);
    }

    /**
     * @return the maximum height of the roof
     */
    public int getMaxHeight() {
        return this.maxHeight;
    }

    /**
     * @return the slope
     */
    public double getPitch() {
        return this.pitch;
    }

}
