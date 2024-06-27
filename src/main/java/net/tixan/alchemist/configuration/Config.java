package net.tixan.alchemist.configuration;

import com.google.inject.Inject;
import lombok.Getter;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class Config extends ConfigBase {

    @Inject
    transient private MiniMessage miniMessage;

    private transient Set<Integer> filledSlotSet;

    protected void setFilledSlotSet() {
        filledSlotSet = Arrays
                .stream(super.fillers)
                .map(e -> e.slot)
                .collect(Collectors.toUnmodifiableSet());
    }
}
