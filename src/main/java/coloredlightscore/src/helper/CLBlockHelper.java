package coloredlightscore.src.helper;

import coloredlightscore.src.api.CLApi;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHalfSlab;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class CLBlockHelper {

    public static Block getBlock(IBlockAccess iBlockAccess, int x, int y, int z) {
        return Block.blocksList[iBlockAccess.getBlockId(x, y, z)];
    }

    public static float getBlockAmbientOcclusionLightValue(IBlockAccess iBlockAccess, int x, int y, int z) {
        Block block = Block.blocksList[iBlockAccess.getBlockId(x, y, z)];

        return block != null ? block.getAmbientOcclusionLightValue(iBlockAccess, x, y, z) : 1.0f;
    }

    public static Block setLightValue(Block interceptedReturnValue, Block instance, float par1) {
        // Clamp negative values
        if (par1 < 0.0F) {
            par1 = 0.0F;
        }


        if (par1 < 1.0F) {
            // If the incoming light value is a plain white call, then "color" the light value white
            Block.lightValue[instance.blockID] = CLApi.makeRGBLightValue(par1, par1, par1);
        } else {
            // Otherwise, let whatever it is through
            Block.lightValue[instance.blockID] = (int) (15.0F * par1);
        }

        return instance;
    }

    public static int getMixedBrightnessForBlockWithColor(IBlockAccess blockAccess, int x, int y, int z) {
        int l;

        int blockID = blockAccess.getBlockId(x, y, z);

        if (blockAccess instanceof World)
            l = CLWorldHelper.getLightBrightnessForSkyBlocks((World) blockAccess, x, y, z, Block.lightValue[blockID]);
        else if (blockAccess instanceof ChunkCache)
            l = CLChunkCacheHelper.getLightBrightnessForSkyBlocks((ChunkCache) blockAccess, x, y, z, Block.lightValue[blockID]);
        else
            l = 0;

        Block block = Block.blocksList[blockID];

        if (l == 0 && block instanceof BlockHalfSlab) {
            --y;
            block = getBlock(blockAccess, x, y, z);
            if (blockAccess instanceof World)
                return CLWorldHelper.getLightBrightnessForSkyBlocks((World) blockAccess, x, y, z, Block.lightValue[blockID]);
            else if (blockAccess instanceof ChunkCache)
                return CLChunkCacheHelper.getLightBrightnessForSkyBlocks((ChunkCache) blockAccess, x, y, z, Block.lightValue[blockID]);
            else
                return 0;
        } else {
            return l;
        }
    }
}
