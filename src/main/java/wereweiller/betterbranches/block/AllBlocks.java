package wereweiller.betterbranches.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ChorusPlantBlock;
import net.minecraft.block.PillarBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import static wereweiller.betterbranches.BetterBranches.LOGGER;
import static wereweiller.betterbranches.BetterBranches.MOD_ID;

public class AllBlocks {

    public static final Block OAK_BRANCH_SUPER = registerBlock("oak_branch_super",
            new BranchSuperBlock(AbstractBlock.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(MOD_ID, "oak_branch_super")))
                    .strength(4f)
                    .requiresTool().sounds(BlockSoundGroup.WOOD)));



    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, name),
                new BlockItem(block, new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, name)))));
    }

    public static void initialize() {
        LOGGER.info("Registering Blocks for " + MOD_ID);
    }
}