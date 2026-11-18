package dev.devce.rocketnautics.content.sputnik.comms;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Server-global channel registry used by the Sputnik Redstone Receiver/Transmitter nodes.
 * Signals expire if the transmitter stops refreshing them for more than 2 ticks.
 */
public final class WirelessRedstoneManager {
    public static class ChannelData {
        public int strength;
        public long lastGameTime;
    }

    private static final Map<String, ChannelData> CHANNELS = new ConcurrentHashMap<>();

    private WirelessRedstoneManager() {}

    public static void transmit(String channel, int strength, long gameTime) {
        if (channel == null || channel.isEmpty()) return;
        if (strength <= 0) {
            CHANNELS.remove(channel);
        } else {
            ChannelData data = CHANNELS.get(channel);
            if (data == null) {
                data = new ChannelData();
                CHANNELS.put(channel, data);
            }
            data.strength = Math.max(0, Math.min(15, strength));
            data.lastGameTime = gameTime;
        }
    }

    public static int receive(String channel, long gameTime) {
        if (channel == null || channel.isEmpty()) return 0;
        ChannelData data = CHANNELS.get(channel);
        if (data == null) return 0;
        if (gameTime > 0 && data.lastGameTime > 0 && (gameTime - data.lastGameTime) > 2) {
            CHANNELS.remove(channel);
            return 0;
        }
        return data.strength;
    }
}
