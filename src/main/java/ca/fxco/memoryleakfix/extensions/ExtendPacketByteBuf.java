package ca.fxco.memoryleakfix.extensions;

import io.netty.buffer.ByteBuf;

public interface ExtendPacketByteBuf {
    ByteBuf getParent();
}