package dex.mapwriter3;

import dex.mapwriter3.region.BlockColours;
import dex.mapwriter3.util.Logging;
import dex.mapwriter3.util.Render;
import dex.mapwriter3.util.Texture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

// Static class to generate BlockColours.
// This is separate from BlockColours because it needs to run in the GL
// rendering thread
// whereas the generated BlockColours object is used only in the background
// thread.
// So basically split to make it clear that BlockColourGen and the generated
// BlockColours
// must not have any interaction after it is generated.

@Environment(EnvType.CLIENT)
public class BlockColourGen {

    private static int getIconMapColour(Sprite icon, Texture terrainTexture) {
        // flipped icons have the U and V coords reversed (minU > maxU, minV >
        // maxV).
        // thanks go to taelnia for fixing this.
        /*int iconX = Math.round((terrainTexture.w) * Math.min(icon.getMinU(), icon.getMaxU()));
        int iconY = Math.round((terrainTexture.h) * Math.min(icon.getMinV(), icon.getMaxV()));
        int iconWidth = Math.round((terrainTexture.w) * Math.abs(icon.getMaxU() - icon.getMinU()));
        int iconHeight = Math.round((terrainTexture.h) * Math.abs(icon.getMaxV() - icon.getMinV()));*/
        int iconX = Math.round(Math.min(icon.getMinU(), icon.getMaxU()));
        int iconY = Math.round(Math.min(icon.getMinV(), icon.getMaxV()));
        int iconWidth = Math.round(Math.abs(icon.getMaxU() - icon.getMinU()));
        int iconHeight = Math.round(Math.abs(icon.getMaxV() - icon.getMinV()));


        int[] pixels = new int[iconWidth * iconHeight];

        // MwUtil.log("(%d, %d) %dx%d", iconX, iconY, iconWidth, iconHeight);

        terrainTexture.getRGB(iconX, iconY, iconWidth, iconHeight, pixels, 0, iconWidth, icon);

        // need to use custom averaging routine rather than scaling down to one
        // pixel to
        // stop transparent pixel colours being included in the average.
        return Render.getAverageColourOfArray(pixels);
    }

    private static void genBiomeColours(BlockColours bc) {
        // generate array of foliage, grass, and water colour multipliers
        // for each biome.

        for (Biome biome : Registry.BIOME) {
            Identifier biomeID = Registry.BIOME.getId(biome);

            bc.setBiomeWaterShading(biomeID, biome.getWaterColor());
            bc.setBiomeFoliageShading(biomeID, biome.getFoliageColor());
            bc.setBiomeGrassShading(biomeID, biome.getGrassColorAt(1, 1)); //randomly chosen sample point
        }


        /*for (int i = 0; i < BiomeGenBase.getBiomeGenArray().length; i++) {
            if (BiomeGenBase.getBiomeGenArray()[i] != null) {
                bc.setBiomeWaterShading(i, BiomeGenBase.getBiomeGenArray()[i].getWaterColorMultiplier() & 0xffffff);

                double temp = MathHelper.clamp(BiomeGenBase.getBiomeGenArray()[i].temperature, 0.0F, 1.0F);
                double rain = MathHelper.clamp(BiomeGenBase.getBiomeGenArray()[i].rainfall, 0.0F, 1.0F);
                int grasscolor = GrassColors.getColor(temp, rain);
                int foliagecolor = FoliageColors.getColor(temp, rain);

                bc.setBiomeGrassShading(i, grasscolor & 0xffffff);
                bc.setBiomeFoliageShading(i, foliagecolor & 0xffffff);
            } else {
                bc.setBiomeWaterShading(i, 0xffffff);
                bc.setBiomeGrassShading(i, 0xffffff);
                bc.setBiomeFoliageShading(i, 0xffffff);
            }
        }*/
    }

    public static void genBlockColours(BlockColours bc) {

        Logging.log("generating block map colours from textures");

        // copy terrain texture to MwRender pixel bytebuffer

        // bind the terrain texture
        // MinecraftClient.getInstance().func_110434_K().func_110577_a(TextureMap.field_110575_b);
        // get the bound texture id
        // int terrainTextureId = Render.getBoundTextureId();

        /*int terrainTextureId = MinecraftClient.getInstance().renderEngine.getTexture(TextureMap.locationBlocksTexture).getGlTextureId();

        // create texture object from the currently bound GL texture
        if (terrainTextureId == 0) {
            Logging.log("error: could get terrain texture ID");
            return;
        }*/
        Texture terrainTexture = new Texture(9); //random id chosen //todo check

        double u1Last = 0;
        double u2Last = 0;
        double v1Last = 0;
        double v2Last = 0;
        int blockColourLast = 0;
        int e_count = 0;
        int b_count = 0;
        int s_count = 0;

        for (Block block : Registry.BLOCK) {

            //for (int dv = 0; dv < 16; dv++) {
            // int blockAndMeta = ((blockID & 0xfff) << 4) | (dv & 0xf);
            int blockColour = 0;

            if (block.getRenderType(block.getDefaultState()) != BlockRenderType.INVISIBLE) {

                Sprite icon = null;
                try {
                    icon = MinecraftClient.getInstance().getBlockRenderManager().getModels().getSprite(block.getDefaultState());
                    //icon = MinecraftClient.getInstance().getBlockRendererDispatcher().getBlockModelShapes().getTexture(block.getStateFromMeta(dv));
                } catch (Exception e) {
                    // MwUtil.log("genFromTextures: exception caught when requesting block texture for %03x:%x",
                    // blockID, dv);
                    // e.printStackTrace();
                    e_count++;
                }

                if (icon != null) {
                    double u1 = icon.getMinU();
                    double u2 = icon.getMaxU();
                    double v1 = icon.getMinV();
                    double v2 = icon.getMaxV();

                    if ((u1 == u1Last) && (u2 == u2Last) && (v1 == v1Last) && (v2 == v2Last)) {
                        blockColour = blockColourLast;
                        s_count++;
                    } else {
                        //terrainTexture = MinecraftClient.getInstance().getBlockRenderManager().getModels().getSprite(blockState).getAtlas();
                        blockColour = getIconMapColour(icon, terrainTexture);
                        // request icon with meta 16, carpenterblocks uses
                        // this method to get the real texture
                        // this makes the carpenterblocks render as brown
                        // blocks on the map
                        if (Registry.BLOCK.getId(block).getNamespace().contains("CarpentersBlocks")) {
                            // icon = block.getIcon(1, 16);
                            // blockColour = getIconMapColour(icon,
                            // terrainTexture);
                        }

                        u1Last = u1;
                        u2Last = u2;
                        v1Last = v1;
                        v2Last = v2;
                        blockColourLast = blockColour;
                        b_count++;
                    }
                }
            }
            bc.setColour(Registry.BLOCK.getId(block), blockColour);
        }

        Logging.log("processed %d block textures, %d skipped, %d exceptions", b_count, s_count, e_count);

        genBiomeColours(bc);
    }
}
