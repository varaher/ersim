package com.example.data

// ============================================================================
// INDIAN PATIENT CHARACTER SYSTEM V1
// Pure data layer (no Android/R references) so it can be reused by the
// Compose UI now and by voice / XR front ends later.
// Source of truth stays OralBoardCase. This file adds no clinical facts.
// ============================================================================

enum class PatientAgeBand(val label: String, val isPediatric: Boolean) {
    NEONATE("Neonate (<28 days)", true),
    INFANT("Infant", true),
    PRESCHOOL_CHILD("Preschool child (1–5 y)", true),
    SCHOOL_CHILD("School-aged child (6–11 y)", true),
    ADOLESCENT("Adolescent (12–17 y)", true),
    ADULT("Adult", false),
    OLDER_ADULT("Older adult (65+)", false)
}

enum class PatientSex { MALE, FEMALE }

/** Baseline (healthy) skin tone of the character art. Appearance only. */
enum class BaselineSkinTone(val label: String) {
    TONE_LIGHT_BROWN("Light brown"),
    TONE_MEDIUM_BROWN("Medium brown"),
    TONE_BROWN("Brown"),
    TONE_DEEP_BROWN("Deep brown")
}

enum class BodyBuild { SLIM, AVERAGE, HEAVY }

/** Observable presentation sign from OralBoardCase.patientSkinToneTag. Rendering must respect Q1 gating. */
enum class ClinicalSkinSign { NORMAL, CYANOTIC, PALE, FLUSHED, JAUNDICED;
    companion object {
        fun fromTag(tag: String): ClinicalSkinSign =
            entries.firstOrNull { it.name.equals(tag.trim(), ignoreCase = true) } ?: NORMAL
    }
}

enum class Historian(val label: String) {
    SELF("Patient"),
    MOTHER("Mother"),
    FATHER("Father"),
    PARENT_AND_SELF("Patient + parent"),
    SON("Son"),
    DAUGHTER("Daughter"),
    SPOUSE("Spouse")
}

/** Exam regions. [key] MUST match the existing performedExamKeys codes used by the workspace tab. */
enum class ExamRegion(val key: String, val title: String) {
    GENERAL("GENERAL", "General Appearance"),
    HEENT("HEENT", "HEENT & Neck"),
    PULM("PULM", "Chest & Lungs"),
    CARD("CARD", "Cardiovascular"),
    ABD("ABD", "Abdomen, Pelvis & GU"),
    SKIN("SKIN", "Extremities & Skin"),
    NEURO("NEURO", "Neurologic");

    fun findingFor(exam: PhysicalExamClues): String = when (this) {
        GENERAL -> exam.generalAppearance
        HEENT -> exam.heentAndNeck
        PULM -> exam.chestAndLungs
        CARD -> exam.cardiovascular
        ABD -> exam.abdomenAndPelvis
        SKIN -> exam.extremitiesAndSkin
        NEURO -> exam.neurologic
    }
}

/** Authored, non-clinical identity data per case. */
data class IndianPatientProfile(
    val caseNumber: Int,
    val regionState: String,
    val primaryLanguage: String,
    val historian: Historian,
    val baselineSkinTone: BaselineSkinTone,
    val isObstetric: Boolean = false
)

/** Fully resolved character for rendering / voice. */
data class PatientCharacter(
    val caseNumber: Int,
    val displayName: String,
    val ageBand: PatientAgeBand,
    val sex: PatientSex,
    val baselineSkinTone: BaselineSkinTone,
    val build: BodyBuild,
    val clinicalSkinSign: ClinicalSkinSign,
    val profile: IndianPatientProfile
) {
    /** e.g. "adult_male", "neonate_female", "obstetric_female" */
    val assetFamilyKey: String
        get() = if (profile.isObstetric) "obstetric_female"
        else "${ageBand.name.lowercase()}_${sex.name.lowercase()}"

    /** e.g. "adult_male_tone_brown_heavy" */
    val assetVariantKey: String
        get() = "${assetFamilyKey}_${baselineSkinTone.name.lowercase()}_${build.name.lowercase()}"
}

object PatientCharacterRegistry {

