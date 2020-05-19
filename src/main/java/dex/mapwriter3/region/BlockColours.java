package dex.mapwriter3.region;

import dex.mapwriter3.util.Logging;
import dex.mapwriter3.util.MwReference;
import dex.mapwriter3.util.Render;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

@Environment(EnvType.CLIENT)
public class BlockColours {

    public static final int MAX_BLOCKS = 4096;
    public static final int MAX_META = 16;
    public static final int MAX_BIOMES = 256;

    public static final String biomeSectionString = "[biomes]";
    public static final String blockSectionString = "[blocks]";

    private HashMap<Identifier, Integer> waterMultiplierArray = new HashMap<>();
    private HashMap<Identifier, Integer> grassMultiplierArray = new HashMap<>();
    private HashMap<Identifier, Integer> foliageMultiplierArray = new HashMap<>();


    private LinkedHashMap<Identifier, BlockData> bcMap = new LinkedHashMap<>();

    public enum BlockType {
        NORMAL,
        GRASS,
        LEAVES,
        FOLIAGE,
        WATER,
        OPAQUE
    }

    public BlockColours() {
    }

    public CompoundTag CombineBlockMeta(BlockState blockState) {
        return NbtHelper.fromBlockState(blockState);
    }

    public String CombineBlockMeta(Identifier BlockName) {
        return BlockName.toString();
    }

    public int getColour(Identifier block) {

        BlockData data = new BlockData();

        if (this.bcMap.containsKey(block)) {
            data = this.bcMap.get(block);
        }

        return data.color;
    }

    public void setColour(Identifier block, int colour) {

        if (this.bcMap.containsKey(block)) {
            BlockData data = this.bcMap.get(block);
            data.color = colour;
        } else {
            BlockData data = new BlockData();
            data.color = colour;
            this.bcMap.put(block, data);
        }
    }

    private int getGrassColourMultiplier(Identifier biome) {
        return this.grassMultiplierArray.getOrDefault(biome, 0xffffff);
    }

    private int getWaterColourMultiplier(Identifier biome) {
        return this.waterMultiplierArray.getOrDefault(biome, 0xffffff);
    }

    private int getFoliageColourMultiplier(Identifier biome) {
        return this.foliageMultiplierArray.getOrDefault(biome, 0xffffff);
    }

    public int getBiomeColour(Identifier block, Identifier biome) {
        int colourMultiplier = 0xffffff;

        if (this.bcMap.containsKey(block)) {
            switch (this.bcMap.get(block).type) {
                case GRASS:
                    colourMultiplier = this.getGrassColourMultiplier(biome);
                    break;
                case LEAVES:
                case FOLIAGE:
                    colourMultiplier = this.getFoliageColourMultiplier(biome);
                    break;
                case WATER:
                    colourMultiplier = this.getWaterColourMultiplier(biome);
                    break;
                default:
                    colourMultiplier = 0xffffff;
                    break;
            }
        }
        return colourMultiplier;
    }

    public void setBiomeWaterShading(Identifier biomeID, int colour) {
        this.waterMultiplierArray.put(biomeID, colour);
    }

    public void setBiomeGrassShading(Identifier biomeID, int colour) {
        this.grassMultiplierArray.put(biomeID, colour);
    }

    public void setBiomeFoliageShading(Identifier biomeID, int colour) {
        this.foliageMultiplierArray.put(biomeID, colour);
    }

    private static BlockType getBlockTypeFromString(String typeString) {
        BlockType blockType = BlockType.NORMAL;
        if (typeString.equalsIgnoreCase("normal")) {
            blockType = BlockType.NORMAL;
        } else if (typeString.equalsIgnoreCase("grass")) {
            blockType = BlockType.GRASS;
        } else if (typeString.equalsIgnoreCase("leaves")) {
            blockType = BlockType.LEAVES;
        } else if (typeString.equalsIgnoreCase("foliage")) {
            blockType = BlockType.FOLIAGE;
        } else if (typeString.equalsIgnoreCase("water")) {
            blockType = BlockType.WATER;
        } else if (typeString.equalsIgnoreCase("opaque")) {
            blockType = BlockType.OPAQUE;
        } else {
            Logging.logWarning("unknown block type '%s'", typeString);
        }
        return blockType;
    }

    private static String getBlockTypeAsString(BlockType blockType) {
        String s = "normal";
        switch (blockType) {
            case NORMAL:
                s = "normal";
                break;
            case GRASS:
                s = "grass";
                break;
            case LEAVES:
                s = "leaves";
                break;
            case FOLIAGE:
                s = "foliage";
                break;
            case WATER:
                s = "water";
                break;
            case OPAQUE:
                s = "opaque";
                break;
        }
        return s;
    }

