// Copyright 2012 Google Inc. All Rights Reserved.

package com.android.tools.sdkcontroller.lib;

/**
 * Contains declarations of constants that are tied to emulator implementation.
 * These constants can be changed only simultaneously in both places.
 */
public final class ProtocolConstants {
    /*
     * Constants related to data transfer.
     */

    /** Signature of a packet sent via SDK controller socket ('SDKC') */
    public static final int PACKET_SIGNATURE = 0x53444B43;

    /*
     * Header sizes for packets sent / received by SDK controller emulator.
     */

    /**
     * 12 bytes (3 ints) for the packet header:
     * <p/>
     * - Signature.
     * <p/>
     * - Total packet size.
     * <p/>
     * - Packet type.
     */
    public static final int PACKET_HEADER_SIZE = 12;
    /**
     * 16 bytes (4 ints) for the message header:
     * <p/>
     * - Common packet header.
     * <p/>
     * - Message type.
     */
    public static final int MESSAGE_HEADER_SIZE = 16;
    /**
     * 20 bytes (5 ints) for the query header:
     * <p/>
     * - Common packet header.
     * <p/>
     * - Query ID.
     * <p/>
     * - Query type.
     */
    public static final int QUERY_HEADER_SIZE = 20;
    /**
     * 16 bytes (4 ints) for the query response:
     * <p/>
     * - Common packet header.
     * <p/>
     * - Query ID.
     */
    public static final int QUERY_RESP_HEADER_SIZE = 16;

    /*
     * Types of packets transferred via SDK Controller channel.
     */

    /** Packet is a message. */
    public static final int PACKET_TYPE_MESSAGE = 1;
    /** Packet is a query. */
    public static final int PACKET_TYPE_QUERY = 2;
    /** Packet is a response to a query. */
    public static final int PACKET_TYPE_QUERY_RESPONSE = 3;

    /*
     * Constants related to handshake protocol between the emulator and a channel.
     */

    /**
     * Query type for a special "handshake" query.
     * <p/>
     * When emulator connects to SDK controller, the first thing that goes
     * through the socket is a special "handshake" query that delivers channel name
     * to the service.
     */
    public static final int QUERY_HANDSHAKE = -1;
    /**
     * Handshake query response on condition that service-side channel is available
     * (registered).
     */
    public static final int HANDSHAKE_RESP_CONNECTED = 0;
    /**
     * Handshake query response on condition that service-side channel is not
     * available (not registered).
     */
    public static final int HANDSHAKE_RESP_NOPORT = 1;
    /**
     * Handshake query response on condition that there is already an existing
     * emulator connection for this channel. Emulator should stop connection
     * attempts in this case.
     */
    public static final int HANDSHAKE_RESP_DUP = -1;
    /** Response to an unknown handshake query type. */
    public static final int HANDSHAKE_RESP_QUERY_UNKNOWN = -2;

    /*
     * Constants related to multi-touch emulation.
     */

    /** Received frame is JPEG image. */
    public static final int MT_FRAME_JPEG = 1;
    /** Received frame is RGB565 bitmap. */
    public static final int MT_FRAME_RGB565 = 2;
    /** Received frame is RGB888 bitmap. */
    public static final int MT_FRAME_RGB888 = 3;

    /** Pointer(s) moved. */
    public static final int MT_MOVE = 1;
    /** First pointer down message. */
    public static final int MT_FISRT_DOWN = 2;
    /** Last pointer up message. */
    public static final int MT_LAST_UP = 3;
    /** Pointer down message. */
    public static final int MT_POINTER_DOWN = 4;
    /** Pointer up message. */
    public static final int MT_POINTER_UP = 5;
    /** Sends framebuffer update. */
    public static final int MT_FB_UPDATE = 6;
    /** Frame buffer update has been received. */
    public static final int MT_FB_ACK = 7;
    /** Frame buffer update has been handled. */
    public static final int MT_FB_HANDLED = 8;
    /** Size of an event entry in the touch event message to the emulator. */
    public static final int MT_EVENT_ENTRY_SIZE = 16;

    /*
     * Constants related to sensor emulation.
     */

    /** Query type for a query that should return the list of available sensors. */
    public static final int SENSORS_QUERY_LIST = 1;
    /** Message that starts sensor emulation. */
    public static final int SENSORS_START = 1;
    /** Message that stops sensor emulation. */
    public static final int SENSORS_STOP = 2;
    /** Message that enables emulation of a particular sensor. */
    public static final int SENSORS_ENABLE = 3;
    /** Message that disables emulation of a particular sensor. */
    public static final int SENSORS_DISABLE = 4;
    /** Message that delivers sensor events to emulator. */
    public static final int SENSORS_SENSOR_EVENT = 5;
}
