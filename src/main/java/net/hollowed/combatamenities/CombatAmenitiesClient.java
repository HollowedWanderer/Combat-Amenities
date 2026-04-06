package net.hollowed.combatamenities;

//import com.zigythebird.playeranim.animation.PlayerAnimationController;
//import com.zigythebird.playeranim.api.PlayerAnimationFactory;
//import com.zigythebird.playeranimcore.enums.PlayState;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.hollowed.combatamenities.networking.slots.SlotClientPacket;
import net.hollowed.combatamenities.networking.slots.SoundPacket;
import net.hollowed.combatamenities.index.CAParticles;
import net.hollowed.combatamenities.index.CAKeyBindings;
import net.hollowed.combatamenities.util.delay.ClientTickDelayScheduler;
// import net.minecraft.resources.Identifier;

public class CombatAmenitiesClient implements ClientModInitializer {

    // public static Identifier ANIMATION_LAYER_ID = CombatAmenities.id("factory");

    @Override
    public void onInitializeClient() {
        CAKeyBindings.initialize();
        SlotClientPacket.registerClientPacket();
        SoundPacket.registerClientPacket();
        CAParticles.initializeClient();

        ClientTickEvents.END_CLIENT_TICK.register(_ -> ClientTickDelayScheduler.tick());

//        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(ANIMATION_LAYER_ID, 1000,
//                player -> new PlayerAnimationController(player,
//                        (controller, state, animSetter) -> PlayState.STOP
//                )
//        );
    }
}
