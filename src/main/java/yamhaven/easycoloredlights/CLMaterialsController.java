package yamhaven.easycoloredlights;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import yamhaven.easycoloredlights.blocks.CLLamp;
import yamhaven.easycoloredlights.blocks.CLStone;
import yamhaven.easycoloredlights.items.CLDust;
import yamhaven.easycoloredlights.items.ItemCLBlock;
import yamhaven.easycoloredlights.lib.BlockInfo;
import yamhaven.easycoloredlights.lib.ModInfo;

public class CLMaterialsController {
    public static CLLamp CLBlockIdle;
    public static CLLamp CLBlockOn;
    public static Block CLStone;
    public static Item CLDust;
    
    public static void init() {
        CLBlockIdle = (CLLamp) new CLLamp(false).setUnlocalizedName(BlockInfo.CLLamp);
        CLBlockOn = (CLLamp) new CLLamp(true).setUnlocalizedName(BlockInfo.CLLamp + "On");
        CLStone = new CLStone().setUnlocalizedName(BlockInfo.CLStone);
        CLDust = new CLDust().setUnlocalizedName(BlockInfo.CLDust);
        
        CLBlockIdle.setSwitchBlock(CLBlockOn);
        CLBlockOn.setSwitchBlock(CLBlockIdle);
    }

    public static void registerMaterials() {
        GameRegistry.registerBlock(CLBlockIdle, ItemCLBlock.class, ModInfo.ID + BlockInfo.CLLamp);
        GameRegistry.registerBlock(CLBlockOn, ItemCLBlock.class, ModInfo.ID + BlockInfo.CLLamp + "On");
        GameRegistry.registerBlock(CLStone, ItemCLBlock.class, ModInfo.ID + BlockInfo.CLStone);
        GameRegistry.registerItem(CLDust, ModInfo.ID + BlockInfo.CLDust);
    }

    public static void addRecipes() {
        for (int i = 0; i < 16; i++) {
            GameRegistry.addRecipe(new ItemStack(CLDust, 4, i), " s ", "sds", " s ",
                    's', Item.glowstone,
                    'd', new ItemStack(Item.dyePowder, 1, i)
            );

            GameRegistry.addRecipe(new ItemStack(CLStone, 1, i), "cc", "cc",
                    'c', new ItemStack(CLDust, 1, i)
            );
            
            GameRegistry.addRecipe(new ItemStack(CLBlockIdle, 8, i), " r ", "rsr", " r ",
                    'r', Item.redstone,
                    's', new ItemStack(CLStone, 8, i)
            );
        }
    }
}
