package wereweiller.betterbranches.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import static wereweiller.betterbranches.BetterBranches.MOD_ID;

public class AllTags {
    public static class Blocks {

        public static final TagKey<Block> BRANCHES = createTag("branches");

        private static TagKey<Block> createTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, Identifier.of(MOD_ID, name));
        }

    }

    public static class Items {

        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, name));
        }
    }
}
