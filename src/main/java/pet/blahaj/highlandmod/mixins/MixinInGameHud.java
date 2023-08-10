package pet.blahaj.highlandmod.mixins;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Random;
import java.util.Set;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud {


    @Unique
    private HashMap<ItemStack, Vector2f> offsets = new HashMap<>();

    @Unique
    Random random = new Random();

    @Inject(method = "renderHotbarItem", at = @At("HEAD"), cancellable = true)
    private void renderHotbarItem(DrawContext context, int x, int y, float f, PlayerEntity player, ItemStack stack, int seed, CallbackInfo ci) {

        Vector2f offset = new Vector2f(0);
        if (offsets.containsKey(stack)) offset = offsets.get(stack);
        offset.x += (float) random.nextDouble(-1, 1);
        offset.y += (float) random.nextDouble(-1, 1);
        if (offset.x > 20) offset.x = 20;
        if (offset.x < -20) offset.x = -20;
        if (offset.y > 20) offset.y = 20;
        if (offset.y < -20) offset.y = -20;
        offsets.put(stack, offset);

        x += offset.x;
        y += offset.y;

        if (stack.isEmpty()) {
            return;
        }
        float g = (float)stack.getBobbingAnimationTime() - f;
        if (g > 0.0f) {
            float h = 1.0f + g / 5.0f;
            context.getMatrices().push();
            context.getMatrices().translate(x + 8, y + 12, 0.0f);
            context.getMatrices().scale(1.0f / h, (h + 1.0f) / 2.0f, 1.0f);
            context.getMatrices().translate(-(x + 8), -(y + 12), 0.0f);
        }
        context.drawItem(player, stack, x, y, seed);
        if (g > 0.0f) {
            context.getMatrices().pop();
        }
        context.drawItemInSlot(MinecraftClient.getInstance().textRenderer, stack, x, y);
        ci.cancel();
    }
}
