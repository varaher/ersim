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

    fun assessHeartRate(hr: Int, isPediatric: Boolean, onDarkMonitorWell: Boolean = true): ClinicalColorStyle {
        val (critLow, low, high, critHigh) = if (isPediatric) {
            listOf(60, 80, 145, 175)
        } else {
            listOf(45, 59, 101, 130)
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
        onDarkMonitorWell: Boolean = true
    ): ClinicalColorStyle {
        val (critLowSys, lowSys, highSys, critHighSys) = if (isPediatric) {
            listOf(70, 80, 125, 145)
        } else {
            listOf(85, 95, 145, 180)
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

    fun assessRespRate(rr: Int, isPediatric: Boolean, onDarkMonitorWell: Boolean = true): ClinicalColorStyle {
        val (critLow, low, high, critHigh) = if (isPediatric) {
            listOf(12, 16, 34, 46)
        } else {
            listOf(8, 11, 21, 28)
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

    fun assessRhythm(rhythmLabel: String): ClinicalSeverity {
        val r = rhythmLabel.lowercase()
        return when {
            r.contains("vf") || r.contains("v-fib") || r.contains("vt") || r.contains("v-tach") ||
                r.contains("torsades") || r.contains("asystole") || r.contains("pea") ||
                r.contains("sine wave") || r.contains("complete heart block") ||
                r.contains("stemi") || r.contains("st elevation") || r.contains("arrest") -> ClinicalSeverity.CRITICAL
            r.contains("tachy") || r.contains("afib") || r.contains("a-fib") ||
                r.contains("flutter") || r.contains("svt") || r.contains("block") -> ClinicalSeverity.HIGH
            r.contains("brady") -> ClinicalSeverity.LOW
            else -> ClinicalSeverity.NORMAL
        }
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
    fun criticalVitalTags(vitals: VitalSigns, isPediatric: Boolean): List<String> {
        val tags = mutableListOf<String>()
        if (assessBloodPressure(vitals.systolic, vitals.diastolic, isPediatric).isCritical) {
            tags.add("BP ${vitals.bpDisplay}")
        }
        if (assessHeartRate(vitals.hr, isPediatric).isCritical) {
            tags.add("HR ${vitals.hr}")
        }
        if (assessSpo2(vitals.spo2).isCritical) {
            tags.add("SpO₂ ${vitals.spo2?.let { "$it%" } ?: "None"}")
        }
        if (assessRespRate(vitals.rr, isPediatric).isCritical) {
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
