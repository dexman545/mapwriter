package dex.mapwriter3;

import com.google.common.collect.Maps;
import dex.mapwriter3.config.Config;
import dex.mapwriter3.region.MwChunk;
import dex.mapwriter3.tasks.SaveChunkTask;
import dex.mapwriter3.tasks.UpdateSurfaceChunksTask;
import dex.mapwriter3.util.Utils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MinecraftClient;
import net.minecraft.tileentity.BlockEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import java.util.Arrays;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class ChunkManager {
    public Mw mw;
    private boolean closed = false;
    private CircularHashMap<WorldChunk, Integer> chunkMap = new CircularHashMap<WorldChunk, Integer>();

    private static final int VISIBLE_FLAG = 0x01;
    private static final int VIEWED_FLAG = 0x02;

    public ChunkManager(Mw mw) {
        this.mw = mw;
    }

    public synchronized void close() {
        this.closed = true;
        this.saveChunks();
        this.chunkMap.clear();
    }

    // create MwChunk from Minecraft chunk.
    // only MwChunk's should be used in the background thread.
    // make this a full copy of chunk data to prevent possible race conditions
    // <-- done
    public static MwChunk copyToMwChunk(WorldChunk chunk) {
        byte[][] lightingArray = new byte[16][];
        Map<BlockPos, BlockEntity> BlockEntityMap = Maps.newHashMap();
        BlockEntityMap = Utils.checkedMapByCopy(chunk.getBlockEntities(), BlockPos.class, BlockEntity.class, false);
        char[][] dataArray = new char[16][];

        ExtendedBlockStorage[] storageArrays = chunk.getBlockStorageArray();
        if (storageArrays != null) {
            for (ExtendedBlockStorage storage : storageArrays) {
                if (storage != null) {
                    int y = (storage.getYLocation() >> 4) & 0xf;
                    dataArray[y] = storage.getData();
                    lightingArray[y] = (storage.getBlocklightArray() != null) ? Arrays.copyOf(storage.getBlocklightArray().getData(), storage.getBlocklightArray().getData().length) : null;
                }
            }
        }

        return new MwChunk(chunk.getPos().x, chunk.getPos().z, chunk.getWorld().dimension.getType(), dataArray, lightingArray, Arrays.copyOf(chunk.getBiomeArray(), chunk.getBiomeArray().length), BlockEntityMap);
    }

    public synchronized void addChunk(WorldChunk chunk) {
        if (!this.closed && (chunk != null)) {
            this.chunkMap.put(chunk, 0);
        }
    }

    public synchronized void removeChunk(WorldChunk chunk) {
        if (!this.closed && (chunk != null)) {
            if (!this.chunkMap.containsKey(chunk)) {
                return; // FIXME: Is this failsafe enough for unloading?
            }
            int flags = this.chunkMap.get(chunk);
            if ((flags & VIEWED_FLAG) != 0) {
                this.addSaveChunkTask(chunk);
            }
            this.chunkMap.remove(chunk);
        }
    }

    public synchronized void saveChunks() {
        for (Map.Entry<WorldChunk, Integer> entry : this.chunkMap.entrySet()) {
            int flags = entry.getValue();
            if ((flags & VIEWED_FLAG) != 0) {
                this.addSaveChunkTask(entry.getKey());
            }
        }
    }

    public void updateUndergroundChunks() {
        int chunkArrayX = (this.mw.playerXInt >> 4) - 1;
        int chunkArrayZ = (this.mw.playerZInt >> 4) - 1;
        MwChunk[] chunkArray = new MwChunk[9];
        for (int z = 0; z < 3; z++) {
            for (int x = 0; x < 3; x++) {
                WorldChunk chunk = this.mw.mc.world.getChunk(chunkArrayX + x, chunkArrayZ + z);
                if (!chunk.isEmpty()) {
                    chunkArray[(z * 3) + x] = copyToMwChunk(chunk);
                }
            }
        }
    }

    public void updateSurfaceChunks() {
        int chunksToUpdate = Math.min(this.chunkMap.size(), Config.chunksPerTick);
        MwChunk[] chunkArray = new MwChunk[chunksToUpdate];
        for (int i = 0; i < chunksToUpdate; i++) {
            Map.Entry<WorldChunk, Integer> entry = this.chunkMap.getNextEntry();
            if (entry != null) {
                // if this chunk is within a certain distance to the player then
                // add it to the viewed set
                WorldChunk chunk = entry.getKey();

                int flags = entry.getValue();
                if (Utils.distToChunkSq(this.mw.playerXInt, this.mw.playerZInt, chunk) <= Config.maxChunkSaveDistSq) {
                    flags |= (VISIBLE_FLAG | VIEWED_FLAG);
                } else {
                    flags &= ~VISIBLE_FLAG;
                }
                entry.setValue(flags);

                if ((flags & VISIBLE_FLAG) != 0) {
                    chunkArray[i] = copyToMwChunk(chunk);
                    this.mw.executor.addTask(new UpdateSurfaceChunksTask(this.mw, chunkArray[i]));
                } else {
                    chunkArray[i] = null;
                }
            }
        }

        // this.mw.executor.addTask(new UpdateSurfaceChunksTask(this.mw,
        // chunkArray));
    }

    public void onTick() {
        if (!this.closed) {
            if ((this.mw.tickCounter & 0xf) == 0) {
                this.updateUndergroundChunks();
            } else {
                this.updateSurfaceChunks();
            }
        }
    }

    private void addSaveChunkTask(WorldChunk chunk) {
        if ((MinecraftClient.getInstance().isInSingleplayer() && Config.regionFileOutputEnabledMP) || (!MinecraftClient.getInstance().isInSingleplayer() && Config.regionFileOutputEnabledSP)) {
            if (!chunk.isEmpty()) {
                this.mw.executor.addTask(new SaveChunkTask(copyToMwChunk(chunk), this.mw.regionManager));
            }
        }
    }
}