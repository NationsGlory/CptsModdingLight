package coloredlightscore.src.helper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;

// 56-59 fps w/o renderCache

public class CLRenderBlocksHelper {

    public CLRenderBlocksHelper() {
        // Ehhhh?
    }

    public static boolean renderStandardBlockWithAmbientOcclusionPartial(RenderBlocks instance, Block block, int x, int y, int z, float r, float g, float b) {
        return renderStandardBlockWithAmbientOcclusion(instance, block, x, y, z, r, g, b);
    }

    public static boolean renderStandardBlockWithAmbientOcclusion(RenderBlocks instance, Block block, int x, int y, int z, float r, float g, float b) {
        instance.enableAO = true;
        boolean flag = false;
        float topLeftAoLightValue = 0.0F;
        float bottomLeftAoLightValue = 0.0F;
        float bottomRightAoLightValue = 0.0F;
        float topRightAoLightValue = 0.0F;
        boolean notGrassAndNotOverridden = true;
        int blockBrightness = block.getMixedBrightnessForBlock(instance.blockAccess, x, y, z);
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(0xf000f);

        if (instance.getBlockIcon(block).getIconName().equals("grass_top")) {
            // Don't tint the dirt part of grass blocks!
            notGrassAndNotOverridden = false;
        } else if (instance.hasOverrideBlockTexture()) {
            // Err... only tint the top of overridden textures?
            notGrassAndNotOverridden = false;
        }

        // Whether kitty-corner blocks are air or similar (fire, redstone, etc.)
        boolean isAirish1N;
        boolean isAirish1P;
        boolean isAirish2N;
        boolean isAirish2P;
        // Extra shading per-side to add depth
        float topColorMultiplier = 1.0f;
        float bottomColorMultiplier = 0.5f;
        float northSouthColorMultiplier = 0.8f;
        float eastWestColorMultiplier = 0.6f;

        float normalAoValue;
        int brightnessScratchValue;

        // Under side of block
        if (instance.renderAllFaces || block.shouldSideBeRendered(instance.blockAccess, x, y - 1, z, 0)) {
            if (instance.renderMinY <= 0.0D) {
                --y;
            }

            instance.aoBrightnessXYNN = block.getMixedBrightnessForBlock(instance.blockAccess, x - 1, y, z);
            instance.aoBrightnessYZNN = block.getMixedBrightnessForBlock(instance.blockAccess, x, y, z - 1);
            instance.aoBrightnessYZNP = block.getMixedBrightnessForBlock(instance.blockAccess, x, y, z + 1);
            instance.aoBrightnessXYPN = block.getMixedBrightnessForBlock(instance.blockAccess, x + 1, y, z);
            instance.aoLightValueScratchXYNN = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x - 1, y, z);
            instance.aoLightValueScratchYZNN = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x, y, z - 1);
            instance.aoLightValueScratchYZNP = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x, y, z + 1);
            instance.aoLightValueScratchXYPN = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x + 1, y, z);
            isAirish1P = Block.canBlockGrass[instance.blockAccess.getBlockId(x + 1, y, z)];
            isAirish1N = Block.canBlockGrass[instance.blockAccess.getBlockId(x - 1, y, z)];
            isAirish2P = Block.canBlockGrass[instance.blockAccess.getBlockId(x, y, z + 1)];
            isAirish2N = Block.canBlockGrass[instance.blockAccess.getBlockId(x, y, z - 1)];

            if (!isAirish2N && !isAirish1N) {
                instance.aoLightValueScratchXYZNNN = instance.aoLightValueScratchXYNN;
                instance.aoBrightnessXYZNNN = instance.aoBrightnessXYNN;
            } else {
                instance.aoLightValueScratchXYZNNN = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x - 1, y, z - 1);
                instance.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(instance.blockAccess, x - 1, y, z - 1);
            }

            if (!isAirish2P && !isAirish1N) {
                instance.aoLightValueScratchXYZNNP = instance.aoLightValueScratchXYNN;
                instance.aoBrightnessXYZNNP = instance.aoBrightnessXYNN;
            } else {
                instance.aoLightValueScratchXYZNNP = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x - 1, y, z + 1);
                instance.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(instance.blockAccess, x - 1, y, z + 1);
            }

            if (!isAirish2N && !isAirish1P) {
                instance.aoLightValueScratchXYZPNN = instance.aoLightValueScratchXYPN;
                instance.aoBrightnessXYZPNN = instance.aoBrightnessXYPN;
            } else {
                instance.aoLightValueScratchXYZPNN = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x + 1, y, z - 1);
                instance.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(instance.blockAccess, x + 1, y, z - 1);
            }

            if (!isAirish2P && !isAirish1P) {
                instance.aoLightValueScratchXYZPNP = instance.aoLightValueScratchXYPN;
                instance.aoBrightnessXYZPNP = instance.aoBrightnessXYPN;
            } else {
                instance.aoLightValueScratchXYZPNP = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x + 1, y, z + 1);
                instance.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(instance.blockAccess, x + 1, y, z + 1);
            }

            if (instance.renderMinY <= 0.0D) {
                ++y;
            }

            brightnessScratchValue = blockBrightness;

            if (instance.renderMinY <= 0.0D || !instance.blockAccess.isBlockOpaqueCube(x, y - 1, z)) {
                brightnessScratchValue = block.getMixedBrightnessForBlock(instance.blockAccess, x, y - 1, z);
            }

            normalAoValue = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x, y - 1, z);
            topLeftAoLightValue = (instance.aoLightValueScratchXYZNNP + instance.aoLightValueScratchXYNN + instance.aoLightValueScratchYZNP + normalAoValue) / 4.0F;
            topRightAoLightValue = (instance.aoLightValueScratchYZNP + normalAoValue + instance.aoLightValueScratchXYZPNP + instance.aoLightValueScratchXYPN) / 4.0F;
            bottomRightAoLightValue = (normalAoValue + instance.aoLightValueScratchYZNN + instance.aoLightValueScratchXYPN + instance.aoLightValueScratchXYZPNN) / 4.0F;
            bottomLeftAoLightValue = (instance.aoLightValueScratchXYNN + instance.aoLightValueScratchXYZNNN + normalAoValue + instance.aoLightValueScratchYZNN) / 4.0F;
            instance.brightnessTopLeft = getAoBrightness(instance.aoBrightnessXYZNNP, instance.aoBrightnessXYNN, instance.aoBrightnessYZNP, brightnessScratchValue);
            instance.brightnessTopRight = getAoBrightness(instance.aoBrightnessYZNP, instance.aoBrightnessXYZPNP, instance.aoBrightnessXYPN, brightnessScratchValue);
            instance.brightnessBottomRight = getAoBrightness(instance.aoBrightnessYZNN, instance.aoBrightnessXYPN, instance.aoBrightnessXYZPNN, brightnessScratchValue);
            instance.brightnessBottomLeft = getAoBrightness(instance.aoBrightnessXYNN, instance.aoBrightnessXYZNNN, instance.aoBrightnessYZNN, brightnessScratchValue);

            if (notGrassAndNotOverridden) {
                instance.colorRedTopLeft = instance.colorRedBottomLeft = instance.colorRedBottomRight = instance.colorRedTopRight = r * bottomColorMultiplier;
                instance.colorGreenTopLeft = instance.colorGreenBottomLeft = instance.colorGreenBottomRight = instance.colorGreenTopRight = g * bottomColorMultiplier;
                instance.colorBlueTopLeft = instance.colorBlueBottomLeft = instance.colorBlueBottomRight = instance.colorBlueTopRight = b * bottomColorMultiplier;
            } else {
                instance.colorRedTopLeft = instance.colorRedBottomLeft = instance.colorRedBottomRight = instance.colorRedTopRight = bottomColorMultiplier;
                instance.colorGreenTopLeft = instance.colorGreenBottomLeft = instance.colorGreenBottomRight = instance.colorGreenTopRight = bottomColorMultiplier;
                instance.colorBlueTopLeft = instance.colorBlueBottomLeft = instance.colorBlueBottomRight = instance.colorBlueTopRight = bottomColorMultiplier;
            }

            instance.colorRedTopLeft *= topLeftAoLightValue;
            instance.colorGreenTopLeft *= topLeftAoLightValue;
            instance.colorBlueTopLeft *= topLeftAoLightValue;
            instance.colorRedBottomLeft *= bottomLeftAoLightValue;
            instance.colorGreenBottomLeft *= bottomLeftAoLightValue;
            instance.colorBlueBottomLeft *= bottomLeftAoLightValue;
            instance.colorRedBottomRight *= bottomRightAoLightValue;
            instance.colorGreenBottomRight *= bottomRightAoLightValue;
            instance.colorBlueBottomRight *= bottomRightAoLightValue;
            instance.colorRedTopRight *= topRightAoLightValue;
            instance.colorGreenTopRight *= topRightAoLightValue;
            instance.colorBlueTopRight *= topRightAoLightValue;
            instance.renderFaceYNeg(block, (double) x, (double) y, (double) z, instance.getBlockIcon(block, instance.blockAccess, x, y, z, 0));
            flag = true;
        }

        // Top face of block
        if (instance.renderAllFaces || block.shouldSideBeRendered(instance.blockAccess, x, y + 1, z, 1)) {
            if (instance.renderMaxY >= 1.0D) {
                ++y;
            }

            instance.aoBrightnessXYNP = block.getMixedBrightnessForBlock(instance.blockAccess, x - 1, y, z);
            instance.aoBrightnessXYPP = block.getMixedBrightnessForBlock(instance.blockAccess, x + 1, y, z);
            instance.aoBrightnessYZPN = block.getMixedBrightnessForBlock(instance.blockAccess, x, y, z - 1);
            instance.aoBrightnessYZPP = block.getMixedBrightnessForBlock(instance.blockAccess, x, y, z + 1);
            instance.aoLightValueScratchXYNP = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x - 1, y, z);
            instance.aoLightValueScratchXYPP = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x + 1, y, z);
            instance.aoLightValueScratchYZPN = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x, y, z - 1);
            instance.aoLightValueScratchYZPP = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x, y, z + 1);
            isAirish1P = Block.canBlockGrass[instance.blockAccess.getBlockId(x + 1, y, z)];
            isAirish1N = Block.canBlockGrass[instance.blockAccess.getBlockId(x - 1, y, z)];
            isAirish2P = Block.canBlockGrass[instance.blockAccess.getBlockId(x, y, z + 1)];
            isAirish2N = Block.canBlockGrass[instance.blockAccess.getBlockId(x, y, z - 1)];

            if (!isAirish2N && !isAirish1N) {
                instance.aoLightValueScratchXYZNPN = instance.aoLightValueScratchXYNP;
                instance.aoBrightnessXYZNPN = instance.aoBrightnessXYNP;
            } else {
                instance.aoLightValueScratchXYZNPN = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x - 1, y, z - 1);
                instance.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(instance.blockAccess, x - 1, y, z - 1);
            }

            if (!isAirish2N && !isAirish1P) {
                instance.aoLightValueScratchXYZPPN = instance.aoLightValueScratchXYPP;
                instance.aoBrightnessXYZPPN = instance.aoBrightnessXYPP;
            } else {
                instance.aoLightValueScratchXYZPPN = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x + 1, y, z - 1);
                instance.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(instance.blockAccess, x + 1, y, z - 1);
            }

            if (!isAirish2P && !isAirish1N) {
                instance.aoLightValueScratchXYZNPP = instance.aoLightValueScratchXYNP;
                instance.aoBrightnessXYZNPP = instance.aoBrightnessXYNP;
            } else {
                instance.aoLightValueScratchXYZNPP = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x - 1, y, z + 1);
                instance.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(instance.blockAccess, x - 1, y, z + 1);
            }

            if (!isAirish2P && !isAirish1P) {
                instance.aoLightValueScratchXYZPPP = instance.aoLightValueScratchXYPP;
                instance.aoBrightnessXYZPPP = instance.aoBrightnessXYPP;
            } else {
                instance.aoLightValueScratchXYZPPP = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x + 1, y, z + 1);
                instance.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(instance.blockAccess, x + 1, y, z + 1);
            }

            if (instance.renderMaxY >= 1.0D) {
                --y;
            }

            brightnessScratchValue = blockBrightness;

            if (instance.renderMaxY >= 1.0D || !instance.blockAccess.isBlockOpaqueCube(x, y + 1, z)) {
                brightnessScratchValue = block.getMixedBrightnessForBlock(instance.blockAccess, x, y + 1, z);
            }

            normalAoValue = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x, y + 1, z);
            topRightAoLightValue = (instance.aoLightValueScratchXYZNPP + instance.aoLightValueScratchXYNP + instance.aoLightValueScratchYZPP + normalAoValue) / 4.0F;
            topLeftAoLightValue = (instance.aoLightValueScratchYZPP + normalAoValue + instance.aoLightValueScratchXYZPPP + instance.aoLightValueScratchXYPP) / 4.0F;
            bottomLeftAoLightValue = (normalAoValue + instance.aoLightValueScratchYZPN + instance.aoLightValueScratchXYPP + instance.aoLightValueScratchXYZPPN) / 4.0F;
            bottomRightAoLightValue = (instance.aoLightValueScratchXYNP + instance.aoLightValueScratchXYZNPN + normalAoValue + instance.aoLightValueScratchYZPN) / 4.0F;
            instance.brightnessTopRight = getAoBrightness(instance.aoBrightnessXYZNPP, instance.aoBrightnessXYNP, instance.aoBrightnessYZPP, brightnessScratchValue);
            instance.brightnessTopLeft = getAoBrightness(instance.aoBrightnessYZPP, instance.aoBrightnessXYZPPP, instance.aoBrightnessXYPP, brightnessScratchValue);
            instance.brightnessBottomLeft = getAoBrightness(instance.aoBrightnessYZPN, instance.aoBrightnessXYPP, instance.aoBrightnessXYZPPN, brightnessScratchValue);
            instance.brightnessBottomRight = getAoBrightness(instance.aoBrightnessXYNP, instance.aoBrightnessXYZNPN, instance.aoBrightnessYZPN, brightnessScratchValue);
            instance.colorRedTopLeft = instance.colorRedBottomLeft = instance.colorRedBottomRight = instance.colorRedTopRight = r * topColorMultiplier;
            instance.colorGreenTopLeft = instance.colorGreenBottomLeft = instance.colorGreenBottomRight = instance.colorGreenTopRight = g * topColorMultiplier;
            instance.colorBlueTopLeft = instance.colorBlueBottomLeft = instance.colorBlueBottomRight = instance.colorBlueTopRight = b * topColorMultiplier;
            instance.colorRedTopLeft *= topLeftAoLightValue;
            instance.colorGreenTopLeft *= topLeftAoLightValue;
            instance.colorBlueTopLeft *= topLeftAoLightValue;
            instance.colorRedBottomLeft *= bottomLeftAoLightValue;
            instance.colorGreenBottomLeft *= bottomLeftAoLightValue;
            instance.colorBlueBottomLeft *= bottomLeftAoLightValue;
            instance.colorRedBottomRight *= bottomRightAoLightValue;
            instance.colorGreenBottomRight *= bottomRightAoLightValue;
            instance.colorBlueBottomRight *= bottomRightAoLightValue;
            instance.colorRedTopRight *= topRightAoLightValue;
            instance.colorGreenTopRight *= topRightAoLightValue;
            instance.colorBlueTopRight *= topRightAoLightValue;
            instance.renderFaceYPos(block, (double) x, (double) y, (double) z, instance.getBlockIcon(block, instance.blockAccess, x, y, z, 1));
            flag = true;
        }

        Icon iicon;

        // North face of block
        if (instance.renderAllFaces || block.shouldSideBeRendered(instance.blockAccess, x, y, z - 1, 2)) {
            if (instance.renderMinZ <= 0.0D) {
                --z;
            }

            instance.aoLightValueScratchXZNN = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x - 1, y, z);
            instance.aoLightValueScratchYZNN = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x, y - 1, z);
            instance.aoLightValueScratchYZPN = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x, y + 1, z);
            instance.aoLightValueScratchXZPN = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x + 1, y, z);
            instance.aoBrightnessXZNN = block.getMixedBrightnessForBlock(instance.blockAccess, x - 1, y, z);
            instance.aoBrightnessYZNN = block.getMixedBrightnessForBlock(instance.blockAccess, x, y - 1, z);
            instance.aoBrightnessYZPN = block.getMixedBrightnessForBlock(instance.blockAccess, x, y + 1, z);
            instance.aoBrightnessXZPN = block.getMixedBrightnessForBlock(instance.blockAccess, x + 1, y, z);
            isAirish1P = Block.canBlockGrass[instance.blockAccess.getBlockId(x + 1, y, z)];
            isAirish1N = Block.canBlockGrass[instance.blockAccess.getBlockId(x - 1, y, z)];
            isAirish2P = Block.canBlockGrass[instance.blockAccess.getBlockId(x, y + 1, z)];
            isAirish2N = Block.canBlockGrass[instance.blockAccess.getBlockId(x, y - 1, z)];

            if (!isAirish1N && !isAirish2N) {
                instance.aoLightValueScratchXYZNNN = instance.aoLightValueScratchXZNN;
                instance.aoBrightnessXYZNNN = instance.aoBrightnessXZNN;
            } else {
                instance.aoLightValueScratchXYZNNN = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x - 1, y - 1, z);
                instance.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(instance.blockAccess, x - 1, y - 1, z);
            }

            if (!isAirish1N && !isAirish2P) {
                instance.aoLightValueScratchXYZNPN = instance.aoLightValueScratchXZNN;
                instance.aoBrightnessXYZNPN = instance.aoBrightnessXZNN;
            } else {
                instance.aoLightValueScratchXYZNPN = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x - 1, y + 1, z);
                instance.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(instance.blockAccess, x - 1, y + 1, z);
            }

            if (!isAirish1P && !isAirish2N) {
                instance.aoLightValueScratchXYZPNN = instance.aoLightValueScratchXZPN;
                instance.aoBrightnessXYZPNN = instance.aoBrightnessXZPN;
            } else {
                instance.aoLightValueScratchXYZPNN = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x + 1, y - 1, z);
                instance.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(instance.blockAccess, x + 1, y - 1, z);
            }

            if (!isAirish1P && !isAirish2P) {
                instance.aoLightValueScratchXYZPPN = instance.aoLightValueScratchXZPN;
                instance.aoBrightnessXYZPPN = instance.aoBrightnessXZPN;
            } else {
                instance.aoLightValueScratchXYZPPN = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x + 1, y + 1, z);
                instance.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(instance.blockAccess, x + 1, y + 1, z);
            }

            if (instance.renderMinZ <= 0.0D) {
                ++z;
            }

            brightnessScratchValue = blockBrightness;

            if (instance.renderMinZ <= 0.0D || !instance.blockAccess.isBlockOpaqueCube(x, y, z - 1)) {
                brightnessScratchValue = block.getMixedBrightnessForBlock(instance.blockAccess, x, y, z - 1);
            }

            normalAoValue = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x, y, z - 1);
            topLeftAoLightValue = (instance.aoLightValueScratchXZNN + instance.aoLightValueScratchXYZNPN + normalAoValue + instance.aoLightValueScratchYZPN) / 4.0F;
            bottomLeftAoLightValue = (normalAoValue + instance.aoLightValueScratchYZPN + instance.aoLightValueScratchXZPN + instance.aoLightValueScratchXYZPPN) / 4.0F;
            bottomRightAoLightValue = (instance.aoLightValueScratchYZNN + normalAoValue + instance.aoLightValueScratchXYZPNN + instance.aoLightValueScratchXZPN) / 4.0F;
            topRightAoLightValue = (instance.aoLightValueScratchXYZNNN + instance.aoLightValueScratchXZNN + instance.aoLightValueScratchYZNN + normalAoValue) / 4.0F;
            instance.brightnessTopLeft = getAoBrightness(instance.aoBrightnessXZNN, instance.aoBrightnessXYZNPN, instance.aoBrightnessYZPN, brightnessScratchValue);
            instance.brightnessBottomLeft = getAoBrightness(instance.aoBrightnessYZPN, instance.aoBrightnessXZPN, instance.aoBrightnessXYZPPN, brightnessScratchValue);
            instance.brightnessBottomRight = getAoBrightness(instance.aoBrightnessYZNN, instance.aoBrightnessXYZPNN, instance.aoBrightnessXZPN, brightnessScratchValue);
            instance.brightnessTopRight = getAoBrightness(instance.aoBrightnessXYZNNN, instance.aoBrightnessXZNN, instance.aoBrightnessYZNN, brightnessScratchValue);

            if (notGrassAndNotOverridden) {
                instance.colorRedTopLeft = instance.colorRedBottomLeft = instance.colorRedBottomRight = instance.colorRedTopRight = r * northSouthColorMultiplier;
                instance.colorGreenTopLeft = instance.colorGreenBottomLeft = instance.colorGreenBottomRight = instance.colorGreenTopRight = g * northSouthColorMultiplier;
                instance.colorBlueTopLeft = instance.colorBlueBottomLeft = instance.colorBlueBottomRight = instance.colorBlueTopRight = b * northSouthColorMultiplier;
            } else {
                instance.colorRedTopLeft = instance.colorRedBottomLeft = instance.colorRedBottomRight = instance.colorRedTopRight = northSouthColorMultiplier;
                instance.colorGreenTopLeft = instance.colorGreenBottomLeft = instance.colorGreenBottomRight = instance.colorGreenTopRight = northSouthColorMultiplier;
                instance.colorBlueTopLeft = instance.colorBlueBottomLeft = instance.colorBlueBottomRight = instance.colorBlueTopRight = northSouthColorMultiplier;
            }

            instance.colorRedTopLeft *= topLeftAoLightValue;
            instance.colorGreenTopLeft *= topLeftAoLightValue;
            instance.colorBlueTopLeft *= topLeftAoLightValue;
            instance.colorRedBottomLeft *= bottomLeftAoLightValue;
            instance.colorGreenBottomLeft *= bottomLeftAoLightValue;
            instance.colorBlueBottomLeft *= bottomLeftAoLightValue;
            instance.colorRedBottomRight *= bottomRightAoLightValue;
            instance.colorGreenBottomRight *= bottomRightAoLightValue;
            instance.colorBlueBottomRight *= bottomRightAoLightValue;
            instance.colorRedTopRight *= topRightAoLightValue;
            instance.colorGreenTopRight *= topRightAoLightValue;
            instance.colorBlueTopRight *= topRightAoLightValue;
            iicon = instance.getBlockIcon(block, instance.blockAccess, x, y, z, 2);
            instance.renderFaceZNeg(block, (double) x, (double) y, (double) z, iicon);

            if (RenderBlocks.fancyGrass && iicon.getIconName().equals("grass_side") && !instance.hasOverrideBlockTexture()) {
                instance.colorRedTopLeft *= r;
                instance.colorRedBottomLeft *= r;
                instance.colorRedBottomRight *= r;
                instance.colorRedTopRight *= r;
                instance.colorGreenTopLeft *= g;
                instance.colorGreenBottomLeft *= g;
                instance.colorGreenBottomRight *= g;
                instance.colorGreenTopRight *= g;
                instance.colorBlueTopLeft *= b;
                instance.colorBlueBottomLeft *= b;
                instance.colorBlueBottomRight *= b;
                instance.colorBlueTopRight *= b;
                instance.renderFaceZNeg(block, (double) x, (double) y, (double) z, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        // South face of block
        if (instance.renderAllFaces || block.shouldSideBeRendered(instance.blockAccess, x, y, z + 1, 3)) {
            if (instance.renderMaxZ >= 1.0D) {
                ++z;
            }

            instance.aoLightValueScratchXZNP = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x - 1, y, z);
            instance.aoLightValueScratchXZPP = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x + 1, y, z);
            instance.aoLightValueScratchYZNP = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x, y - 1, z);
            instance.aoLightValueScratchYZPP = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x, y + 1, z);
            instance.aoBrightnessXZNP = block.getMixedBrightnessForBlock(instance.blockAccess, x - 1, y, z);
            instance.aoBrightnessXZPP = block.getMixedBrightnessForBlock(instance.blockAccess, x + 1, y, z);
            instance.aoBrightnessYZNP = block.getMixedBrightnessForBlock(instance.blockAccess, x, y - 1, z);
            instance.aoBrightnessYZPP = block.getMixedBrightnessForBlock(instance.blockAccess, x, y + 1, z);
            isAirish1P = Block.canBlockGrass[instance.blockAccess.getBlockId(x + 1, y, z)];
            isAirish1N = Block.canBlockGrass[instance.blockAccess.getBlockId(x - 1, y, z)];
            isAirish2P = Block.canBlockGrass[instance.blockAccess.getBlockId(x, y + 1, z)];
            isAirish2N = Block.canBlockGrass[instance.blockAccess.getBlockId(x, y - 1, z)];

            if (!isAirish1N && !isAirish2N) {
                instance.aoLightValueScratchXYZNNP = instance.aoLightValueScratchXZNP;
                instance.aoBrightnessXYZNNP = instance.aoBrightnessXZNP;
            } else {
                instance.aoLightValueScratchXYZNNP = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x - 1, y - 1, z);
                instance.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(instance.blockAccess, x - 1, y - 1, z);
            }

            if (!isAirish1N && !isAirish2P) {
                instance.aoLightValueScratchXYZNPP = instance.aoLightValueScratchXZNP;
                instance.aoBrightnessXYZNPP = instance.aoBrightnessXZNP;
            } else {
                instance.aoLightValueScratchXYZNPP = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x - 1, y + 1, z);
                instance.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(instance.blockAccess, x - 1, y + 1, z);
            }

            if (!isAirish1P && !isAirish2N) {
                instance.aoLightValueScratchXYZPNP = instance.aoLightValueScratchXZPP;
                instance.aoBrightnessXYZPNP = instance.aoBrightnessXZPP;
            } else {
                instance.aoLightValueScratchXYZPNP = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x + 1, y - 1, z);
                instance.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(instance.blockAccess, x + 1, y - 1, z);
            }

            if (!isAirish1P && !isAirish2P) {
                instance.aoLightValueScratchXYZPPP = instance.aoLightValueScratchXZPP;
                instance.aoBrightnessXYZPPP = instance.aoBrightnessXZPP;
            } else {
                instance.aoLightValueScratchXYZPPP = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x + 1, y + 1, z);
                instance.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(instance.blockAccess, x + 1, y + 1, z);
            }

            if (instance.renderMaxZ >= 1.0D) {
                --z;
            }

            brightnessScratchValue = blockBrightness;

            if (instance.renderMaxZ >= 1.0D || !instance.blockAccess.isBlockOpaqueCube(x, y, z + 1)) {
                brightnessScratchValue = block.getMixedBrightnessForBlock(instance.blockAccess, x, y, z + 1);
            }

            normalAoValue = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x, y, z + 1);
            topLeftAoLightValue = (instance.aoLightValueScratchXZNP + instance.aoLightValueScratchXYZNPP + normalAoValue + instance.aoLightValueScratchYZPP) / 4.0F;
            topRightAoLightValue = (normalAoValue + instance.aoLightValueScratchYZPP + instance.aoLightValueScratchXZPP + instance.aoLightValueScratchXYZPPP) / 4.0F;
            bottomRightAoLightValue = (instance.aoLightValueScratchYZNP + normalAoValue + instance.aoLightValueScratchXYZPNP + instance.aoLightValueScratchXZPP) / 4.0F;
            bottomLeftAoLightValue = (instance.aoLightValueScratchXYZNNP + instance.aoLightValueScratchXZNP + instance.aoLightValueScratchYZNP + normalAoValue) / 4.0F;
            instance.brightnessTopLeft = getAoBrightness(instance.aoBrightnessXZNP, instance.aoBrightnessXYZNPP, instance.aoBrightnessYZPP, brightnessScratchValue);
            instance.brightnessTopRight = getAoBrightness(instance.aoBrightnessYZPP, instance.aoBrightnessXZPP, instance.aoBrightnessXYZPPP, brightnessScratchValue);
            instance.brightnessBottomRight = getAoBrightness(instance.aoBrightnessYZNP, instance.aoBrightnessXYZPNP, instance.aoBrightnessXZPP, brightnessScratchValue);
            instance.brightnessBottomLeft = getAoBrightness(instance.aoBrightnessXYZNNP, instance.aoBrightnessXZNP, instance.aoBrightnessYZNP, brightnessScratchValue);

            if (notGrassAndNotOverridden) {
                instance.colorRedTopLeft = instance.colorRedBottomLeft = instance.colorRedBottomRight = instance.colorRedTopRight = r * northSouthColorMultiplier;
                instance.colorGreenTopLeft = instance.colorGreenBottomLeft = instance.colorGreenBottomRight = instance.colorGreenTopRight = g * northSouthColorMultiplier;
                instance.colorBlueTopLeft = instance.colorBlueBottomLeft = instance.colorBlueBottomRight = instance.colorBlueTopRight = b * northSouthColorMultiplier;
            } else {
                instance.colorRedTopLeft = instance.colorRedBottomLeft = instance.colorRedBottomRight = instance.colorRedTopRight = northSouthColorMultiplier;
                instance.colorGreenTopLeft = instance.colorGreenBottomLeft = instance.colorGreenBottomRight = instance.colorGreenTopRight = northSouthColorMultiplier;
                instance.colorBlueTopLeft = instance.colorBlueBottomLeft = instance.colorBlueBottomRight = instance.colorBlueTopRight = northSouthColorMultiplier;
            }

            instance.colorRedTopLeft *= topLeftAoLightValue;
            instance.colorGreenTopLeft *= topLeftAoLightValue;
            instance.colorBlueTopLeft *= topLeftAoLightValue;
            instance.colorRedBottomLeft *= bottomLeftAoLightValue;
            instance.colorGreenBottomLeft *= bottomLeftAoLightValue;
            instance.colorBlueBottomLeft *= bottomLeftAoLightValue;
            instance.colorRedBottomRight *= bottomRightAoLightValue;
            instance.colorGreenBottomRight *= bottomRightAoLightValue;
            instance.colorBlueBottomRight *= bottomRightAoLightValue;
            instance.colorRedTopRight *= topRightAoLightValue;
            instance.colorGreenTopRight *= topRightAoLightValue;
            instance.colorBlueTopRight *= topRightAoLightValue;
            iicon = instance.getBlockIcon(block, instance.blockAccess, x, y, z, 3);
            instance.renderFaceZPos(block, (double) x, (double) y, (double) z, instance.getBlockIcon(block, instance.blockAccess, x, y, z, 3));

            if (RenderBlocks.fancyGrass && iicon.getIconName().equals("grass_side") && !instance.hasOverrideBlockTexture()) {
                instance.colorRedTopLeft *= r;
                instance.colorRedBottomLeft *= r;
                instance.colorRedBottomRight *= r;
                instance.colorRedTopRight *= r;
                instance.colorGreenTopLeft *= g;
                instance.colorGreenBottomLeft *= g;
                instance.colorGreenBottomRight *= g;
                instance.colorGreenTopRight *= g;
                instance.colorBlueTopLeft *= b;
                instance.colorBlueBottomLeft *= b;
                instance.colorBlueBottomRight *= b;
                instance.colorBlueTopRight *= b;
                instance.renderFaceZPos(block, (double) x, (double) y, (double) z, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        // West face of block
        if (instance.renderAllFaces || block.shouldSideBeRendered(instance.blockAccess, x - 1, y, z, 4)) {
            if (instance.renderMinX <= 0.0D) {
                --x;
            }

            instance.aoLightValueScratchXYNN = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x, y - 1, z);
            instance.aoLightValueScratchXZNN = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x, y, z - 1);
            instance.aoLightValueScratchXZNP = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x, y, z + 1);
            instance.aoLightValueScratchXYNP = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x, y + 1, z);
            instance.aoBrightnessXYNN = block.getMixedBrightnessForBlock(instance.blockAccess, x, y - 1, z);
            instance.aoBrightnessXZNN = block.getMixedBrightnessForBlock(instance.blockAccess, x, y, z - 1);
            instance.aoBrightnessXZNP = block.getMixedBrightnessForBlock(instance.blockAccess, x, y, z + 1);
            instance.aoBrightnessXYNP = block.getMixedBrightnessForBlock(instance.blockAccess, x, y + 1, z);
            isAirish1P = Block.canBlockGrass[instance.blockAccess.getBlockId(x, y + 1, z)];
            isAirish1N = Block.canBlockGrass[instance.blockAccess.getBlockId(x, y - 1, z)];
            isAirish2P = Block.canBlockGrass[instance.blockAccess.getBlockId(x, y, z - 1)];
            isAirish2N = Block.canBlockGrass[instance.blockAccess.getBlockId(x, y, z + 1)];

            if (!isAirish2P && !isAirish1N) {
                instance.aoLightValueScratchXYZNNN = instance.aoLightValueScratchXZNN;
                instance.aoBrightnessXYZNNN = instance.aoBrightnessXZNN;
            } else {
                instance.aoLightValueScratchXYZNNN = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x, y - 1, z - 1);
                instance.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(instance.blockAccess, x, y - 1, z - 1);
            }

            if (!isAirish2N && !isAirish1N) {
                instance.aoLightValueScratchXYZNNP = instance.aoLightValueScratchXZNP;
                instance.aoBrightnessXYZNNP = instance.aoBrightnessXZNP;
            } else {
                instance.aoLightValueScratchXYZNNP = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x, y - 1, z + 1);
                instance.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(instance.blockAccess, x, y - 1, z + 1);
            }

            if (!isAirish2P && !isAirish1P) {
                instance.aoLightValueScratchXYZNPN = instance.aoLightValueScratchXZNN;
                instance.aoBrightnessXYZNPN = instance.aoBrightnessXZNN;
            } else {
                instance.aoLightValueScratchXYZNPN = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x, y + 1, z - 1);
                instance.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(instance.blockAccess, x, y + 1, z - 1);
            }

            if (!isAirish2N && !isAirish1P) {
                instance.aoLightValueScratchXYZNPP = instance.aoLightValueScratchXZNP;
                instance.aoBrightnessXYZNPP = instance.aoBrightnessXZNP;
            } else {
                instance.aoLightValueScratchXYZNPP = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x, y + 1, z + 1);
                instance.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(instance.blockAccess, x, y + 1, z + 1);
            }

            if (instance.renderMinX <= 0.0D) {
                ++x;
            }

            brightnessScratchValue = blockBrightness;

            if (instance.renderMinX <= 0.0D || !instance.blockAccess.isBlockOpaqueCube(x - 1, y, z)) {
                brightnessScratchValue = block.getMixedBrightnessForBlock(instance.blockAccess, x - 1, y, z);
            }

            normalAoValue = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x - 1, y, z);
            topRightAoLightValue = (instance.aoLightValueScratchXYNN + instance.aoLightValueScratchXYZNNP + normalAoValue + instance.aoLightValueScratchXZNP) / 4.0F;
            topLeftAoLightValue = (normalAoValue + instance.aoLightValueScratchXZNP + instance.aoLightValueScratchXYNP + instance.aoLightValueScratchXYZNPP) / 4.0F;
            bottomLeftAoLightValue = (instance.aoLightValueScratchXZNN + normalAoValue + instance.aoLightValueScratchXYZNPN + instance.aoLightValueScratchXYNP) / 4.0F;
            bottomRightAoLightValue = (instance.aoLightValueScratchXYZNNN + instance.aoLightValueScratchXYNN + instance.aoLightValueScratchXZNN + normalAoValue) / 4.0F;
            instance.brightnessTopRight = getAoBrightness(instance.aoBrightnessXYNN, instance.aoBrightnessXYZNNP, instance.aoBrightnessXZNP, brightnessScratchValue);
            instance.brightnessTopLeft = getAoBrightness(instance.aoBrightnessXZNP, instance.aoBrightnessXYNP, instance.aoBrightnessXYZNPP, brightnessScratchValue);
            instance.brightnessBottomLeft = getAoBrightness(instance.aoBrightnessXZNN, instance.aoBrightnessXYZNPN, instance.aoBrightnessXYNP, brightnessScratchValue);
            instance.brightnessBottomRight = getAoBrightness(instance.aoBrightnessXYZNNN, instance.aoBrightnessXYNN, instance.aoBrightnessXZNN, brightnessScratchValue);

            if (notGrassAndNotOverridden) {
                instance.colorRedTopLeft = instance.colorRedBottomLeft = instance.colorRedBottomRight = instance.colorRedTopRight = r * eastWestColorMultiplier;
                instance.colorGreenTopLeft = instance.colorGreenBottomLeft = instance.colorGreenBottomRight = instance.colorGreenTopRight = g * eastWestColorMultiplier;
                instance.colorBlueTopLeft = instance.colorBlueBottomLeft = instance.colorBlueBottomRight = instance.colorBlueTopRight = b * eastWestColorMultiplier;
            } else {
                instance.colorRedTopLeft = instance.colorRedBottomLeft = instance.colorRedBottomRight = instance.colorRedTopRight = eastWestColorMultiplier;
                instance.colorGreenTopLeft = instance.colorGreenBottomLeft = instance.colorGreenBottomRight = instance.colorGreenTopRight = eastWestColorMultiplier;
                instance.colorBlueTopLeft = instance.colorBlueBottomLeft = instance.colorBlueBottomRight = instance.colorBlueTopRight = eastWestColorMultiplier;
            }

            instance.colorRedTopLeft *= topLeftAoLightValue;
            instance.colorGreenTopLeft *= topLeftAoLightValue;
            instance.colorBlueTopLeft *= topLeftAoLightValue;
            instance.colorRedBottomLeft *= bottomLeftAoLightValue;
            instance.colorGreenBottomLeft *= bottomLeftAoLightValue;
            instance.colorBlueBottomLeft *= bottomLeftAoLightValue;
            instance.colorRedBottomRight *= bottomRightAoLightValue;
            instance.colorGreenBottomRight *= bottomRightAoLightValue;
            instance.colorBlueBottomRight *= bottomRightAoLightValue;
            instance.colorRedTopRight *= topRightAoLightValue;
            instance.colorGreenTopRight *= topRightAoLightValue;
            instance.colorBlueTopRight *= topRightAoLightValue;
            iicon = instance.getBlockIcon(block, instance.blockAccess, x, y, z, 4);
            instance.renderFaceXNeg(block, (double) x, (double) y, (double) z, iicon);

            if (RenderBlocks.fancyGrass && iicon.getIconName().equals("grass_side") && !instance.hasOverrideBlockTexture()) {
                instance.colorRedTopLeft *= r;
                instance.colorRedBottomLeft *= r;
                instance.colorRedBottomRight *= r;
                instance.colorRedTopRight *= r;
                instance.colorGreenTopLeft *= g;
                instance.colorGreenBottomLeft *= g;
                instance.colorGreenBottomRight *= g;
                instance.colorGreenTopRight *= g;
                instance.colorBlueTopLeft *= b;
                instance.colorBlueBottomLeft *= b;
                instance.colorBlueBottomRight *= b;
                instance.colorBlueTopRight *= b;
                instance.renderFaceXNeg(block, (double) x, (double) y, (double) z, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        // East face of block
        if (instance.renderAllFaces || block.shouldSideBeRendered(instance.blockAccess, x + 1, y, z, 5)) {
            if (instance.renderMaxX >= 1.0D) {
                ++x;
            }

            instance.aoLightValueScratchXYPN = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x, y - 1, z);
            instance.aoLightValueScratchXZPN = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x, y, z - 1);
            instance.aoLightValueScratchXZPP = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x, y, z + 1);
            instance.aoLightValueScratchXYPP = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x, y + 1, z);
            instance.aoBrightnessXYPN = block.getMixedBrightnessForBlock(instance.blockAccess, x, y - 1, z);
            instance.aoBrightnessXZPN = block.getMixedBrightnessForBlock(instance.blockAccess, x, y, z - 1);
            instance.aoBrightnessXZPP = block.getMixedBrightnessForBlock(instance.blockAccess, x, y, z + 1);
            instance.aoBrightnessXYPP = block.getMixedBrightnessForBlock(instance.blockAccess, x, y + 1, z);
            isAirish1P = Block.canBlockGrass[instance.blockAccess.getBlockId(x, y + 1, z)];
            isAirish1N = Block.canBlockGrass[instance.blockAccess.getBlockId(x, y - 1, z)];
            isAirish2P = Block.canBlockGrass[instance.blockAccess.getBlockId(x, y, z + 1)];
            isAirish2N = Block.canBlockGrass[instance.blockAccess.getBlockId(x, y, z - 1)];

            if (!isAirish1N && !isAirish2N) {
                instance.aoLightValueScratchXYZPNN = instance.aoLightValueScratchXZPN;
                instance.aoBrightnessXYZPNN = instance.aoBrightnessXZPN;
            } else {
                instance.aoLightValueScratchXYZPNN = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x, y - 1, z - 1);
                instance.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(instance.blockAccess, x, y - 1, z - 1);
            }

            if (!isAirish1N && !isAirish2P) {
                instance.aoLightValueScratchXYZPNP = instance.aoLightValueScratchXZPP;
                instance.aoBrightnessXYZPNP = instance.aoBrightnessXZPP;
            } else {
                instance.aoLightValueScratchXYZPNP = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x, y - 1, z + 1);
                instance.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(instance.blockAccess, x, y - 1, z + 1);
            }

            if (!isAirish1P && !isAirish2N) {
                instance.aoLightValueScratchXYZPPN = instance.aoLightValueScratchXZPN;
                instance.aoBrightnessXYZPPN = instance.aoBrightnessXZPN;
            } else {
                instance.aoLightValueScratchXYZPPN = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x, y + 1, z - 1);
                instance.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(instance.blockAccess, x, y + 1, z - 1);
            }

            if (!isAirish1P && !isAirish2P) {
                instance.aoLightValueScratchXYZPPP = instance.aoLightValueScratchXZPP;
                instance.aoBrightnessXYZPPP = instance.aoBrightnessXZPP;
            } else {
                instance.aoLightValueScratchXYZPPP = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x, y + 1, z + 1);
                instance.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(instance.blockAccess, x, y + 1, z + 1);
            }

            if (instance.renderMaxX >= 1.0D) {
                --x;
            }

            brightnessScratchValue = blockBrightness;

            if (instance.renderMaxX >= 1.0D || !instance.blockAccess.isBlockOpaqueCube(x + 1, y, z)) {
                brightnessScratchValue = block.getMixedBrightnessForBlock(instance.blockAccess, x + 1, y, z);
            }

            normalAoValue = CLBlockHelper.getBlockAmbientOcclusionLightValue(instance.blockAccess,x + 1, y, z);
            topLeftAoLightValue = (instance.aoLightValueScratchXYPN + instance.aoLightValueScratchXYZPNP + normalAoValue + instance.aoLightValueScratchXZPP) / 4.0F;
            bottomLeftAoLightValue = (instance.aoLightValueScratchXYZPNN + instance.aoLightValueScratchXYPN + instance.aoLightValueScratchXZPN + normalAoValue) / 4.0F;
            bottomRightAoLightValue = (instance.aoLightValueScratchXZPN + normalAoValue + instance.aoLightValueScratchXYZPPN + instance.aoLightValueScratchXYPP) / 4.0F;
            topRightAoLightValue = (normalAoValue + instance.aoLightValueScratchXZPP + instance.aoLightValueScratchXYPP + instance.aoLightValueScratchXYZPPP) / 4.0F;
            instance.brightnessTopLeft = getAoBrightness(instance.aoBrightnessXYPN, instance.aoBrightnessXYZPNP, instance.aoBrightnessXZPP, brightnessScratchValue);
            instance.brightnessTopRight = getAoBrightness(instance.aoBrightnessXZPP, instance.aoBrightnessXYPP, instance.aoBrightnessXYZPPP, brightnessScratchValue);
            instance.brightnessBottomRight = getAoBrightness(instance.aoBrightnessXZPN, instance.aoBrightnessXYZPPN, instance.aoBrightnessXYPP, brightnessScratchValue);
            instance.brightnessBottomLeft = getAoBrightness(instance.aoBrightnessXYZPNN, instance.aoBrightnessXYPN, instance.aoBrightnessXZPN, brightnessScratchValue);

            if (notGrassAndNotOverridden) {
                instance.colorRedTopLeft = instance.colorRedBottomLeft = instance.colorRedBottomRight = instance.colorRedTopRight = r * eastWestColorMultiplier;
                instance.colorGreenTopLeft = instance.colorGreenBottomLeft = instance.colorGreenBottomRight = instance.colorGreenTopRight = g * eastWestColorMultiplier;
                instance.colorBlueTopLeft = instance.colorBlueBottomLeft = instance.colorBlueBottomRight = instance.colorBlueTopRight = b * eastWestColorMultiplier;
            } else {
                instance.colorRedTopLeft = instance.colorRedBottomLeft = instance.colorRedBottomRight = instance.colorRedTopRight = eastWestColorMultiplier;
                instance.colorGreenTopLeft = instance.colorGreenBottomLeft = instance.colorGreenBottomRight = instance.colorGreenTopRight = eastWestColorMultiplier;
                instance.colorBlueTopLeft = instance.colorBlueBottomLeft = instance.colorBlueBottomRight = instance.colorBlueTopRight = eastWestColorMultiplier;
            }

            instance.colorRedTopLeft *= topLeftAoLightValue;
            instance.colorGreenTopLeft *= topLeftAoLightValue;
            instance.colorBlueTopLeft *= topLeftAoLightValue;
            instance.colorRedBottomLeft *= bottomLeftAoLightValue;
            instance.colorGreenBottomLeft *= bottomLeftAoLightValue;
            instance.colorBlueBottomLeft *= bottomLeftAoLightValue;
            instance.colorRedBottomRight *= bottomRightAoLightValue;
            instance.colorGreenBottomRight *= bottomRightAoLightValue;
            instance.colorBlueBottomRight *= bottomRightAoLightValue;
            instance.colorRedTopRight *= topRightAoLightValue;
            instance.colorGreenTopRight *= topRightAoLightValue;
            instance.colorBlueTopRight *= topRightAoLightValue;
            iicon = instance.getBlockIcon(block, instance.blockAccess, x, y, z, 5);
            instance.renderFaceXPos(block, (double) x, (double) y, (double) z, iicon);

            if (RenderBlocks.fancyGrass && iicon.getIconName().equals("grass_side") && !instance.hasOverrideBlockTexture()) {
                instance.colorRedTopLeft *= r;
                instance.colorRedBottomLeft *= r;
                instance.colorRedBottomRight *= r;
                instance.colorRedTopRight *= r;
                instance.colorGreenTopLeft *= g;
                instance.colorGreenBottomLeft *= g;
                instance.colorGreenBottomRight *= g;
                instance.colorGreenTopRight *= g;
                instance.colorBlueTopLeft *= b;
                instance.colorBlueBottomLeft *= b;
                instance.colorBlueBottomRight *= b;
                instance.colorBlueTopRight *= b;
                instance.renderFaceXPos(block, (double) x, (double) y, (double) z, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        instance.enableAO = false;
        return flag;
    }

    /**
     * Renders a standard cube block at the given coordinates, with a given color ratio.  Args: block, x, y, z, r, g, b
     * <p>
     * Accepts and tints blocks according to their colored light value
     * CptSpaceToaster
     * <p>
     * 03-05-2014 heaton84 - Ported to helper method, refactored to match 1.7.2 architecture
     */
    public static boolean renderStandardBlockWithColorMultiplier(RenderBlocks instance, Block par1Block, int par2X, int par3Y, int par4Z, float par5R, float par6G, float par7B) {
        instance.enableAO = false;
        Tessellator tessellator = Tessellator.instance;
        boolean flag = false;
        float f3 = 0.5F;
        float f4 = 1.0F;
        float f5 = 0.8F;
        float f6 = 0.6F;
        float f7 = f4 * par5R;
        float f8 = f4 * par6G;
        float f9 = f4 * par7B;
        float f10 = f3;
        float f11 = f5;
        float f12 = f6;
        float f13 = f3;
        float f14 = f5;
        float f15 = f6;
        float f16 = f3;
        float f17 = f5;
        float f18 = f6;
        Icon blockIcon;

        if (par1Block != Block.grass) {
            f10 = f3 * par5R;
            f11 = f5 * par5R;
            f12 = f6 * par5R;
            f13 = f3 * par6G;
            f14 = f5 * par6G;
            f15 = f6 * par6G;
            f16 = f3 * par7B;
            f17 = f5 * par7B;
            f18 = f6 * par7B;
        }

        int l = CLBlockHelper.getMixedBrightnessForBlockWithColor(instance.blockAccess, par2X, par3Y, par4Z);

        if (instance.renderAllFaces || par1Block.shouldSideBeRendered(instance.blockAccess, par2X, par3Y - 1, par4Z, 0)) {
            int i = instance.renderMinY > 0.0D ? l : CLBlockHelper.getMixedBrightnessForBlockWithColor(instance.blockAccess, par2X, par3Y - 1, par4Z);
            tessellator.setBrightness(i);
            tessellator.setColorOpaque_F(f10, f13, f16);
            instance.renderFaceYNeg(par1Block, par2X, par3Y, par4Z, instance.getBlockIcon(par1Block, instance.blockAccess, par2X, par3Y, par4Z, 0));
            flag = true;
        }

        if (instance.renderAllFaces || par1Block.shouldSideBeRendered(instance.blockAccess, par2X, par3Y + 1, par4Z, 1)) {
            int i = instance.renderMaxY < 1.0D ? l : CLBlockHelper.getMixedBrightnessForBlockWithColor(instance.blockAccess, par2X, par3Y + 1, par4Z);
            tessellator.setBrightness(i);
            tessellator.setColorOpaque_F(f7, f8, f9);
            instance.renderFaceYPos(par1Block, par2X, par3Y, par4Z, instance.getBlockIcon(par1Block, instance.blockAccess, par2X, par3Y, par4Z, 1));
            flag = true;
        }

        if (instance.renderAllFaces || par1Block.shouldSideBeRendered(instance.blockAccess, par2X, par3Y, par4Z - 1, 2)) {
            int i = instance.renderMinZ > 0.0D ? l : CLBlockHelper.getMixedBrightnessForBlockWithColor(instance.blockAccess, par2X, par3Y, par4Z - 1);
            tessellator.setBrightness(i);
            tessellator.setColorOpaque_F(f11, f14, f17);
            blockIcon = instance.getBlockIcon(par1Block, instance.blockAccess, par2X, par3Y, par4Z, 2);
            instance.renderFaceZNeg(par1Block, par2X, par3Y, par4Z, blockIcon);

            if (RenderBlocks.fancyGrass && blockIcon.getIconName().equals("grass_side") && !instance.hasOverrideBlockTexture()) {
                tessellator.setColorOpaque_F(f11 * par5R, f14 * par6G, f17 * par7B);
                instance.renderFaceZNeg(par1Block, par2X, par3Y, par4Z, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (instance.renderAllFaces || par1Block.shouldSideBeRendered(instance.blockAccess, par2X, par3Y, par4Z + 1, 3)) {
            int i = instance.renderMaxZ < 1.0D ? l : CLBlockHelper.getMixedBrightnessForBlockWithColor(instance.blockAccess, par2X, par3Y, par4Z + 1);
            tessellator.setBrightness(i);
            tessellator.setColorOpaque_F(f11, f14, f17);
            blockIcon = instance.getBlockIcon(par1Block, instance.blockAccess, par2X, par3Y, par4Z, 3);
            instance.renderFaceZPos(par1Block, par2X, par3Y, par4Z, blockIcon);

            if (RenderBlocks.fancyGrass && blockIcon.getIconName().equals("grass_side") && !instance.hasOverrideBlockTexture()) {
                tessellator.setColorOpaque_F(f11 * par5R, f14 * par6G, f17 * par7B);
                instance.renderFaceZPos(par1Block, par2X, par3Y, par4Z, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (instance.renderAllFaces || par1Block.shouldSideBeRendered(instance.blockAccess, par2X - 1, par3Y, par4Z, 4)) {
            int i = instance.renderMinX > 0.0D ? l : CLBlockHelper.getMixedBrightnessForBlockWithColor(instance.blockAccess, par2X - 1, par3Y, par4Z);
            tessellator.setBrightness(i);
            tessellator.setColorOpaque_F(f12, f15, f18);
            blockIcon = instance.getBlockIcon(par1Block, instance.blockAccess, par2X, par3Y, par4Z, 4);
            instance.renderFaceXNeg(par1Block, par2X, par3Y, par4Z, blockIcon);

            if (RenderBlocks.fancyGrass && blockIcon.getIconName().equals("grass_side") && !instance.hasOverrideBlockTexture()) {
                tessellator.setColorOpaque_F(f12 * par5R, f15 * par6G, f18 * par7B);
                instance.renderFaceXNeg(par1Block, par2X, par3Y, par4Z, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (instance.renderAllFaces || par1Block.shouldSideBeRendered(instance.blockAccess, par2X + 1, par3Y, par4Z, 5)) {
            int i = instance.renderMaxX < 1.0D ? l : CLBlockHelper.getMixedBrightnessForBlockWithColor(instance.blockAccess, par2X + 1, par3Y, par4Z);
            tessellator.setBrightness(i);
            tessellator.setColorOpaque_F(f12, f15, f18);
            blockIcon = instance.getBlockIcon(par1Block, instance.blockAccess, par2X, par3Y, par4Z, 5);
            instance.renderFaceXPos(par1Block, par2X, par3Y, par4Z, blockIcon);

            if (RenderBlocks.fancyGrass && blockIcon.getIconName().equals("grass_side") && !instance.hasOverrideBlockTexture()) {
                tessellator.setColorOpaque_F(f12 * par5R, f15 * par6G, f18 * par7B);
                instance.renderFaceXPos(par1Block, par2X, par3Y, par4Z, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        return flag;
    }

    public static int getAoBrightness(RenderBlocks instance, int p_147778_1_, int p_147778_2_, int p_147778_3_, int p_147778_4_) {
        return getAoBrightness(p_147778_1_, p_147778_2_, p_147778_3_, p_147778_4_);
    }

    /**
     * Get ambient occlusion brightness
     */
    public static int getAoBrightness(int p_147778_1_, int p_147778_2_, int p_147778_3_, int p_147778_4_) {
        // SSSS BBBB GGGG RRRR LLLL 0000
        // 1111 0000 0000 0000 1111 0000 = 15728880

        if (p_147778_1_ == 0) {
            p_147778_1_ = p_147778_4_;
        }

        if (p_147778_2_ == 0) {
            p_147778_2_ = p_147778_4_;
        }

        if (p_147778_3_ == 0) {
            p_147778_3_ = p_147778_4_;
        }

        //return (p_147778_1_ & 15728880) + (p_147778_2_ & 15728880) + (p_147778_3_ & 15728880) + (p_147778_4_ & 15728880) >> 2 & 15728880;

        // Must mix all 5 channels now
        return mixColorChannel(20, p_147778_1_, p_147778_2_, p_147778_3_, p_147778_4_) | // SSSS
                mixColorChannel(16, p_147778_1_, p_147778_2_, p_147778_3_, p_147778_4_) | // BBBB
                mixColorChannel(12, p_147778_1_, p_147778_2_, p_147778_3_, p_147778_4_) | // GGGG this is the problem child
                mixColorChannel(8, p_147778_1_, p_147778_2_, p_147778_3_, p_147778_4_) | // RRRR
                mixColorChannel(4, p_147778_1_, p_147778_2_, p_147778_3_, p_147778_4_); // LLLL
    }

    public static int mixColorChannel(int startBit, int p1, int p2, int p3, int p4) {
        int avg;

        int q1 = (p1 >> startBit) & 0xf;
        int q2 = (p2 >> startBit) & 0xf;
        int q3 = (p3 >> startBit) & 0xf;
        int q4 = (p4 >> startBit) & 0xf;

        avg = (q1 + q2 + q3 + q4) / 4;

        if (avg > 15)
            avg = 15; // Cap to 4 bits again

        return avg << startBit;
    }
    /* 
     * Unused
     *  
    private static class renderCacheEntry
    {
    	public boolean isEmpty;
    	public float aoLightValue;
    	public int mixedBrightness;
    	public boolean canGrass;
    	
    	public renderCacheEntry fill(IBlockAccess world, int x, int y, int z)
    	{
    		Block block = world.getBlock(x,  y, z);
    		
    		aoLightValue = block.getLightValue();
    		canGrass = block.getCanBlockGrass();
    		mixedBrightness = CLBlockHelper.getMixedBrightnessForBlockWithColor(world, x, y, z);

    		isEmpty = false;
    		
    		return this;
    	}
    	
    	public void discard()
    	{
    		isEmpty = true;
    	}
    }
    */
}
