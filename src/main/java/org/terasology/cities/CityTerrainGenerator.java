/*
 * Copyright 2013 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.cities;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.terasology.cities.common.Orientation;
import org.terasology.cities.heightmap.HeightMap;
import org.terasology.cities.heightmap.HeightMaps;
import org.terasology.cities.model.City;
import org.terasology.cities.model.Road;
import org.terasology.cities.model.Sector;
import org.terasology.cities.model.Sectors;
import org.terasology.cities.raster.Brush;
import org.terasology.cities.raster.ChunkBrush;
import org.terasology.cities.raster.RasterRegistry;
import org.terasology.cities.raster.TerrainInfo;
import org.terasology.cities.raster.standard.RoadRasterizer;
import org.terasology.cities.raster.standard.StandardRegistry;
import org.terasology.world.WorldBiomeProvider;
import org.terasology.world.chunks.Chunk;
import org.terasology.world.generator.FirstPassGenerator;

import com.google.common.collect.Sets;

/**
 * Generates roads and settlements on top of a given terrain
 * @author Martin Steiger
 */
public class CityTerrainGenerator implements FirstPassGenerator {

    private final HeightMap heightMap;

    private final BlockTheme theme = new BlockTheme();
    private WorldFacade facade;

    private final CityWorldConfig config;
    
    // private WorldBiomeProvider worldBiomeProvider;

    /**
     * @param heightMap the height map to use
     * @param config the world config
     */
    public CityTerrainGenerator(HeightMap heightMap, CityWorldConfig config) {
        this.heightMap = heightMap;
        this.config = config;

        theme.register(BlockTypes.ROAD_SURFACE, "core:Gravel");
        theme.register(BlockTypes.LOT_EMPTY, "core:dirt");
        theme.register(BlockTypes.BUILDING_WALL, "Cities:stonawall1");
        theme.register(BlockTypes.BUILDING_FLOOR, "Cities:stonawall1dark");
        theme.register(BlockTypes.BUILDING_FOUNDATION, "core:gravel");
        theme.register(BlockTypes.ROOF_FLAT, "Cities:rooftiles2");
        theme.register(BlockTypes.ROOF_HIP, "Cities:wood3");
        theme.register(BlockTypes.ROOF_SADDLE, "Cities:wood3");
        theme.register(BlockTypes.ROOF_DOME, "core:plank");
        theme.register(BlockTypes.ROOF_GABLE, "core:plank");

        theme.register(BlockTypes.TOWER_WALL, "Cities:stonawall1");

        // -- require Fences modules
        theme.registerFamily(BlockTypes.FENCE, "Fences:Fence");
        // there is no fence gate :-(
        theme.registerFamily(BlockTypes.FENCE_GATE, "Engine:Air");
    }

    @Override
    public void setWorldSeed(String worldSeed) {

        facade = new WorldFacade(worldSeed, heightMap, config);
        
    }

    @Override
    public void setWorldBiomeProvider(WorldBiomeProvider worldBiomeProvider) {
//        this.worldBiomeProvider = worldBiomeProvider;
    }

    /**
     * Not sure what this method does - it does not seem to be used though
     */
    @Override
    public Map<String, String> getInitParameters() {
        return Collections.emptyMap();
    }

    /**
     * Not sure what this method does - it does not seem to be used though
     */
    @Override
    public void setInitParameters(Map<String, String> initParameters) {
        // ignore
    }

    @Override
    public void generateChunk(Chunk chunk) {
        int wx = chunk.getBlockWorldPosX(0);
        int wz = chunk.getBlockWorldPosZ(0);

        Sector sector = Sectors.getSectorForBlock(wx, wz);

        Brush brush = new ChunkBrush(chunk, theme);

        HeightMap cachedHm = HeightMaps.caching(heightMap, brush.getAffectedArea(), 1);
        TerrainInfo ti = new TerrainInfo(cachedHm); 
        
        drawCities(sector, ti, brush);
        drawRoads(sector, ti, brush);
    }

    private void drawRoads(Sector sector, TerrainInfo ti, Brush brush) {
        Set<Road> roads = facade.getRoads(sector);

        RoadRasterizer rr = new RoadRasterizer();
        for (Road road : roads) {
            rr.raster(brush, ti, road);
        }
    }

    private void drawCities(Sector sector, TerrainInfo ti, Brush brush) {
        Set<City> cities = Sets.newHashSet(facade.getCities(sector));

        for (Orientation dir : Orientation.values()) {
            cities.addAll(facade.getCities(sector.getNeighbor(dir)));
        }
        
        RasterRegistry registry = StandardRegistry.getInstance();

        for (City city : cities) {
            registry.rasterize(brush, ti, city);
        }
    }    
}
