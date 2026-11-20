package com.example.ui.components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import com.example.data.ClinicalVisualPattern
import com.example.data.ImagingStudy
import com.example.ui.theme.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ClinicalImagingFigureViewer(
    study: ImagingStudy,
    modifier: Modifier = Modifier
) {
    val labelPaint = remember {
        Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.WHITE
            textSize = 22f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        }
    }
    val subPaint = remember {
        Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.parseColor("#38BDF8")
            textSize = 17f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(215.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF03070E))
            .border(1.5.dp, ResusBorderBlue, RoundedCornerShape(12.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cx = w * 0.5f
            val cy = h * 0.52f

            when (study.visualPattern) {
                ClinicalVisualPattern.ABDOMINAL_RADIOGRAPH_IRON -> {
                    // Case 1: KUB Radiograph with Radiopaque Iron Tablets in Gastric Fundus
                    drawRoundRect(
                        color = Color(0xFF1E293B),
                        topLeft = Offset(cx - 110f, 24f),
                        size = Size(220f, h - 42f),
                        cornerRadius = CornerRadius(32f, 32f)
                    )
                    // Spine vertebrae column
                    for (v in 0..5) {
                        drawRoundRect(
                            color = Color(0xFF64748B).copy(alpha = 0.55f),
                            topLeft = Offset(cx - 14f, 36f + v * 28f),
                            size = Size(28f, 20f),
                            cornerRadius = CornerRadius(4f, 4f)
                        )
                    }
                    // Gastric silhouette in LUQ filled with bright radiopaque iron tablets
                    drawOval(
                        color = Color(0xFF0F172A),
                        topLeft = Offset(cx + 8f, 48f),
                        size = Size(82f, 64f)
                    )
                    val pillOffsets = listOf(
                        Offset(28f, 68f), Offset(44f, 64f), Offset(58f, 72f),
                        Offset(24f, 82f), Offset(38f, 84f), Offset(52f, 86f),
                        Offset(66f, 82f), Offset(34f, 96f), Offset(48f, 98f),
                        Offset(60f, 95f), Offset(42f, 75f), Offset(72f, 90f)
                    )
                    pillOffsets.forEach { off ->
                        drawOval(
                            color = Color.White,
                            topLeft = Offset(cx + off.x, off.y),
                            size = Size(11f, 7f)
                        )
                    }
                }

                ClinicalVisualPattern.PYLORIC_ULTRASOUND -> {
                    // Case 2: Pyloric Ultrasound showing hypertrophied muscle > 4.8 mm & channel > 18 mm
                    drawCircle(Color(0xFF111827), radius = 85f, center = Offset(cx, cy))
                    // Target/Doughnut Sign & Elongated Pyloric Canal
                    drawCircle(Color(0xFF94A3B8), radius = 52f, center = Offset(cx - 50f, cy), style = Stroke(18f))
                    drawCircle(Color(0xFF020617), radius = 20f, center = Offset(cx - 50f, cy))
                    // Longitudinal view on right
                    drawRoundRect(
                        color = Color(0xFF94A3B8),
                        topLeft = Offset(cx + 15f, cy - 34f),
                        size = Size(115f, 68f),
                        cornerRadius = CornerRadius(14f, 14f),
                        style = Stroke(16f)
                    )
                    drawLine(CapnoAmber, Offset(cx + 15f, cy), Offset(cx + 130f, cy), 4f)
                }

                ClinicalVisualPattern.HEAD_CT_ICH -> {
                    // Case 3: Non-Contrast Head CT with Hyperdense Left Basal Ganglia ICH & Midline Shift
                    drawOval(
                        color = Color(0xFFE2E8F0),
                        topLeft = Offset(cx - 86f, cy - 82f),
                        size = Size(172f, 164f),
                        style = Stroke(10f)
                    )
                    drawOval(
                        color = Color(0xFF334155),
                        topLeft = Offset(cx - 80f, cy - 76f),
                        size = Size(160f, 152f)
                    )
                    // Displaced falx/midline shift bowed to the right
                    val shiftPath = Path().apply {
                        moveTo(cx, cy - 74f)
                        quadraticTo(cx - 24f, cy, cx, cy + 74f)
                    }
                    drawPath(shiftPath, TelemetryCyan, style = Stroke(3f))
                    // Bright Hyperdense Left Hematoma
                    drawOval(
                        color = Color.White,
                        topLeft = Offset(cx + 10f, cy - 35f),
                        size = Size(54f, 66f)
                    )
                    drawOval(
                        color = ArterialRed.copy(alpha = 0.45f),
                        topLeft = Offset(cx + 2f, cy - 43f),
                        size = Size(70f, 82f),
                        style = Stroke(4f)
                    )
                }

                ClinicalVisualPattern.CXR_TENSION_PTX -> {
                    // Case 4: Right-Sided Tension Pneumothorax with Mediastinal Shift to Left
                    // Right hemithorax (viewer left): jet-black hyperlucent collapsed lung
                    drawRoundRect(
                        color = Color(0xFF020617),
                        topLeft = Offset(cx - 115f, 34f),
                        size = Size(98f, h - 56f),
                        cornerRadius = CornerRadius(18f, 18f),
                        style = Stroke(3f)
                    )
                    // Collapsed right lung stump at hilum
                    drawOval(
                        color = Color(0xFF64748B),
                        topLeft = Offset(cx - 38f, cy - 22f),
                        size = Size(26f, 44f)
                    )
                    // Left hemithorax (viewer right): normal lung markings + shifted heart/trachea
                    drawRoundRect(
                        color = Color(0xFF334155),
                        topLeft = Offset(cx + 15f, 34f),
                        size = Size(98f, h - 56f),
                        cornerRadius = CornerRadius(18f, 18f)
                    )
                    // Shifted heart silhouette & trachea deviated left
                    drawOval(
                        color = Color(0xFFE2E8F0),
                        topLeft = Offset(cx + 10f, cy - 10f),
                        size = Size(65f, 65f)
                    )
                    drawLine(CapnoAmber, Offset(cx, 24f), Offset(cx + 28f, cy - 20f), 6f)
                }

                ClinicalVisualPattern.RUQ_ULTRASOUND_GALLBLADDER -> {
                    // Case 5: RUQ Biliary Ultrasound with Gallstones, Acoustic Shadow & Wall Thickening
                    drawOval(
                        color = Color(0xFF0F172A),
                        topLeft = Offset(cx - 75f, cy - 45f),
                        size = Size(150f, 84f)
                    )
                    drawOval(
                        color = Color(0xFF94A3B8),
                        topLeft = Offset(cx - 75f, cy - 45f),
                        size = Size(150f, 84f),
                        style = Stroke(12f) // thickened wall 5.5 mm
                    )
                    // Bright echogenic gallstones with posterior dark acoustic shadow
                    drawCircle(Color.White, radius = 14f, center = Offset(cx - 25f, cy + 5f))
                    drawCircle(Color.White, radius = 11f, center = Offset(cx + 12f, cy + 10f))
                    val shadowPath = Path().apply {
                        moveTo(cx - 39f, cy + 15f)
                        lineTo(cx - 11f, cy + 15f)
                        lineTo(cx - 4f, h - 10f)
                        lineTo(cx - 46f, h - 10f)
                        close()
                    }
                    drawPath(shadowPath, Color.Black.copy(alpha = 0.8f))
                }

                ClinicalVisualPattern.INFANT_CXR_SEPSIS,
                ClinicalVisualPattern.CXR_BOERHAAVE_PNEUMOMEDIASTINUM -> {
                    // Case 6 & Case 7 Chest Radiograph
                    drawRoundRect(
                        color = Color(0xFF1E293B),
                        topLeft = Offset(cx - 100f, 34f),
                        size = Size(85f, h - 56f),
                        cornerRadius = CornerRadius(18f, 18f)
                    )
                    drawRoundRect(
                        color = Color(0xFF1E293B),
                        topLeft = Offset(cx + 15f, 34f),
                        size = Size(85f, h - 56f),
                        cornerRadius = CornerRadius(18f, 18f)
                    )
                    // Heart & Mediastinum
                    drawOval(
                        color = Color(0xFFCBD5E1),
                        topLeft = Offset(cx - 28f, cy - 15f),
                        size = Size(64f, 68f)
                    )
                    if (study.visualPattern == ClinicalVisualPattern.CXR_BOERHAAVE_PNEUMOMEDIASTINUM) {
                        // Linear bright air streaks around mediastinum (Pneumomediastinum) & Left Pleural Effusion
                        drawLine(CapnoAmber, Offset(cx - 34f, 40f), Offset(cx - 38f, cy + 45f), 3.5f)
                        drawLine(CapnoAmber, Offset(cx + 38f, 40f), Offset(cx + 42f, cy + 45f), 3.5f)
                        drawRect(Color(0xFF94A3B8), topLeft = Offset(cx + 15f, h - 62f), size = Size(85f, 40f))
                    } else {
                        // Case 6 Infant ETT tube above carina
                        drawLine(TelemetryCyan, Offset(cx, 22f), Offset(cx, cy - 22f), 5f)
                    }
                }

                ClinicalVisualPattern.SPINE_MRI_EPIDURAL_ABSCESS -> {
                    // Case 8: Sagittal Spine MRI with Rim-Enhancing Posterior Epidural Abscess Compressing Cord
                    for (v in 0..4) {
                        drawRoundRect(
                            color = Color(0xFF475569),
                            topLeft = Offset(cx - 60f, 34f + v * 32f),
                            size = Size(46f, 24f),
                            cornerRadius = CornerRadius(4f, 4f)
                        )
                    }
                    // Spinal cord (CSF bright on T2, cord gray)
                    drawLine(Color(0xFFCBD5E1), Offset(cx + 6f, 30f), Offset(cx + 6f, h - 20f), 14f)
                    // Rim-enhancing posterior epidural abscess collection compressing the cord
                    drawOval(
                        color = ArterialRed,
                        topLeft = Offset(cx + 10f, cy - 36f),
                        size = Size(28f, 76f)
                    )
                    drawOval(
                        color = CapnoAmber,
                        topLeft = Offset(cx + 10f, cy - 36f),
                        size = Size(28f, 76f),
                        style = Stroke(3f)
                    )
                }

                ClinicalVisualPattern.SOFT_TISSUE_CT_NEC_FASC -> {
                    // Case 9: Lower Extremity CT / X-Ray showing Deep Fascial Gas Pockets
                    drawCircle(Color(0xFF334155), radius = 72f, center = Offset(cx, cy))
                    // Tibia & Fibula bones
                    drawCircle(Color.White, radius = 18f, center = Offset(cx - 16f, cy - 12f))
                    drawCircle(Color.White, radius = 10f, center = Offset(cx + 26f, cy + 16f))
                    // Dark black gas pockets tracking along deep fascia
                    val gasBubbles = listOf(
                        Offset(-38f, 18f), Offset(-32f, 32f), Offset(-18f, 42f),
                        Offset(5f, 38f), Offset(22f, -24f), Offset(36f, -14f), Offset(-44f, -6f)
                    )
                    gasBubbles.forEach { g ->
                        drawCircle(Color.Black, radius = 7f, center = Offset(cx + g.x, cy + g.y))
                        drawCircle(CapnoAmber, radius = 7f, center = Offset(cx + g.x, cy + g.y), style = Stroke(1.5f))
                    }
                }

                ClinicalVisualPattern.ECG_HYPERKALEMIA_SINE_WAVE,
                ClinicalVisualPattern.ECG_TORSADES_LONG_QT,
                ClinicalVisualPattern.ABG_SALICYLATE_GRAPH -> {
                    // ECG Grid Paper Background
                    val gridStep = 18f
                    for (gx in 0..(w / gridStep).toInt()) {
                        drawLine(Color(0xFF1F2937), Offset(gx * gridStep, 0f), Offset(gx * gridStep, h), 1f)
                    }
                    for (gy in 0..(h / gridStep).toInt()) {
                        drawLine(Color(0xFF1F2937), Offset(0f, gy * gridStep), Offset(w, gy * gridStep), 1f)
                    }

                    val path = Path()
                    val steps = 160
                    for (i in 0..steps) {
                        val frac = i.toFloat() / steps
                        val px = 16f + frac * (w - 32f)
                        val py = when (study.visualPattern) {
                            ClinicalVisualPattern.ECG_HYPERKALEMIA_SINE_WAVE -> {
                                // Wide-QRS Sine Wave merging with tall peaked T waves
                                cy + sin(frac * 10f * PI.toFloat()) * 54f
                            }
                            ClinicalVisualPattern.ECG_TORSADES_LONG_QT -> {
                                // Polymorphic VT twisting around the isoelectric line
                                val envelope = sin(frac * 3f * PI.toFloat())
                                cy + sin(frac * 28f * PI.toFloat()) * 58f * envelope
                            }
                            else -> {
                                // Sinus Tachycardia + Salicylate curve
                                val t = (frac * 6f) % 1.0f
                                val qrs = if (t in 0.30f..0.38f) -46f else if (t in 0.55f..0.70f) -14f else 0f
                                cy + qrs
                            }
                        }
                        if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                    }
                    drawPath(path, EcgEmerald, style = Stroke(width = 3.2f))
                }

                ClinicalVisualPattern.SLIT_LAMP_GLAUCOMA,
                ClinicalVisualPattern.FUNDUS_CRVO -> {
                    if (study.visualPattern == ClinicalVisualPattern.SLIT_LAMP_GLAUCOMA) {
                        // Case 11: Acute Angle-Closure Glaucoma (ciliary flush, hazy steamy cornea, fixed mid-dilated pupil)
                        drawCircle(Color(0xFFFEE2E2), radius = 72f, center = Offset(cx, cy))
                        drawCircle(ArterialRed, radius = 72f, center = Offset(cx, cy), style = Stroke(6f))
                        // Hazy iris & steamy cornea
                        drawCircle(Color(0xFF64748B), radius = 44f, center = Offset(cx, cy))
                        // Fixed mid-dilated 5.5 mm pupil
                        drawCircle(Color(0xFF0F172A), radius = 26f, center = Offset(cx, cy))
                    } else {
                        // Case 16: Central Retinal Vein Occlusion ("Blood and Thunder" Fundus)
                        drawCircle(Color(0xFF991B1B), radius = 74f, center = Offset(cx, cy))
                        // Optic disc
                        drawCircle(Color(0xFFFDE047), radius = 16f, center = Offset(cx - 24f, cy))
                        // Tortuous dilated retinal veins & diffuse 4-quadrant flame hemorrhages
                        for (a in 0..7) {
                            val ang = a * (PI.toFloat() / 4f)
                            drawLine(
                                ArterialRed,
                                Offset(cx - 24f, cy),
                                Offset(cx + cos(ang) * 64f, cy + sin(ang) * 64f),
                                strokeWidth = 5f
                            )
                        }
                    }
                }

                ClinicalVisualPattern.CT_STRANGULATED_HERNIA -> {
                    // Case 12: Dilated Small-Bowel Loops with Stepladder Air-Fluid Levels & Right Inguinal Hernia
                    for (loop in 0..2) {
                        val lx = cx - 75f + loop * 48f
                        val ly = cy - 35f + loop * 12f
                        drawRoundRect(
                            color = Color(0xFF475569),
                            topLeft = Offset(lx, ly),
                            size = Size(52f, 44f),
                            cornerRadius = CornerRadius(16f, 16f)
                        )
                        // Horizontal air-fluid level line
                        drawLine(TelemetryCyan, Offset(lx + 4f, ly + 22f), Offset(lx + 48f, ly + 22f), 3f)
                    }
                    // Strangulated Right Inguinal Loop
                    drawCircle(ArterialRed, radius = 24f, center = Offset(cx - 55f, cy + 52f), style = Stroke(4f))
                }

                ClinicalVisualPattern.PURPURA_BOWEL_USG,
                ClinicalVisualPattern.COAG_PANEL_SNAKEBITE -> {
                    // Lower extremity outline with either HSP Palpable Purpura (Case 14) or Dry Pit-Viper Fang Marks (Case 15)
                    drawRoundRect(
                        color = Color(0xFFD6B499),
                        topLeft = Offset(cx - 35f, 32f),
                        size = Size(70f, h - 54f),
                        cornerRadius = CornerRadius(24f, 24f)
                    )
                    if (study.visualPattern == ClinicalVisualPattern.PURPURA_BOWEL_USG) {
                        val spots = listOf(
                            Offset(-14f, -20f), Offset(12f, -15f), Offset(-8f, 5f),
                            Offset(16f, 18f), Offset(-18f, 28f), Offset(4f, 38f), Offset(-6f, 52f)
                        )
                        spots.forEach { s ->
                            drawCircle(Color(0xFF7E22CE), radius = 7f, center = Offset(cx + s.x, cy + s.y))
                        }
                    } else {
                        // Two distinct fang punctures at lateral ankle without progressive edema
                        drawCircle(ArterialRed, radius = 4.5f, center = Offset(cx - 8f, cy + 36f))
                        drawCircle(ArterialRed, radius = 4.5f, center = Offset(cx + 8f, cy + 36f))
                        drawLine(TelemetryCyan, Offset(cx - 42f, cy + 16f), Offset(cx + 42f, cy + 16f), 2.5f)
                    }
                }

                ClinicalVisualPattern.LATERAL_NECK_XRAY_RPA -> {
                    // Case 18: Lateral Soft-Tissue Neck X-Ray showing Prevertebral Widening (>14 mm)
                    for (c in 0..4) {
                        drawRoundRect(
                            color = Color(0xFFCBD5E1),
                            topLeft = Offset(cx - 55f, 40f + c * 26f),
                            size = Size(28f, 20f),
                            cornerRadius = CornerRadius(4f, 4f)
                        )
                    }
                    // Markedly widened retropharyngeal prevertebral soft tissue pushing airway column anteriorly
                    drawOval(
                        color = ArterialRed.copy(alpha = 0.55f),
                        topLeft = Offset(cx - 24f, 48f),
                        size = Size(48f, 105f)
                    )
                    // Anteriorly displaced dark tracheal air column
                    drawLine(Color.Black, Offset(cx + 38f, 40f), Offset(cx + 38f, h - 24f), 16f)
                }

                ClinicalVisualPattern.PELVIS_XRAY_SCFE -> {
                    // Case 19: Bilateral Hip X-Ray with Left SCFE & Klein's Line (Trethowan's Sign)
                    // Right Hip (Normal: Klein's line intersects femoral epiphysis)
                    drawLine(Color(0xFFCBD5E1), Offset(cx - 85f, cy + 35f), Offset(cx - 45f, cy - 10f), 18f)
                    drawCircle(Color.White, radius = 20f, center = Offset(cx - 42f, cy - 15f))
                    drawLine(EcgEmerald, Offset(cx - 95f, cy + 25f), Offset(cx - 28f, cy - 35f), 3f)

                    // Left Hip (SCFE: Epiphysis slipped posteriorly/medially below Klein's line!)
                    drawLine(Color(0xFFCBD5E1), Offset(cx + 85f, cy + 35f), Offset(cx + 45f, cy - 10f), 18f)
                    drawCircle(CapnoAmber, radius = 20f, center = Offset(cx + 48f, cy + 6f))
                    drawLine(ArterialRed, Offset(cx + 95f, cy + 25f), Offset(cx + 28f, cy - 35f), 3f)
                }

                ClinicalVisualPattern.DOPPLER_USG_TESTICULAR_TORSION -> {
                    // Case 20: Color Doppler Scrotal Ultrasound (Right normal flow vs Left absent arterial flow)
                    // Right Testis (Normal Vascular Flow)
                    drawOval(
                        color = Color(0xFF334155),
                        topLeft = Offset(cx - 105f, cy - 38f),
                        size = Size(78f, 76f)
                    )
                    drawCircle(ArterialRed, radius = 7f, center = Offset(cx - 75f, cy - 8f))
                    drawCircle(Spo2Sky, radius = 7f, center = Offset(cx - 55f, cy + 8f))
                    drawCircle(ArterialRed, radius = 6f, center = Offset(cx - 65f, cy + 16f))

                    // Left Testis (Torsion — Enlarged, Heterogeneous, ZERO Color Doppler Flow!)
                    drawOval(
                        color = Color(0xFF1E293B),
                        topLeft = Offset(cx + 25f, cy - 44f),
                        size = Size(86f, 88f)
                    )
                    drawOval(
                        color = CapnoAmber,
                        topLeft = Offset(cx + 25f, cy - 44f),
                        size = Size(86f, 88f),
                        style = Stroke(3f)
                    )
                }

                else -> {
                    drawCircle(TelemetryCyan, radius = 40f, center = Offset(cx, cy), style = Stroke(3f))
                }
            }

            // Header & Footer Overlay Text inside Medical Figure
            drawContext.canvas.nativeCanvas.apply {
                drawText("${study.modality} • ${study.figureRef}", 16f, 26f, labelPaint)
                drawText("Source PDF: ${study.pdfPages}", 16f, h - 12f, subPaint)
            }
        }
    }
}
