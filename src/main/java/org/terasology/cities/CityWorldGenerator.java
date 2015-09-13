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

package org.terasology.cities;

import org.terasology.cities.blocked.BlockedAreaFacetProvider;
import org.terasology.cities.fences.FenceFacetProvider;
import org.terasology.cities.fences.SimpleFenceRasterizer;
import org.terasology.cities.lakes.LakeFacetProvider;
import org.terasology.cities.parcels.ParcelFacetProvider;
import org.terasology.cities.roads.RoadFacetProvider;
import org.terasology.cities.roads.RoadRasterizer;
import org.terasology.cities.settlements.SettlementFacetProvider;
import org.terasology.cities.sites.SiteFacetProvider;
import org.terasology.cities.surface.InfiniteSurfaceHeightFacetProvider;
import org.terasology.cities.surface.SurfaceHeightFacetProvider;
import org.terasology.cities.terrain.BuildableTerrainFacetProvider;
import org.terasology.core.world.generator.facetProviders.DefaultFloraProvider;
import org.terasology.core.world.generator.facetProviders.EnsureSpawnableChunkZeroProvider;
import org.terasology.core.world.generator.facetProviders.SeaLevelProvider;
import org.terasology.core.world.generator.facetProviders.SurfaceToDensityProvider;
import org.terasology.core.world.generator.rasterizers.FloraRasterizer;
import org.terasology.core.world.generator.rasterizers.SolidRasterizer;
import org.terasology.engine.SimpleUri;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.spawner.Spawner;
import org.terasology.math.geom.Vector3f;
import org.terasology.registry.CoreRegistry;
import org.terasology.registry.In;
import org.terasology.world.block.BlockManager;
import org.terasology.world.generation.BaseFacetedWorldGenerator;
import org.terasology.world.generation.World;
import org.terasology.world.generation.WorldBuilder;
import org.terasology.world.generator.RegisterWorldGenerator;
import org.terasology.world.generator.plugin.WorldGeneratorPluginLibrary;

@RegisterWorldGenerator(id = "city", displayName = "City World")
public class CityWorldGenerator extends BaseFacetedWorldGenerator {

    World world;

    private final Spawner spawner = new CitySpawner();

    @In
    private BlockManager blockManager;

    private BlockTheme theme;

    /**
     * @param uri the uri
     */
    public CityWorldGenerator(SimpleUri uri) {
        super(uri);
    }

    @Override
    public void initialize() {
        super.initialize();

//        noiseMap = new NoiseHeightMap();
//        heightMap = HeightMaps.symmetric(noiseMap, Symmetries.alongNegativeDiagonal());

//        register(new HeightMapTerrainGenerator(heightMap));
//        register(new BoundaryGenerator(heightMap));
//        register(new CityTerrainGenerator(heightMap));
//        register(new FloraGeneratorFast(heightMap));
    }

    @Override
    public Vector3f getSpawnPosition(EntityRef entity) {
        return spawner.getSpawnPosition(getWorld(), entity);
    }

    @Override
    protected WorldBuilder createWorld() {
        int seaLevel = 2;

        theme = BlockTheme.builder(blockManager)
            .register(BlockTypes.ROAD_FILL, "core:dirt")
            .register(BlockTypes.ROAD_SURFACE, "core:Gravel")
            .register(BlockTypes.LOT_EMPTY, "core:dirt")
            .register(BlockTypes.BUILDING_WALL, "Cities:stonawall1")
            .register(BlockTypes.BUILDING_FLOOR, "Cities:stonawall1dark")
            .register(BlockTypes.BUILDING_FOUNDATION, "core:gravel")
            .register(BlockTypes.ROOF_FLAT, "Cities:rooftiles2")
            .register(BlockTypes.ROOF_HIP, "Cities:wood3")
            .register(BlockTypes.ROOF_SADDLE, "Cities:wood3")
            .register(BlockTypes.ROOF_DOME, "core:plank")
            .register(BlockTypes.ROOF_GABLE, "core:plank")

            .register(BlockTypes.TOWER_WALL, "Cities:stonawall1")

             // -- requires Fences module
            .registerFamily(BlockTypes.FENCE, "Fences:Fence")
            .registerFamily(BlockTypes.FENCE_GATE, BlockManager.AIR_ID)  // there is no fence gate :-(
            .build();

        WorldBuilder worldBuilder = new WorldBuilder(CoreRegistry.get(WorldGeneratorPluginLibrary.class))
                .setSeaLevel(seaLevel)
                .addProvider(new SeaLevelProvider(seaLevel))
                .addProvider(new InfiniteSurfaceHeightFacetProvider())
                .addProvider(new SurfaceHeightFacetProvider())
                .addProvider(new SurfaceToDensityProvider())
                .addProvider(new BuildableTerrainFacetProvider())
                .addProvider(new BlockedAreaFacetProvider())
                .addProvider(new LakeFacetProvider())
                .addProvider(new SimpleBiomeProvider())
                .addProvider(new SiteFacetProvider())
                .addProvider(new RoadFacetProvider())
                .addProvider(new ParcelFacetProvider())
                .addProvider(new FenceFacetProvider())
                .addProvider(new SettlementFacetProvider())
                .addProvider(new DefaultFloraProvider())
                .addProvider(new EnsureSpawnableChunkZeroProvider())
                .addRasterizer(new SolidRasterizer())
                .addPlugins()
                .addEntities(new SettlementEntityProvider())
                .addRasterizer(new RoadRasterizer(theme))
                .addRasterizer(new SimpleFenceRasterizer(theme))
                .addRasterizer(new FloraRasterizer());
        return worldBuilder;
    }

}
