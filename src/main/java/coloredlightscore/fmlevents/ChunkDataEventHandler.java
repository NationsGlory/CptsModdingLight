package coloredlightscore.fmlevents;

import coloredlightscore.server.ChunkStorageRGB;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;

import java.util.logging.Level;

import static coloredlightscore.src.asm.ColoredLightsCoreLoadingPlugin.CLLog;

public class ChunkDataEventHandler {

    public ChunkDataEventHandler() {
    }

    @ForgeSubscribe
    public void LoadChunk(ChunkDataEvent.Load event) {
        Chunk chunk = event.getChunk();
        NBTTagCompound data = event.getData();

        if (!ChunkStorageRGB.loadColorData(chunk, data)) {
            //CLLog.warn("Failed to load color data for chunk at ({}, {})", chunk.xPosition, chunk.zPosition);
        }
    }

    @ForgeSubscribe
    public void SaveChunk(ChunkDataEvent.Save event) {
        Chunk chunk = event.getChunk();
        NBTTagCompound data = event.getData();

        if (!ChunkStorageRGB.saveColorData(chunk, data)) {
            CLLog.log(Level.WARNING, "Failed to save color data for chunk at ({}, {})", new Object[]{chunk.xPosition, chunk.zPosition});
        }
    }

    @ForgeSubscribe
    public void UnloadChunk(ChunkWatchEvent.UnWatch event) {
        //CLLog.info("UnloadChunk at ({},{}) for {}", event.chunk.chunkXPos, event.chunk.chunkZPos, event.player.getDisplayName());
    }

}
