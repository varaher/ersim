package com.example.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.data.LabFlag
import com.example.data.VitalSigns

// ============================================================================
// CLINICAL SEVERITY & VISUAL COLOR ENGINE
// Evaluates vitals (adult & paediatric ranges), cardiac rhythms, and lab flags
// so every critical, high, low, and normal clinical value has an unmistakable,
// consistent color signature across monitors, tablets, drawers, and charts.
// ============================================================================

enum class ClinicalSeverity(val shortBadge: String, val label: String) {
    CRITICAL("CRIT", "Critical"),
    HIGH("HIGH", "Elevated"),
    LOW("LOW", "Low"),
    NORMAL("OK", "Normal")
}

data class ClinicalColorStyle(
    val severity: ClinicalSeverity,
    val badgeText: String,
    val accent: Color,
    val containerTint: Color,
    val borderColor: Color
) {
    val isCritical: Boolean get() = severity == ClinicalSeverity.CRITICAL
    val isAbnormal: Boolean get() = severity != ClinicalSeverity.NORMAL
}

object ClinicalSeverityEngine {

    /**
     * Builds a color style. When [onDarkMonitorWell] is true (e.g. bedside monitor tiles),
     * always uses bright neon monitor colors regardless of app light/dark theme.
     */
    fun styleFor(
        severity: ClinicalSeverity,
        badgeOverride: String? = null,
        onDarkMonitorWell: Boolean = false
    ): ClinicalColorStyle {
        val accent = when (severity) {
            ClinicalSeverity.CRITICAL -> if (onDarkMonitorWell) MonitorRed else ErSimRed
            ClinicalSeverity.HIGH -> if (onDarkMonitorWell) MonitorAmber else ErSimAmber
            ClinicalSeverity.LOW -> if (onDarkMonitorWell) MonitorSky else ErSimSky
            ClinicalSeverity.NORMAL -> if (onDarkMonitorWell) MonitorMint else ErSimMint
        }
        val bgAlpha = when (severity) {
            ClinicalSeverity.CRITICAL -> if (onDarkMonitorWell) 0.22f else 0.16f
            ClinicalSeverity.HIGH -> if (onDarkMonitorWell) 0.15f else 0.12f
            ClinicalSeverity.LOW -> if (onDarkMonitorWell) 0.15f else 0.12f
            ClinicalSeverity.NORMAL -> if (onDarkMonitorWell) 0.08f else 0.07f
        }
        val borderAlpha = when (severity) {
            ClinicalSeverity.CRITICAL -> 0.95f
            ClinicalSeverity.HIGH -> 0.75f
            ClinicalSeverity.LOW -> 0.75f
            ClinicalSeverity.NORMAL -> 0.42f
        }
        return ClinicalColorStyle(
            severity = severity,
            badgeText = badgeOverride ?: severity.shortBadge,
            accent = accent,
            containerTint = accent.copy(alpha = bgAlpha),
            borderColor = accent.copy(alpha = borderAlpha)
        )
    }

    fun assessHeartRate(
        hr: Int,
        isPediatric: Boolean,
        onDarkMonitorWell: Boolean = true,
        ageYears: Double? = null
    ): ClinicalColorStyle {
        val (critLow, low, high, critHigh) = when {
            isPediatric && ageYears != null && ageYears < 18 -> PaedsBands.heartRate(ageYears)
            isPediatric -> listOf(60, 80, 145, 175)
            else -> listOf(45, 59, 101, 130)
        }
        return when {
            hr == 0 -> styleFor(ClinicalSeverity.CRITICAL, "ARREST", onDarkMonitorWell)
            hr < critLow -> styleFor(ClinicalSeverity.CRITICAL, "CRIT LOW", onDarkMonitorWell)
            hr >= critHigh -> styleFor(ClinicalSeverity.CRITICAL, "CRIT HIGH", onDarkMonitorWell)
            hr > high -> styleFor(ClinicalSeverity.HIGH, "TACHY", onDarkMonitorWell)
            hr < low -> styleFor(ClinicalSeverity.LOW, "BRADY", onDarkMonitorWell)
            else -> styleFor(ClinicalSeverity.NORMAL, "NORMAL", onDarkMonitorWell)
        }
    }

