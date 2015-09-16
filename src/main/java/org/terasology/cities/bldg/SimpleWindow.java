/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.cities.bldg;

import org.terasology.commonworld.Orientation;
import org.terasology.math.geom.Rect2i;

/**
 * A rectangular window in a wall
 */
public class SimpleWindow implements Window {

    private final Orientation orientation;
    private final Rect2i rect;
    private final int baseHeight;
    private final int topHeight;

    /**
     * @param orientation the orientation
     * @param rect the layout shape rect
     * @param baseHeight the height at the bottom
     * @param topHeight the height at the top
     */
    public SimpleWindow(Orientation orientation, Rect2i rect, int baseHeight, int topHeight) {
        this.orientation = orientation;
        this.rect = rect;
        this.baseHeight = baseHeight;
        this.topHeight = topHeight;
    }

    /**
     * @return the orientation
     */
    public Orientation getOrientation() {
        return this.orientation;
    }

    /**
     * @return the rect
     */
    public Rect2i getRect() {
        return this.rect;
    }

    /**
     * @return the baseHeight
     */
    public int getBaseHeight() {
        return this.baseHeight;
    }

    /**
     * @return the topHeight
     */
    public int getTopHeight() {
        return this.topHeight;
    }
}