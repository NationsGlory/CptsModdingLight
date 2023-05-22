package coloredlightscore.network;

import coloredlightscore.server.ChunkStorageRGB;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraftforge.common.MinecraftForge;

import java.util.logging.Level;

import static coloredlightscore.src.asm.ColoredLightsCoreLoadingPlugin.CLLog;

public class PacketHandler implements IPacketHandler {

    public static void sendChunkColorData(Chunk chunk, EntityPlayerMP player) {
        try {
            ChunkColorDataPacket packet = new ChunkColorDataPacket();
            NibbleArray[] redColorArray = ChunkStorageRGB.getRedColorArrays(chunk);
            NibbleArray[] greenColorArray = ChunkStorageRGB.getGreenColorArrays(chunk);
            NibbleArray[] blueColorArray = ChunkStorageRGB.getBlueColorArrays(chunk);

            if (redColorArray == null || greenColorArray == null || blueColorArray == null) {
                return;
            }

            packet.chunkXPosition = chunk.xPosition;
            packet.chunkZPosition = chunk.zPosition;
            packet.arraySize = redColorArray.length;
            packet.yLocation = ChunkStorageRGB.getYLocationArray(chunk);
            packet.RedColorArray = redColorArray;
            packet.GreenColorArray = greenColorArray;
            packet.BlueColorArray = blueColorArray;

            //this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
            //this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
            //this.channels.get(Side.SERVER).writeOutbound(packet);		

            //Think this is right

            ByteArrayDataOutput data = ByteStreams.newDataOutput();
            packet.toBytes(data);

            PacketDispatcher.sendPacketToPlayer(PacketDispatcher.getPacket("ColoredLightsCore", data.toByteArray()), (Player) player);

            //CLLog.info("SendChunkColorData()  Sent for {}, {}", chunk.xPosition, chunk.zPosition);
        } catch (Exception e) {
            CLLog.log(Level.WARNING, "SendChunkColorData()  ", e);
        }

    }

    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
        if(FMLCommonHandler.instance().getSide().isClient()) {
            ChunkColorDataPacket chunkColorDataPacket = new ChunkColorDataPacket();
            ByteArrayDataInput data = ByteStreams.newDataInput(packet.data);
            chunkColorDataPacket.fromBytes(data);
            chunkColorDataPacket.processColorDataPacket();
        }
    }
}