    fun assessBloodPressure(
        systolic: Int,
        diastolic: Int,
        isPediatric: Boolean,
        onDarkMonitorWell: Boolean = true,
        ageYears: Double? = null
    ): ClinicalColorStyle {
        val (critLowSys, lowSys, highSys, critHighSys) = when {
            isPediatric && ageYears != null && ageYears < 18 -> PaedsBands.systolic(ageYears)
            isPediatric -> listOf(70, 80, 125, 145)
            else -> listOf(85, 95, 145, 180)
        }
        return when {
            systolic <= 0 -> styleFor(ClinicalSeverity.CRITICAL, "NO BP", onDarkMonitorWell)
            systolic < critLowSys -> styleFor(ClinicalSeverity.CRITICAL, "SHOCK", onDarkMonitorWell)
            systolic >= critHighSys || diastolic >= 115 -> styleFor(ClinicalSeverity.CRITICAL, "HTN CRIT", onDarkMonitorWell)
            systolic < lowSys -> styleFor(ClinicalSeverity.LOW, "HYPO", onDarkMonitorWell)
            systolic >= highSys || diastolic >= 95 -> styleFor(ClinicalSeverity.HIGH, "HIGH BP", onDarkMonitorWell)
            else -> styleFor(ClinicalSeverity.NORMAL, "NORMAL", onDarkMonitorWell)
        }
    }

    fun assessSpo2(spo2: Int?, onDarkMonitorWell: Boolean = true): ClinicalColorStyle {
        return when {
            spo2 == null -> styleFor(ClinicalSeverity.CRITICAL, "UNREAD", onDarkMonitorWell)
            spo2 < 90 -> styleFor(ClinicalSeverity.CRITICAL, "HYPOXIA", onDarkMonitorWell)
            spo2 < 94 -> styleFor(ClinicalSeverity.LOW, "LOW O₂", onDarkMonitorWell)
            else -> styleFor(ClinicalSeverity.NORMAL, "NORMAL", onDarkMonitorWell)
        }
    }

    fun assessRespRate(
        rr: Int,
        isPediatric: Boolean,
        onDarkMonitorWell: Boolean = true,
        ageYears: Double? = null
    ): ClinicalColorStyle {
        val (critLow, low, high, critHigh) = when {
            isPediatric && ageYears != null && ageYears < 18 -> PaedsBands.respRate(ageYears)
            isPediatric -> listOf(12, 16, 34, 46)
            else -> listOf(8, 11, 21, 28)
        }
        return when {
            rr <= 0 -> styleFor(ClinicalSeverity.CRITICAL, "APNEA", onDarkMonitorWell)
            rr <= critLow -> styleFor(ClinicalSeverity.CRITICAL, "CRIT LOW", onDarkMonitorWell)
            rr >= critHigh -> styleFor(ClinicalSeverity.CRITICAL, "CRIT HIGH", onDarkMonitorWell)
            rr > high -> styleFor(ClinicalSeverity.HIGH, "TACHYP", onDarkMonitorWell)
            rr < low -> styleFor(ClinicalSeverity.LOW, "LOW RR", onDarkMonitorWell)
            else -> styleFor(ClinicalSeverity.NORMAL, "NORMAL", onDarkMonitorWell)
        }
    }

    fun assessTemp(tempC: Float, onDarkMonitorWell: Boolean = true): ClinicalColorStyle {
        return when {
            tempC < 35.0f -> styleFor(ClinicalSeverity.CRITICAL, "HYPOTHERM", onDarkMonitorWell)
            tempC >= 39.5f -> styleFor(ClinicalSeverity.CRITICAL, "HYPERPYR", onDarkMonitorWell)
            tempC >= 38.0f -> styleFor(ClinicalSeverity.HIGH, "FEVER", onDarkMonitorWell)
            tempC < 36.0f -> styleFor(ClinicalSeverity.LOW, "COOL", onDarkMonitorWell)
            else -> styleFor(ClinicalSeverity.NORMAL, "NORM", onDarkMonitorWell)
        }
    }

