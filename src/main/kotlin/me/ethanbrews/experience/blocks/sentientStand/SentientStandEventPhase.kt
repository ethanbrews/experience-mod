package me.ethanbrews.experience.blocks.sentientStand

enum class SentientStandEventPhase {
    /** The SETUP phase lasts one tick and occurs immediately upon ritual trigger */
    SETUP,
    /** The CHARGING phase lasts [SentientStandEntity.SETUP_INTERVAL] ticks. It follows the [SETUP] phase */
    CHARGING,
    /** The FINISH phase lasts [SentientStandEntity.FINISH_INTERVAL] ticks. It follows the [CONSUME] phase */
    FINISH,
    /** The CONSUME phase lasts [SentientStandEntity.ITEM_INTERVAL] ticks for each ingredient item. It follows the [CHARGING] phase */
    CONSUME,
    /** The FINISHED phase is the final phase, lasting one tick. It follows the [FINISH] phase. */
    FINISHED
}