    public BlockType getBlockType(Identifier block, int meta) {

        BlockData data = new BlockData();

        if (this.bcMap.containsKey(block)) {
            data = this.bcMap.get(block);
        }
        return data.type;
    }

    public void setBlockType(Identifier block, BlockType type) {

        if (this.bcMap.containsKey(block)) {
            BlockData data = this.bcMap.get(block);
            data.type = type;
            data.color = adjustBlockColourFromType(block, type, data.color);
        } else {
            BlockData data = new BlockData();
            data.type = type;
            this.bcMap.put(block, data);
        }
    }

    private static int adjustBlockColourFromType(Identifier blockID, BlockType type, int blockColour) {
        // for normal blocks multiply the block colour by the render colour.
        // for other blocks the block colour will be multiplied by the biome
        // colour.
        Block block = Registry.BLOCK.get(blockID);

        switch (type) {

            case OPAQUE:
                blockColour |= 0xff000000;
            case NORMAL:
                // fix crash when mods don't implement getRenderColor for all
                // block meta values.
                try {
                    int renderColour = block.getMaterial(block.getDefaultState()).getColor().color;
                    if (renderColour != 0xffffff) {
                        blockColour = Render.multiplyColours(blockColour, 0xff000000 | renderColour);
                    }
                } catch (RuntimeException e) {
                    // do nothing
                }
                break;
            case LEAVES:
                // leaves look weird on the map if they are not opaque.
                // they also look too dark if the render colour is applied.
                blockColour |= 0xff000000;
                break;
            case GRASS:
                // the icon returns the dirt texture so hardcode it to the grey
                // undertexture.
                blockColour = 0xff9b9b9b;
            default:
                break;
        }
        return blockColour;
    }

    public static int getColourFromString(String s) {
        return (int) (Long.parseLong(s, 16) & 0xffffffffL);
    }

    //
    // Methods for loading block colours from file:
    //

    // read biome colour multiplier values.
    // line format is:
    // biome <biomeId> <waterMultiplier> <grassMultiplier> <foliageMultiplier>
    // accepts "*" wildcard for biome id (meaning for all biomes).
    private void loadBiomeLine(String[] split) {
        try {
            for (Identifier biomeId : Registry.BIOME.getIds()) {
                int waterMultiplier = getColourFromString(split[2]) & 0xffffff;
                int grassMultiplier = getColourFromString(split[3]) & 0xffffff;
                int foliageMultiplier = getColourFromString(split[4]) & 0xffffff;

                if (!split[1].equals("*") && Identifier.isValid(split[1])) {
                    this.setBiomeWaterShading(Identifier.tryParse(split[1]), waterMultiplier);
                    this.setBiomeGrassShading(Identifier.tryParse(split[1]), grassMultiplier);
                    this.setBiomeFoliageShading(Identifier.tryParse(split[1]), foliageMultiplier);
                    break;
                } else {
                    this.setBiomeWaterShading(biomeId, waterMultiplier);
                    this.setBiomeGrassShading(biomeId, grassMultiplier);
                    this.setBiomeFoliageShading(biomeId, foliageMultiplier);
                }
            }

        } catch (NumberFormatException e) {
            Logging.logWarning("invalid biome colour line '%s %s %s %s %s'", split[0], split[1], split[2], split[3], split[4]);
        }
    }

    // read block colour values.
    // line format is:
    // block <blockName> <colour>
    // the biome id, meta value, and colour code are in hex.
    // accepts "*" wildcard for biome id and meta (meaning for all blocks and/or
    // meta values).
    private void loadBlockLine(String[] split) {
        try {
            // block colour line
            int colour = getColourFromString(split[2]);
            this.setColour(Identifier.tryParse(split[1]), colour);

        } catch (NumberFormatException e) {
            Logging.logWarning("invalid block colour line '%s %s %s %s'", split[0], split[1], split[2], split[3]);
        }
    }

    private void loadBlockTypeLine(String[] split) {
        try {
            // block type line
            BlockType type = getBlockTypeFromString(split[2]);
            this.setBlockType(Identifier.tryParse(split[1]), type);
        } catch (NumberFormatException e) {
            Logging.logWarning("invalid block colour line '%s %s %s %s'", split[0], split[1], split[2], split[3]);
        }
    }

    public void loadFromFile(File f) {
        Scanner fin = null;
        try {
            fin = new Scanner(new FileReader(f));

            while (fin.hasNextLine()) {
                // get next line and remove comments (part of line after #)
                String line = fin.nextLine().split("#")[0].trim();
                if (line.length() > 0) {
                    String[] lineSplit = line.split(" ");
                    if (lineSplit[0].equals("biome") && (lineSplit.length == 5)) {
                        this.loadBiomeLine(lineSplit);
                    } else if (lineSplit[0].equals("block") && (lineSplit.length == 4)) {
                        this.loadBlockLine(lineSplit);
                    } else if (lineSplit[0].equals("blocktype") && (lineSplit.length == 4)) {
                        this.loadBlockTypeLine(lineSplit);
                    } else if (lineSplit[0].equals("version:")) {

                    } else {
                        Logging.logWarning("invalid map colour line '%s'", line);
                    }
                }
            }
        } catch (IOException e) {
            Logging.logError("loading block colours: no such file '%s'", f);

        } finally {
            if (fin != null) {
                fin.close();
            }
        }
    }

