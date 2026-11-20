package com.example.ui.components

import androidx.annotation.DrawableRes
import com.example.R
import com.example.data.ExamRegion
import com.example.data.PatientCharacter

// ============================================================================
// PATIENT BAY ART — ASSET SLOTS
// To add real Indian character art:
//   1. Drop image in res/drawable (e.g. img_bay_adult_male.webp)
//   2. Add one entry to characterSlots keyed by assetVariantKey or assetFamilyKey
//   3. isPlaceholder = false
// ============================================================================

/** Anchor in normalised image coordinates (0..1 of the source image). */
data class NormalizedAnchor(val x: Float, val y: Float)

data class BayArt(
    @DrawableRes val drawableRes: Int,
    val imageWidthPx: Int,
    val imageHeightPx: Int,
    val examAnchors: Map<ExamRegion, NormalizedAnchor>,
    /** true = generic art that does not depict this character. */
    val isPlaceholder: Boolean
)

object PatientBayArtRegistry {

    // Existing art (896 × 1200). Patient painted in; anchors traced on the images.
    private val fallbackAdult = BayArt(
        drawableRes = R.drawable.img_resus_bay_adult_1795157814594,
        imageWidthPx = 896,
        imageHeightPx = 1200,
        examAnchors = mapOf(
            ExamRegion.NEURO to NormalizedAnchor(0.400f, 0.280f),
            ExamRegion.HEENT to NormalizedAnchor(0.450f, 0.345f),
            ExamRegion.PULM to NormalizedAnchor(0.355f, 0.425f),
            ExamRegion.CARD to NormalizedAnchor(0.470f, 0.400f),
            ExamRegion.ABD to NormalizedAnchor(0.470f, 0.500f),
            ExamRegion.GENERAL to NormalizedAnchor(0.570f, 0.530f),
            ExamRegion.SKIN to NormalizedAnchor(0.730f, 0.555f)
        ),
        isPlaceholder = true
    )

    private val fallbackPeds = BayArt(
        drawableRes = R.drawable.img_resus_bay_peds_1795157817023,
        imageWidthPx = 896,
        imageHeightPx = 1200,
        examAnchors = mapOf(
            ExamRegion.NEURO to NormalizedAnchor(0.300f, 0.430f),
            ExamRegion.HEENT to NormalizedAnchor(0.385f, 0.470f),
            ExamRegion.PULM to NormalizedAnchor(0.320f, 0.525f),
            ExamRegion.CARD to NormalizedAnchor(0.420f, 0.515f),
            ExamRegion.ABD to NormalizedAnchor(0.430f, 0.600f),
            ExamRegion.GENERAL to NormalizedAnchor(0.555f, 0.620f),
            ExamRegion.SKIN to NormalizedAnchor(0.680f, 0.610f)
        ),
        isPlaceholder = true
    )

    /**
     * Real character art goes here. Families: neonate_*, infant_*, preschool_child_*,
     * school_child_*, adolescent_*, adult_*, older_adult_*, obstetric_female.
     */
    private val characterSlots: Map<String, BayArt> = emptyMap()

    /** [usePediatricBay] keeps the existing rule (OralBoardCase.isPediatricOrInfant) for fallback art. */
    fun artFor(character: PatientCharacter, usePediatricBay: Boolean): BayArt =
        characterSlots[character.assetVariantKey]
            ?: characterSlots[character.assetFamilyKey]
            ?: if (usePediatricBay) fallbackPeds else fallbackAdult
}
