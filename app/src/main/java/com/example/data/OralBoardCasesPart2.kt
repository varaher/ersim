package com.example.data

object OralBoardCasesPart2 {

    val cases11To20: List<OralBoardCase> = listOf(
        // CASE 11
        OralBoardCase(
            caseNumber = 11,
            scenarioCategory = "Eye pain",
            mainDiagnosis = "Acute angle-closure glaucoma",
            pdfPages = "335–350",
            sourceFiguresCount = 0,
            patientName = "Kamala Reddy",
            ageDesc = "64-year-old Female",
            gender = "Female",
            weightKg = 62f,
            chiefComplaint = "Sudden severe right eye pain, frontal headache, blurred vision with halos around lights, and nausea/vomiting",
            triageNote = "Symptoms started 2 hours ago while watching a movie in a dark theater. Complains of intense right orbital ache, seeing rainbow halos, and vomited twice.",
            patientSkinToneTag = "NORMAL",
            initialVitals = VitalSigns("142/91", 142, 91, 95, 16, 100, 36.0f, 38, "Normal Sinus Rhythm"),
            stabilizedVitals = VitalSigns("128/80", 128, 80, 78, 14, 100, 36.4f, 38, "Normal Sinus Rhythm"),
            ventilatorPreset = VentilatorSettings(mode = "Standby (Room Air)", isActive = false),
            defibrillatorPreset = DefibrillatorSettings(mode = "Telemetry Monitor"),
            primarySurvey = PrimarySurveyABCDE(
                airway = "Patent.",
                breathing = "RR 16/min, SpO2 100% on room air.",
                circulation = "BP 142/91 mmHg, HR 95 bpm.",
                disability = "GCS 15; Right pupil is mid-dilated (5.5 mm), fixed, and non-reactive to light! Visual acuity OD: hand motion only (OS: 20/25).",
                exposure = "Afebrile at 36.0°C."
            ),
            physicalExam = PhysicalExamClues(
                generalAppearance = "Distressed older woman shielding her right eye from light and holding an emesis basin.",
                heentAndNeck = "Right eye shows marked perilimbal ciliary flush (conjunctival injection), steamy/hazy corneal edema, shallow anterior chamber, and a fixed mid-dilated 5.5 mm pupil. Right globe feels rock-hard to gentle palpation; Tonopen IOP OD = 58 mmHg (OS = 14 mmHg).",
                chestAndLungs = "Clear bilaterally.",
                cardiovascular = "Regular rate and rhythm at 95 bpm.",
                abdomenAndPelvis = "Soft, non-tender despite active nausea and vomiting.",
                extremitiesAndSkin = "Warm and dry.",
                neurologic = "Cranial nerves intact except right visual loss and fixed mid-dilated pupil."
            ),
            labReports = listOf(
                LabEntry("CBC & Chemistry", "Intraocular Pressure (Tonometry OD)", "58", "mmHg", "10–21", LabFlag.CRITICAL),
                LabEntry("CBC & Chemistry", "Intraocular Pressure (Tonometry OS)", "14", "mmHg", "10–21", LabFlag.NORMAL),
                LabEntry("CBC & Chemistry", "Serum Electrolytes & Cr", "Normal (Cr 0.8)", "mg/dL", "0.6–1.2", LabFlag.NORMAL)
            ),
            abgVbgReports = listOf(
                LabEntry("ABG / VBG", "Venous pH", "7.42", "", "7.35–7.45", LabFlag.NORMAL)
            ),
            imagingAndEcg = listOf(
                ImagingStudy(
                    id = "c11_fig1",
                    modality = "CLINICAL FIG",
                    title = "Slit-Lamp & Tonometry Ocular Assessment (0 PDF Figures; Clinical Simulation View)",
                    figureRef = "Case 11 Ocular Diagnostics (0 Source Figures in PDF)",
                    pdfPages = "pp. 335–350",
                    findings = "Right eye perilimbal ciliary injection, hazy edematous cornea, shallow anterior chamber with iris bombé blocking trabecular meshwork outflow, fixed mid-dilated pupil (5.5 mm), and IOP 58 mmHg.",
                    impression = "Acute angle-closure glaucoma (OD).",
                    visualPattern = ClinicalVisualPattern.SLIT_LAMP_GLAUCOMA
                )
            ),
            criticalActions = listOf(
                CriticalAction("c11_a1", 1, "Perform complete ocular exam including visual acuity, slit-lamp exam, and intraocular pressure tonometry (IOP 58 mmHg OD)", OralBoardDomain.DIAGNOSTIC_STUDIES, 25, listOf("exam", "torch", "tonometry", "iop")),
                CriticalAction("c11_a2", 2, "Administer parenteral antiemetic and analgesia for severe ocular pain and vomiting", OralBoardDomain.PATIENT_MANAGEMENT, 25, listOf("analgesi", "pain", "morphine", "ondansetron")),
                CriticalAction("c11_a3", 3, "Initiate multi-agent IOP-lowering therapy: topical beta-blocker (Timolol 0.5%), alpha-agonist (Apraclonidine), systemic Carbonic Anhydrase Inhibitor (Acetazolamide 500 mg IV), osmotic agent (Mannitol), and miotic (Pilocarpine 1-2% once IOP begins dropping)", OralBoardDomain.PATIENT_MANAGEMENT, 25, listOf("timolol", "acetazolamide", "pilocarpine", "mannitol", "glaucoma")),
                CriticalAction("c11_a4", 4, "Obtain emergent Ophthalmology consultation for definitive laser peripheral iridotomy", OralBoardDomain.DISPOSITION, 25, listOf("ophthalmology", "iridotomy"))
            ),
            defaultQuestions = listOf(
                UnlockQuestion(
                    QuestionGateType.Q0_GATE,
                    "Bay Entrance Gate (Q0)",
                    prompt = "A 64-year-old woman exits a dark movie theater with sudden severe right eye pain, halos around lights, vomiting, a hazy cornea, and a fixed mid-dilated pupil. Why did the dark theater trigger this emergency?",
                    options = listOf("Pupillary dilation (mydriasis) in darkness bunches the peripheral iris against the lens/trabecular meshwork, blocking aqueous humor outflow", "Darkness causes retinal detachment", "Movie screens emit ultraviolet keratitis rays", "Darkness lowers intraocular pressure too much"),
                    correctIndex = 0,
                    clinicalExplanation = "Mid-dilation in a dark room causes pupillary block and crowding of the iridocorneal angle, acutely obstructing aqueous humor outflow."
                ),
                UnlockQuestion(
                    QuestionGateType.Q1_SURVEY,
                    "Bedside Survey Station (Q1)",
                    prompt = "On ocular examination, what intraocular pressure (IOP) measurement on tonometry confirms acute angle-closure glaucoma?",
                    options = listOf("IOP markedly elevated (typically 40–70+ mmHg; normal is 10–21 mmHg)", "IOP < 5 mmHg", "IOP 12 mmHg bilaterally", "Tonometry is contraindicated in glaucoma"),
                    correctIndex = 0,
                    clinicalExplanation = "Normal IOP is 10–21 mmHg; in acute angle-closure glaucoma, IOP typically spikes above 40–60 mmHg."
                ),
                UnlockQuestion(
                    QuestionGateType.Q2_LABS,
                    "Point-of-Care Lab Console (Q2)",
                    prompt = "Which pharmacological combination from Crash Cart Drawer 1 rapidly reduces aqueous humor production and lowers intraocular pressure?",
                    options = listOf("Topical Timolol 0.5% + IV Acetazolamide 500 mg + IV Mannitol + delayed Pilocarpine 1–2% miotic drops", "Topical Atropine 1% mydriatic drops", "Phenylephrine ophthalmic drops", "Oral prednisone monotherapy"),
                    correctIndex = 0,
                    clinicalExplanation = "Timolol and Acetazolamide suppress aqueous humor production, Mannitol osmotically dehydrates the vitreous, and Pilocarpine constricts the pupil to open the angle once iris ischemia resolves."
                ),
                UnlockQuestion(
                    QuestionGateType.Q3_IMAGING,
                    "Mobile Radiology Terminal (Q3)",
                    prompt = "Why should a non-contrast Head CT not distract the team when a patient presents with frontal headache, nausea/vomiting, and a painful red eye with a fixed mid-dilated pupil?",
                    options = listOf("Acute angle-closure glaucoma frequently masquerades as a neurologic headache or GI illness; measuring IOP prevents permanent optic nerve blindness", "Head CT cures glaucoma", "Glaucoma only affects the occipital lobe", "Mydriatics should be given before tonometry"),
                    correctIndex = 0,
                    clinicalExplanation = "Patients often present with severe frontal headache and vomiting; checking visual acuity, pupils, corneal clarity, and IOP avoids disastrous delay in treating angle-closure glaucoma."
                )
            ),
            dispositionAndPearls = "Emergent Ophthalmology consultation for serial IOP checks (<20 mmHg target) and bilateral laser peripheral iridotomy.",
            instructorClues = listOf(
                "Use theAcute Angle-Closure Glaucoma Pack in Crash Cart Drawer 1 (Timolol + Acetazolamide + Pilocarpine + Mannitol).",
                "Never use mydriatic (dilating) drops like atropine or tropicamide—they worsen angle closure!"
            )
        ),

        // CASE 12
        OralBoardCase(
            caseNumber = 12,
            scenarioCategory = "Abdominal pain",
            mainDiagnosis = "Incarcerated/strangulated inguinal hernia with bowel obstruction",
            pdfPages = "351–369",
            sourceFiguresCount = 3,
            patientName = "Subrata Banerjee",
            ageDesc = "68-year-old Male",
            gender = "Male",
            weightKg = 79f,
            chiefComplaint = "Severe crampy abdominal pain, persistent feculent vomiting, and a painful right groin bulge for 18 hours",
            triageNote = "Has had a reducible right groin bulge for 3 years; after lifting heavy firewood yesterday it became locked, hot, and intensely painful.",
            patientSkinToneTag = "PALE",
            initialVitals = VitalSigns("86/57", 86, 57, 89, 20, 99, 37.1f, 34, "Normal Sinus Rhythm"),
            stabilizedVitals = VitalSigns("118/74", 118, 74, 80, 16, 99, 37.0f, 38, "Normal Sinus Rhythm"),
            ventilatorPreset = VentilatorSettings(mode = "Standby (Room Air)", isActive = false),
            defibrillatorPreset = DefibrillatorSettings(mode = "Telemetry Monitor"),
            primarySurvey = PrimarySurveyABCDE(
                airway = "Patent, active bilious/feculent emesis.",
                breathing = "RR 20/min, SpO2 99% on room air.",
                circulation = "Hypotensive! BP 86/57 mmHg, HR 89 bpm, dry mucous membranes.",
                disability = "GCS 15, in acute abdominal & right groin distress.",
                exposure = "Must expose groin/genitalia! Tense, erythematous, exquisitely tender 7 cm non-reducible right inguinoscrotal mass."
            ),
            physicalExam = PhysicalExamClues(
                generalAppearance = "Dehydrated older male with knees flexed to relieve abdominal and groin tension.",
                heentAndNeck = "Dry tongue, sunken eyes.",
                chestAndLungs = "Clear to auscultation.",
                cardiovascular = "BP 86/57 mmHg, normal rhythm.",
                abdomenAndPelvis = "Distended, tympanitic abdomen with high-pitched tinkling bowel sounds; right inguinal canal has a rock-hard, erythematous, hot, non-reducible mass extending toward the scrotum.",
                extremitiesAndSkin = "Cool extremities, delayed capillary refill.",
                neurologic = "Intact."
            ),
            labReports = listOf(
                LabEntry("CBC & Chemistry", "WBC Count", "19.2", "x10³/µL", "4.5–11.0", LabFlag.HIGH),
                LabEntry("CBC & Chemistry", "Serum Lactate", "4.4", "mmol/L", "0.5–2.0", LabFlag.CRITICAL),
                LabEntry("CBC & Chemistry", "BUN / Creatinine", "BUN 42, Cr 1.7", "mg/dL", "Cr 0.7–1.3", LabFlag.HIGH)
            ),
            abgVbgReports = listOf(
                LabEntry("ABG / VBG", "Venous pH", "7.30", "", "7.35–7.45", LabFlag.LOW),
                LabEntry("ABG / VBG", "HCO3-", "17", "mEq/L", "22–28", LabFlag.LOW)
            ),
            imagingAndEcg = listOf(
                ImagingStudy(
                    id = "c12_fig1",
                    modality = "CT",
                    title = "Abdominal X-Ray Series & CT Abdomen/Pelvis",
                    figureRef = "Case 12 Fig 12.1–12.3 (3 Source Figures)",
                    pdfPages = "pp. 351–369",
                    findings = "Dilated loops of small bowel (>3 cm) with multiple air-fluid levels in a stepladder pattern and paucity of colonic gas; CT confirms herniation of a closed small-bowel loop through the right inguinal canal with mesenteric stranding and compromised bowel wall enhancement.",
                    impression = "Strangulated right inguinal hernia causing high-grade mechanical small-bowel obstruction.",
                    visualPattern = ClinicalVisualPattern.CT_STRANGULATED_HERNIA
                )
            ),
            criticalActions = listOf(
                CriticalAction("c12_a1", 1, "Perform complete physical examination including full exposure of the inguinal region and genitalia (identifying the strangulated hernia)", OralBoardDomain.DATA_ACQUISITION, 16, listOf("exam", "groin", "hernia")),
                CriticalAction("c12_a2", 2, "Establish large-bore IV access and administer aggressive isotonic crystalloid (0.9% NS) fluid boluses for hypotension (BP 86/57)", OralBoardDomain.ACUITY_RECOGNITION, 17, listOf("normal saline", "fluid", "bolus", "iv access")),
                CriticalAction("c12_a3", 3, "Keep NPO, place Nasogastric (NG) tube for gastric decompression, and give parenteral analgesia & antiemetics", OralBoardDomain.PATIENT_MANAGEMENT, 17, listOf("nasogastric", "ng tube", "decompression", "analgesi", "ondansetron")),
                CriticalAction("c12_a4", 4, "AVOID manual bedside reduction of a strangulated hernia (overlying erythema, systemic hypotension, lactic acidosis)", OralBoardDomain.SAFETY_REASSESSMENT, 16, listOf("surgery", "strangulated")),
                CriticalAction("c12_a5", 5, "Administer broad-spectrum preoperative IV antibiotics covering enteric Gram-negatives and anaerobes", OralBoardDomain.PATIENT_MANAGEMENT, 17, listOf("antibiotic", "piperacillin", "ceftriaxone")),
                CriticalAction("c12_a6", 6, "Obtain emergent General Surgery consultation for immediate operative hernia repair and bowel resection", OralBoardDomain.DISPOSITION, 17, listOf("surgery", "or"))
            ),
            defaultQuestions = listOf(
                UnlockQuestion(
                    QuestionGateType.Q0_GATE,
                    "Bay Entrance Gate (Q0)",
                    prompt = "A 68-year-old man presents with vomiting, abdominal distension, BP 86/57, lactate 4.4 mmol/L, and a hot, erythematous, non-reducible right inguinal mass. Why is manual bedside taxis (reduction) strictly CONTRAINDICATED?",
                    options = listOf("Forcibly reducing strangulated, gangrenous bowel pushes necrotic perforated intestine into the peritoneal cavity ('reduction en masse')", "Manual reduction causes hyperkalaemia", "Inguinal hernias only contain omentum", "Taxis should always be done before IV fluids"),
                    correctIndex = 0,
                    clinicalExplanation = "Signs of strangulation (erythema/warmth over hernia sac, hypotension, leukocytosis, lactic acidosis, peritonitis) contraindicate manual reduction due to risk of returning necrotic bowel to the abdomen."
                ),
                UnlockQuestion(
                    QuestionGateType.Q1_SURVEY,
                    "Bedside Survey Station (Q1)",
                    prompt = "What is the most common cause of mechanical Small-Bowel Obstruction (SBO) when a patient has no history of prior abdominal surgery?",
                    options = listOf("Incarcerated abdominal wall or inguinal/femoral hernia (which is missed if the groin is not exposed during exam!)", "Post-operative adhesions", "Meckel diverticulum", "Pyloric stenosis"),
                    correctIndex = 0,
                    clinicalExplanation = "In patients without prior laparotomy, an incarcerated hernia is the leading cause of SBO; always inspect the inguinal and femoral canals."
                ),
                UnlockQuestion(
                    QuestionGateType.Q2_LABS,
                    "Point-of-Care Lab Console (Q2)",
                    prompt = "Which bedside decompression procedure from Crash Cart Drawer 6 relieves vomiting and prevents aspiration in mechanical small-bowel obstruction?",
                    options = listOf("Nasogastric (NG) tube placement connected to low intermittent wall suction", "Whole-bowel polyethylene glycol irrigation", "Lumbar puncture", "Needle thoracostomy"),
                    correctIndex = 0,
                    clinicalExplanation = "Nasogastric tube decompression evacuates air and feculent fluid proximal to the obstruction, reducing bowel wall tension and aspiration risk."
                ),
                UnlockQuestion(
                    QuestionGateType.Q3_IMAGING,
                    "Mobile Radiology Terminal (Q3)",
                    prompt = "What upright abdominal radiograph and CT findings (Case 12 Fig 12.1–12.3) characterize high-grade mechanical small-bowel obstruction?",
                    options = listOf("Dilated small-bowel loops > 3 cm with differential air-fluid levels, string-of-pearls sign, and transition point at the right inguinal ring", "Thumbprinting of the epiglottis", "Calcified gallstones in the RUQ", "Left-to-right cerebral midline shift"),
                    correctIndex = 0,
                    clinicalExplanation = "Dilated small-bowel loops (>3 cm) with multiple air-fluid levels and a sharp transition point within the right inguinal canal confirm SBO from hernia strangulation."
                )
            ),
            dispositionAndPearls = "Immediate transfer to Operating Room with General Surgery for exploratory laparotomy / inguinal exploration and bowel resection.",
            instructorClues = listOf(
                "Give 0.9% Normal Saline boluses from Drawer 5 immediately for hypotension (BP 86/57).",
                "Place an NG tube from Drawer 6 and administer broad-spectrum IV antibiotics from Drawer 1; do NOT attempt manual reduction."
            )
        ),

        // CASE 13
        OralBoardCase(
            caseNumber = 13,
            scenarioCategory = "Ringing in the ears",
            mainDiagnosis = "Acute salicylate toxicity",
            pdfPages = "370–388",
            sourceFiguresCount = 2,
            patientName = "Sneha Joshi",
            ageDesc = "24-year-old Female",
            gender = "Female",
            weightKg = 56f,
            chiefComplaint = "Bilateral ringing in the ears (tinnitus), muffled hearing, nausea, and rapid deep breathing after taking a bottle of aspirin",
            triageNote = "Ingested ~80 extra-strength aspirin (325 mg each) tablets 4 hours ago. Complains of loud roaring tinnitus, profuse sweating, and hyperventilation.",
            patientSkinToneTag = "FLUSHED",
            initialVitals = VitalSigns("107/56", 107, 56, 117, 22, null, 37.2f, 22, "Sinus Tachycardia"),
            stabilizedVitals = VitalSigns("118/72", 118, 72, 88, 18, 99, 37.0f, 32, "Normal Sinus Rhythm"),
            ventilatorPreset = VentilatorSettings(mode = "Standby — AVOID Intubation Unless Exhausted (High Minute Volume Needed!)", tidalVolumeMlPerKg = 10, respRatePerMin = 28, fio2Percent = 100, isActive = false),
            defibrillatorPreset = DefibrillatorSettings(mode = "Continuous ECG Telemetry"),
            primarySurvey = PrimarySurveyABCDE(
                airway = "Patent, speaking loudly because of muffled hearing and tinnitus.",
                breathing = "Hyperpneic and tachypneic (RR 22/min with deep tidal volumes), lungs clear, SpO2 initially None (probe placed -> 99%).",
                circulation = "BP 107/56 mmHg, HR 117 bpm, diaphoretic warm skin.",
                disability = "GCS 15, anxious and mildly restless; pupils 4 mm equal.",
                exposure = "Diaphoretic, T 37.2°C."
            ),
            physicalExam = PhysicalExamClues(
                generalAppearance = "Diaphoretic young woman breathing deeply and asking staff to speak up due to loud ringing in her ears.",
                heentAndNeck = "Tympanic membranes normal; bilateral sensorineural tinnitus.",
                chestAndLungs = "Clear breath sounds with marked hyperpnea (Kussmaul-like deep respirations).",
                cardiovascular = "Tachycardic at 117 bpm.",
                abdomenAndPelvis = "Mild epigastric tenderness, normoactive bowel sounds.",
                extremitiesAndSkin = "Flushed, warm, diaphoretic skin.",
                neurologic = "Alert, mild tremor, no focal motor deficits."
            ),
            labReports = listOf(
                LabEntry("Coagulation & Tox", "Serum Salicylate Level", "74", "mg/dL", "Therapeutic 10–30 (Toxic >30)", LabFlag.CRITICAL),
                LabEntry("Coagulation & Tox", "Acetaminophen Level", "Undetectable", "µg/mL", "Negative", LabFlag.NORMAL),
                LabEntry("CBC & Chemistry", "Serum Potassium (K+)", "3.1", "mEq/L", "3.5–5.0", LabFlag.LOW),
                LabEntry("CBC & Chemistry", "Serum Bicarbonate (HCO3-)", "13", "mEq/L", "22–28", LabFlag.CRITICAL),
                LabEntry("CBC & Chemistry", "Anion Gap", "24", "mEq/L", "8–12", LabFlag.CRITICAL),
                LabEntry("Urinalysis / CSF", "Urine pH & Pregnancy Test", "Urine pH 5.2, hCG Negative", "", "Target Urine pH 7.5–8.0", LabFlag.LOW)
            ),
            abgVbgReports = listOf(
                LabEntry("ABG / VBG", "Arterial pH", "7.43", "", "7.35–7.45", LabFlag.NORMAL),
                LabEntry("ABG / VBG", "PaCO2", "20", "mmHg", "35–45", LabFlag.CRITICAL),
                LabEntry("ABG / VBG", "HCO3-", "13", "mEq/L", "22–28", LabFlag.CRITICAL),
                LabEntry("ABG / VBG", "Acid-Base Pattern", "Mixed Primary Respiratory Alkalosis + High-Anion-Gap Metabolic Acidosis", "", "Normal", LabFlag.CRITICAL)
            ),
            imagingAndEcg = listOf(
                ImagingStudy(
                    id = "c13_fig1",
                    modality = "ECG",
                    title = "12-Lead ECG & Serial Salicylate Acid-Base Nomogram",
                    figureRef = "Case 13 Fig 13.1–13.2 (2 Source Figures)",
                    pdfPages = "pp. 370–388",
                    findings = "Sinus tachycardia at 117 bpm with normal QRS/QTc and flat T waves from hypokalemia; serial salicylate curve demonstrates rising/sustained toxic level (74 mg/dL) requiring systemic & urinary alkalinization and hemodialysis.",
                    impression = "Severe acute salicylate (aspirin) toxicity.",
                    visualPattern = ClinicalVisualPattern.ABG_SALICYLATE_GRAPH
                )
            ),
            criticalActions = listOf(
                CriticalAction("c13_a1", 1, "Place on cardiac monitor/pulse oximetry and administer oral Activated Charcoal for GI decontamination", OralBoardDomain.PATIENT_MANAGEMENT, 16, listOf("monitor", "charcoal", "decontaminat")),
                CriticalAction("c13_a2", 2, "Order stat serum salicylate level (and repeat every 2 hours), ABG, BMP, and co-ingestant screen", OralBoardDomain.DIAGNOSTIC_STUDIES, 17, listOf("blood", "salicylate", "abg")),
                CriticalAction("c13_a3", 3, "Recognize mixed primary respiratory alkalosis and high-anion-gap metabolic acidosis on ABG", OralBoardDomain.PROBLEM_SOLVING, 17, listOf("abg", "acidosis", "alkalosis")),
                CriticalAction("c13_a4", 4, "Administer IV crystalloid fluid resuscitation and IV Dextrose (to prevent CNS neuroglycopenia)", OralBoardDomain.PATIENT_MANAGEMENT, 16, listOf("normal saline", "dextrose", "fluid")),
                CriticalAction("c13_a5", 5, "Initiate IV Sodium Bicarbonate bolus and infusion (with Potassium replacement) for serum & urinary alkalinization (target urine pH 7.5–8.0)", OralBoardDomain.PATIENT_MANAGEMENT, 17, listOf("bicarbonate", "alkaliniz")),
                CriticalAction("c13_a6", 6, "Consult Nephrology for emergent hemodialysis and Poison Control / Medical ICU admission", OralBoardDomain.DISPOSITION, 17, listOf("nephrology", "dialysis", "icu"))
            ),
            defaultQuestions = listOf(
                UnlockQuestion(
                    QuestionGateType.Q0_GATE,
                    "Bay Entrance Gate (Q0)",
                    prompt = "A 24-year-old presents with tinnitus, hyperpnea, HR 117, and RR 22 after ingesting 80 aspirin tablets. What is the signature dual acid-base disturbance of acute adult salicylate toxicity?",
                    options = listOf("Mixed primary respiratory alkalosis (direct medullary stimulation) AND high-anion-gap metabolic acidosis (uncoupled oxidative phosphorylation)", "Pure respiratory acidosis with metabolic alkalosis", "Hypochloremic metabolic alkalosis only", "Normal anion gap renal tubular acidosis"),
                    correctIndex = 0,
                    clinicalExplanation = "Salicylates directly stimulate the medullary respiratory center (causing early respiratory alkalosis) and uncouple mitochondrial oxidative phosphorylation (causing high-anion-gap lactic/keto-acidosis)."
                ),
                UnlockQuestion(
                    QuestionGateType.Q1_SURVEY,
                    "Bedside Survey Station (Q1)",
                    prompt = "Why is routine endotracheal intubation and sedation extremely dangerous in a hyperventilating salicylate-poisoned patient?",
                    options = listOf("Apnea during RSI causes an abrupt rise in PaCO2 and drop in blood pH, driving non-ionized salicylic acid rapidly across the blood-brain barrier into the brain", "Aspirin dissolves endotracheal tube cuffs", "Intubation causes immediate hypertension", "Salicylates cause vocal cord ankylosis"),
                    correctIndex = 0,
                    clinicalExplanation = "Even brief hypoventilation/acidemia during RSI shifts salicylate to its non-ionized lipid-soluble form, causing lethal CNS salicylate entry."
                ),
                UnlockQuestion(
                    QuestionGateType.Q2_LABS,
                    "Point-of-Care Lab Console (Q2)",
                    prompt = "How does an IV Sodium Bicarbonate infusion (target urine pH 7.5–8.0) dramatically enhance renal salicylate elimination ('ion trapping')?",
                    options = listOf("Alkalinizing the urine converts salicylic acid into its ionized form, preventing renal tubular reabsorption and increasing clearance >10-fold (provided hypokalemia is corrected!)", "Sodium bicarbonate chelates aspirin into ferrioxamine", "Bicarbonate blocks gastric acid secretion only", "Bicarbonate causes osmotic diuresis without changing pH"),
                    correctIndex = 0,
                    clinicalExplanation = "Urinary alkalinization (pH 7.5–8.0) traps ionized salicylate in the renal tubules; hypokalemia (K+ 3.1) must be repleted simultaneously or the kidney will excrete H+ instead of K+."
                ),
                UnlockQuestion(
                    QuestionGateType.Q3_IMAGING,
                    "Mobile Radiology Terminal (Q3)",
                    prompt = "Why must serum salicylate levels (Case 13 Fig 13.1–13.2) be repeated every 2 hours until clearly declining, and when is hemodialysis indicated?",
                    options = listOf("Enteric-coated or pharmacobezoar aspirin causes delayed erratic absorption; hemodialysis is indicated for levels >90–100 mg/dL, CNS alteration, pulmonary edema, or refractory acidosis", "A single 1-hour level rules out toxicity", "Dialysis cannot remove salicylates", "Only urine levels matter"),
                    correctIndex = 0,
                    clinicalExplanation = "Salicylate tablets can form gastric concretions causing rising levels hours later; Nephrology consultation for hemodialysis is crucial in severe toxicity."
                )
            ),
            dispositionAndPearls = "Admit to Medical ICU with IV Sodium Bicarbonate infusion, potassium supplementation, serial q2h salicylate levels, and Nephrology consultation for hemodialysis.",
            instructorClues = listOf(
                "Give IV Sodium Bicarbonate from Drawer 1 and Normal Saline from Drawer 5.",
                "Even with a normal serum glucose, salicylate toxicity causes CNS neuroglycopenia—supplement with IV Dextrose!"
            )
        ),

        // CASE 14
        OralBoardCase(
            caseNumber = 14,
            scenarioCategory = "Vomiting child",
            mainDiagnosis = "Henoch–Schönlein purpura (IgA vasculitis) with gastrointestinal involvement",
            pdfPages = "389–406",
            sourceFiguresCount = 1,
            patientName = "Aarav Pillai",
            ageDesc = "6-year-old Male Child",
            gender = "Male",
            weightKg = 21f,
            chiefComplaint = "Colicky abdominal pain, recurrent vomiting, painful swollen knees/ankles, and a purplish rash on the legs and buttocks",
            triageNote = "Had an upper respiratory cold 10 days ago. Over the past 48 hours developed ankle pain, a non-blanching rash on both legs, and today severe episodic abdominal pain with vomiting.",
            patientSkinToneTag = "NORMAL",
            isPediatricOrInfant = true,
            initialVitals = VitalSigns("96/63", 96, 63, 136, 28, null, 36.1f, 36, "Pediatric Sinus Tachycardia"),
            stabilizedVitals = VitalSigns("104/68", 104, 68, 102, 20, 99, 36.6f, 38, "Normal Pediatric Sinus"),
            ventilatorPreset = VentilatorSettings(mode = "Standby (Room Air)", isActive = false),
            defibrillatorPreset = DefibrillatorSettings(mode = "Pediatric Pulse Oximetry & Telemetry"),
            primarySurvey = PrimarySurveyABCDE(
                airway = "Patent, crying during abdominal cramps.",
                breathing = "RR 28/min, lungs clear, SpO2 99% once pediatric probe placed.",
                circulation = "BP 96/63 mmHg, HR 136 bpm, capillary refill 2.5 sec.",
                disability = "GCS 15, alert and interactive between pain episodes.",
                exposure = "Must examine lower extremities and buttocks! Symmetric crop of palpable purpura across both buttocks, posterior thighs, and lower legs."
            ),
            physicalExam = PhysicalExamClues(
                generalAppearance = "Uncomfortable 6-year-old boy drawing his knees up during waves of abdominal pain.",
                heentAndNeck = "Tacky oral mucosa, normocephalic.",
                chestAndLungs = "Clear to auscultation bilaterally.",
                cardiovascular = "Tachycardic at 136 bpm, normal S1/S2.",
                abdomenAndPelvis = "Diffuse periumbilical tenderness without rigid guarding; guaiac-positive (occult blood positive) stool on rectal exam; normal scrotal/testicular exam.",
                extremitiesAndSkin = "Classic symmetric palpable purpura and petechiae over the extensor surfaces of the lower legs, ankles, and buttocks; periarticular swelling and tenderness of both ankles and knees.",
                neurologic = "Intact."
            ),
            labReports = listOf(
                LabEntry("CBC & Chemistry", "Platelet Count", "310", "x10³/µL (NORMAL!)", "150–400", LabFlag.NORMAL),
                LabEntry("Coagulation & Tox", "PT / INR / aPTT", "Normal (INR 1.0)", "", "Normal", LabFlag.NORMAL),
                LabEntry("CBC & Chemistry", "BUN / Creatinine", "BUN 16, Cr 0.5", "mg/dL", "0.3–0.7", LabFlag.NORMAL),
                LabEntry("Urinalysis / CSF", "Urinalysis", "15–20 RBC/hpf (Microscopic Hematuria), 1+ Protein", "", "Negative", LabFlag.HIGH)
            ),
            abgVbgReports = listOf(
                LabEntry("ABG / VBG", "Venous pH", "7.39", "", "7.35–7.45", LabFlag.NORMAL)
            ),
            imagingAndEcg = listOf(
                ImagingStudy(
                    id = "c14_fig1",
                    modality = "USG",
                    title = "Lower Extremity Purpura Inspection & Abdominal Ultrasound",
                    figureRef = "Case 14 Fig 14.1 (1 Source Figure)",
                    pdfPages = "pp. 389–406",
                    findings = "Clinical inspection shows gravity-dependent palpable purpura on lower legs and buttocks. Abdominal ultrasound demonstrates bowel wall edema/hematoma without ileo-ileal intussusception ('target sign' negative).",
                    impression = "Henoch–Schönlein purpura (IgA vasculitis) with GI and renal involvement; intussusception excluded.",
                    visualPattern = ClinicalVisualPattern.PURPURA_BOWEL_USG
                )
            ),
            criticalActions = listOf(
                CriticalAction("c14_a1", 1, "Fully undress and examine the child's lower extremities, buttocks, and scrotum to identify classic palpable purpura", OralBoardDomain.DATA_ACQUISITION, 20, listOf("exam", "purpura", "skin")),
                CriticalAction("c14_a2", 2, "Establish IV access and administer 20 mL/kg isotonic crystalloid (0.9% NS) fluid hydration, analgesia, and antiemetic", OralBoardDomain.PATIENT_MANAGEMENT, 20, listOf("normal saline", "fluid", "bolus", "ondansetron")),
                CriticalAction("c14_a3", 3, "Obtain CBC and coagulation studies to confirm normal platelet count and exclude thrombocytopenic purpura (ITP/HUS/meningococcemia)", OralBoardDomain.DIAGNOSTIC_STUDIES, 20, listOf("blood", "platelet", "cbc")),
                CriticalAction("c14_a4", 4, "Obtain Urinalysis and renal function panel to screen for HSP nephritis (hematuria/proteinuria)", OralBoardDomain.DIAGNOSTIC_STUDIES, 20, listOf("urine", "renal")),
                CriticalAction("c14_a5", 5, "Perform Abdominal Ultrasound to evaluate for HSP-associated intussusception and admit to Pediatrics with corticosteroid therapy for severe GI pain", OralBoardDomain.DISPOSITION, 20, listOf("ultrasound", "usg", "pediatrics"))
            ),
            defaultQuestions = listOf(
                UnlockQuestion(
                    QuestionGateType.Q0_GATE,
                    "Bay Entrance Gate (Q0)",
                    prompt = "A 6-year-old boy presents with colicky abdominal pain, vomiting, ankle arthralgias, and palpable purpura on the buttocks and lower legs. What is the classic clinical tetrad of Henoch–Schönlein Purpura (IgA vasculitis)?",
                    options = listOf("Palpable purpura (with normal platelets), arthralgias/arthritis, colicky abdominal pain/GI bleeding, and renal disease (hematuria/IgA nephritis)", "Fever, strawberry tongue, desquamating rash, and coronary aneurysms", "Thrombocytopenia, microangiopathic hemolytic anemia, and anuria", "Conjunctivitis, urethritis, and asymmetric arthritis"),
                    correctIndex = 0,
                    clinicalExplanation = "HSP is an IgA-mediated small-vessel leukocytoclastic vasculitis characterized by non-thrombocytopenic palpable purpura, arthritis, GI pain/bleeding, and glomerulonephritis."
                ),
                UnlockQuestion(
                    QuestionGateType.Q1_SURVEY,
                    "Bedside Survey Station (Q1)",
                    prompt = "What surgical abdominal complication occurs with increased frequency in children with HSP due to submucosal bowel wall hematomas acting as a lead point?",
                    options = listOf("Intussusception (most commonly ileo-ileal in HSP)", "Hypertrophic pyloric stenosis", "Boerhaave esophageal perforation", "Sigmoid volvulus"),
                    correctIndex = 0,
                    clinicalExplanation = "Submucosal bowel hematomas in HSP act as a pathological lead point for intussusception (often ileo-ileal), requiring abdominal ultrasound evaluation."
                ),
                UnlockQuestion(
                    QuestionGateType.Q2_LABS,
                    "Point-of-Care Lab Console (Q2)",
                    prompt = "How does the platelet count on CBC distinguish HSP purpura from Immune Thrombocytopenic Purpura (ITP) or Hemolytic Uremic Syndrome (HUS)?",
                    options = listOf("Platelet count and coagulation studies are completely NORMAL in HSP (it is a vasculitis, not thrombocytopenia)", "Platelets are always < 10,000/µL in HSP", "PT/INR is > 8.0 in HSP", "Fibrinogen is absent in HSP"),
                    correctIndex = 0,
                    clinicalExplanation = "HSP causes non-thrombocytopenic palpable purpura with normal platelet counts and normal coagulation times."
                ),
                UnlockQuestion(
                    QuestionGateType.Q3_IMAGING,
                    "Mobile Radiology Terminal (Q3)",
                    prompt = "Why is Abdominal Ultrasound (Case 14 Fig 14.1) preferred over contrast barium enema when screening an HSP child for intussusception?",
                    options = listOf("HSP intussusceptions are frequently ileo-ileal (small-bowel to small-bowel), which ultrasound detects but a colonic contrast enema can miss", "Ultrasound uses ionizing radiation", "Barium enema diagnoses glomerulonephritis", "Ultrasound measures platelet counts"),
                    correctIndex = 0,
                    clinicalExplanation = "Because HSP intussusceptions are often confined to the small bowel (ileo-ileal), abdominal ultrasound is the diagnostic test of choice."
                )
            ),
            dispositionAndPearls = "Admit to Inpatient Pediatrics for IV hydration, pain control, corticosteroids for severe GI vasculitis pain, and serial blood pressure/urinalysis monitoring.",
            instructorClues = listOf(
                "Examine the child's buttocks and lower legs (Exposure) to reveal the palpable purpura.",
                "Remember to check Urinalysis (shows microscopic hematuria) and order an Abdominal Ultrasound to rule out intussusception."
            )
        ),

        // CASE 15
        OralBoardCase(
            caseNumber = 15,
            scenarioCategory = "Snake bite",
            mainDiagnosis = "Pit-viper bite without envenomation (dry bite)",
            pdfPages = "407–422",
            sourceFiguresCount = 0,
            patientName = "Murugan Selvam",
            ageDesc = "31-year-old Male",
            gender = "Male",
            weightKg = 82f,
            chiefComplaint = "Bitten on the right ankle by a rattlesnake while hiking 45 minutes ago",
            triageNote = "Stepped near a brush pile; heard a rattle and felt a sharp strike to the right lateral malleolus. Friends applied a tight belt tourniquet above his calf en route.",
            patientSkinToneTag = "NORMAL",
            initialVitals = VitalSigns("101/57", 101, 57, 97, 20, null, 36.0f, 38, "Normal Sinus Rhythm"),
            stabilizedVitals = VitalSigns("118/74", 118, 74, 78, 16, 99, 36.6f, 38, "Normal Sinus Rhythm"),
            ventilatorPreset = VentilatorSettings(mode = "Standby (Room Air)", isActive = false),
            defibrillatorPreset = DefibrillatorSettings(mode = "Telemetry & Pulse Oximetry"),
            primarySurvey = PrimarySurveyABCDE(
                airway = "Patent, anxious.",
                breathing = "RR 20/min, SpO2 99% once pulse oximeter placed.",
                circulation = "BP 101/57 mmHg, HR 97 bpm.",
                disability = "GCS 15, no perioral paresthesias, no metallic taste, no fasciculations.",
                exposure = "Two distinct puncture marks 1.2 cm apart over the right lateral ankle with minimal pinpoint oozing; tight leather belt tourniquet tied around right mid-calf!"
            ),
            physicalExam = PhysicalExamClues(
                generalAppearance = "Anxious hiker with a makeshift belt tourniquet on his right lower leg.",
                heentAndNeck = "No ptosis, no tongue swelling, no gum bleeding.",
                chestAndLungs = "Clear to auscultation.",
                cardiovascular = "Regular rhythm at 97 bpm.",
                abdomenAndPelvis = "Soft, non-tender.",
                extremitiesAndSkin = "Two fang puncture marks on right lateral ankle. Once tourniquet is removed: NO progressive swelling, NO ecchymosis, NO hemorrhagic blebs, soft calf compartments, 2+ dorsalis pedis pulse. Limb circumference remains unchanged over 8 hours of serial checks.",
                neurologic = "No paresthesias or cranial nerve deficits."
            ),
            labReports = listOf(
                LabEntry("CBC & Chemistry", "Platelet Count (0 hr & 6 hr)", "265 → 260", "x10³/µL", "150–400", LabFlag.NORMAL),
                LabEntry("Coagulation & Tox", "PT / INR (0 hr & 6 hr)", "11.8 sec / INR 1.0", "", "0.9–1.1", LabFlag.NORMAL),
                LabEntry("Coagulation & Tox", "Plasma Fibrinogen (0 hr & 6 hr)", "320 → 315", "mg/dL", "200–400", LabFlag.NORMAL),
                LabEntry("CBC & Chemistry", "Creatine Kinase (CK)", "110", "U/L", "30–200", LabFlag.NORMAL)
            ),
            abgVbgReports = listOf(
                LabEntry("ABG / VBG", "Venous pH", "7.40", "", "7.35–7.45", LabFlag.NORMAL)
            ),
            imagingAndEcg = listOf(
                ImagingStudy(
                    id = "c15_fig1",
                    modality = "CLINICAL FIG",
                    title = "Serial Limb Circumference Tracking & Coagulation Profile (0 PDF Figures)",
                    figureRef = "Case 15 Serial Envenomation Chart (0 Source Figures in PDF)",
                    pdfPages = "pp. 407–422",
                    findings = "Ankle fang puncture site without progressive local edema/ecchymosis across 15-minute marks, normal baseline and 6-hour repeat fibrinogen, platelets, and PT/INR.",
                    impression = "Crotalinae (pit-viper) dry bite (no envenomation) — antivenom NOT indicated.",
                    visualPattern = ClinicalVisualPattern.COAG_PANEL_SNAKEBITE
                )
            ),
            criticalActions = listOf(
                CriticalAction("c15_a1", 1, "Place on monitor/pulse oximetry, establish IV access, and immediately remove the constricting prehospital tourniquet", OralBoardDomain.ACUITY_RECOGNITION, 15, listOf("tourniquet", "iv access", "monitor")),
                CriticalAction("c15_a2", 2, "Cleanse wound, immobilize the bitten extremity in a functional position at heart level, and mark the leading edge/circumference every 15–30 minutes", OralBoardDomain.PATIENT_MANAGEMENT, 15, listOf("immobiliz", "splint", "marker", "snake")),
                CriticalAction("c15_a3", 3, "Obtain baseline AND serial (6-hour repeat) coagulation studies: CBC with platelets, PT/INR, fibrinogen, and BMP/CK", OralBoardDomain.DIAGNOSTIC_STUDIES, 14, listOf("blood", "fibrinogen", "platelet", "coag")),
                CriticalAction("c15_a4", 4, "Administer tetanus prophylaxis and pain control (avoiding NSAIDs/aspirin due to potential coagulopathy)", OralBoardDomain.PATIENT_MANAGEMENT, 14, listOf("tetanus", "analgesi", "pain", "fentanyl")),
                CriticalAction("c15_a5", 5, "Recognize a 'dry bite' (absence of progressive local swelling, systemic symptoms, or coagulopathy) and WITHHOLD Crotalidae antivenom (CroFab)", OralBoardDomain.PROBLEM_SOLVING, 14, listOf("dry bite", "withhold", "antivenom")),
                CriticalAction("c15_a6", 6, "Withhold unnecessary prophylactic antibiotics", OralBoardDomain.SAFETY_REASSESSMENT, 14, listOf("observation")),
                CriticalAction("c15_a7", 7, "Consult Poison Control / Toxicology and observe for at least 8–12 hours with repeat labs before safe discharge", OralBoardDomain.DISPOSITION, 14, listOf("poison", "observation"))
            ),
            defaultQuestions = listOf(
                UnlockQuestion(
                    QuestionGateType.Q0_GATE,
                    "Bay Entrance Gate (Q0)",
                    prompt = "Approximately what percentage of North American Crotalinae (pit-viper / rattlesnake) strikes are 'dry bites' in which no venom is injected?",
                    options = listOf("Approximately 20% to 25% of pit-viper bites are dry bites without envenomation", "0% — every fang puncture injects lethal venom", "99% are dry bites", "Only coral snakes deliver dry bites"),
                    correctIndex = 0,
                    clinicalExplanation = "Roughly 25% of pit-viper strikes are dry bites lacking local tissue progression, systemic toxicity, or hematologic abnormality."
                ),
                UnlockQuestion(
                    QuestionGateType.Q1_SURVEY,
                    "Bedside Survey Station (Q1)",
                    prompt = "Why should the resuscitation team remove the hiker's tight calf belt tourniquet and avoid ice immersion, incision/suction, or prophylactic antibiotics?",
                    options = listOf("Arterial/tight tourniquets, cryotherapy, and incision cause severe ischemic tissue necrosis without improving survival; snakebite infection rate is <3%", "Tourniquets should be left in place for 72 hours", "Ice neutralizes rattlesnake metalloproteinases", "Incision with a scalpel removes 90% of venom"),
                    correctIndex = 0,
                    clinicalExplanation = "Constricting tourniquets concentrate venom locally and cause limb ischemia; wound cleansing, tetanus update, splint immobilization, and serial circumference marking are standard."
                ),
                UnlockQuestion(
                    QuestionGateType.Q2_LABS,
                    "Point-of-Care Lab Console (Q2)",
                    prompt = "Which hematologic laboratory panel must be checked at baseline AND repeated at 6 hours to rule out delayed pit-viper venom coagulopathy?",
                    options = listOf("Platelet count, Prothrombin Time (PT/INR), and Plasma Fibrinogen level (plus CK and BMP)", "Serum Iron and TIBC", "Lipase and Amylase", "TSH and Free T4"),
                    correctIndex = 0,
                    clinicalExplanation = "Pit-viper hemotoxins cause venom-induced consumptive coagulopathy (hypofibrinogenemia, thrombocytopenia, prolonged PT/INR); normal labs at 0 and 6–8 hours confirm no hematologic envenomation."
                ),
                UnlockQuestion(
                    QuestionGateType.Q3_IMAGING,
                    "Mobile Radiology Terminal (Q3)",
                    prompt = "Why is Crotalidae Polyvalent Immune Fab (CroFab) antivenom NOT administered to this patient?",
                    options = listOf("He has a confirmed dry bite (no progressive swelling, no systemic neuro/hemodynamic toxicity, and normal serial coag/platelets/fibrinogen)", "CroFab is only given for bee stings", "Antivenom is given blindly to all fang marks regardless of symptoms", "He needs immediate fasciotomy instead"),
                    correctIndex = 0,
                    clinicalExplanation = "Antivenom carries risks of anaphylaxis/serum sickness and high cost; it is indicated only when local swelling progresses, systemic toxicity appears, or coagulation/platelets deteriorate."
                )
            ),
            dispositionAndPearls = "Observe for 8–12 hours in ED Observation Unit in consultation with Poison Control; discharge home with return precautions after repeat 6–8 hour labs and limb measurements remain normal.",
            instructorClues = listOf(
                "Use the Splint, Skin Marker & Tetanus Pack from Crash Cart Drawer 6.",
                "Do NOT administer antivenom or prophylactic antibiotics—this is a classic pit-viper dry bite!"
            )
        ),

        // CASE 16
        OralBoardCase(
            caseNumber = 16,
            scenarioCategory = "Visual impairment",
            mainDiagnosis = "Retinal vascular occlusion (central retinal vein occlusion)",
            pdfPages = "423–440",
            sourceFiguresCount = 2,
            patientName = "Saraswathi Amma",
            ageDesc = "69-year-old Female",
            gender = "Female",
            weightKg = 70f,
            chiefComplaint = "Sudden painless loss of vision in the left eye noticed upon waking 3 hours ago",
            triageNote = "History of hypertension, hyperlipidemia, and open-angle glaucoma. Woke up with severely blurred vision in the left eye ('like looking through dark fog'); completely painless.",
            patientSkinToneTag = "NORMAL",
            initialVitals = VitalSigns("146/74", 146, 74, 84, 20, 98, 36.9f, 38, "Normal Sinus Rhythm"),
            stabilizedVitals = VitalSigns("138/72", 138, 72, 78, 16, 99, 36.8f, 38, "Normal Sinus Rhythm"),
            ventilatorPreset = VentilatorSettings(mode = "Standby (Room Air)", isActive = false),
            defibrillatorPreset = DefibrillatorSettings(mode = "Telemetry Monitor"),
            primarySurvey = PrimarySurveyABCDE(
                airway = "Patent.",
                breathing = "RR 20/min, SpO2 98% on room air.",
                circulation = "BP 146/74 mmHg, HR 84 bpm.",
                disability = "GCS 15; Visual acuity OS: 20/400 (count fingers at 3 feet), OD: 20/25; Left relative afferent pupillary defect (Marcus Gunn pupil).",
                exposure = "External left eye is white and non-injected (painless)."
            ),
            physicalExam = PhysicalExamClues(
                generalAppearance = "Calm, alert older woman concerned about painless left monocular vision loss.",
                heentAndNeck = "External eyes clear, non-tender temporal arteries with strong pulses. Funduscopic exam of LEFT eye shows dramatic diffuse retinal hemorrhages in all four quadrants ('blood and thunder' fundus), dilated tortuous retinal veins, optic disc edema, and cotton-wool spots. IOP is normal (16 mmHg bilaterally).",
                chestAndLungs = "Clear to auscultation.",
                cardiovascular = "Regular rate and rhythm, no carotid bruits.",
                abdomenAndPelvis = "Soft, non-tender.",
                extremitiesAndSkin = "Warm, intact pulses.",
                neurologic = "No facial asymmetry, normal speech and extremity strength (excluding hemispheric stroke)."
            ),
            labReports = listOf(
                LabEntry("CBC & Chemistry", "Point-of-Care Glucose", "114", "mg/dL", "70–100", LabFlag.NORMAL),
                LabEntry("CBC & Chemistry", "ESR", "16", "mm/hr (Normal)", "< 30", LabFlag.NORMAL),
                LabEntry("CBC & Chemistry", "CBC & Viscosity Panel", "Normal Hgb 13.8, Plt 240", "", "Normal", LabFlag.NORMAL)
            ),
            abgVbgReports = listOf(
                LabEntry("ABG / VBG", "Venous pH", "7.41", "", "7.35–7.45", LabFlag.NORMAL)
            ),
            imagingAndEcg = listOf(
                ImagingStudy(
                    id = "c16_fig1",
                    modality = "CLINICAL FIG",
                    title = "Direct Ophthalmoscopy / Fundus Photography (Left Eye)",
                    figureRef = "Case 16 Fig 16.1–16.2 (2 Source Figures)",
                    pdfPages = "pp. 423–440",
                    findings = "Classic 'blood and thunder' appearance of the left retina: extensive flame-shaped and dot-blot intraretinal hemorrhages radiating across all 4 quadrants, markedly dilated and tortuous retinal veins, cotton-wool spots, and blurred hyperemic optic disc margins.",
                    impression = "Central Retinal Vein Occlusion (CRVO) of the left eye.",
                    visualPattern = ClinicalVisualPattern.FUNDUS_CRVO
                )
            ),
            criticalActions = listOf(
                CriticalAction("c16_a1", 1, "Perform complete ocular and funduscopic examination (visual acuity, pupillary reflex, IOP, and direct ophthalmoscopy identifying 'blood and thunder' fundus of CRVO) plus neuro exam", OralBoardDomain.DIAGNOSTIC_STUDIES, 50, listOf("exam", "torch", "fundus", "ophthalmoscopy")),
                CriticalAction("c16_a2", 2, "Obtain Ophthalmology consultation for macular edema / neovascularization management (anti-VEGF / laser photocoagulation) and arrange medical risk-factor workup", OralBoardDomain.DISPOSITION, 50, listOf("ophthalmology", "consult"))
            ),
            defaultQuestions = listOf(
                UnlockQuestion(
                    QuestionGateType.Q0_GATE,
                    "Bay Entrance Gate (Q0)",
                    prompt = "A 69-year-old hypertensive woman wakes up with sudden painless monocular vision loss in her left eye. Which funduscopic appearance is pathognomonic for Central Retinal Vein Occlusion (CRVO)?",
                    options = listOf("'Blood and thunder' fundus: diffuse flame-shaped retinal hemorrhages in all 4 quadrants, dilated tortuous retinal veins, cotton-wool spots, and optic disc edema", "Pale ischemic retina with a cherry-red spot at the fovea and attenuated arterioles", "Cupped pale optic disc with IOP 65 mmHg", "Dendritic branching fluorescein corneal ulcer"),
                    correctIndex = 0,
                    clinicalExplanation = "CRVO produces a classic 'blood and thunder' (or 'pizza pie') fundus with diffuse 4-quadrant retinal hemorrhages and dilated tortuous veins, whereas CRAO produces a pale retina with a cherry-red spot."
                ),
                UnlockQuestion(
                    QuestionGateType.Q1_SURVEY,
                    "Bedside Survey Station (Q1)",
                    prompt = "On the swinging-flashlight pupillary exam, what finding indicates optic nerve / severe retinal dysfunction in the affected left eye?",
                    options = listOf("Relative Afferent Pupillary Defect (RAPD / Marcus Gunn pupil), where both pupils paradoxical dilate when light swings from the good eye to the affected eye", "Argyll Robertson pupil", "Bilateral pinpoint opioid pupils", "Horner ptosis and anhidrosis"),
                    correctIndex = 0,
                    clinicalExplanation = "Ischemic CRVO causes a Relative Afferent Pupillary Defect (Marcus Gunn pupil) due to impaired afferent retinal transmission."
                ),
                UnlockQuestion(
                    QuestionGateType.Q2_LABS,
                    "Point-of-Care Lab Console (Q2)",
                    prompt = "Why is checking an ESR/CRP and palpating the temporal arteries important in an older adult with acute painless monocular vision loss?",
                    options = listOf("To exclude Giant Cell (Temporal) Arteritis causing arteritic ischemic optic neuropathy or retinal artery occlusion", "To diagnose iron deficiency", "To check for salicylate poisoning", "To rule out appendicitis"),
                    correctIndex = 0,
                    clinicalExplanation = "Screening for Giant Cell Arteritis (jaw claudication, temporal artery tenderness, elevated ESR/CRP) is essential in elderly patients with acute monocular visual loss."
                ),
                UnlockQuestion(
                    QuestionGateType.Q3_IMAGING,
                    "Mobile Radiology Terminal (Q3)",
                    prompt = "What are the major sight-threatening complications of Central Retinal Vein Occlusion (Case 16 Fig 16.1–16.2) managed by Ophthalmology?",
                    options = listOf("Macular edema and retinal/iris neovascularization leading to neovascular ('90-day') glaucoma", "Rhegmatogenous retinal tear from coughing", "Bilateral cataract extraction emergency", "Orbital blowout fracture"),
                    correctIndex = 0,
                    clinicalExplanation = "Ophthalmology follow-up is critical in CRVO to treat macular edema with intravitreal anti-VEGF injections and prevent neovascular glaucoma with panretinal photocoagulation."
                )
            ),
            dispositionAndPearls = "Urgent Ophthalmology consultation and coordination with Primary Care/Internal Medicine for hypertension, glaucoma, and hyperviscosity risk-factor control.",
            instructorClues = listOf(
                "Case 16 has 2 core critical actions in the PDF index: (1) Thorough ocular & funduscopic examination diagnosing CRVO, and (2) Ophthalmology consultation.",
                "Contrast the 'blood and thunder' fundus of CRVO (Fig 16.1–16.2) with the cherry-red spot of CRAO."
            )
        ),

        // CASE 17 (Explicit Defibrillator Preset: 4 J/kg)
        OralBoardCase(
            caseNumber = 17,
            scenarioCategory = "Syncope",
            mainDiagnosis = "Prolonged QT syndrome complicated by torsades de pointes",
            pdfPages = "441–463",
            sourceFiguresCount = 3,
            patientName = "Tenzin Dolma",
            ageDesc = "14-year-old Female Adolescent",
            gender = "Female",
            weightKg = 45f,
            chiefComplaint = "Sudden syncopal episode during school track practice after starting new antibiotics and antihistamines; recurrent dizziness in ED",
            triageNote = "Fainted while sprinting at track practice 40 minutes ago; regained consciousness within 1 minute. Uncle died suddenly at age 22 from an 'unknown heart rhythm'. Suddenly becomes unresponsive on the resus bay stretcher!",
            patientSkinToneTag = "PALE",
            isPediatricOrInfant = true,
            initialVitals = VitalSigns("100/70", 100, 70, 96, 18, 99, 37.2f, 36, "Prolonged QTc (540 ms) → Paroxysms of Torsades de Pointes!"),
            stabilizedVitals = VitalSigns("112/72", 112, 72, 78, 16, 100, 37.1f, 38, "Sinus Rhythm post-MgSO4 & Defibrillation"),
            ventilatorPreset = VentilatorSettings(mode = "High-Flow O2 / BVM Standby during Arrest", isActive = false),
            defibrillatorPreset = DefibrillatorSettings(
                mode = "Unsynchronized Defibrillation Ready",
                energyJoules = 180,
                energyPerKgLabel = "4 J/kg (180 J for 45 kg patient)",
                isCharged = true,
                syncEnabled = false,
                sourceCitationNote = "Case 17 PDF Explicit Defibrillator Preset: 4 J/kg (180 J for 45 kg)"
            ),
            primarySurvey = PrimarySurveyABCDE(
                airway = "Initially patent; during recurrent syncopal paroxysm in bay becomes unresponsive requiring BVM airway support.",
                breathing = "RR 18/min initially, SpO2 99%.",
                circulation = "BP 100/70 mmHg, HR 96 bpm initially; suddenly converts on telemetry to polymorphic ventricular tachycardia (Torsades de Pointes) at 240 bpm with loss of pulses!",
                disability = "GCS 15 initially -> drops to GCS 3 during pulseless Torsades paroxysm -> returns to GCS 15 after 4 J/kg shock & IV Magnesium Sulfate.",
                exposure = "Healthy teenage athlete in track uniform."
            ),
            physicalExam = PhysicalExamClues(
                generalAppearance = "Alert 14-year-old girl who suddenly experiences palpitations and syncope with polymorphic VT on the live 3D monitor.",
                heentAndNeck = "Normocephalic, no tongue bite.",
                chestAndLungs = "Clear breath sounds bilaterally.",
                cardiovascular = "Normal heart sounds during sinus rhythm; absent pulse during Torsades de Pointes paroxysm.",
                abdomenAndPelvis = "Soft, non-tender.",
                extremitiesAndSkin = "Warm initially, pale during arrhythmia.",
                neurologic = "Normal neurologic exam once sinus rhythm is restored."
            ),
            labReports = listOf(
                LabEntry("CBC & Chemistry", "Serum Potassium (K+)", "3.3", "mEq/L", "3.5–5.0", LabFlag.LOW),
                LabEntry("CBC & Chemistry", "Serum Magnesium (Mg2+)", "1.4", "mg/dL", "1.7–2.2", LabFlag.LOW),
                LabEntry("CBC & Chemistry", "Serum Calcium", "9.1", "mg/dL", "8.5–10.2", LabFlag.NORMAL),
                LabEntry("Urinalysis / CSF", "Urine Pregnancy & Tox Screen", "Negative", "", "Negative", LabFlag.NORMAL)
            ),
            abgVbgReports = listOf(
                LabEntry("ABG / VBG", "Venous pH", "7.38", "", "7.35–7.45", LabFlag.NORMAL)
            ),
            imagingAndEcg = listOf(
                ImagingStudy(
                    id = "c17_fig1",
                    modality = "ECG",
                    title = "12-Lead ECG & Telemetry Rhythm Strip (Prolonged QT & Torsades de Pointes)",
                    figureRef = "Case 17 Fig 17.1–17.3 (3 Source Figures)",
                    pdfPages = "pp. 441–463",
                    findings = "Baseline 12-lead ECG shows sinus rhythm with markedly prolonged corrected QT interval (QTc = 545 ms). Telemetry strip captures R-on-T premature ventricular contractions initiating polymorphic ventricular tachycardia with QRS axis twisting around the isoelectric baseline ('Torsades de Pointes').",
                    impression = "Congenital/Drug-Exacerbated Long QT Syndrome complicated by Torsades de Pointes.",
                    visualPattern = ClinicalVisualPattern.ECG_TORSADES_LONG_QT
                )
            ),
            criticalActions = listOf(
                CriticalAction("c17_a1", 1, "Place on continuous cardiac monitor and obtain 12-lead ECG (identifying prolonged QTc > 500 ms and Torsades de Pointes)", OralBoardDomain.DIAGNOSTIC_STUDIES, 25, listOf("ecg", "monitor", "defib")),
                CriticalAction("c17_a2", 2, "Perform immediate unsynchronized defibrillation at 4 J/kg when patient develops unstable/pulseless Torsades de Pointes", OralBoardDomain.ACUITY_RECOGNITION, 25, listOf("defib", "shock", "4 j/kg", "torsades")),
                CriticalAction("c17_a3", 3, "Administer first-line IV Magnesium Sulfate (25–50 mg/kg up to 2 g IV) and replete potassium/magnesium while stopping all QT-prolonging drugs", OralBoardDomain.PATIENT_MANAGEMENT, 25, listOf("magnesium", "torsades")),
                CriticalAction("c17_a4", 4, "Consult Pediatric Cardiology / Electrophysiology and admit to ICU on telemetry", OralBoardDomain.DISPOSITION, 25, listOf("cardiology", "icu"))
            ),
            defaultQuestions = listOf(
                UnlockQuestion(
                    QuestionGateType.Q0_GATE,
                    "Bay Entrance Gate (Q0)",
                    prompt = "A 14-year-old athlete with syncope develops polymorphic ventricular tachycardia with QRS complexes twisting around the baseline ('Torsades de Pointes') and loses her pulse. According to Case 17 guidelines, what defibrillator setting is used?",
                    options = listOf("Immediate unsynchronized defibrillation at 4 J/kg (180 J for 45 kg), followed by IV Magnesium Sulfate", "Low-energy 0.2 J/kg synchronized shock after 10 minutes", "IV Amiodarone and Procainamide bolus first without shock", "Carotid sinus massage"),
                    correctIndex = 0,
                    clinicalExplanation = "Case 17 explicitly prescribes 4 J/kg defibrillation for unstable/pulseless Torsades de Pointes because polymorphic QRS peaks prevent reliable R-wave synchronization."
                ),
                UnlockQuestion(
                    QuestionGateType.Q1_SURVEY,
                    "Bedside Survey Station (Q1)",
                    prompt = "Which family history red flag on focused history strongly points toward Congenital Long QT Syndrome (e.g., Romano-Ward or Jervell and Lange-Nielsen syndrome)?",
                    options = listOf("Exertional or auditory-triggered syncope and a family history of unexplained sudden cardiac death or drowning in a young relative", "Family history of osteoarthritis", "History of seasonal allergic rhinitis", "Recent consumption of prenatal iron"),
                    correctIndex = 0,
                    clinicalExplanation = "Syncope during exertion, swimming, or sudden startle along with unexplained sudden death in a young relative is classic for Congenital Long QT Syndrome."
                ),
                UnlockQuestion(
                    QuestionGateType.Q2_LABS,
                    "Point-of-Care Lab Console (Q2)",
                    prompt = "Which medication in Crash Cart Drawer 1 is the first-line pharmacologic therapy to suppress Torsades de Pointes, even when serum magnesium is normal?",
                    options = listOf("IV Magnesium Sulfate (25–50 mg/kg, max 2 g slow IV push), while strictly avoiding QT-prolonging Class IA/III antiarrhythmics (procainamide, amiodarone, sotalol)", "IV Procainamide infusion", "IV Sotalol bolus", "IV Haloperidol"),
                    correctIndex = 0,
                    clinicalExplanation = "IV Magnesium Sulfate suppresses early afterdepolarizations that trigger Torsades de Pointes; Class IA/III antiarrhythmics prolong the QT interval further and are contraindicated."
                ),
                UnlockQuestion(
                    QuestionGateType.Q3_IMAGING,
                    "Mobile Radiology Terminal (Q3)",
                    prompt = "On the baseline 12-lead ECG (Case 17 Fig 17.1–17.3), what corrected QT interval (QTc) threshold places a patient at high risk for Torsades de Pointes?",
                    options = listOf("QTc > 470–500 ms (or T-wave duration extending well past the midpoint of the R-R interval)", "QTc < 300 ms", "PR interval > 200 ms", "QRS duration < 80 ms"),
                    correctIndex = 0,
                    clinicalExplanation = "A QTc > 450–460 ms is prolonged, and a QTc > 500 ms carries a substantially elevated risk of R-on-T phenomenon and Torsades de Pointes."
                )
            ),
            dispositionAndPearls = "Admit to Pediatric ICU with continuous telemetry and Pediatric Cardiology/Electrophysiology consultation for beta-blocker therapy and ICD evaluation.",
            instructorClues = listOf(
                "Case 17 Explicit Device Preset active on the 3D Defibrillator: 4 J/kg (180 J for 45 kg).",
                "Deliver the 4 J/kg shock on the Defibrillator or take Defib Pads + Magnesium Sulfate from the Crash Cart!"
            )
        ),

        // CASE 18
        OralBoardCase(
            caseNumber = 18,
            scenarioCategory = "Sore throat",
            mainDiagnosis = "Retropharyngeal abscess",
            pdfPages = "464–485",
            sourceFiguresCount = 1,
            patientName = "Zoya Khan",
            ageDesc = "3-year-old Female Child",
            gender = "Female",
            weightKg = 15f,
            chiefComplaint = "High fever, refusal to swallow, drooling, muffled voice, and holding her neck stiffly in extension",
            triageNote = "Had a viral pharyngitis 4 days ago; over the past 24 hours developed fever to 39.1°C, drooling onto her shirt, and cries whenever she tries to move her neck.",
            patientSkinToneTag = "FLUSHED",
            isPediatricOrInfant = true,
            initialVitals = VitalSigns("114/80", 114, 80, 150, 28, 98, 39.1f, 36, "Pediatric Sinus Tachycardia"),
            stabilizedVitals = VitalSigns("106/68", 106, 68, 118, 22, 99, 37.8f, 38, "Normal Pediatric Sinus"),
            ventilatorPreset = VentilatorSettings(mode = "Humidified Blow-By O2 — Keep Child Calm!", isActive = false),
            defibrillatorPreset = DefibrillatorSettings(mode = "Pediatric Telemetry"),
            primarySurvey = PrimarySurveyABCDE(
                airway = "Threatened! Child sits leaning forward with neck hyperextended ('sniffing/bolting position'), drooling, muffled 'hot potato' voice, and soft inspiratory stridor.",
                breathing = "RR 28/min, SpO2 98% on room air, clear lower lung fields.",
                circulation = "BP 114/80 mmHg, HR 150 bpm.",
                disability = "GCS 15, anxious toddler clinging to mother.",
                exposure = "Febrile at 39.1°C; tender bilateral anterior cervical lymphadenopathy."
            ),
            physicalExam = PhysicalExamClues(
                generalAppearance = "Ill-appearing 3-year-old girl sitting upright with neck extended, refusing to flex or rotate her neck (torticollis/meningismus mimic).",
                heentAndNeck = "Careful inspection without forceful tongue-blade gagging reveals symmetric/midline-to-paramedian bulging of the posterior pharyngeal wall; uvula is midline (unlike unilateral peritonsillar abscess).",
                chestAndLungs = "Transmitted upper airway stridor; lungs clear.",
                cardiovascular = "Tachycardic at 150 bpm.",
                abdomenAndPelvis = "Soft, non-tender.",
                extremitiesAndSkin = "Warm, capillary refill 2 seconds.",
                neurologic = "Alert, no true meningeal irritation once retropharyngeal mass is recognized."
            ),
            labReports = listOf(
                LabEntry("CBC & Chemistry", "WBC Count", "22.8", "x10³/µL (Left shift)", "5.0–14.5", LabFlag.CRITICAL),
                LabEntry("CBC & Chemistry", "Blood Culture", "Pending (Gram+ cocci / anaerobes)", "", "Negative", LabFlag.HIGH)
            ),
            abgVbgReports = listOf(
                LabEntry("ABG / VBG", "Venous pH", "7.38", "", "7.35–7.45", LabFlag.NORMAL)
            ),
            imagingAndEcg = listOf(
                ImagingStudy(
                    id = "c18_fig1",
                    modality = "X-RAY",
                    title = "Lateral Soft-Tissue Neck Radiograph & Contrast Neck CT",
                    figureRef = "Case 18 Fig 18.1 (1 Source Figure)",
                    pdfPages = "pp. 464–485",
                    findings = "Lateral neck X-ray taken during inspiration in mild extension shows marked widening of the prevertebral retropharyngeal soft-tissue space: >14 mm at C2 (normal ≤7 mm, or > width of adjacent vertebral body) and >22 mm at C6 with reversal of cervical lordosis and anterior displacement of the airway.",
                    impression = "Retropharyngeal abscess (RPA) with impending upper airway compromise.",
                    visualPattern = ClinicalVisualPattern.LATERAL_NECK_XRAY_RPA
                )
            ),
            criticalActions = listOf(
                CriticalAction("c18_a1", 1, "Keep child calm in position of comfort with mother and prepare difficult airway equipment at bedside (avoiding forceful pharyngeal palpation/rupture)", OralBoardDomain.ACUITY_RECOGNITION, 20, listOf("airway", "laryngoscop", "ett", "oxygen")),
                CriticalAction("c18_a2", 2, "Obtain portable Lateral Soft-Tissue Neck Radiograph (showing widened prevertebral soft tissue >7 mm at C2) and/or Neck CT with IV contrast", OralBoardDomain.DIAGNOSTIC_STUDIES, 20, listOf("xray", "lateral neck", "ct")),
                CriticalAction("c18_a3", 3, "Establish IV access and administer isotonic crystalloid hydration and antipyretic/analgesia", OralBoardDomain.PATIENT_MANAGEMENT, 20, listOf("normal saline", "fluid", "iv access")),
                CriticalAction("c18_a4", 4, "Administer broad-spectrum parenteral antibiotics covering Group A Strep, Staph aureus, and oral anaerobes (Clindamycin or Ampicillin-Sulbactam ± Vancomycin)", OralBoardDomain.PATIENT_MANAGEMENT, 20, listOf("antibiotic", "clindamycin", "vancomycin")),
                CriticalAction("c18_a5", 5, "Obtain emergent Otolaryngology (ENT) consultation for airway management in the OR and surgical incision & drainage", OralBoardDomain.DISPOSITION, 20, listOf("ent", "otolaryngology", "surgery"))
            ),
            defaultQuestions = listOf(
                UnlockQuestion(
                    QuestionGateType.Q0_GATE,
                    "Bay Entrance Gate (Q0)",
                    prompt = "A 3-year-old girl presents with fever 39.1°C, drooling, muffled voice, stridor, and unwillingness to move or flex her neck. Why does Retropharyngeal Abscess occur predominantly in children under age 4–5?",
                    options = listOf("The retropharyngeal lymph nodes (nodes of Rouvière) drain the nasopharynx/middle ear in young children and naturally atrophy after age 4–5", "Children under 5 lack an epiglottis", "Adults have no retropharyngeal space", "It is caused by congenital pyloric hypertrophy"),
                    correctIndex = 0,
                    clinicalExplanation = "Suppurative lymphadenitis of the retropharyngeal nodes of Rouvière occurs predominantly in children aged 6 months to 4 years before those nodes regress."
                ),
                UnlockQuestion(
                    QuestionGateType.Q1_SURVEY,
                    "Bedside Survey Station (Q1)",
                    prompt = "Why must the team avoid forcefully gagging or palpating the posterior pharyngeal wall with a tongue blade in this drooling toddler?",
                    options = listOf("It can provoke crying/laryngospasm or rupture the abscess directly into the unprotected airway, causing fatal aspiration and asphyxiation", "It causes hyperkalaemia", "It dislocates the temporomandibular joint", "It lowers intraocular pressure"),
                    correctIndex = 0,
                    clinicalExplanation = "Keep the child calm in the parent's lap and avoid aggressive intraoral manipulation that can precipitate complete airway obstruction or abscess rupture."
                ),
                UnlockQuestion(
                    QuestionGateType.Q2_LABS,
                    "Point-of-Care Lab Console (Q2)",
                    prompt = "Which polymicrobial pathogens must empiric IV antibiotics (e.g., IV Clindamycin or Ampicillin-Sulbactam ± Vancomycin) cover in retropharyngeal abscess?",
                    options = listOf("Group A beta-hemolytic Streptococcus, Staphylococcus aureus (including MRSA), Haemophilus species, and oral anaerobes (Bacteroides, Fusobacterium)", "Pseudomonas aeruginosa only", "Mycobacterium leprae", "Plasmodium falciparum"),
                    correctIndex = 0,
                    clinicalExplanation = "Retropharyngeal abscesses are polymicrobial infections involving Group A Strep, Staph aureus, respiratory flora, and oral anaerobes."
                ),
                UnlockQuestion(
                    QuestionGateType.Q3_IMAGING,
                    "Mobile Radiology Terminal (Q3)",
                    prompt = "On a true Lateral Soft-Tissue Neck X-Ray taken during inspiration (Case 18 Fig 18.1), what prevertebral soft-tissue thickness is diagnostic of retropharyngeal pathology?",
                    options = listOf("Prevertebral soft-tissue thickness > 7 mm at C2 (or > 14 mm in children / > 22 mm in adults at C6, or wider than the adjacent cervical vertebral body)", "Prevertebral space < 2 mm at C2", "Steeple sign of subglottic narrowing on AP view only", "Normal cervical lordosis with 3 mm soft tissue"),
                    correctIndex = 0,
                    clinicalExplanation = "Prevertebral soft-tissue widening > 7 mm at C2 (or exceeding the anteroposterior width of the adjacent vertebral body) on an inspiratory lateral neck radiograph indicates retropharyngeal expansion."
                )
            ),
            dispositionAndPearls = "Emergent ENT consultation for airway stabilization in the Operating Room, CT neck with contrast if stable, and transoral surgical drainage + PICU admission.",
            instructorClues = listOf(
                "Keep the toddler calm! Have Drawer 3 pediatric intubation equipment ready at the bedside.",
                "Administer IV Clindamycin / broad-spectrum antibiotics from Drawer 1 and consult ENT early."
            )
        ),

        // CASE 19
        OralBoardCase(
            caseNumber = 19,
            scenarioCategory = "Knee pain",
            mainDiagnosis = "Slipped capital femoral epiphysis (SCFE)",
            pdfPages = "486–502",
            sourceFiguresCount = 3,
            patientName = "Kabir Mehta",
            ageDesc = "13-year-old Male Adolescent",
            gender = "Male",
            weightKg = 78f,
            chiefComplaint = "Aching left knee pain and a progressive limp for 2 weeks, worsened today after stepping off a school bus",
            triageNote = "Overweight 13-year-old boy (>95th percentile BMI) complaining exclusively of dull left medial knee pain; denies direct knee trauma.",
            patientSkinToneTag = "NORMAL",
            isPediatricOrInfant = true,
            initialVitals = VitalSigns("118/77", 118, 77, 84, 16, 99, 36.8f, 38, "Normal Sinus Rhythm"),
            stabilizedVitals = VitalSigns("116/74", 116, 74, 78, 16, 99, 36.8f, 38, "Normal Sinus Rhythm"),
            ventilatorPreset = VentilatorSettings(mode = "Standby (Room Air)", isActive = false),
            defibrillatorPreset = DefibrillatorSettings(mode = "Telemetry Monitor"),
            primarySurvey = PrimarySurveyABCDE(
                airway = "Patent.",
                breathing = "RR 16/min, SpO2 99% on room air.",
                circulation = "BP 118/77 mmHg, HR 84 bpm.",
                disability = "GCS 15.",
                exposure = "Left lower extremity rests in slight external rotation; antalgic gait."
            ),
            physicalExam = PhysicalExamClues(
                generalAppearance = "Obese 13-year-old boy resting comfortably on the stretcher with his left leg externally rotated.",
                heentAndNeck = "Unremarkable.",
                chestAndLungs = "Clear to auscultation.",
                cardiovascular = "Regular rate and rhythm.",
                abdomenAndPelvis = "Soft abdomen, normal testicular exam.",
                extremitiesAndSkin = "LEFT KNEE exam is completely normal (no effusion, no joint-line tenderness, stable ligaments). LEFT HIP exam reveals restricted internal rotation, abduction, and flexion; passive flexion of the left hip causes obligatory external rotation (Drehmann sign) and reproduces his 'knee' pain!",
                neurologic = "Distal neurovascular status intact (2+ DP/PT pulses, normal sensation)."
            ),
            labReports = listOf(
                LabEntry("CBC & Chemistry", "WBC & ESR", "WBC 7.4, ESR 10 mm/hr (Normal)", "", "Afebrile / Non-septic", LabFlag.NORMAL),
                LabEntry("CBC & Chemistry", "TSH / Metabolic Panel", "Normal", "", "Normal", LabFlag.NORMAL)
            ),
            abgVbgReports = listOf(
                LabEntry("ABG / VBG", "Venous pH", "7.40", "", "7.35–7.45", LabFlag.NORMAL)
            ),
            imagingAndEcg = listOf(
                ImagingStudy(
                    id = "c19_fig1",
                    modality = "X-RAY",
                    title = "AP & Frog-Leg Lateral Bilateral Hip Radiographs",
                    figureRef = "Case 19 Fig 19.1–19.3 (3 Source Figures)",
                    pdfPages = "pp. 486–502",
                    findings = "Left knee radiograph is completely normal. AP and Frog-Leg Lateral pelvis/hip radiographs demonstrate widening of the left proximal femoral physis with posterior and medial displacement ('ice cream slipping off the cone') of the left femoral head epiphysis relative to the femoral neck; Klein's line along the superior left femoral neck fails to intersect the lateral femoral epiphysis (Trethowan's sign).",
                    impression = "Left Slipped Capital Femoral Epiphysis (SCFE).",
                    visualPattern = ClinicalVisualPattern.PELVIS_XRAY_SCFE
                )
            ),
            criticalActions = listOf(
                CriticalAction("c19_a1", 1, "Examine the ipsilateral HIP in an adolescent presenting with knee pain and a limp (identifying loss of internal rotation and obligatory external rotation)", OralBoardDomain.DATA_ACQUISITION, 25, listOf("exam", "hip", "knee")),
                CriticalAction("c19_a2", 2, "Order bilateral AP and Frog-Leg Lateral Hip Radiographs (identifying posterior-inferior slippage and abnormal Klein's line)", OralBoardDomain.DIAGNOSTIC_STUDIES, 25, listOf("xray", "hip", "pelvis", "klein")),
                CriticalAction("c19_a3", 3, "Make the patient strictly Non-Weight-Bearing (strict bedrest/wheelchair) and administer analgesia to prevent acute slip progression or avascular necrosis", OralBoardDomain.PATIENT_MANAGEMENT, 25, listOf("non-weight", "splint", "analgesi", "pain")),
                CriticalAction("c19_a4", 4, "Obtain Orthopedic Surgery consultation for admission and in-situ percutaneous screw fixation", OralBoardDomain.DISPOSITION, 25, listOf("orthopedic", "surgery"))
            ),
            defaultQuestions = listOf(
                UnlockQuestion(
                    QuestionGateType.Q0_GATE,
                    "Bay Entrance Gate (Q0)",
                    prompt = "An overweight 13-year-old boy presents with a 2-week limp and dull medial left knee pain, but his knee exam is completely normal. Why does hip pathology frequently present as isolated knee pain in children?",
                    options = listOf("Referred pain via the articular branches of the obturator nerve, which innervates both the hip joint capsule and the medial knee", "The sciatic nerve terminates at the patella", "Knee X-rays always show hip fractures", "Adolescents cannot localize pain above the knee"),
                    correctIndex = 0,
                    clinicalExplanation = "Because the obturator and femoral nerves supply sensory fibers to both the hip capsule and the distal medial thigh/knee, hip disorders like SCFE and Legg-Calvé-Perthes routinely refer pain to the knee."
                ),
                UnlockQuestion(
                    QuestionGateType.Q1_SURVEY,
                    "Bedside Survey Station (Q1)",
                    prompt = "What is the hallmark physical examination finding when passively flexing the affected hip in a patient with Slipped Capital Femoral Epiphysis (SCFE)?",
                    options = listOf("Obligatory external rotation and abduction of the thigh as the hip is flexed (Drehmann sign), along with painful loss of internal rotation", "Hyper-internal rotation to 90 degrees", "Positive Anterior Drawer test at the knee", "Absent dorsalis pedis pulse"),
                    correctIndex = 0,
                    clinicalExplanation = "As the affected hip is flexed, the femur obligatorily rotates externally and abducts due to the posterior-inferior displacement of the capital epiphysis."
                ),
                UnlockQuestion(
                    QuestionGateType.Q2_LABS,
                    "Point-of-Care Lab Console (Q2)",
                    prompt = "Once SCFE is suspected or diagnosed, what immediate activity restriction is critical to prevent transforming a stable slip into an unstable slip with avascular necrosis (AVN)?",
                    options = listOf("Strict Non-Weight-Bearing on the affected extremity (bedrest/stretcher) and NEVER attempting forceful closed reduction of the hip", "Have the patient jog down the hallway to test gait", "Forceful internal rotation manipulation under sedation", "Discharge home with weight-bearing physical therapy"),
                    correctIndex = 0,
                    clinicalExplanation = "Continued weight-bearing or forceful manipulation can shear the retinacular vessels supplying the femoral head, causing catastrophic avascular necrosis (AVN) and chondrolysis."
                ),
                UnlockQuestion(
                    QuestionGateType.Q3_IMAGING,
                    "Mobile Radiology Terminal (Q3)",
                    prompt = "On an AP Pelvis/Hip radiograph (Case 19 Fig 19.1–19.3), what is Klein's line and Trethowan's sign in SCFE?",
                    options = listOf("A line drawn along the superior border of the femoral neck fails to intersect the lateral portion of the slipped capital femoral epiphysis ('ice cream slipping off the cone')", "A fracture line across the tibial tuberosity", "Shenton's line disruption from posterior hip dislocation", "Subperiosteal bone resorption of the phalanges"),
                    correctIndex = 0,
                    clinicalExplanation = "Klein's line normally transects the lateral edge of the superior femoral epiphysis; in SCFE, the epiphysis slips posteriorly and medially below Klein's line (Trethowan's sign)."
                )
            ),
            dispositionAndPearls = "Admit to Orthopedic Surgery strictly Non-Weight-Bearing for in-situ single-cannulated-screw internal fixation (and evaluation of the contralateral hip, as 20–40% are bilateral).",
            instructorClues = listOf(
                "Ordering only a Knee X-ray misses the diagnosis! You must examine the hip and order Bilateral AP & Frog-Leg Hip X-rays.",
                "Place the patient on strict Non-Weight-Bearing precautions (Drawer 6 Immobilization/Splint pack) and give analgesia."
            )
        ),

        // CASE 20
        OralBoardCase(
            caseNumber = 20,
            scenarioCategory = "Abdominal pain",
            mainDiagnosis = "Left testicular torsion",
            pdfPages = "503–523",
            sourceFiguresCount = 3,
            patientName = "Rohan D'Souza",
            ageDesc = "15-year-old Male Adolescent",
            gender = "Male",
            weightKg = 64f,
            chiefComplaint = "Sudden severe left lower quadrant abdominal pain and nausea/vomiting that woke him from sleep 2.5 hours ago",
            triageNote = "Embarrassed teenager initially mentions only lower abdominal pain and vomiting; has not told triage nurse about scrotal pain unless examined or asked directly.",
            patientSkinToneTag = "PALE",
            isPediatricOrInfant = true,
            initialVitals = VitalSigns("89/61", 89, 61, 126, 24, 100, 36.9f, 36, "Sinus Tachycardia"),
            stabilizedVitals = VitalSigns("114/72", 114, 72, 82, 16, 100, 36.8f, 38, "Normal Sinus Rhythm"),
            ventilatorPreset = VentilatorSettings(mode = "Standby (Room Air)", isActive = false),
            defibrillatorPreset = DefibrillatorSettings(mode = "Telemetry Monitor"),
            primarySurvey = PrimarySurveyABCDE(
                airway = "Patent, nauseated.",
                breathing = "RR 24/min, SpO2 100% on room air.",
                circulation = "BP 89/61 mmHg (vasovagal/pain response & vomiting), HR 126 bpm.",
                disability = "GCS 15, pale and diaphoretic in severe pain.",
                exposure = "Mandatory genitourinary/scrotal examination! Left hemiscrotum is swollen, erythematous, with a high-riding, transversely oriented left testicle."
            ),
            physicalExam = PhysicalExamClues(
                generalAppearance = "Diaphoretic 15-year-old boy curled on his side holding his lower left abdomen.",
                heentAndNeck = "Moist mucosa.",
                chestAndLungs = "Clear to auscultation.",
                cardiovascular = "Tachycardic at 126 bpm.",
                abdomenAndPelvis = "Soft lower left quadrant without rebound; GENITAL EXAM reveals an exquisitely tender, swollen, high-riding LEFT testicle with a horizontal lie ('bell-clapper' deformity), absent left cremasteric reflex, and negative Prehn's sign.",
                extremitiesAndSkin = "Pale, diaphoretic.",
                neurologic = "Intact."
            ),
            labReports = listOf(
                LabEntry("Urinalysis / CSF", "Urinalysis", "0–2 WBC/hpf, Negative Leukocyte Esterase/Nitrites", "", "Normal", LabFlag.NORMAL),
                LabEntry("CBC & Chemistry", "WBC Count", "11.8", "x10³/µL", "4.5–11.0", LabFlag.HIGH)
            ),
            abgVbgReports = listOf(
                LabEntry("ABG / VBG", "Venous pH", "7.39", "", "7.35–7.45", LabFlag.NORMAL)
            ),
            imagingAndEcg = listOf(
                ImagingStudy(
                    id = "c20_fig1",
                    modality = "USG",
                    title = "Color Doppler Scrotal Ultrasound",
                    figureRef = "Case 20 Fig 20.1–20.3 (3 Source Figures)",
                    pdfPages = "pp. 503–523",
                    findings = "Enlarged, heterogeneous left testicle with complete absence of intratesticular arterial and venous color Doppler blood flow compared to normal vascularity in the right testicle, plus a twisted spermatic cord ('whirlpool sign') and reactive hydrocele.",
                    impression = "Acute left testicular torsion (<6-hour salvage window!).",
                    visualPattern = ClinicalVisualPattern.DOPPLER_USG_TESTICULAR_TORSION
                )
            ),
            criticalActions = listOf(
                CriticalAction("c20_a1", 1, "Perform a complete genitourinary/scrotal examination in a male patient presenting with lower abdominal pain and vomiting (identifying high-riding left testicle and absent cremasteric reflex)", OralBoardDomain.DATA_ACQUISITION, 15, listOf("exam", "scrotal", "testic")),
                CriticalAction("c20_a2", 2, "Keep patient strictly NPO and establish IV access with isotonic crystalloid (0.9% NS) fluid bolus for BP 89/61 and HR 126", OralBoardDomain.PATIENT_MANAGEMENT, 14, listOf("normal saline", "fluid", "bolus", "iv access")),
                CriticalAction("c20_a3", 3, "Administer parenteral opioid analgesia and antiemetic therapy", OralBoardDomain.PATIENT_MANAGEMENT, 14, listOf("analgesi", "pain", "morphine", "fentanyl", "ondansetron")),
                CriticalAction("c20_a4", 4, "Obtain emergent Urology consultation immediately upon clinical recognition (do not delay surgical exploration for imaging if Urologists are ready)", OralBoardDomain.DISPOSITION, 15, listOf("urology", "surgery")),
                CriticalAction("c20_a5", 5, "Obtain bedside Color Doppler Scrotal Ultrasound (showing absent intratesticular arterial flow) and urinalysis", OralBoardDomain.DIAGNOSTIC_STUDIES, 14, listOf("ultrasound", "usg", "doppler")),
                CriticalAction("c20_a6", 6, "Attempt bedside manual detorsion ('opening the book' — medial-to-lateral rotation) while awaiting the Operating Room", OralBoardDomain.PATIENT_MANAGEMENT, 14, listOf("detorsion", "open the book")),
                CriticalAction("c20_a7", 7, "Transfer immediately to Operating Room for surgical detorsion and bilateral orchiopexy within the 6-hour golden salvage window", OralBoardDomain.DISPOSITION, 14, listOf("orchiopexy", "or"))
            ),
            defaultQuestions = listOf(
                UnlockQuestion(
                    QuestionGateType.Q0_GATE,
                    "Bay Entrance Gate (Q0)",
                    prompt = "A 15-year-old boy wakes from sleep with sudden LLQ abdominal pain and vomiting (BP 89/61, HR 126). Why do adolescents with testicular torsion often complain only of lower abdominal pain at triage?",
                    options = listOf("Shared T10–L1 autonomic visceral innervation refers scrotal pain to the lower abdomen, compounded by adolescent embarrassment", "Testicular torsion starts in the sigmoid colon", "Torsion never causes scrotal swelling", "Only adults have scrotal pain"),
                    correctIndex = 0,
                    clinicalExplanation = "Every male infant, child, or adolescent presenting with acute lower abdominal pain and vomiting MUST have a genitourinary/scrotal examination to avoid missing testicular torsion."
                ),
                UnlockQuestion(
                    QuestionGateType.Q1_SURVEY,
                    "Bedside Survey Station (Q1)",
                    prompt = "Which physical examination findings on scrotal inspection and palpation are classic for acute left testicular torsion?",
                    options = listOf("A high-riding, transversely oriented ('bell-clapper') left testicle with an absent ipsilateral cremasteric reflex and negative Prehn's sign", "Relief of pain with scrotal elevation (positive Prehn's sign) and intact cremasteric reflex", "A painless transilluminating hydrocele", "A 'bag of worms' varicocele that disappears when supine"),
                    correctIndex = 0,
                    clinicalExplanation = "Loss of the cremasteric reflex (normal is elevation of the testis when stroking the inner thigh) and a high-riding horizontal testis are the most sensitive physical exam signs of testicular torsion."
                ),
                UnlockQuestion(
                    QuestionGateType.Q2_LABS,
                    "Point-of-Care Lab Console (Q2)",
                    prompt = "When performing bedside manual detorsion of the left testicle while the Operating Room is being prepared, in which direction do you initially rotate the testicle?",
                    options = listOf("Medial-to-lateral rotation ('opening a book': rotating the patient's left testicle clockwise/outward toward his left thigh by 180°–540°)", "Lateral-to-medial ('closing a book')", "Pulling straight caudally without rotation", "Manual detorsion replaces the need for bilateral surgical orchiopexy"),
                    correctIndex = 0,
                    clinicalExplanation = "Two-thirds of testicular torsions twist lateral-to-medial, so manual detorsion is performed medial-to-lateral ('opening a book'); bilateral surgical orchiopexy is still mandatory afterward."
                ),
                UnlockQuestion(
                    QuestionGateType.Q3_IMAGING,
                    "Mobile Radiology Terminal (Q3)",
                    prompt = "What is the time-critical testicular salvage rate relationship and the classic Color Doppler Ultrasound finding (Case 20 Fig 20.1–20.3) in testicular torsion?",
                    options = listOf(">90% salvage if detorsed within < 6 hours (dropping to <20% after 12–24 hours); Color Doppler shows absent or markedly decreased intratesticular arterial blood flow", "100% salvage up to 7 days; Doppler shows hyperemia ('ring of fire')", "Doppler ultrasound shows renal calculi", "Ultrasound must delay Urology notification by at least 4 hours"),
                    correctIndex = 0,
                    clinicalExplanation = "Testicular salvage exceeds 90% when blood flow is restored within 6 hours of symptom onset; Color Doppler demonstrates absent intratesticular blood flow."
                )
            ),
            dispositionAndPearls = "Immediate transfer to Operating Room with Urology for surgical detorsion and bilateral orchiopexy (fixing both testes to prevent future torsion of the contralateral bell-clapper deformity).",
            instructorClues = listOf(
                "Always expose and examine the scrotum in any male patient with lower abdominal pain!",
                "Give a 0.9% Normal Saline bolus (BP 89/61), IV analgesia/antiemetic, consult Urology immediately, and perform bedside manual detorsion ('open the book')."
            )
        )
    )
}

object OralBoardRepository {
    val allCases: List<OralBoardCase> = OralBoardCasesPart1.cases1To10 + OralBoardCasesPart2.cases11To20
    val crashCartDrawers: List<CrashCartDrawer> = OralBoardCasesPart1.crashCartDrawers

    fun getCaseByNumber(caseNumber: Int): OralBoardCase {
        return allCases.firstOrNull { it.caseNumber == caseNumber } ?: allCases.first()
    }
}