    fun assessEtco2(etco2: Int, onDarkMonitorWell: Boolean = true): ClinicalColorStyle {
        return when {
            etco2 < 20 || etco2 > 55 -> styleFor(ClinicalSeverity.CRITICAL, "CRIT", onDarkMonitorWell)
            etco2 > 45 -> styleFor(ClinicalSeverity.HIGH, "HIGH CO₂", onDarkMonitorWell)
            etco2 < 32 -> styleFor(ClinicalSeverity.LOW, "LOW CO₂", onDarkMonitorWell)
            else -> styleFor(ClinicalSeverity.NORMAL, "NORM", onDarkMonitorWell)
        }
    }

    private val criticalRhythmWords = Regex("""\b(vf|vt|pvt|pea)\b""")

    fun assessRhythm(rhythmLabel: String): ClinicalSeverity {
        val r = rhythmLabel.lowercase()
        return when {
            // Whole-word abbreviations only, so "repeat" never matches "pea"
            criticalRhythmWords.containsMatchIn(r) ||
                r.contains("ventricular fibrillation") || r.contains("ventricular tachycardia") ||
                r.contains("v-fib") || r.contains("v-tach") || r.contains("torsades") ||
                r.contains("asystole") || r.contains("pulseless") || r.contains("no pulse") ||
                r.contains("sine wave") || r.contains("complete heart block") ||
                r.contains("third degree") || r.contains("third-degree") || r.contains("3rd degree") ||
                r.contains("stemi") || r.contains("st elevation") || r.contains("arrest") -> ClinicalSeverity.CRITICAL
            r.contains("tachy") || r.contains("afib") || r.contains("a-fib") || r.contains("atrial fibrillation") ||
                r.contains("flutter") || r.contains("svt") || r.contains("block") -> ClinicalSeverity.HIGH
            r.contains("brady") -> ClinicalSeverity.LOW
            else -> ClinicalSeverity.NORMAL
        }
    }

    /** Approximate age in years for a classic case, from its age description. */
    fun ageYearsOf(case: com.example.data.OralBoardCase): Double =
        when (com.example.data.PatientCharacterRegistry.ageBandFor(case)) {
            com.example.data.PatientAgeBand.NEONATE -> 0.04
            com.example.data.PatientAgeBand.INFANT -> 0.5
            com.example.data.PatientAgeBand.PRESCHOOL_CHILD -> 3.0
            com.example.data.PatientAgeBand.SCHOOL_CHILD -> 8.0
            com.example.data.PatientAgeBand.ADOLESCENT -> 14.0
            com.example.data.PatientAgeBand.ADULT -> 40.0
            com.example.data.PatientAgeBand.OLDER_ADULT -> 70.0
        }

    fun labStyle(flag: LabFlag, onDarkMonitorWell: Boolean = false): ClinicalColorStyle {
        return when (flag) {
            LabFlag.CRITICAL -> styleFor(ClinicalSeverity.CRITICAL, "CRITICAL", onDarkMonitorWell)
            LabFlag.HIGH -> styleFor(ClinicalSeverity.HIGH, "HIGH", onDarkMonitorWell)
            LabFlag.LOW -> styleFor(ClinicalSeverity.LOW, "LOW", onDarkMonitorWell)
            LabFlag.NORMAL -> styleFor(ClinicalSeverity.NORMAL, "NORMAL", onDarkMonitorWell)
        }
    }

