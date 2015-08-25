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

package org.terasology.cities.roads;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.terasology.cities.sites.Settlement;
import org.terasology.cities.sites.SettlementFacet;
import org.terasology.cities.terrain.BuildableTerrainFacet;
import org.terasology.core.world.generator.facets.BiomeFacet;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.BaseVector2f;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.Vector2i;
import org.terasology.utilities.procedural.PerlinNoise;
import org.terasology.world.generation.Border3D;
import org.terasology.world.generation.Facet;
import org.terasology.world.generation.FacetBorder;
import org.terasology.world.generation.FacetProvider;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Produces;
import org.terasology.world.generation.Requires;

import com.google.common.math.DoubleMath;

/**
 *
 */
@Produces(RoadFacet.class)
@Requires({
    @Facet(value = SettlementFacet.class, border = @FacetBorder(sides = 500)),
    @Facet(BuildableTerrainFacet.class)})
public class RoadFacetProvider implements FacetProvider {

    private PerlinNoise noiseX;
    private PerlinNoise noiseY;

    @Override
    public void setSeed(long seed) {
        this.noiseX = new PerlinNoise(seed ^ 533231280);
        this.noiseY = new PerlinNoise(seed ^ 198218712);
    }

    @Override
    public void process(GeneratingRegion region) {
        BuildableTerrainFacet terrainFacet = region.getRegionFacet(BuildableTerrainFacet.class);
        SettlementFacet siteFacet = region.getRegionFacet(SettlementFacet.class);

        Border3D border = region.getBorderForFacet(BiomeFacet.class);
        RoadFacet roadFacet = new RoadFacet(region.getRegion(), border);

        int thres = siteFacet.getCertainWorldRegion().width() / 2;

        List<Road> candidates = new ArrayList<>();

        List<Settlement> siteList = new ArrayList<>(siteFacet.getSettlements());
        for (int i = 0; i < siteList.size(); i++) {
            Settlement siteA = siteList.get(i);
            Vector2i posA = siteA.getPos();
            for (int j = i + 1; j < siteList.size(); j++) {
                Settlement siteB = siteList.get(j);
                Vector2i posB = siteB.getPos();

                int distX = Math.abs(posB.getX() - posA.getX());
                int distY = Math.abs(posB.getY() - posA.getY());
                if (distX < thres && distY < thres) {
                    Optional<Road> opt = tryBuild(posA, posB, 6f, terrainFacet);
                    if (opt.isPresent()) {
                        candidates.add(opt.get());
                    }
                }
            }
        }

        for (Road road : candidates) {
            roadFacet.addRoad(road);
        }

        region.setRegionFacet(RoadFacet.class, roadFacet);
    }

    private Optional<Road> tryBuild(Vector2i posA, Vector2i posB, float width, BuildableTerrainFacet terrainFacet) {
        double length = posA.distance(posB);
        // segments should be about N blocks long
        int segLength = 48;
        int segCount = TeraMath.ceilToInt(length / segLength);  // ceil avoids division by zero for short distances

        List<Vector2i> segPoints = new ArrayList<>();

        segPoints.add(posA);
        float smoothness = 0.005f;
        for (int i = 1; i < segCount; i++) {
            Vector2i pos = lerp(posA, posB, (float)i / segCount, RoundingMode.HALF_UP);

            // first and last point receive only half the noise distortion to smoothen the end points
            float applyFactor = (i == 1 || i == segCount - 1) ? 0.5f : 1f;
            pos.x += noiseX.noise(pos.x * smoothness, 0, pos.y * smoothness) * segLength * applyFactor;
            pos.y += noiseY.noise(pos.x * smoothness, 0, pos.y * smoothness) * segLength * applyFactor;

            if (!terrainFacet.isPassable(pos)) {
                // TODO: consider creating a bridge segment instead if (water-passable)
                return Optional.empty();
            }

            segPoints.add(pos);
        }
        segPoints.add(posB);

        return Optional.of(new Road(segPoints, width));
    }

    private static Vector2i lerp(BaseVector2i a, BaseVector2i b, double t, RoundingMode mode) {
        double x = (1 - t) * a.getX() + t * b.getX();
        double y = (1 - t) * a.getY() + t * b.getY();
        return new Vector2i(DoubleMath.roundToInt(x, mode), DoubleMath.roundToInt(y, mode));
    }
}