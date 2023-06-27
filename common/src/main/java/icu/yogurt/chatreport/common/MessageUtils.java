package icu.yogurt.chatreport.common;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.List;

public class MessageUtils {
    public static void sendMessage(Audience audience, String message) {
        // Create a message component builder
        ComponentBuilder<TextComponent, TextComponent.Builder> builder = Component.text();

        // Split the message into blocks by prefix
        String[] blocks = message.split("(?=text:|hover:|action:)");

        // Process each block separately
        for (String block : blocks) {
            // Find the prefix in this block
            PrefixType prefix = getPrefix(block);

            // If no prefix was found, this is a regular block
            if (prefix == null) {
                builder.append(LegacyComponentSerializer.legacyAmpersand().deserialize(block));
                continue;
            }

            // Remove the prefix from the block
            String specialPart = block.substring(prefix.getLabel().length());

            // Process the special part according to the prefix
            switch (prefix) {
                case TEXT:
                    appendText(builder, specialPart);
                    break;
                case HOVER:
                    appendHover(builder, specialPart);
                    break;
                case ACTION:
                    appendAction(builder, specialPart);
                    break;
            }
        }

        // Build the message component and send it to the audience
        Component messageComponent = builder.build();
        audience.sendMessage(messageComponent);
    }


    private static PrefixType getPrefix(String block) {
        for (PrefixType prefix : PrefixType.values()) {
            if (block.startsWith(prefix.getLabel())) {
                return prefix;
            }
        }
        return null;
    }

    private static void appendText(ComponentBuilder<TextComponent, TextComponent.Builder> builder, String specialPart) {
        builder.append(LegacyComponentSerializer.legacyAmpersand().deserialize(specialPart));
    }

    private static void appendHover(ComponentBuilder<TextComponent, TextComponent.Builder> builder, String specialPart) {
        builder.append(Component.empty())
                .hoverEvent(HoverEvent.showText(LegacyComponentSerializer.legacyAmpersand().deserialize(specialPart)));
    }

    private static void appendAction(ComponentBuilder<TextComponent, TextComponent.Builder> builder, String specialPart) {
        String[] actionParts = specialPart.split(":", 2);
        if (actionParts.length == 2) {
            ClickEvent.Action action = ClickEvent.Action.valueOf(actionParts[0].toUpperCase());
            String value = actionParts[1];
            builder.append(Component.empty())
                    .clickEvent(ClickEvent.clickEvent(action, value));
        }
    }



    public static void sendMessage(Audience audience, List<String> messages) {
        for (String message : messages) {
            sendMessage(audience, message);
        }
    }

    private enum PrefixType {
        TEXT("text:"),
        HOVER("hover:"),
        ACTION("action:");

        private final String label;

        PrefixType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

    }
}
