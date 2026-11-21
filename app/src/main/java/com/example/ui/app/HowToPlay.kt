package com.example.ui.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.QuestionGateType
import com.example.ui.theme.*

// ============================================================================
// HOW TO PLAY — guide content, full guide screen, first-play tutorial
// One source of text so the guide, the tutorial and in-game help never disagree.
// ============================================================================

data class GuideSection(val id: String, val title: String, val icon: ImageVector, val points: List<String>)

object GuideContent {

    val sections: List<GuideSection> by lazy {
        listOf(
            GuideSection(
                "goal", "What you do", Icons.Default.LocalHospital,
                listOf(
                    "You are the emergency doctor. A patient arrives and you run the case.",
                    "Talk to the patient, examine, order tests and give treatment.",
                    "The patient reacts to what you do, and to what you don't do."
                )
            ),
            GuideSection(
                "modes", "Two ways to play", Icons.Default.SportsEsports,
                listOf(
                    "Interactive cases (NEW): a live patient in real time. Every decision changes the outcome. Scored with stars.",
                    "Classic simulator: 20 oral-board style cases in a resuscitation bay. Answer question gates to unlock each part of the case.",
                    "New players: start with an Interactive case from Home."
                )
            ),
            GuideSection(
                "clock", "The clock", Icons.Default.Timer,
                listOf(
                    "The clock starts as soon as the case opens. One second in the game is one real second.",
                    "If you don't treat in time, the patient gets worse on schedule (for example: heart block, then cardiac arrest).",
                    "Pause stops the clock while you think or read. A real ER has no pause, so use it to learn.",
                    "Wait 1 min jumps the clock ahead, for example while waiting for a result. The patient keeps changing while you wait.",
                    "The clock stops on its own if you leave the app."
                )
            ),
            GuideSection(
                "monitor", "The monitor", Icons.Default.MonitorHeart,
                listOf(
                    "Shows heart rate, blood pressure, SpO₂, breathing rate, temperature and rhythm.",
                    "A number turns red when it is dangerous. The panel border turns red when anything is abnormal.",
                    "--- means no pulse: start resuscitation."
                )
            ),
            GuideSection(
                "actions", "Your actions", Icons.Default.TouchApp,
                listOf(
                    "Talk: ask questions. The patient answers in the speech bubble.",
                    "Examine: check each body region. Re-examine after treatment, because findings change.",
                    "Tests: order ECGs, bloods and imaging. Each result takes time to come back.",
                    "Treat: drugs, fluids, procedures and calls. Doses are shown. Every order is checked against the patient's current state.",
                    "Log: the full timeline of everything that happened."
                )
            ),
            GuideSection(
                "feedback", "Nurse and feedback", Icons.Default.NotificationsActive,
                listOf(
                    "The nurse calls out when the patient changes or a result is back.",
                    "HARM (red): that order hurt the patient. It is recorded for the debrief.",
                    "BLOCKED (amber): not possible yet. For example, clot-busting drugs need an ECG diagnosis first.",
                    "NOTE (amber): allowed, but not needed for this patient.",
                    "CHANGE (red): the patient's condition just changed. Look at the monitor."
                )
            ),
            GuideSection(
                "rules", "Rules", Icons.Default.Gavel,
                listOf(
                    "Treat the patient in front of you. Reassess after every treatment.",
                    "Time limits are real. For example, an ECG within 10 minutes for chest pain.",
                    "Think before you give. A wrong drug at the wrong time causes harm.",
                    "Don't stand idle waiting for results. Keep assessing and treating.",
                    "Tap End at any time to finish and see your debrief.",
                    "ER SIM is for learning only. Never use it for real patient decisions."
                )
            ),
            GuideSection(
                "score", "Scoring and stars", Icons.Default.Star,
                listOf(
                    "Each case has critical actions. Doing them on time earns points.",
                    "Some points are for avoiding a dangerous action.",
                    "Complications are only scored if they happened in your game.",
                    "Tapping everything does not raise your score. Harm events are listed in your debrief.",
                    "★★★ 90% or more  •  ★★ 70% or more  •  ★ 40% or more. Your best stars are shown on Home."
                )
            ),
            GuideSection(
                "debrief", "The debrief", Icons.Default.Assignment,
                listOf(
                    "Shows the diagnosis, your score and the time.",
                    "Each critical action is marked done, late or missed, with a short teaching note.",
                    "Then: harm events, the right disposition, and key teaching points.",
                    "Tap Replay to try again. Different decisions lead to different outcomes."
                )
            ),
            GuideSection(
                "classic", "Classic simulator: question gates", Icons.Default.Lock,
                listOf("Parts of each classic case are locked. Tap a glowing Q beacon in the bay and answer its question to unlock:") +
                    QuestionGateType.entries.map { "${it.code}: ${it.unlocksLabel}" } +
                    listOf(
                        "The top bar has the case timer, Pause, Restart and End Case.",
                        "Side tabs: Patient, Exam, Stabilize and Differential.",
                        "Tap the case name to switch case. The back arrow returns to Home."
                    )
            )
        )
    }

