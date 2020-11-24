package com.skybeats.rtc.media;

public interface PackableEx extends Packable {
    void unmarshal(ByteBuf in);
}
