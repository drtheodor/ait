package dev.amble.ait.client.renderers;

import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Renders inputs for Fabricator recipes in JEI with a range, rather than a specific number of items
 */
public class BlueprintInputItemStackRenderer implements IIngredientRenderer<ItemStack> {

    private final int minCount;
    private final int maxCount;

    public BlueprintInputItemStackRenderer(int minCount, int maxCount) {
        this.minCount = minCount;
        this.maxCount = maxCount;
    }

    public void render(DrawContext context, @Nullable ItemStack maxCountStack) {
        this.render(context, maxCountStack, 0, 0);
    }

    public void render(DrawContext context, @Nullable ItemStack ingredient, int posX, int posY) {
        if (ingredient == null) {
            return;
        }
        RenderSystem.enableDepthTest();

        MinecraftClient minecraft = MinecraftClient.getInstance();
        context.drawItemWithoutEntity(ingredient, posX, posY);

        TextRenderer textRenderer = this.getFontRenderer(minecraft, ingredient);
        context.drawItemInSlot(textRenderer, ingredient, posX, posY, "");
        this.drawText(context, textRenderer, posX, posY);

        RenderSystem.disableBlend();
    }

    /**
     * Custom text drawing method to fit the range of items within the item slot
     */
    private void drawText(DrawContext context, TextRenderer textRenderer, int posX, int posY) {
        String range = minCount == maxCount ? String.valueOf(minCount) : minCount + "-" + maxCount;
        if (range.equals("1")) {
            return;
        }

        MatrixStack matrixStack = context.getMatrices();
        matrixStack.push();
        matrixStack.translate(0.0F, 0.0F, 200.0F);

        if (textRenderer.getWidth(range) > 26) {
            context.drawText(
                    textRenderer,
                    String.valueOf(minCount),
                    posX + 20 - textRenderer.getWidth(String.valueOf(minCount)),
                    posY + 4,
                    16777215,
                    true
            );
            context.drawText(
                    textRenderer,
                    "-" + maxCount,
                    posX + 20 - textRenderer.getWidth("-" + maxCount),
                    posY + 12,
                    16777215,
                    true
            );
        } else {
            context.drawText(
                    textRenderer,
                    range,
                    posX + 20 - textRenderer.getWidth(range),
                    posY + 12,
                    16777215,
                    true
            );
        }
        matrixStack.pop();
    }

    /**
     * This is both required and deprecated for some reason so it has to be here
     */
    @Override
    public List<Text> getTooltip(ItemStack ingredient, TooltipContext tooltipFlag) {
        return List.of();
    }


    public void getTooltip(ITooltipBuilder tooltip, ItemStack ingredient, TooltipContext tooltipFlag) {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        PlayerEntity player = minecraft.player;
        List<Text> components = ingredient.getTooltip(player, tooltipFlag);
        tooltip.addAll(components);
    }

}

