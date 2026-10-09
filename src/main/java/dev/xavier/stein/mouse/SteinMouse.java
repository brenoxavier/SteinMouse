/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2026 Breno Xavier
 */

package dev.xavier.stein.mouse;

import java.util.HashSet;
import java.util.Set;

import dev.xavier.stein.loader.api.Inventory;
import dev.xavier.stein.loader.api.Keys;
import dev.xavier.stein.loader.api.Log;
import dev.xavier.stein.loader.api.ModContext;
import dev.xavier.stein.loader.api.SteinMod;

/**
 * Shift e arrastar no inventario, no baú e em qualquer tela de container: com o Shift segurado, o botao esquerdo
 * arrastando transfere a pilha de cada slot por onde o cursor passa. E o gesto que se faz dezenas de vezes ao esvaziar
 * um bau, e no jogo puro e um shift+clique por pilha.
 *
 * <p>Sempre ligado, sem opcoes e sem pagina no Painel: e um gesto so, e quem nao quer e so nao segurar o Shift. (A
 * roda que movia itens saiu na 0.1.4.)
 *
 * <p>Arrastar com o botao esquerdo ou com o direito <b>segurando</b> itens ja e do 1.8 (espalhar em partes iguais e
 * um por slot), entao isso fica com o jogo: dois gestos disputando o mesmo arrasto so dariam resultado incerto.
 *
 * <p>Tudo sai em cliques normais (Inventory.clickLater, o mesmo caminho do clique de verdade), entao o servidor ve
 * exatamente o que veria se a pessoa clicasse — nada de pacote inventado, e anticheat nao tem o que estranhar alem
 * da velocidade. Os cliques saem um por tick, pela fila do Loader, e a fila e da janela em que o clique foi pedido: se
 * outra janela abre, o que sobrou e descartado, e um slot de bau nunca cai noutra janela.
 */
public final class SteinMouse implements SteinMod {

    /** O id do mod: o dono da fila de cliques dele no Loader. */
    private static final String MOD = "steinmouse";

    private final ModContext ctx = ModContext.of(this);
    private final Log log = ctx.log();

    /** Teto da fila: gesto comprido nao vira fila infinita. */
    private static final int MAX_PENDING = 128;

    /** Botao segurado agora (0 esquerdo), ou -1. */
    private int held = -1;
    /** Slots ja transferidos neste arrasto: o cursor vai e volta, e a pilha nao pode ir duas vezes. */
    private final Set<Integer> done = new HashSet<>();
    /** Ha cliques deste mod na fila do Loader (para so descartar a fila quando ela e nossa). */
    private boolean queued;

    @Override
    public void afterStartGame() {
        log.info("iniciado (shift+arrastar)");
    }

    @Override
    public void onTickEnd() {
        if (!queued) {
            return;
        }
        if (Inventory.pendingClicks(MOD) == 0) {
            queued = false;
        } else if (!Inventory.isScreenOpen()) {
            // A tela fechou: os slots de la nao valem mais. Fechar o proprio inventario nao troca a janela (continua a
            // 0), entao a fila do Loader nao descartaria sozinha.
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
        return false;   // o clique em si continua sendo do jogo
    }

    /**
     * O arrasto e visto depois de cada quadro, e nao em cada evento de mouse: o slot sob o cursor (o que o jogo
     * destaca) e atualizado ao desenhar a tela. Olhando no evento, o slot seria o do quadro anterior, e parar o cursor
     * em cima de um slot nao o transferiria ate o proximo movimento.
     */
    @Override
    public void afterDrawScreen(Object screen, int mouseX, int mouseY, float partialTicks) {
        if (held == 0 && Keys.isMouseDown(0) && Keys.isShiftDown() && Inventory.isScreenOpen()
                && Inventory.windowId() >= 0) {
            dragged();
        }
    }

    /** Transfere o slot sob o cursor, uma vez por slot enquanto o botao nao solta. */
    private void dragged() {
        // Com itens na mao o shift+clique do jogo nao faz nada: o gesto seria so cliques a toa.
        if (Inventory.cursor() != null) {
            return;
        }
        int slot = Inventory.hoveredSlot();
        if (slot < 0 || Inventory.slotStack(slot) == null || !done.add(slot)) {
            return;
        }
        if (Inventory.pendingClicks(MOD) < MAX_PENDING) {
            Inventory.clickLater(MOD, slot, 0, Inventory.QUICK_MOVE);   // o mesmo do shift+clique
            queued = true;
        }
    }
}
