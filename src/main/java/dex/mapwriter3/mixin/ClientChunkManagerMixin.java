package dex.mapwriter3.mixin;

import dex.mapwriter3.events.ChunkLoadCallback;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientChunkManager.class)
public abstract class ClientChunkManagerMixin {

    @Inject(method = "unload(II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientChunkManager$ClientChunkMap;compareAndSet(ILnet/minecraft/world/chunk/WorldChunk;Lnet/minecraft/world/chunk/WorldChunk;)Lnet/minecraft/world/chunk/WorldChunk;"), locals = LocalCapture.PRINT)
    private void unloadChunkEvent(int chunkX, int chunkZ, CallbackInfo ci) {
        //if (this.chunks.compareAndSet(i, worldChunk, (WorldChunk)null) != null) {
            //TODO finish, get locals
        //}
    }

    @Inject(method = "loadChunkFromPacket(IILnet/minecraft/world/biome/source/BiomeArray;Lnet/minecraft/util/PacketByteBuf;Lnet/minecraft/nbt/CompoundTag;I)Lnet/minecraft/world/chunk/WorldChunk;", at = @At("RETURN"))
    private void loadChunkEvent(int i, int j, BiomeArray biomeArray, PacketByteBuf packetByteBuf, CompoundTag compoundTag, int k, CallbackInfoReturnable<WorldChunk> cir) {
        if (cir.getReturnValue() != null) ChunkLoadCallback.EVENT.invoker().load(cir.getReturnValue());
    }

}