    /** Returns concise labels of all currently critical vitals for the top alert strip. */
    fun criticalVitalTags(vitals: VitalSigns, isPediatric: Boolean, ageYears: Double? = null): List<String> {
        val tags = mutableListOf<String>()
        if (assessBloodPressure(vitals.systolic, vitals.diastolic, isPediatric, ageYears = ageYears).isCritical) {
            tags.add("BP ${vitals.bpDisplay}")
        }
        if (assessHeartRate(vitals.hr, isPediatric, ageYears = ageYears).isCritical) {
            tags.add("HR ${vitals.hr}")
        }
        if (assessSpo2(vitals.spo2).isCritical) {
            tags.add("SpO₂ ${vitals.spo2?.let { "$it%" } ?: "None"}")
        }
        if (assessRespRate(vitals.rr, isPediatric, ageYears = ageYears).isCritical) {
            tags.add("RR ${vitals.rr}")
        }
        if (assessTemp(vitals.tempCelsius).isCritical) {
            tags.add("T ${vitals.tempCelsius}°C")
        }
        if (assessRhythm(vitals.rhythmLabel) == ClinicalSeverity.CRITICAL) {
            tags.add(vitals.rhythmLabel)
        }
        return tags
    }

    /** Semantic accent color for case cards and category headers. */
    fun categoryAccent(category: String, isPediatric: Boolean): Color {
        val c = category.lowercase()
        return when {
            c.contains("cardiac") || c.contains("chest") || c.contains("arrest") ||
                c.contains("shock") || c.contains("torsades") || c.contains("hyperkal") -> ErSimRed
            c.contains("resp") || c.contains("airway") || c.contains("pneumo") ||
                c.contains("breath") || c.contains("asthma") -> ErSimSky
            c.contains("neuro") || c.contains("stroke") || c.contains("head") ||
                c.contains("spine") || c.contains("eye") || c.contains("glaucoma") -> ErSimViolet
            c.contains("tox") || c.contains("overdose") || c.contains("poison") ||
                c.contains("enven") || c.contains("snake") || c.contains("iron") -> ErSimOrange
            isPediatric -> ErSimRose
            c.contains("abdom") || c.contains("gi") || c.contains("sepsis") ||
                c.contains("trauma") || c.contains("infect") -> ErSimAmber
            else -> ErSimMint
        }
    }
}

/**
 * Age-specific paediatric alarm bands: listOf(critLow, low, high, critHigh).
 * Normal ranges follow PALS (AHA 2020) awake values; critical bradycardia is HR < 60
 * (the CPR threshold in infants/children); hypotension is the PALS SBP limit
 * (<60 neonate, <70 infant, <70 + 2×age 1–10 y, <90 over 10 y).
 * Clinical reviewer to confirm before APPROVED.
 */
object PaedsBands {
    private const val NEONATE_MAX_YEARS = 28.0 / 365.0

    fun heartRate(age: Double): List<Int> = when {
        age < NEONATE_MAX_YEARS -> listOf(60, 100, 205, 220)
        age < 1 -> listOf(60, 100, 180, 220)
        age < 3 -> listOf(60, 98, 140, 180)
        age < 6 -> listOf(60, 80, 120, 180)
        age < 12 -> listOf(50, 75, 118, 160)
        else -> listOf(45, 60, 100, 140)
    }

    fun respRate(age: Double): List<Int> = when {
        age < NEONATE_MAX_YEARS -> listOf(20, 30, 60, 70)
        age < 1 -> listOf(20, 30, 53, 65)
        age < 3 -> listOf(15, 22, 37, 50)
        age < 6 -> listOf(12, 20, 28, 40)
        age < 12 -> listOf(10, 18, 25, 35)
        else -> listOf(8, 12, 20, 30)
    }

    fun systolic(age: Double): List<Int> {
        val hypotension = when {
            age < NEONATE_MAX_YEARS -> 60
            age < 1 -> 70
            age <= 10 -> (70 + 2 * age).toInt()
            else -> 90
        }
        val (high, critHigh) = if (age < 12) 125 to 145 else 135 to 160
        return listOf(hypotension, hypotension + 10, high, critHigh)
    }
}
