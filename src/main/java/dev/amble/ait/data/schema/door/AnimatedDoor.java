package dev.amble.ait.data.schema.door;

import dev.amble.ait.core.tardis.animation.v2.bedrock.BedrockAnimationRegistry;

import java.util.Optional;

public interface AnimatedDoor {
	default Optional<BedrockAnimationRegistry.Reference> getLeftAnimation() {
		return Optional.empty();
	}

	default Optional<BedrockAnimationRegistry.Reference> getRightAnimation() {
		return Optional.empty();
	}
}