    //
    // Methods for saving block colours to file.
    //

    // save biome colour multipliers to a file.
    public void saveBiomes(Writer fout) throws IOException {
        fout.write("biome * ffffff ffffff ffffff\n");

        for (Identifier biomeID : Registry.BIOME.getIds()) {
            int waterMultiplier = this.getWaterColourMultiplier(biomeID) & 0xffffff;
            int grassMultiplier = this.getGrassColourMultiplier(biomeID) & 0xffffff;
            int foliageMultiplier = this.getFoliageColourMultiplier(biomeID) & 0xffffff;

            // don't add lines that are covered by the default.
            if ((waterMultiplier != 0xffffff) || (grassMultiplier != 0xffffff) || (foliageMultiplier != 0xffffff)) {
                fout.write(String.format("biome %s %06x %06x %06x\n", biomeID.toString(), waterMultiplier, grassMultiplier, foliageMultiplier));
            }
        }
    }

    private static String getMostOccurringKey(Map<String, Integer> map, String defaultItem) {
        // find the most commonly occurring key in a hash map.
        // only return a key if there is more than 1.
        int maxCount = 1;
        String mostOccurringKey = defaultItem;
        for (Entry<String, Integer> entry : map.entrySet()) {
            String key = entry.getKey();
            int count = entry.getValue();

            if (count > maxCount) {
                maxCount = count;
                mostOccurringKey = key;
            }
        }

        return mostOccurringKey;
    }

    // to use the least number of lines possible find the most commonly
    // occurring
    // item for the different meta values of a block.
    // an 'item' is either a block colour or a block type.
    // the most commonly occurring item is then used as the wildcard entry for
    // the block, and all non matching items added afterwards.
    private static void writeMinimalBlockLines(Writer fout, String lineStart, List<String> items, String defaultItem) throws IOException {

        Map<String, Integer> frequencyMap = new HashMap<String, Integer>();

        // first count the number of occurrences of each item.
        for (String item : items) {
            int count = 0;
            if (frequencyMap.containsKey(item)) {
                count = frequencyMap.get(item);
            }
            frequencyMap.put(item, count + 1);
        }

        // then find the most commonly occurring item.
        String mostOccurringItem = getMostOccurringKey(frequencyMap, defaultItem);

        // only add a wildcard line if it actually saves lines.
        if (!mostOccurringItem.equals(defaultItem)) {
            fout.write(String.format("%s * %s\n", lineStart, mostOccurringItem));
        }

        // add lines for items that don't match the wildcard line.

        int meta = 0;
        for (String s : items) {
            if (!s.equals(mostOccurringItem) && !s.equals(defaultItem)) {
                fout.write(String.format("%s %d %s\n", lineStart, meta, s));
            }
            meta++;
        }
    }

    public void saveBlocks(Writer fout) throws IOException {
        fout.write("block * * 00000000\n");

        String LastBlock = "";
        List<String> colours = new ArrayList<String>();

        for (Map.Entry<Identifier, BlockData> entry : this.bcMap.entrySet()) {
            Identifier block = entry.getKey();

            String color = String.format("%08x", entry.getValue().color);

            if (!LastBlock.equals(block.toString()) && !LastBlock.isEmpty()) {
                String lineStart = String.format("block %s", LastBlock);
                writeMinimalBlockLines(fout, lineStart, colours, "00000000");

                colours.clear();
            }

            colours.add(color);
            LastBlock = block.toString();
        }
    }

    public void saveBlockTypes(Writer fout) throws IOException {
        fout.write("blocktype * * normal\n");

        String LastBlock = "";
        List<String> blockTypes = new ArrayList<String>();

        for (Map.Entry<Identifier, BlockData> entry : this.bcMap.entrySet()) {
            Identifier block = entry.getKey();

            String Type = getBlockTypeAsString(entry.getValue().type);

            if (!LastBlock.equals(block.toString()) && !LastBlock.isEmpty()) {
                String lineStart = String.format("blocktype %s", LastBlock);
                writeMinimalBlockLines(fout, lineStart, blockTypes, getBlockTypeAsString(BlockType.NORMAL));

                blockTypes.clear();
            }

            blockTypes.add(Type);
            LastBlock = block.toString();
        }
    }