    private val profiles: Map<Int, IndianPatientProfile> = listOf(
        IndianPatientProfile(1, "Kerala", "Malayalam", Historian.SELF, BaselineSkinTone.TONE_MEDIUM_BROWN),
        IndianPatientProfile(2, "Kerala", "Malayalam", Historian.MOTHER, BaselineSkinTone.TONE_BROWN),
        IndianPatientProfile(3, "Kerala", "Malayalam", Historian.SON, BaselineSkinTone.TONE_BROWN),
        IndianPatientProfile(4, "Punjab", "Punjabi / Hindi", Historian.SELF, BaselineSkinTone.TONE_LIGHT_BROWN),
        IndianPatientProfile(5, "Tamil Nadu", "Tamil", Historian.SELF, BaselineSkinTone.TONE_BROWN),
        IndianPatientProfile(6, "Uttar Pradesh", "Hindi", Historian.MOTHER, BaselineSkinTone.TONE_MEDIUM_BROWN),
        IndianPatientProfile(7, "Bihar", "Hindi", Historian.SELF, BaselineSkinTone.TONE_BROWN),
        IndianPatientProfile(8, "Kerala", "Malayalam", Historian.SELF, BaselineSkinTone.TONE_MEDIUM_BROWN),
        IndianPatientProfile(9, "Maharashtra", "Marathi", Historian.SELF, BaselineSkinTone.TONE_BROWN),
        IndianPatientProfile(10, "Kerala", "Malayalam", Historian.SELF, BaselineSkinTone.TONE_BROWN),
        IndianPatientProfile(11, "Telangana", "Telugu", Historian.SELF, BaselineSkinTone.TONE_BROWN),
        IndianPatientProfile(12, "West Bengal", "Bengali", Historian.SELF, BaselineSkinTone.TONE_MEDIUM_BROWN),
        IndianPatientProfile(13, "Gujarat", "Gujarati", Historian.SELF, BaselineSkinTone.TONE_LIGHT_BROWN),
        IndianPatientProfile(14, "Kerala", "Malayalam", Historian.MOTHER, BaselineSkinTone.TONE_MEDIUM_BROWN),
        IndianPatientProfile(15, "Tamil Nadu", "Tamil", Historian.SELF, BaselineSkinTone.TONE_DEEP_BROWN),
        IndianPatientProfile(16, "Kerala", "Malayalam", Historian.SELF, BaselineSkinTone.TONE_BROWN),
        IndianPatientProfile(17, "Sikkim", "Nepali / Hindi", Historian.PARENT_AND_SELF, BaselineSkinTone.TONE_LIGHT_BROWN),
        IndianPatientProfile(18, "Telangana (Hyderabad)", "Urdu / Hindi", Historian.FATHER, BaselineSkinTone.TONE_MEDIUM_BROWN),
        IndianPatientProfile(19, "Delhi", "Hindi", Historian.PARENT_AND_SELF, BaselineSkinTone.TONE_LIGHT_BROWN),
        IndianPatientProfile(20, "Goa", "Konkani / English", Historian.PARENT_AND_SELF, BaselineSkinTone.TONE_MEDIUM_BROWN)
    ).associateBy { it.caseNumber }

    fun profileFor(case: OralBoardCase): IndianPatientProfile =
        profiles[case.caseNumber] ?: IndianPatientProfile(
            caseNumber = case.caseNumber,
            regionState = "India",
            primaryLanguage = "English",
            historian = if (case.isPediatricOrInfant) Historian.MOTHER else Historian.SELF,
            baselineSkinTone = BaselineSkinTone.TONE_BROWN
        )

    fun characterFor(case: OralBoardCase): PatientCharacter {
        val band = ageBandFor(case)
        return PatientCharacter(
            caseNumber = case.caseNumber,
            displayName = case.patientName,
            ageBand = band,
            sex = if (case.gender.trim().startsWith("F", ignoreCase = true)) PatientSex.FEMALE else PatientSex.MALE,
            baselineSkinTone = profileFor(case).baselineSkinTone,
            build = buildFor(band, case.weightKg),
            clinicalSkinSign = ClinicalSkinSign.fromTag(case.patientSkinToneTag),
            profile = profileFor(case)
        )
    }

    /** Parses ageDesc such as "4-week-old Male Infant", "3-week-old Male Neonate", "64-year-old Female". */
    fun ageBandFor(case: OralBoardCase): PatientAgeBand {
        val desc = case.ageDesc.lowercase()
        if ("neonate" in desc || "newborn" in desc) return PatientAgeBand.NEONATE
        val n = Regex("""(\d+)""").find(desc)?.groupValues?.get(1)?.toIntOrNull()
        return when {
            n == null -> if (case.isPediatricOrInfant) PatientAgeBand.SCHOOL_CHILD else PatientAgeBand.ADULT
            "day" in desc -> if (n < 28) PatientAgeBand.NEONATE else PatientAgeBand.INFANT
            "week" in desc -> if ("infant" in desc || n > 4) PatientAgeBand.INFANT else PatientAgeBand.NEONATE
            "month" in desc -> if (n < 12) PatientAgeBand.INFANT else PatientAgeBand.PRESCHOOL_CHILD
            n < 1 -> PatientAgeBand.INFANT
            n <= 5 -> PatientAgeBand.PRESCHOOL_CHILD
            n <= 11 -> PatientAgeBand.SCHOOL_CHILD
            n <= 17 -> PatientAgeBand.ADOLESCENT
            n < 65 -> PatientAgeBand.ADULT
            else -> PatientAgeBand.OLDER_ADULT
        }
    }

    private fun buildFor(band: PatientAgeBand, weightKg: Float): BodyBuild = when (band) {
        PatientAgeBand.ADULT, PatientAgeBand.OLDER_ADULT -> when {
            weightKg < 55f -> BodyBuild.SLIM
            weightKg > 85f -> BodyBuild.HEAVY
            else -> BodyBuild.AVERAGE
        }
        else -> BodyBuild.AVERAGE
    }
}
