/*
 * Copyright (c) 2026 Breno Xavier. All rights reserved.
 * Proprietary software: see the LICENSE file at the root of this repository.
 */

package dev.xavier.stein.mouse;

import dev.xavier.stein.loader.api.Inventory;
import dev.xavier.stein.loader.api.Keys;
import dev.xavier.stein.loader.api.ModContext;
import dev.xavier.stein.loader.api.SteinMod;

import java.util.BitSet;

/**
 * Shift and drag in the inventory, a chest or any other container screen: with Shift held, dragging
 * with the left button transfers the stack of every slot the cursor passes over. It is the gesture
 * players repeat dozens of times when emptying a chest, which in the vanilla game takes one
 * shift-click per stack.
 *
 * <p>Always on, with no options and no Panel page: it is a single gesture, and a player who does
 * not want it simply does not hold Shift. (The mouse wheel item moving was removed in 0.1.4.)
 *
 * <p>Dragging with a button while <b>holding</b> items is already a 1.8 gesture (spread evenly, or
 * one per slot), so that stays with the game: two gestures fighting over the same drag would only
 * give unpredictable results.
 *
 * <p>Every transfer is a normal click ({@link Inventory#clickLater}, the same path as a real
 * click), so the server sees exactly what it would see if the player clicked: no invented packets.
 * The clicks go out one per tick through the Loader's queue, and the queue belongs to the window
 * the click was asked for: if another window opens, what is left is dropped, so a chest slot never
 * lands in another window.
 */
public final class SteinMouse implements SteinMod {

    /** The mod id: the owner of this mod's click queue in the Loader. */
    private static final String MOD = "steinmouse";

    /** Queue cap: a long gesture does not become an endless queue. */
    private static final int MAX_PENDING = 128;

    private final ModContext ctx = ModContext.of(this);

    /** The button held right now (0 is the left one), or -1. */
    private int held = -1;

    /**
     * Slots already transferred in this drag: the cursor goes back and forth, and a stack must not
     * move twice. A BitSet, because it is checked every frame while dragging.
     */
    private final BitSet done = new BitSet();

    /** This mod has clicks in the Loader's queue (so only our own queue is ever dropped). */
    private boolean queued;

    @Override
    public void afterStartGame() {
        ctx.log().info("started (shift and drag)");
    }

    @Override
    public void onTickEnd() {
        if (!queued) {
            return;
        }
        if (Inventory.pendingClicks(MOD) == 0) {
            queued = false;
        } else if (!Inventory.isScreenOpen()) {
            // The screen closed: its slots no longer apply. Closing the player's own inventory does
            // not change the window (it stays 0), so the Loader's queue would not drop them by
            // itself.
            Inventory.clearPending(MOD);
            queued = false;
        }
    }

    @Override
    public boolean onGuiMouseInput(Object screen) {
        if (!Inventory.isScreenOpen()) {
            held = -1;
            return false;
        }
        int button = Keys.mouseButton();
        if (button >= 0) {
            held = Keys.mousePressed() ? button : -1;
            done.clear();
        }
        return false; // the click itself still belongs to the game
    }

    /**
     * The drag is looked at after every frame, not on every mouse event: the slot under the cursor
     * (the one the game highlights) is updated while the screen is drawn. Looking at it in the
     * event would give the previous frame's slot, and resting the cursor on a slot would not
     * transfer it until the next movement.
     */
    @Override
    public void afterDrawScreen(Object screen, int mouseX, int mouseY, float partialTicks) {
        if (held == 0
                && Keys.isMouseDown(0)
                && Keys.isShiftDown()
                && Inventory.isScreenOpen()
                && Inventory.windowId() >= 0) {
            dragged();
        }
    }

    /** Transfers the slot under the cursor, once per slot until the button is released. */
    private void dragged() {
        // With items on the cursor the game's shift-click does nothing: the gesture would only
        // send useless clicks.
        if (Inventory.cursor() != null) {
            return;
        }
        int slot = Inventory.hoveredSlot();
        if (slot < 0 || Inventory.slotStack(slot) == null || done.get(slot)) {
            return;
        }
        done.set(slot);
        if (Inventory.pendingClicks(MOD) < MAX_PENDING) {
            // The same as a shift-click.
            Inventory.clickLater(MOD, slot, 0, Inventory.QUICK_MOVE);
            queued = true;
        }
    }
}