    /** Short pages for the first interactive case. */
    val tutorialPages: List<GuideSection> by lazy {
        val byId = sections.associateBy { it.id }
        listOf(
            GuideSection(
                "t1", "Your patient is waiting", Icons.Default.LocalHospital,
                listOf(
                    "You are the ER doctor. Assess, decide and treat.",
                    "The patient reacts to everything you do, and to everything you don't."
                )
            ),
            byId.getValue("clock"),
            byId.getValue("actions"),
            byId.getValue("feedback"),
            byId.getValue("score")
        )
    }
}

private fun sectionAccent(id: String): Color = when (id) {
    "goal", "t1" -> ErSimMint
    "modes" -> ErSimSky
    "clock" -> ErSimAmber
    "monitor" -> ErSimRed
    "actions" -> ErSimCyan
    "feedback" -> ErSimOrange
    "rules" -> ErSimRose
    "score" -> ErSimAmber
    "debrief" -> ErSimViolet
    "classic" -> ErSimSky
    else -> ErSimMint
}

// ============================================================================
// FULL GUIDE SCREEN
// ============================================================================

@Composable
fun HowToPlayScreen(onBack: () -> Unit, startSectionId: String? = null) {
    BackHandler { onBack() }
    var expanded by rememberSaveable { mutableStateOf(setOf(startSectionId ?: "goal")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ErSimBg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("how_to_play_screen")
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ErSimTextPrimary)
            }
            Text("How to play", color = ErSimTextPrimary, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        }
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(GuideContent.sections, key = { it.id }) { sec ->
                val open = sec.id in expanded
                val accent = sectionAccent(sec.id)
                Surface(
                    color = ErSimSurface,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, if (open) accent.copy(alpha = 0.65f) else ErSimBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = if (open) expanded - sec.id else expanded + sec.id }
                        .testTag("guide_${sec.id}")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(accent.copy(alpha = 0.16f), RoundedCornerShape(10.dp))
                            ) { Icon(sec.icon, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp)) }
                            Spacer(Modifier.width(12.dp))
                            Text(sec.title, color = ErSimTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            Icon(
                                if (open) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (open) "Collapse" else "Expand",
                                tint = if (open) accent else ErSimTextMuted
                            )
                        }
                        AnimatedVisibility(visible = open) {
                            Column(modifier = Modifier.padding(top = 10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                sec.points.forEach { GuidePoint(it, accent) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GuidePoint(text: String, accent: Color = ErSimMint) {
    Row {
        Box(Modifier.padding(top = 7.dp).size(6.dp).background(accent, CircleShape))
        Spacer(Modifier.width(10.dp))
        Text(text, color = ErSimTextPrimary, fontSize = 14.sp, lineHeight = 20.sp)
    }
}

// ============================================================================
// FIRST-PLAY TUTORIAL (shown before the clock starts)
// ============================================================================

@Composable
fun GameTutorialDialog(onDone: () -> Unit) {
    val pages = GuideContent.tutorialPages
    var page by rememberSaveable { mutableIntStateOf(0) }
    val sec = pages[page]
    val accent = sectionAccent(sec.id)
    val last = page == pages.lastIndex

    Dialog(onDismissRequest = {}, properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)) {
        Surface(
            color = ErSimSurface,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.2.dp, accent.copy(alpha = 0.55f)),
            modifier = Modifier.fillMaxWidth().testTag("game_tutorial")
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "HOW TO PLAY • ${page + 1}/${pages.size}",
                        color = accent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.2.sp,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = onDone, modifier = Modifier.testTag("tutorial_skip")) { Text("Skip", color = ErSimTextMuted) }
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(52.dp)
                        .background(accent.copy(alpha = 0.16f), RoundedCornerShape(14.dp))
                ) { Icon(sec.icon, contentDescription = null, tint = accent, modifier = Modifier.size(28.dp)) }
                Text(sec.title, color = ErSimTextPrimary, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) { sec.points.forEach { GuidePoint(it, accent) } }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    pages.indices.forEach { i ->
                        Box(Modifier.size(if (i == page) 10.dp else 7.dp).background(if (i == page) accent else ErSimBorder, CircleShape))
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    if (page > 0) {
                        OutlinedButton(
                            onClick = { page-- },
                            border = BorderStroke(1.dp, ErSimBorder),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ErSimTextPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).height(48.dp)
                        ) { Text("Back") }
                    }
                    Button(
                        onClick = { if (last) onDone() else page++ },
                        colors = ButtonDefaults.buttonColors(containerColor = ErSimMint, contentColor = ErSimOnAccent),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(48.dp).testTag("tutorial_next")
                    ) { Text(if (last) "Start the case" else "Next", fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}
