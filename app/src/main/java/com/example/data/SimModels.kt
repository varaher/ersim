package com.example.data

enum class QuestionGateType(
    val code: String,
    val title: String,
    val unlocksLabel: String,
    val defaultMapX: Float,
    val defaultMapY: Float
) {
    Q0_GATE(
        code = "Q0",
        title = "Resus Bay Entrance Gate Question",
        unlocksLabel = "Patient Details, Initial Vitals & Virtual Tablet Access",
        defaultMapX = 0.50f,
        defaultMapY = 0.86f
    ),
    Q1_SURVEY(
        code = "Q1",
        title = "Bedside Airway & Survey Question 1",
        unlocksLabel = "Primary Survey (ABCDE) & Physical Examination",
        defaultMapX = 0.24f,
        defaultMapY = 0.52f
    ),
    Q2_LABS(
        code = "Q2",
        title = "Point-of-Care Analyzer Question 2",
        unlocksLabel = "Laboratory Panels & ABG / VBG Blood Gas Report",
        defaultMapX = 0.78f,
        defaultMapY = 0.64f
    ),
    Q3_IMAGING(
        code = "Q3",
        title = "Radiology & Telemetry Terminal Question 3",
        unlocksLabel = "12-Lead ECG & Diagnostic Imaging (CT / MRI / USG / X-Ray)",
        defaultMapX = 0.20f,
        defaultMapY = 0.24f
    )
}

data class UnlockQuestion(
    val gateType: QuestionGateType,
    val stationName: String,
    val mapX: Float = gateType.defaultMapX,
    val mapY: Float = gateType.defaultMapY,
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val clinicalExplanation: String,
    val assignedByInstructor: String = "Dr. Attending (Instructor)"
)

data class VitalSigns(
    val bpDisplay: String,
    val systolic: Int,
    val diastolic: Int,
    val hr: Int,
    val rr: Int,
    val spo2: Int?, // null represents "None" / unobtainable initially as per PDF index
    val tempCelsius: Float,
    val etco2: Int = 38,
    val rhythmLabel: String = "Sinus Rhythm"
)

data class VentilatorSettings(
    val mode: String = "AC / Volume Control",
    val tidalVolumeMlPerKg: Int = 8,
    val respRatePerMin: Int = 16,
    val fio2Percent: Int = 40,
    val peepCmH2O: Int = 5,
    val isActive: Boolean = false,
    val sourceCitationNote: String = "Standard ICU resuscitation preset"
)

data class DefibrillatorSettings(
    val mode: String = "Monitor / Pads Ready",
    val energyJoules: Int = 150,
    val energyPerKgLabel: String = "2 J/kg (Adult 150-200 J Biphasic)",
    val isCharged: Boolean = false,
    val syncEnabled: Boolean = false,
    val pacingRate: Int = 0,
    val shocksDelivered: Int = 0,
    val sourceCitationNote: String = "Standard Biphasic Monitor/Defibrillator"
)

data class PrimarySurveyABCDE(
    val airway: String,
    val breathing: String,
    val circulation: String,
    val disability: String,
    val exposure: String
)

data class PhysicalExamClues(
    val generalAppearance: String,
    val heentAndNeck: String,
    val chestAndLungs: String,
    val cardiovascular: String,
    val abdomenAndPelvis: String,
    val extremitiesAndSkin: String,
    val neurologic: String
)

enum class LabFlag {
    NORMAL, HIGH, LOW, CRITICAL
}

data class LabEntry(
    val category: String, // "CBC & Chemistry", "ABG / VBG", "Coagulation & Tox", "Urinalysis / CSF"
    val testName: String,
    val value: String,
    val unit: String,
    val referenceRange: String,
    val flag: LabFlag = LabFlag.NORMAL
)

enum class ClinicalVisualPattern {
    ABDOMINAL_RADIOGRAPH_IRON,
    ECG_SINUS_TACH,
    PYLORIC_ULTRASOUND,
    HEAD_CT_ICH,
    CXR_TENSION_PTX,
    RUQ_ULTRASOUND_GALLBLADDER,
    INFANT_CXR_SEPSIS,
    CXR_BOERHAAVE_PNEUMOMEDIASTINUM,
    SPINE_MRI_EPIDURAL_ABSCESS,
    SOFT_TISSUE_CT_NEC_FASC,
    ECG_HYPERKALEMIA_SINE_WAVE,
    SLIT_LAMP_GLAUCOMA,
    CT_STRANGULATED_HERNIA,
    ABG_SALICYLATE_GRAPH,
    PURPURA_BOWEL_USG,
    COAG_PANEL_SNAKEBITE,
    FUNDUS_CRVO,
    ECG_TORSADES_LONG_QT,
    LATERAL_NECK_XRAY_RPA,
    PELVIS_XRAY_SCFE,
    DOPPLER_USG_TESTICULAR_TORSION
}

