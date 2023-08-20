package com.dephoegon.delchoco.common.items;

import com.dephoegon.delbase.aid.util.kb;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ChocoboLeashPointer extends Item {
    private String tip1;
    private String tip2;
    private BlockPos centerPoint;
    private BlockPos leashPoint;
    private int leashDistance;
    public ChocoboLeashPointer(Item.Settings pProperties) {
        super(pProperties);
    }
    public BlockPos getCenterPoint() { return this.centerPoint; }
    public BlockPos getLeashPoint() { return this.leashPoint; }
    public int getLeashDistance() { return this.leashDistance; }
    @Override
    public void appendTooltip(@NotNull ItemStack stack, @Nullable World level, @NotNull List<Text> toolTip, @NotNull TooltipContext flag) {
        super.appendTooltip(stack, level, toolTip, flag);
        if (centerPoint != null) {
            this.tip1 = new TranslatableText("delchoco.leash.center").getString() + " : X - "+this.centerPoint.getX()+" || Z - "+this.centerPoint.getZ();
            if (leashPoint != null) {
                int leash = this.leashDistance;
                tip2 = new TranslatableText("delchoco.leash.spot").getString() + " - "+leash;
            }
        } else if (this.leashPoint != null) { this.tip2 = new TranslatableText("delchoco.leash.dist").getString() + " : X - "+ this.leashPoint.getX()+" || Z - "+ this.leashPoint.getZ()+" || Y~ - "+ this.leashPoint.getY(); }
        String hybridTip0 = new TranslatableText("delchoco.leash.none").getString();
        if (this.tip1 != null) {
            if (this.tip2 != null) { hybridTip0 = new TranslatableText("delchoco.leash.center").getString() + " & " + new TranslatableText("delchoco.leash.dist").getString(); }
            else { hybridTip0 = new TranslatableText("delchoco.leash.dist").getString(); }
        } else if (this.tip2 != null) { hybridTip0 = new TranslatableText("delchoco.leash.dist").getString(); }
        if(!kb.HShift() && !kb.HCtrl()) toolTip.add(new LiteralText(hybridTip0)); //if neither pressed, show tip0 (if not empty)
        if(kb.HCtrl() && this.tip2 != null) toolTip.add(new TranslatableText(this.tip2)); //if ctrl, show tip2 (if not empty), do first
        if(kb.HShift() && this.tip1 != null) toolTip.add(new TranslatableText(this.tip1)); //if shifted, show tip1 (if not empty)
    }
    public @NotNull ActionResult useOnBlock(@NotNull ItemUsageContext context) {
        boolean shift = context.getPlayer() != null && context.getPlayer().isSneaking();
        BlockPos selection = null;
        String line = " ";
        boolean invalid = false;
        if (shift) {
            this.leashPoint = context.getBlockPos();
            selection = this.leashPoint;
            line = new TranslatableText("delchoco.leash.dist").getString();
        } else {
            this.centerPoint = context.getBlockPos();
            selection = this.centerPoint;
            line = new TranslatableText("delchoco.leash.center").getString();
        }
        if (this.centerPoint != null && this.leashPoint != null) {
            this.leashDistance = Math.max(positiveDifference(this.centerPoint.getX(), this.leashPoint.getX()), positiveDifference(this.centerPoint.getZ(), this.leashPoint.getZ()));
            if (this.leashDistance < 6 || this.leashDistance > 40) {
                line = String.valueOf(this.leashDistance);
                invalid = true;
                this.leashDistance = 40;
                this.leashPoint = null;
                this.centerPoint = null;
            }
        }
        if (invalid) {
            String out = new TranslatableText("delchoco.leash.invalid").getString()+" "+ line;
            context.getPlayer().sendMessage(new LiteralText(out), true);
        } else {
            context.getPlayer().sendMessage(new LiteralText(line+" @ X:"+ selection.getX()+" Z:"+selection.getZ()+" Y:"+selection.getY() ), true);
        }
        return ActionResult.PASS;
    }
    protected static int positiveDifference(int center, int leash) {
        return center < leash ? leash - center : center - leash;
    }
}