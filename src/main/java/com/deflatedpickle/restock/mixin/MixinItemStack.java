/* Copyright (c) 2021-2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.restock.mixin;

import com.deflatedpickle.restock.Restock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("UnusedMixin")
@Mixin(ItemStack.class)
public abstract class MixinItemStack {
  @Shadow private int count;
  public int preCount = 0;

  @Inject(method = "useOnBlock", at = @At("HEAD"))
  public void preUseOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
    preCount = this.count;
  }

  @Inject(method = "useOnBlock", at = @At("TAIL"))
  public void postUseOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
    Restock.INSTANCE.count(preCount, context);
  }
}