data class ImagingStudy(
    val id: String,
    val modality: String, // "ECG", "X-RAY", "CT", "MRI", "USG", "CLINICAL FIG"
    val title: String,
    val figureRef: String,
    val pdfPages: String,
    val findings: String,
    val impression: String,
    val visualPattern: ClinicalVisualPattern
)

enum class OralBoardDomain(val number: Int, val title: String) {
    ACUITY_RECOGNITION(1, "1. Patient Acuity & Immediate Stabilization"),
    DATA_ACQUISITION(2, "2. Focused History & Data Acquisition"),
    DIAGNOSTIC_STUDIES(3, "3. Diagnostic Studies & Interpretation"),
    PROBLEM_SOLVING(4, "4. Clinical Problem Solving & Differential"),
    PATIENT_MANAGEMENT(5, "5. Therapeutics & Procedural Management"),
    INTERPERSONAL_COMMS(6, "6. Team & Patient/Family Communication"),
    DISPOSITION(7, "7. Specialist Consultation & Disposition"),
    SAFETY_REASSESSMENT(8, "8. Reassessment & Medical Safety")
}

data class CriticalAction(
    val id: String,
    val order: Int,
    val description: String,
    val domain: OralBoardDomain,
    val points: Int = 20,
    val triggerKeywords: List<String> = emptyList()
)

data class OralBoardCase(
    val caseNumber: Int,
    val scenarioCategory: String,
    val mainDiagnosis: String,
    val pdfPages: String,
    val sourceFiguresCount: Int,
    val patientName: String,
    val ageDesc: String,
    val gender: String,
    val weightKg: Float,
    val chiefComplaint: String,
    val triageNote: String,
    val patientSkinToneTag: String, // "NORMAL", "CYANOTIC", "PALE", "FLUSHED", "JAUNDICED"
    val isPediatricOrInfant: Boolean = false,
    val initialVitals: VitalSigns,
    val stabilizedVitals: VitalSigns,
    val ventilatorPreset: VentilatorSettings,
    val defibrillatorPreset: DefibrillatorSettings,
    val primarySurvey: PrimarySurveyABCDE,
    val physicalExam: PhysicalExamClues,
    val labReports: List<LabEntry>,
    val abgVbgReports: List<LabEntry>,
    val imagingAndEcg: List<ImagingStudy>,
    val criticalActions: List<CriticalAction>,
    val defaultQuestions: List<UnlockQuestion>,
    val dispositionAndPearls: String,
    val instructorClues: List<String>
)

data class CrashCartItem(
    val id: String,
    val name: String,
    val specification: String,
    val quantity: Int,
    val sourceStandard: String, // "GoI NHSRC Annexure VII" or "Oman MOH Annex One"
    val clinicalUseHint: String,
    val matchingActionKeywords: List<String> = emptyList()
)

data class CrashCartDrawer(
    val drawerNumber: Int, // 0 = Top Shelf, 1..6 = 6 Drawers
    val title: String,
    val subtitle: String,
    val colorHex: Long,
    val items: List<CrashCartItem>
)

data class ChatMessage(
    val id: String,
    val sender: String,
    val roleTag: String, // "INSTRUCTOR", "TEAM", "CRASH_CART", "SYSTEM"
    val text: String,
    val timestampLabel: String
)

data class RoomTeamSession(
    val roomId: String,
    val teamName: String,
    val members: List<String>,
    val activeMember: String,
    val assignedCaseNumber: Int,
    val elapsedDeciseconds: Long = 0L, // 0.1s resolution for accurate tie-breaking!
    val isTimerRunning: Boolean = true,
    val unlockedGates: Set<QuestionGateType> = emptySet(),
    val crackedByMember: Map<QuestionGateType, String> = emptyMap(),
    val completedCriticalActionIds: Set<String> = emptySet(),
    val domainRatings: Map<Int, Int> = (1..8).associateWith { 6 }, // 1..8 scale
    val takenCartItems: List<CrashCartItem> = emptyList(),
    val appliedCartItemsOnPatient: List<String> = emptyList(),
    val customQuestions: Map<QuestionGateType, UnlockQuestion> = emptyMap(),
    val chatMessages: List<ChatMessage> = emptyList(),
    val isPatientStabilized: Boolean = false
) {
    val tasksCompletedCount: Int get() = completedCriticalActionIds.size
    val formattedTimer: String
        get() {
            val totalSec = elapsedDeciseconds / 10
            val mins = totalSec / 60
            val secs = totalSec % 60
            val tenths = elapsedDeciseconds % 10
            return "%02d:%02d.%d".format(mins, secs, tenths)
        }
}
