package dex.mapwriter3.tasks;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import dex.mapwriter3.Mw;
import dex.mapwriter3.map.MapTexture;
import dex.mapwriter3.region.MwChunk;
import dex.mapwriter3.region.RegionManager;
import net.minecraft.world.ChunkCoordIntPair;

public class UpdateSurfaceChunksTask extends Task
{
	private MwChunk chunk;
	private RegionManager regionManager;
	private MapTexture mapTexture;
	private AtomicBoolean Running = new AtomicBoolean();
	private static Map chunksUpdating = new HashMap<Long, UpdateSurfaceChunksTask>();

	public UpdateSurfaceChunksTask(Mw mw, MwChunk chunk)
	{
		this.mapTexture = mw.mapTexture;
		this.regionManager = mw.regionManager;
		this.chunk = chunk;
	}

	@Override
	public void run()
	{
		this.Running.set(true);
		if (this.chunk != null)
		{
			// update the chunk in the region pixels
			this.regionManager.updateChunk(this.chunk);
			// copy updated region pixels to maptexture
			this.mapTexture.updateArea(this.regionManager, this.chunk.x << 4, this.chunk.z << 4, MwChunk.SIZE, MwChunk.SIZE, this.chunk.dimension);
		}
	}

	@Override
	public void onComplete()
	{
		Long coords = this.chunk.getCoordIntPair();
		UpdateSurfaceChunksTask.chunksUpdating.remove(coords);
		this.Running.set(false);
	}

	public void UpdateChunkData(MwChunk chunk)
	{
		this.chunk = chunk;
	}

	@Override
	public boolean CheckForDuplicate()
	{
		Long coords = ChunkCoordIntPair.chunkXZ2Int(this.chunk.x, this.chunk.z);

		if (!UpdateSurfaceChunksTask.chunksUpdating.containsKey(coords))
		{
			UpdateSurfaceChunksTask.chunksUpdating.put(coords, this);
			return false;
		}
		else
		{
			UpdateSurfaceChunksTask task2 = (UpdateSurfaceChunksTask) UpdateSurfaceChunksTask.chunksUpdating.get(coords);
			if (task2.Running.get() == false)
			{
				task2.UpdateChunkData(this.chunk);
			}
			else
			{
				UpdateSurfaceChunksTask.chunksUpdating.put(coords, this);
				return false;
			}
		}
		return true;
	}
}
