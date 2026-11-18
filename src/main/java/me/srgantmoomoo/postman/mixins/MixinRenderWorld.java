package me.srgantmoomoo.postman.mixins;
import me.srgantmoomoo.postman.module.modules.render.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import net.minecraft.world.World;
import net.minecraft.client.world.ClientWorld;
@Mixin(World.class)
public class MixinRenderWorld {
    @Inject(method="getTimeOfDay",at=@At("HEAD"),cancellable=true,require=0)
    private void postman$time(CallbackInfoReturnable<Long> cir){
        if(!((Object)this instanceof ClientWorld))return;
        var w=RenderSupport.enabled("world",me.srgantmoomoo.postman.module.modules.render.World.class);
        if(w!=null)cir.setReturnValue((long)w.time.getValue());
    }
    @Inject(method="getRainGradient",at=@At("HEAD"),cancellable=true,require=0)
    private void postman$rain(float delta,CallbackInfoReturnable<Float> cir){
        if(!((Object)this instanceof ClientWorld))return;
        var nr=RenderSupport.enabled("noRender",NoRender.class);
        if(nr!=null&&nr.rain.isEnabled()){cir.setReturnValue(0f);return;}
        var w=RenderSupport.enabled("world",me.srgantmoomoo.postman.module.modules.render.World.class);
        if(w!=null)cir.setReturnValue(w.weather.is("clear")?0f:1f);
    }
    @Inject(method="getThunderGradient",at=@At("HEAD"),cancellable=true,require=0)
    private void postman$thunder(float delta,CallbackInfoReturnable<Float> cir){
        if(!((Object)this instanceof ClientWorld))return;
        var nr=RenderSupport.enabled("noRender",NoRender.class);
        if(nr!=null&&nr.rain.isEnabled()){cir.setReturnValue(0f);return;}
        var w=RenderSupport.enabled("world",me.srgantmoomoo.postman.module.modules.render.World.class);
        if(w!=null)cir.setReturnValue(w.weather.is("thunderStorm")?1f:0f);
    }
}
