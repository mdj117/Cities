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

import java.awt.Shape;

/**
 * Defines a building in the most common sense
 * @author Martin Steiger
 */
public class Building {
    private final Shape layout;
    private final int wallHeight;
    private final int baseHeight;

    /**
     * @param layout the building layout
     * @param baseHeight the height of the floor level
     * @param wallHeight the building height above the floor level
     */
    public Building(Shape layout, int baseHeight, int wallHeight) {
        this.layout = layout;
        this.baseHeight = baseHeight;
        this.wallHeight = wallHeight;
    }

    /**
     * @return the building layout
     */
    public Shape getLayout() {
        return this.layout;
    }

    /**
     * @return the building height
     */
    public int getWallHeight() {
        return this.wallHeight;
    }

    /**
     * @return the base height
     */
    public int getBaseHeight() {
        return baseHeight;
    }
    
}