    // save block colours and biome colour multipliers to a file.
    public void saveToFile(File f) {
        Writer fout = null;
        try {
            fout = new OutputStreamWriter(new FileOutputStream(f));
            fout.write(String.format("version: %s\n", MwReference.VERSION));
            this.saveBiomes(fout);
            this.saveBlockTypes(fout);
            this.saveBlocks(fout);

        } catch (IOException e) {
            Logging.logError("saving block colours: could not write to '%s'", f);

        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static void writeOverridesFile(File f) {
        Writer fout = null;
        try {
            fout = new OutputStreamWriter(new FileOutputStream(f));
            fout.write(String.format("version: %s\n", MwReference.VERSION));

            fout.write("block minecraft:yellow_flower * 60ffff00	# make dandelions more yellow\n" + "block minecraft:red_flower 0 60ff0000		# make poppy more red\n" + "block minecraft:red_flower 1 601c92d6		# make Blue Orchid more red\n"
                    + "block minecraft:red_flower 2 60b865fb		# make Allium more red\n" + "block minecraft:red_flower 3 60e4eaf2		# make Azure Bluet more red\n" + "block minecraft:red_flower 4 60d33a17		# make Red Tulip more red\n"
                    + "block minecraft:red_flower 5 60e17124		# make Orange Tulip more red\n" + "block minecraft:red_flower 6 60ffffff		# make White Tulip more red\n" + "block minecraft:red_flower 7 60eabeea		# make Pink Tulip more red\n"
                    + "block minecraft:red_flower 8 60eae6ad		# make Oxeye Daisy more red\n" + "block minecraft:double_plant 0 60ffff00		# make Sunflower more Yellow-orrange\n" + "block minecraft:double_plant 1 d09f78a4		# make Lilac more pink\n"
                    + "block minecraft:double_plant 4 60ff0000		# make Rose Bush more red\n" + "block minecraft:double_plant 5 d0e3b8f7		# make Peony more red\n" + "blocktype minecraft:grass * grass			# grass block\n" + "blocktype minecraft:flowing_water * water	# flowing water block\n"
                    + "blocktype minecraft:water * water			# still water block\n" + "blocktype minecraft:leaves * leaves    		# leaves block\n" + "blocktype minecraft:leaves2 * leaves    		# leaves block\n" + "blocktype minecraft:leaves 1 opaque    		# pine leaves (not biome colorized)\n"
                    + "blocktype minecraft:leaves 2 opaque    		# birch leaves (not biome colorized)\n" + "blocktype minecraft:tallgrass * grass     	# tall grass block\n" + "blocktype minecraft:vine * foliage  			# vines block\n" + "blocktype BiomesOPlenty:grass * grass		# BOP grass block\n"
                    + "blocktype BiomesOPlenty:plant_0 * grass		# BOP plant block\n" + "blocktype BiomesOPlenty:plant_1 * grass		# BOP plant block\n" + "blocktype BiomesOPlenty:leaves_0 * leaves	# BOP Leave block\n" + "blocktype BiomesOPlenty:leaves_1 * leaves	# BOP Leave block\n"
                    + "blocktype BiomesOPlenty:leaves_2 * leaves	# BOP Leave block\n" + "blocktype BiomesOPlenty:leaves_3 * leaves	# BOP Leave block\n" + "blocktype BiomesOPlenty:leaves_4 * leaves	# BOP Leave block\n" + "blocktype BiomesOPlenty:leaves_5 * leaves	# BOP Leave block\n"
                    + "blocktype BiomesOPlenty:tree_moss * foliage	# biomes o plenty tree moss\n");
            // TODO: Find out the names and readd these overwrites
            // + "blocktype 2164 * leaves  						# twilight forest leaves\n"
            // +
            // "blocktype 2177 * leaves  						# twilight forest magic leaves\n"

            // + "blocktype 2204 * leaves  						# extrabiomesXL green leaves\n"
            // +
            // "blocktype 2200 * opaque  						# extrabiomesXL autumn leaves\n"

            // + "blocktype 3257 * opaque  						# natura berry bush\n"
            // + "blocktype 3272 * opaque  						# natura darkwood leaves\n"
            // + "blocktype 3259 * leaves  						# natura flora leaves\n"
            // + "blocktype 3278 * opaque 						# natura rare leaves\n"
            // + "blocktype 3258 * opaque  						# natura sakura leaves\n"
        } catch (IOException e) {
            Logging.logError("saving block overrides: could not write to '%s'", f);

        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public boolean CheckFileVersion(File fn) {
        String lineData = "";
        try {
            RandomAccessFile inFile = new RandomAccessFile(fn, "rw");
            lineData = inFile.readLine();
            inFile.close();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }

        if (lineData.equals(String.format("version: %s", MwReference.VERSION))) {
            return true;
        }

        return false;
    }

    public class BlockData {
        public int color = 0;
        public BlockType type = BlockType.NORMAL;
    }
}
