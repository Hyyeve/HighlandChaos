package pet.blahaj.highlandmod.mixins;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.texture.Sprite;
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

@Mixin(HandledScreen.class)
public abstract class MixinHandledScreen {

    @Shadow
    private @Nullable Slot touchDragSlotStart;
    @Shadow
    private ItemStack touchDragStack;
    @Shadow
    private boolean touchIsRightClickDrag;
    @Shadow
    @Final
    protected ScreenHandler handler;
    @Shadow
    protected boolean cursorDragging;
    @Shadow
    @Final
    protected Set<Slot> cursorDragSlots;
    @Shadow
    private int heldButtonType;

    @Shadow
    protected abstract void calculateOffset();

    @Shadow
    protected int backgroundWidth;
    @Unique
    private HashMap<Slot, Vector2f> offsets = new HashMap<>();

    @Unique
    Random random = new Random();

    @Inject(method = "drawSlot", at = @At("HEAD"), cancellable = true)
    void drawSlot(DrawContext context, Slot slot, CallbackInfo ci) {
        Vector2f offset = new Vector2f(0);
        if (offsets.containsKey(slot)) offset = offsets.get(slot);
        offset.x += (float) random.nextDouble(-1, 1);
        offset.y += (float) random.nextDouble(-1, 1);
        if (offset.x > 20) offset.x = 20;
        if (offset.x < -20) offset.x = -20;
        if (offset.y > 20) offset.y = 20;
        if (offset.y < -20) offset.y = -20;
        offsets.put(slot, offset);
        Pair<Identifier, Identifier> pair;
        int i = (int) (slot.x + offset.x);
        int j = (int) (slot.y + offset.y);
        ItemStack itemStack = slot.getStack();
        boolean bl = false;
        boolean bl2 = slot == this.touchDragSlotStart && !this.touchDragStack.isEmpty() && !this.touchIsRightClickDrag;
        ItemStack itemStack2 = ((ScreenHandler) this.handler).getCursorStack();
        String string = null;
        if (slot == this.touchDragSlotStart && !this.touchDragStack.isEmpty() && this.touchIsRightClickDrag && !itemStack.isEmpty()) {
            itemStack = itemStack.copyWithCount(itemStack.getCount() / 2);
        } else if (this.cursorDragging && this.cursorDragSlots.contains(slot) && !itemStack2.isEmpty()) {
            if (this.cursorDragSlots.size() == 1) {
                return;
            }
            if (ScreenHandler.canInsertItemIntoSlot(slot, itemStack2, true) && ((ScreenHandler) this.handler).canInsertIntoSlot(slot)) {
                bl = true;
                int k = Math.min(itemStack2.getMaxCount(), slot.getMaxItemCount(itemStack2));
                int l = slot.getStack().isEmpty() ? 0 : slot.getStack().getCount();
                int m = ScreenHandler.calculateStackSize(this.cursorDragSlots, this.heldButtonType, itemStack2) + l;
                if (m > k) {
                    m = k;
                    string = Formatting.YELLOW.toString() + k;
                }
                itemStack = itemStack2.copyWithCount(m);
            } else {
                this.cursorDragSlots.remove(slot);
                this.calculateOffset();
            }
        }
        context.getMatrices().push();
        context.getMatrices().translate(0.0f, 0.0f, 100.0f);
        if (itemStack.isEmpty() && slot.isEnabled() && (pair = slot.getBackgroundSprite()) != null) {
            Sprite sprite = MinecraftClient.getInstance().getSpriteAtlas(pair.getFirst()).apply(pair.getSecond());
            context.drawSprite(i, j, 0, 16, 16, sprite);
            bl2 = true;
        }
        if (!bl2) {
            if (bl) {
                context.fill(i, j, i + 16, j + 16, -2130706433);
            }
            context.drawItem(itemStack, i, j, slot.x + slot.y * this.backgroundWidth);
            context.drawItemInSlot(MinecraftClient.getInstance().textRenderer, itemStack, i, j, string);
        }
        context.getMatrices().pop();
        ci.cancel();
    }
}
