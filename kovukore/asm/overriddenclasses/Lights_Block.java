package kovukore.asm.overriddenclasses;

import cpw.mods.fml.common.registry.BlockProxy;
import kovukore.asm.transformer.ASMAddField;
import kovukore.asm.transformer.ASMAddMethod;
import kovukore.asm.transformer.ASMReplaceField;
import kovukore.asm.transformer.ASMReplaceMethod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockComparator;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockFlowing;
import net.minecraft.block.BlockFluid;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.BlockRedstoneOre;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.BlockRedstoneTorch;
import net.minecraft.block.BlockStationary;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

/*
 * XXX UPDATE THESE FIELD NAMES AND METHOD NAMES/SIGS ON EACH NEW VERSION OF MINECRAFT
 */
public class Lights_Block extends Block
{
	@ASMAddField
	public final static Float[] l = { 0F, 1F / 15, 2F / 15, 3F / 15, 4F / 15, 5F / 15, 6F / 15, 7F / 15, 8F / 15, 9F / 15, 10F / 15, 11F / 15, 12F / 15, 13F / 15, 14F / 15, 1F };
	@ASMReplaceField
	public static final BlockFluid H = (BlockFluid) (new BlockFlowing(10, Material.lava)).setHardness(0.0F).setLightValue(1.0F).setUnlocalizedName("lava").disableStats().setTextureName("lava_flow").addColorLightValue(l[15], l[12], l[10]);
	@ASMReplaceField
	public static final Block I = (new BlockStationary(11, Material.lava)).setHardness(100.0F).setLightValue(1.0F).setUnlocalizedName("lava").disableStats().setTextureName("lava_still").addColorLightValue(l[15], l[12], l[10]);
	@ASMReplaceField
	public static final Block av = (new BlockTorch(50)).setHardness(0.0F).setLightValue(0.9375F).setStepSound(soundWoodFootstep).setUnlocalizedName("torch").setTextureName("torch_on").addColorLightValue(l[14], l[14], l[11]);
	@ASMReplaceField
	public static final BlockFire aw = (BlockFire) (new BlockFire(51)).setHardness(0.0F).setLightValue(1.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("fire").disableStats().setTextureName("fire").addColorLightValue(l[15], l[14], l[11]);
	@ASMReplaceField
	public static final Block aT = (new BlockRedstoneOre(74, true)).setLightValue(0.625F).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("oreRedstone").setTextureName("redstone_ore").addColorLightValue(l[9], l[8], l[8]);
	@ASMReplaceField
	public static final Block aV = (new BlockRedstoneTorch(76, true)).setHardness(0.0F).setLightValue(0.5F).setStepSound(soundWoodFootstep).setUnlocalizedName("notGate").setCreativeTab(CreativeTabs.tabRedstone).setTextureName("redstone_torch_on").addColorLightValue(l[7], l[5], l[5]);
	@ASMReplaceField
	public static final BlockPortal bj = (BlockPortal) (new BlockPortal(90)).setHardness(-1.0F).setStepSound(soundGlassFootstep).setLightValue(0.75F).setUnlocalizedName("portal").setTextureName("portal").addColorLightValue(l[5], l[1], l[11]);
	@ASMReplaceField
	public static final BlockRedstoneRepeater bn = (BlockRedstoneRepeater) (new BlockRedstoneRepeater(94, true)).setHardness(0.0F).setLightValue(0.625F).setStepSound(soundWoodFootstep).setUnlocalizedName("diode").disableStats().setTextureName("repeater_on").addColorLightValue(l[9], l[7], l[7]);
	@ASMReplaceField
	public static final BlockComparator cr = (BlockComparator) (new BlockComparator(150, true)).setHardness(0.0F).setLightValue(0.625F).setStepSound(soundWoodFootstep).setUnlocalizedName("comparator").disableStats().setTextureName("comparator_on").addColorLightValue(l[9], l[7], l[7]);
	
	public Lights_Block(int par1, Material par2Material)
	{
		super(par1, par2Material);
	}
	
	@ASMReplaceMethod
	public Block a(float par1)
	{
		lightValue[this.blockID] = (int) (15.0F * par1);
		return this.addColorLightValue(par1, par1, par1);
	}

//	@ASMAddMethod
//    public Block setLightValue(int par1)
//    {
//        lightValue[this.blockID] = par1;
//        return this.addColorLightValue(l[par1], l[par1], l[par1]);
//    }
    
	@ASMAddMethod
	public Block addColorLightValue(float r, float g, float b)
	{
		lightValue[this.blockID] &= 15;
		lightValue[this.blockID] |= ((((int) (15.0F * b)) << 15) + (((int) (15.0F * g)) << 10) + (((int) (15.0F * r)) << 5));
		return this;
	}
}