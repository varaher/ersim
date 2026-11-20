package com.example.data

object OralBoardCasesPart1 {

    val crashCartDrawers: List<CrashCartDrawer> = listOf(
        CrashCartDrawer(
            drawerNumber = 0,
            title = "Top Shelf • Monitor, Defibrillator & BVM",
            subtitle = "GoI NHSRC Annexure VII & Oman MOH Top-Shelf Equipment",
            colorHex = 0xFF0284C7,
            items = listOf(
                CrashCartItem("top_bvm_adult", "Adult Self-Inflating Ambu Bag (BVM) with Mask", "1500 mL reservoir + O2 tubing", 2, "GoI NHSRC / Oman MOH", "Manual positive-pressure ventilation & pre-oxygenation", listOf("oxygen", "airway", "ventilat", "intubat", "bvm")),
                CrashCartItem("top_bvm_peds", "Pediatric / Infant Ambu Bag (BVM) with Masks", "250 mL & 500 mL sizes (00, 0, 1)", 2, "Oman MOH Annex One", "Infant/child resuscitation ventilation (Cases 2, 6, 14)", listOf("infant", "oxygen", "ventilat", "intubat")),
                CrashCartItem("top_defib_pads", "Hands-Free Multifunction Defibrillator Pads & Gel", "Adult & Pediatric attenuator pads", 4, "GoI NHSRC / Oman MOH", "ECG telemetry, synchronized cardioversion, defibrillation", listOf("monitor", "ecg", "defib", "cardiovert", "torsades")),
                CrashCartItem("top_steth_torch", "Clinical Stethoscope, Penlight Torch & Glucometer", "Point-of-care pupil & POC glucose check", 1, "GoI NHSRC Annexure VII", "Auscultation, pupillary light reflex, bedside D-stick", listOf("glucose", "pupil", "auscultat", "exam"))
            )
        ),
        CrashCartDrawer(
            drawerNumber = 1,
            title = "Drawer 1 • Emergency Resuscitation Medications",
            subtitle = "NHSRC Drawer 1 & Oman MOH High-Alert Emergency Drugs",
            colorHex = 0xFFDC2626,
            items = listOf(
                CrashCartItem("d1_epi", "Adrenaline (Epinephrine) Injection", "1 mg/mL (1:1000) & 0.1 mg/mL (1:10,000)", 10, "GoI NHSRC / Oman MOH", "Cardiac arrest, severe septic/anaphylactic shock, bradycardia", listOf("epinephrine", "adrenaline", "vasopressor", "shock", "pressor")),
                CrashCartItem("d1_calcium", "Calcium Gluconate 10% / Calcium Chloride 10%", "10 mL ampoule (100 mg/mL)", 6, "GoI NHSRC / Oman MOH", "Cardioprotection in severe hyperkalaemia (Case 10)", listOf("calcium", "hyperkalaemia", "hyperkalemia", "potassium")),
                CrashCartItem("d1_magnesium", "Magnesium Sulfate 50% Injection", "500 mg/mL (2 g / 4 mL ampoule)", 6, "GoI NHSRC / Oman MOH", "First-line therapy for Torsades de Pointes / Prolonged QT (Case 17)", listOf("magnesium", "torsades", "qt")),
                CrashCartItem("d1_bicarb", "Sodium Bicarbonate 8.4% Injection", "50 mEq / 50 mL vial", 6, "GoI NHSRC / Oman MOH", "Serum/urine alkalinization in salicylate toxicity & severe hyperkalaemia (Cases 10, 13)", listOf("bicarbonate", "alkaliniz", "salicylate", "hyperkalaemia")),
                CrashCartItem("d1_deferox", "Deferoxamine Mesylate Lyophilized Vial", "500 mg IV infusion vial", 4, "GoI NHSRC / Toxicology Pack", "Specific iron chelation therapy for acute iron overdose (Case 1)", listOf("deferoxamine", "chelat", "iron")),
                CrashCartItem("d1_pcc_vitk", "4-Factor PCC & Vitamin K1 (Phytomenadione) + FFP Order", "10 mg/mL Vit K + PCC reversal pack", 2, "Oman MOH Emergency Antidote", "Immediate anticoagulation reversal in intracranial haemorrhage (Case 3)", listOf("vitamin k", "pcc", "ffp", "anticoagul", "reversal")),
                CrashCartItem("d1_insulin_d50", "Regular Insulin + Dextrose 50% / 25% / 10%", "D50W 50 mL + D10W pediatric + Regular Insulin", 5, "GoI NHSRC / Oman MOH", "Intracellular potassium shift (Case 10) & pediatric hypoglycemia (Cases 2, 6)", listOf("insulin", "dextrose", "glucose", "hyperkalaemia")),
                CrashCartItem("d1_antibiotics", "Broad-Spectrum IV Antibiotics Pack (Vancomycin, Ceftriaxone, Zosyn, Clindamycin)", "Empiric Sepsis & Necrotizing Fasciitis Kit", 4, "GoI NHSRC Emergency Stock", "Empiric IV antibiotics for sepsis, cholecystitis, epidural/retropharyngeal abscess, nec fasc (Cases 5, 6, 8, 9, 18)", listOf("antibiotic", "vancomycin", "ceftriaxone", "clindamycin", "piperacillin", "sepsis")),
                CrashCartItem("d1_opioid_iem", "Morphine Sulfate / Fentanyl + Ondansetron Antiemetic", "Analgesia & antiemetic ampoules", 6, "GoI NHSRC Drawer 1", "Pain control & antiemetic therapy (Cases 5, 7, 11, 12, 19, 20)", listOf("analgesi", "pain", "morphine", "fentanyl", "antiemetic", "ondansetron")),
                CrashCartItem("d1_eye_glaucoma", "Timolol 0.5% Drops, Acetazolamide 500 mg IV, Pilocarpine 2%, Mannitol 20%", "Acute Angle-Closure Glaucoma Pack", 2, "Emergency Ophthalmic Kit", "IOP-lowering therapy for acute angle-closure glaucoma (Case 11)", listOf("timolol", "acetazolamide", "pilocarpine", "mannitol", "intraocular", "glaucoma"))
            )
        ),
        CrashCartDrawer(
            drawerNumber = 2,
            title = "Drawer 2 • IV Access, IO & Venipuncture Supplies",
            subtitle = "NHSRC Drawer 2 & Oman MOH Vascular Access Disposables",
            colorHex = 0xFFD97706,
            items = listOf(
                CrashCartItem("d2_iv_cannula", "Large-Bore & Pediatric IV Cannulas (14G–24G)", "Sizes 14G, 16G, 18G, 20G, 22G, 24G", 24, "GoI NHSRC / Oman MOH", "Peripheral venous access for fluid resuscitation & blood draw", listOf("iv access", "large-bore", "vascular", "fluid")),
                CrashCartItem("d2_io_needle", "Intraosseous (IO) Infusion Needle & Drill Set", "15 mm (Pediatric) & 25 mm (Adult) needles", 3, "Oman MOH Annex One", "Emergent vascular access in infant septic/hypovolemic shock (Cases 2, 6)", listOf("intraosseous", "io", "infant", "shock")),
                CrashCartItem("d2_blood_tubes", "Vacutainer Blood Tubes, Culture Bottles & Heparin ABG Syringes", "CBC, BMP, Coags, Lactate, Blood Cultures, Type & Cross", 15, "GoI NHSRC Annexure VII", "Venous blood sampling, crossmatch & arterial blood gas", listOf("blood", "culture", "lactate", "crossmatch", "abg"))
            )
        ),
        CrashCartDrawer(
            drawerNumber = 3,
            title = "Drawer 3 • Airway & Intubation Equipment",
            subtitle = "NHSRC Airway Drawer & Oman MOH Adult/Paediatric Airway",
            colorHex = 0xFF16A34A,
            items = listOf(
                CrashCartItem("d3_laryngoscope", "Laryngoscope Handles with Macintosh (2–4) & Miller (0–2) Blades", "Fiberoptic LED adult & infant blades", 2, "GoI NHSRC / Oman MOH", "Direct laryngoscopy for endotracheal intubation (Cases 3, 6, 18)", listOf("intubat", "airway", "laryngoscop", "rsi")),
                CrashCartItem("d3_ett", "Endotracheal Tubes (ETT) 3.0 mm to 8.5 mm + Stylet & Bougie", "Uncuffed/Cuffed pediatric & adult sizes", 14, "GoI NHSRC / Oman MOH", "Definitive endotracheal airway protection", listOf("intubat", "endotracheal", "airway", "ventilat")),
                CrashCartItem("d3_opa_npa_lma", "Oropharyngeal (Guedel), Nasopharyngeal Airways & LMA (Sizes 1–4)", "Complete airway adjunct tray", 10, "Oman MOH Annex One", "Supraglottic rescue airway & airway patency", listOf("airway", "opa", "lma"))
            )
        ),
        CrashCartDrawer(
            drawerNumber = 4,
            title = "Drawer 4 • Breathing, Suction & Thoracostomy",
            subtitle = "NHSRC Suction Drawer & Emergency Pleural Decompression",
            colorHex = 0xFF2563EB,
            items = listOf(
                CrashCartItem("d4_needle_decomp", "Needle Thoracostomy Decompression Kit (14G Over-the-Needle Catheter)", "14G x 3.25 inch angiocath + 10 mL syringe", 4, "GoI NHSRC / ATLS Trauma", "Immediate needle decompression of tension pneumothorax (Case 4)", listOf("needle decompression", "thoracostomy", "pneumothorax", "chest tube")),
                CrashCartItem("d4_chest_tube", "Tube Thoracostomy Set (28–36 Fr Chest Tube) & Underwater Seal Drain", "Trocar/blunt dissection chest drain pack", 2, "GoI NHSRC Annexure VII", "Definitive tube thoracostomy for tension pneumothorax / pleural effusion (Cases 4, 7)", listOf("chest tube", "thoracostomy", "pleural")),
                CrashCartItem("d4_suction_o2", "Yankauer Suction Catheter, Flexible Suction & Non-Rebreather O2 Mask", "High-flow 15 L/min NRB & suction tubing", 8, "GoI NHSRC / Oman MOH", "Orpharyngeal suctioning & 100% high-flow supplemental oxygen", listOf("oxygen", "suction", "nrb"))
            )
        ),
        CrashCartDrawer(
            drawerNumber = 5,
            title = "Drawer 5 • IV Fluids & Volume Resuscitation",
            subtitle = "NHSRC Drawer 5 Crystalloid & Colloid Infusion Stock",
            colorHex = 0xFF7C3AED,
            items = listOf(
                CrashCartItem("d5_ns_bolus", "0.9% Normal Saline (Isotonic Crystalloid) 500 mL / 1000 mL Bags", "Warmed isotonic crystalloid + pressure bag", 8, "GoI NHSRC / Oman MOH", "20 mL/kg pediatric bolus or adult volume resuscitation (Cases 1, 2, 4, 6, 9, 12, 13)", listOf("normal saline", "crystalloid", "fluid", "bolus", "resuscitat")),
                CrashCartItem("d5_rl_d5ns", "Ringer's Lactate & D5 0.45% NS Maintenance Fluid Bags", "500 mL bags with KCl additive option", 6, "GoI NHSRC Annexure VII", "Maintenance hydration & post-bolus electrolyte replacement (Case 2)", listOf("fluid", "hydration", "maintenance"))
            )
        ),
        CrashCartDrawer(
            drawerNumber = 6,
            title = "Drawer 6 • Special Procedures, GI & Surgical Packs",
            subtitle = "NHSRC Drawer 6 Gastric Lavage, Foley, CVC & Procedure Kits",
            colorHex = 0xFFDB2777,
            items = listOf(
                CrashCartItem("d6_ng_wbi", "Nasogastric / Orogastric Tube (10–18 Fr) & Polyethylene Glycol WBI Set", "Gastric decompression & Whole-Bowel Irrigation", 4, "GoI NHSRC Annexure VII", "Whole-bowel irrigation for radiopaque iron tablets (Case 1) & NG decompression for bowel obstruction/pyloric stenosis (Cases 2, 5, 12)", listOf("whole-bowel", "nasogastric", "ng tube", "decompression", "irrigation")),
                CrashCartItem("d6_foley_cvc", "Foley Urinary Catheter + Central Venous Catheter (Triple-Lumen) Kit", "Strict urine output & central hemodynamic monitoring", 3, "GoI NHSRC / Oman MOH", "Urine output monitoring (vin rosé urine in Case 1, sepsis in Cases 8, 9) & central access", listOf("foley", "urine", "central", "catheter")),
                CrashCartItem("d6_splint_marker", "Extremity Immobilization Splint, Skin Marker & Tetanus Toxoid Vial", "Snakebite & Orthopedic Immobilization Pack", 3, "GoI NHSRC Emergency Pack", "Circumference marking, splint immobilization & tetanus prophylaxis (Cases 15, 19)", listOf("immobiliz", "tetanus", "snake", "splint", "non-weight"))
            )
        )
    )

    val cases1To10: List<OralBoardCase> = listOf(
        // CASE 1
        OralBoardCase(
            caseNumber = 1,
            scenarioCategory = "Overdose",
            mainDiagnosis = "Acute iron overdose",
            pdfPages = "123–147",
            sourceFiguresCount = 3,
            patientName = "Ananya Menon",
            ageDesc = "19-year-old Female",
            gender = "Female",
            weightKg = 58f,
            chiefComplaint = "Intentional ingestion of ~60 prenatal iron tablets 90 minutes ago with severe epigastric pain and hematemesis",
            triageNote = "Brought by EMS after ingesting ferrous sulfate tablets following an argument. Vomited dark blood-streaked fluid twice en route.",
            patientSkinToneTag = "PALE",
            initialVitals = VitalSigns("119/71", 119, 71, 96, 22, 98, 37.1f, 35, "Sinus Rhythm"),
            stabilizedVitals = VitalSigns("122/76", 122, 76, 82, 16, 99, 37.0f, 38, "Normal Sinus Rhythm"),
            ventilatorPreset = VentilatorSettings(mode = "Standby (Spontaneous Breathing)", tidalVolumeMlPerKg = 8, respRatePerMin = 16, fio2Percent = 21, isActive = false),
            defibrillatorPreset = DefibrillatorSettings(mode = "3-Lead ECG Telemetry", energyJoules = 150, energyPerKgLabel = "Adult Telemetry Mode"),
            primarySurvey = PrimarySurveyABCDE(
                airway = "Patent, speaking in full sentences despite nausea and active retching.",
                breathing = "RR 22/min, clear breath sounds bilaterally, SpO2 98% on room air.",
                circulation = "BP 119/71 mmHg, HR 96 bpm, mildly cool extremities, capillary refill 2.5 sec.",
                disability = "GCS 15, alert and tearful, pupils 4 mm equal and reactive.",
                exposure = "No external trauma, no rash; dark emesis stain on gown."
            ),
            physicalExam = PhysicalExamClues(
                generalAppearance = "Distressed young woman holding an emesis basin, nauseated and diaphoretic.",
                heentAndNeck = "Moist mucous membranes with metallic odor; oropharynx clear of caustic burns.",
                chestAndLungs = "Symmetric chest wall expansion, clear to auscultation bilaterally.",
                cardiovascular = "Regular tachycardia at 96 bpm, normal S1/S2, no murmurs.",
                abdomenAndPelvis = "Moderate epigastric and diffuse abdominal tenderness without rebound or rigid guarding; hyperactive bowel sounds.",
                extremitiesAndSkin = "Warm, intact pulses, no cyanosis or edema.",
                neurologic = "Alert and oriented x3, normal cranial nerves and motor strength."
            ),
            labReports = listOf(
                LabEntry("Coagulation & Tox", "Serum Iron Level (4-hr peak)", "685", "µg/dL", "50–170", LabFlag.CRITICAL),
                LabEntry("Coagulation & Tox", "Acetaminophen & Salicylate", "Undetectable", "mg/dL", "Negative", LabFlag.NORMAL),
                LabEntry("CBC & Chemistry", "WBC Count", "16.8", "x10³/µL", "4.5–11.0", LabFlag.HIGH),
                LabEntry("CBC & Chemistry", "Serum Glucose", "184", "mg/dL", "70–100", LabFlag.HIGH),
                LabEntry("CBC & Chemistry", "Serum Bicarbonate (HCO3)", "16", "mEq/L", "22–28", LabFlag.LOW),
                LabEntry("CBC & Chemistry", "Anion Gap", "21", "mEq/L", "8–12", LabFlag.HIGH),
                LabEntry("Urinalysis / CSF", "Urine Pregnancy (hCG)", "Positive", "qual", "Negative", LabFlag.HIGH)
            ),
            abgVbgReports = listOf(
                LabEntry("ABG / VBG", "Venous pH", "7.29", "", "7.32–7.42", LabFlag.LOW),
                LabEntry("ABG / VBG", "Serum Lactate", "4.2", "mmol/L", "0.5–2.0", LabFlag.CRITICAL),
                LabEntry("ABG / VBG", "Base Deficit", "-8.4", "mEq/L", "-2 to +2", LabFlag.LOW)
            ),
            imagingAndEcg = listOf(
                ImagingStudy(
                    id = "c1_fig1",
                    modality = "X-RAY",
                    title = "KUB Abdominal Radiograph",
                    figureRef = "Case 1 Fig 1.1–1.3 (3 Source Figures)",
                    pdfPages = "pp. 123–147",
                    findings = "Numerous well-defined radiopaque tablet densities clustered within the gastric fundus and proximal duodenum confirming large unabsorbed elemental iron burden.",
                    impression = "Multiple radiopaque iron tablets in stomach; indication for Whole-Bowel Irrigation (WBI) and IV Deferoxamine.",
                    visualPattern = ClinicalVisualPattern.ABDOMINAL_RADIOGRAPH_IRON
                )
            ),
            criticalActions = listOf(
                CriticalAction("c1_a1", 1, "Obtain abdominal radiograph (KUB) to identify radiopaque iron tablets in the GI tract", OralBoardDomain.DIAGNOSTIC_STUDIES, 20, listOf("xray", "radiograph", "kub")),
                CriticalAction("c1_a2", 2, "Check serum iron concentration (exceeds 500 µg/dL toxic threshold) and co-ingestant tox screen", OralBoardDomain.DIAGNOSTIC_STUDIES, 20, listOf("blood", "iron", "lab")),
                CriticalAction("c1_a3", 3, "Initiate vigorous IV crystalloid fluid resuscitation (0.9% Normal Saline bolus)", OralBoardDomain.PATIENT_MANAGEMENT, 20, listOf("normal saline", "fluid", "bolus")),
                CriticalAction("c1_a4", 4, "Administer IV Deferoxamine chelation therapy (15 mg/kg/hr) for severe iron toxicity (>500 µg/dL & metabolic acidosis)", OralBoardDomain.PATIENT_MANAGEMENT, 20, listOf("deferoxamine", "chelat")),
                CriticalAction("c1_a5", 5, "Perform Whole-Bowel Irrigation (WBI) with polyethylene glycol via NG tube and consult Poison Control / Medical ICU + Psychiatry", OralBoardDomain.DISPOSITION, 20, listOf("whole-bowel", "ng tube", "irrigation"))
            ),
            defaultQuestions = listOf(
                UnlockQuestion(
                    QuestionGateType.Q0_GATE,
                    "Bay Entrance Gate (Q0)",
                    prompt = "A 19-year-old presents 90 minutes after ingesting ~60 prenatal ferrous sulfate tablets. What is the single most pathognomonic urine color change seen after starting IV Deferoxamine chelation?",
                    options = listOf("Vin rosé (pinkish-orange/red) urine from ferrioxamine excretion", "Methylene blue-green urine", "Dark tea-colored myoglobinuria", "Fluorescent yellow urine"),
                    correctIndex = 0,
                    clinicalExplanation = "Deferoxamine binds free ferric iron to form ferrioxamine, a water-soluble complex excreted renally that turns urine a classic 'vin rosé' color."
                ),
                UnlockQuestion(
                    QuestionGateType.Q1_SURVEY,
                    "Bedside Survey Station (Q1)",
                    prompt = "During Stage I (0.5–6 hours) of acute iron toxicity, which clinical presentation predominates during the primary & secondary survey?",
                    options = listOf("Direct corrosive GI mucosal injury causing vomiting, hematemesis, and abdominal pain", "Quiescent latent phase with complete symptom resolution", "Fulminant hepatic necrosis and coagulopathy", "Pyloric outlet scarring and gastric stricture"),
                    correctIndex = 0,
                    clinicalExplanation = "Stage I (0.5–6 hrs) reflects direct corrosive mucosal injury to the stomach and bowel causing severe vomiting, hematemesis, diarrhea, and abdominal pain."
                ),
                UnlockQuestion(
                    QuestionGateType.Q2_LABS,
                    "Point-of-Care Lab Console (Q2)",
                    prompt = "Above what peak serum iron concentration (drawn 4–6 hours post-ingestion) is systemic iron toxicity considered severe, mandating IV Deferoxamine chelation?",
                    options = listOf("> 500 µg/dL (or > 350 µg/dL with symptomatic acidosis/shock)", "> 100 µg/dL", "> 180 µg/dL", "> 50 µg/dL"),
                    correctIndex = 0,
                    clinicalExplanation = "Serum iron levels > 500 µg/dL (or lower levels accompanied by high-anion-gap metabolic acidosis, lethargy, or hypotension) mandate IV Deferoxamine."
                ),
                UnlockQuestion(
                    QuestionGateType.Q3_IMAGING,
                    "Mobile Radiology Terminal (Q3)",
                    prompt = "Why is an abdominal plain radiograph (KUB) uniquely diagnostic in acute elemental iron tablet ingestion compared to most other drug overdoses?",
                    options = listOf("Undissolved adult iron tablets are radiopaque (CHIPES mnemonic) and guide Whole-Bowel Irrigation", "Iron produces free air under the diaphragm within 15 minutes", "Iron causes bilateral hydronephrosis on KUB", "Activated charcoal binds iron avidly on X-ray"),
                    correctIndex = 0,
                    clinicalExplanation = "Elemental iron tablets are radiopaque on KUB (CHIPES: Chloral hydrate, Heavy metals, Iron, Phenothiazines, Enteric-coated, Sustained-release). Note: activated charcoal does NOT bind iron."
                )
            ),
            dispositionAndPearls = "Admit to Medical ICU, continue IV Deferoxamine & Whole-Bowel Irrigation until repeat KUB clears and urine returns to normal color; Poison Control & Psychiatric consultation.",
            instructorClues = listOf(
                "Activated charcoal does NOT bind iron—use Whole-Bowel Irrigation (WBI) with polyethylene glycol via NG tube.",
                "Pregnancy test is positive; Deferoxamine is still indicated for life-threatening maternal iron toxicity (>500 µg/dL).",
                "Place Foley catheter to monitor urine output and observe for 'vin rosé' urine."
            )
        ),

        // CASE 2
        OralBoardCase(
            caseNumber = 2,
            scenarioCategory = "Vomiting infant",
            mainDiagnosis = "Congenital hypertrophic pyloric stenosis",
            pdfPages = "148–168",
            sourceFiguresCount = 1,
            patientName = "Baby of Fathima Rahman",
            ageDesc = "4-week-old Male Infant",
            gender = "Male",
            weightKg = 3.8f,
            chiefComplaint = "Progressive projectile non-bilious vomiting after every feed for 5 days and extreme lethargy today",
            triageNote = "First-born male infant. Mother reports forceful non-bilious formula vomiting across the crib followed by immediate hunger until today when infant became listless with no wet diapers in 14 hours.",
            patientSkinToneTag = "PALE",
            isPediatricOrInfant = true,
            initialVitals = VitalSigns("50/palp", 50, 30, 170, 60, null, 37.2f, 32, "Sinus Tachycardia"),
            stabilizedVitals = VitalSigns("78/46", 78, 46, 138, 38, 99, 37.1f, 36, "Normal Infant Sinus"),
            ventilatorPreset = VentilatorSettings(mode = "Blow-by / High-Flow Nasal Cannula O2", tidalVolumeMlPerKg = 8, respRatePerMin = 35, fio2Percent = 40, isActive = false),
            defibrillatorPreset = DefibrillatorSettings(mode = "Pediatric ECG & Pulse Ox Leads", energyJoules = 15, energyPerKgLabel = "Pediatric Monitor Mode (3.8 kg)"),
            primarySurvey = PrimarySurveyABCDE(
                airway = "Patent, weak cry, dry cracked lips.",
                breathing = "RR 60/min, lungs clear, SpO2 initially unobtainable ('None') due to poor peripheral perfusion.",
                circulation = "BP 50/palp, HR 170 bpm, mottled cool extremities, capillary refill 4.5 seconds, sunken anterior fontanelle.",
                disability = "Lethargic but responsive to tactile stimulation; bedside POC glucose is 42 mg/dL (hypoglycemic!).",
                exposure = "Scaphoid lower abdomen with visible epigastric peristaltic wave; no rash."
            ),
            physicalExam = PhysicalExamClues(
                generalAppearance = "Severely dehydrated, listless 4-week-old male infant with sunken eyes.",
                heentAndNeck = "Depressed anterior fontanelle, parched oral mucosa, absent tears.",
                chestAndLungs = "Tachypneic at 60/min without wheezes or rales.",
                cardiovascular = "Marked tachycardia at 170 bpm, weak peripheral pulses.",
                abdomenAndPelvis = "Firm, mobile, olive-shaped mass ('pyloric olive') palpable in the right upper quadrant / epigastrium after decompression.",
                extremitiesAndSkin = "Poor skin turgor (tenting), cool mottled hands and feet.",
                neurologic = "Hypotonic and lethargic prior to dextrose and saline bolus."
            ),
            labReports = listOf(
                LabEntry("CBC & Chemistry", "Point-of-Care Glucose", "42", "mg/dL", "60–100", LabFlag.CRITICAL),
                LabEntry("CBC & Chemistry", "Serum Sodium (Na+)", "131", "mEq/L", "135–145", LabFlag.LOW),
                LabEntry("CBC & Chemistry", "Serum Potassium (K+)", "2.8", "mEq/L", "3.5–5.2", LabFlag.CRITICAL),
                LabEntry("CBC & Chemistry", "Serum Chloride (Cl-)", "82", "mEq/L", "98–107", LabFlag.CRITICAL),
                LabEntry("CBC & Chemistry", "Serum Bicarbonate (HCO3-)", "36", "mEq/L", "20–26", LabFlag.HIGH)
            ),
            abgVbgReports = listOf(
                LabEntry("ABG / VBG", "Venous pH", "7.54", "", "7.35–7.45", LabFlag.CRITICAL),
                LabEntry("ABG / VBG", "PCO2", "45", "mmHg", "35–45", LabFlag.NORMAL),
                LabEntry("ABG / VBG", "Acid-Base Pattern", "Hypochloremic Hypokalemic Metabolic Alkalosis", "", "Normal", LabFlag.CRITICAL)
            ),
            imagingAndEcg = listOf(
                ImagingStudy(
                    id = "c2_fig1",
                    modality = "USG",
                    title = "Pyloric Ultrasound (Longitudinal & Transverse)",
                    figureRef = "Case 2 Fig 2.1 (1 Source Figure)",
                    pdfPages = "pp. 148–168",
                    findings = "Hypertrophied hypoechoic pyloric muscle wall measuring 4.8 mm thickness (diagnostic > 3 mm) and pyloric channel length 18 mm (diagnostic > 15 mm) with 'target/doughnut' sign.",
                    impression = "Congenital hypertrophic pyloric stenosis.",
                    visualPattern = ClinicalVisualPattern.PYLORIC_ULTRASOUND
                )
            ),
            criticalActions = listOf(
                CriticalAction("c2_a1", 1, "Establish vascular/IO access and administer immediate 20 mL/kg 0.9% Normal Saline bolus for hypovolemic shock", OralBoardDomain.ACUITY_RECOGNITION, 25, listOf("normal saline", "fluid", "bolus", "io", "iv access")),
                CriticalAction("c2_a2", 2, "Check bedside blood glucose and treat infant hypoglycemia with IV Dextrose (D10W 2–4 mL/kg)", OralBoardDomain.PATIENT_MANAGEMENT, 25, listOf("glucose", "dextrose")),
                CriticalAction("c2_a3", 3, "Diagnose congenital hypertrophic pyloric stenosis via abdominal ultrasound and recognize hypochloremic hypokalemic metabolic alkalosis", OralBoardDomain.DIAGNOSTIC_STUDIES, 25, listOf("ultrasound", "usg", "pyloric")),
                CriticalAction("c2_a4", 4, "Correct fluid and electrolyte deficits prior to consulting Pediatric Surgery for Ramstedt pyloromyotomy", OralBoardDomain.DISPOSITION, 25, listOf("surgery", "hydration", "maintenance"))
            ),
            defaultQuestions = listOf(
                UnlockQuestion(
                    QuestionGateType.Q0_GATE,
                    "Bay Entrance Gate (Q0)",
                    prompt = "A 4-week-old male infant presents with projectile non-bilious vomiting, BP 50/palp, HR 170, and unobtainable SpO2. What is the immediate first resuscitation priority?",
                    options = listOf("Vascular/IO access, 20 mL/kg isotonic 0.9% Normal Saline bolus, and bedside POC glucose check", "Immediate transfer to the operating room for pyloromyotomy before fluids", "Barium enema study", "IV sodium bicarbonate infusion"),
                    correctIndex = 0,
                    clinicalExplanation = "Pyloric stenosis is a medical resuscitation emergency first, not an immediate surgical emergency; hypovolemic shock and hypoglycemia must be corrected before surgery."
                ),
                UnlockQuestion(
                    QuestionGateType.Q1_SURVEY,
                    "Bedside Survey Station (Q1)",
                    prompt = "What classic physical examination finding is palpated in the right upper quadrant/epigastrium in congenital hypertrophic pyloric stenosis?",
                    options = listOf("A firm, mobile olive-shaped mass ('pyloric olive') with visible left-to-right peristaltic waves", "A sausage-shaped mass in the right lower quadrant with currant-jelly stool", "Cullen and Grey-Turner ecchymoses", "A pulsatile periumbilical mass"),
                    correctIndex = 0,
                    clinicalExplanation = "Palpation of a firm 'olive' in the RUQ/epigastrium (especially after gastric decompression) is highly specific for hypertrophic pyloric stenosis."
                ),
                UnlockQuestion(
                    QuestionGateType.Q2_LABS,
                    "Point-of-Care Lab Console (Q2)",
                    prompt = "Protracted loss of gastric hydrochloric acid (HCl) from persistent non-bilious emesis produces which classic electrolyte and acid-base disturbance?",
                    options = listOf("Hypochloremic, hypokalemic metabolic alkalosis with paradoxical aciduria", "Hyperchloremic normal-anion-gap metabolic acidosis", "High-anion-gap lactic acidosis with hyperkalaemia", "Respiratory alkalosis with hypernatremia"),
                    correctIndex = 0,
                    clinicalExplanation = "Loss of gastric HCl and volume depletion with aldosterone activation causes hypochloremic, hypokalemic metabolic alkalosis."
                ),
                UnlockQuestion(
                    QuestionGateType.Q3_IMAGING,
                    "Mobile Radiology Terminal (Q3)",
                    prompt = "On pyloric ultrasonography (Case 2 Fig 2.1), what sonographic measurements meet the diagnostic criteria for hypertrophic pyloric stenosis?",
                    options = listOf("Pyloric muscle thickness > 3 mm (or ≥ 4 mm) and pyloric channel length > 15 mm", "Pyloric muscle thickness < 1 mm and channel length < 5 mm", "Whirlpool sign of the superior mesenteric vein", "Double-bubble sign on plain film"),
                    correctIndex = 0,
                    clinicalExplanation = "Ultrasound is the diagnostic modality of choice, showing pyloric muscle wall thickness > 3–4 mm and channel length > 15 mm."
                )
            ),
            dispositionAndPearls = "Admit to Pediatric ICU / Surgical floor for complete fluid & electrolyte rehydration (restore Cl- > 100 and HCO3 < 30) prior to Ramstedt pyloromyotomy.",
            instructorClues = listOf(
                "Infant is hypoglycemic (POC glucose 42 mg/dL) in addition to hypovolemic shock—give D10W bolus alongside 20 mL/kg 0.9% NS.",
                "Never rush to the OR before correcting the metabolic alkalosis and hypokalemia, as post-anesthesia apnea risk is high."
            )
        ),

        // CASE 3
        OralBoardCase(
            caseNumber = 3,
            scenarioCategory = "Altered mental status",
            mainDiagnosis = "Non-traumatic intracranial haemorrhage in an anticoagulated patient",
            pdfPages = "169–192",
            sourceFiguresCount = 3,
            patientName = "Gopalakrishnan Nair",
            ageDesc = "72-year-old Male",
            gender = "Male",
            weightKg = 84f,
            chiefComplaint = "Sudden severe headache followed by rapid decline in consciousness and right-sided hemiplegia",
            triageNote = "History of atrial fibrillation on Warfarin and hypertension. Collapsed in living room 45 minutes ago; wife notes snoring respirations.",
            patientSkinToneTag = "CYANOTIC",
            initialVitals = VitalSigns("140/60", 140, 60, 60, 6, 80, 37.0f, 58, "Sinus Bradycardia (Cushing Reflex)"),
            stabilizedVitals = VitalSigns("136/78", 136, 78, 74, 16, 99, 36.9f, 35, "Normal Sinus Rhythm"),
            ventilatorPreset = VentilatorSettings(mode = "AC / Volume Control (Neuro-Protective)", tidalVolumeMlPerKg = 8, respRatePerMin = 16, fio2Percent = 100, peepCmH2O = 5, isActive = true),
            defibrillatorPreset = DefibrillatorSettings(mode = "Continuous Telemetry", energyJoules = 150, energyPerKgLabel = "Adult Monitor Mode"),
            primarySurvey = PrimarySurveyABCDE(
                airway = "Compromised! Sonorous respirations, pooled secretions, absent gag reflex (GCS 6).",
                breathing = "Bradypneic at RR 6/min, shallow irregular Cushing pattern, SpO2 80% on room air.",
                circulation = "BP 140/60 mmHg (widened pulse pressure), HR 60 bpm (Cushing bradycardia).",
                disability = "GCS 6 (E1 V1 M4); fixed dilated left pupil (6 mm) vs reactive right pupil (3 mm); right hemiplegia.",
                exposure = "Medical alert bracelet in pocket reads 'Atrial Fibrillation – Warfarin (Coumadin)'."
            ),
            physicalExam = PhysicalExamClues(
                generalAppearance = "Obtunded elderly male with snoring respirations and unequal pupils.",
                heentAndNeck = "No scalp lacerations or battle sign; left pupil 6 mm sluggish/fixed, right pupil 3 mm.",
                chestAndLungs = "Hypoventilation at 6 breaths/min; clear once bag-valve-mask ventilation initiated.",
                cardiovascular = "Irregularly irregular bradycardia at 60 bpm.",
                abdomenAndPelvis = "Soft, non-distended, normal bowel sounds.",
                extremitiesAndSkin = "Multiple ecchymoses on forearms; flaccid right upper and lower extremity.",
                neurologic = "GCS 6, left uncal herniation syndrome with ipsilateral blown pupil and contralateral hemiplegia."
            ),
            labReports = listOf(
                LabEntry("Coagulation & Tox", "INR (Prothrombin Time)", "6.8", "", "0.9–1.1 (Goal 2–3)", LabFlag.CRITICAL),
                LabEntry("Coagulation & Tox", "PT", "64.2", "sec", "11–13.5", LabFlag.CRITICAL),
                LabEntry("CBC & Chemistry", "Platelets", "210", "x10³/µL", "150–400", LabFlag.NORMAL),
                LabEntry("CBC & Chemistry", "Point-of-Care Glucose", "118", "mg/dL", "70–100", LabFlag.NORMAL)
            ),
            abgVbgReports = listOf(
                LabEntry("ABG / VBG", "Arterial pH (Initial)", "7.21", "", "7.35–7.45", LabFlag.CRITICAL),
                LabEntry("ABG / VBG", "PaCO2 (Initial)", "68", "mmHg", "35–45", LabFlag.CRITICAL),
                LabEntry("ABG / VBG", "PaO2 (Initial)", "52", "mmHg", "80–100", LabFlag.CRITICAL)
            ),
            imagingAndEcg = listOf(
                ImagingStudy(
                    id = "c3_fig1",
                    modality = "CT",
                    title = "Non-Contrast Head CT Scan",
                    figureRef = "Case 3 Fig 3.1–3.3 (3 Source Figures)",
                    pdfPages = "pp. 169–192",
                    findings = "Large acute hyperdense left basal ganglia / temporoparietal intraparenchymal hematoma with surrounding edema, 9 mm left-to-right midline shift, and intraventricular extension.",
                    impression = "Coagulopathic intracranial haemorrhage with impending uncal herniation.",
                    visualPattern = ClinicalVisualPattern.HEAD_CT_ICH
                )
            ),
            criticalActions = listOf(
                CriticalAction("c3_a1", 1, "Immediately secure airway via Rapid Sequence Intubation (RSI) and mechanical ventilation for GCS 6, RR 6, SpO2 80%", OralBoardDomain.ACUITY_RECOGNITION, 15, listOf("intubat", "airway", "laryngoscop", "ett", "bvm")),
                CriticalAction("c3_a2", 2, "Check bedside blood glucose immediately in an altered patient", OralBoardDomain.DATA_ACQUISITION, 14, listOf("glucose", "torch")),
                CriticalAction("c3_a3", 3, "Obtain emergent non-contrast Head CT scan", OralBoardDomain.DIAGNOSTIC_STUDIES, 14, listOf("ct", "head")),
                CriticalAction("c3_a4", 4, "Identify supratherapeutic warfarin anticoagulation (INR 6.8) via history/medical alert bracelet and stat coagulation panel", OralBoardDomain.DIAGNOSTIC_STUDIES, 14, listOf("inr", "blood", "coag")),
                CriticalAction("c3_a5", 5, "Administer emergent anticoagulation reversal: IV Vitamin K1 (10 mg) plus 4-factor PCC / Fresh Frozen Plasma (FFP)", OralBoardDomain.PATIENT_MANAGEMENT, 15, listOf("vitamin k", "pcc", "ffp", "reversal")),
                CriticalAction("c3_a6", 6, "Initiate ICP-lowering neuro-protective measures (elevate head of bed 30°, Mannitol / Hypertonic Saline, target PaCO2 35 mmHg)", OralBoardDomain.PATIENT_MANAGEMENT, 14, listOf("mannitol", "icp", "ventilat")),
                CriticalAction("c3_a7", 7, "Obtain emergent Neurosurgery consultation and admit to Neuro-ICU", OralBoardDomain.DISPOSITION, 14, listOf("neurosurgery", "icu"))
            ),
            defaultQuestions = listOf(
                UnlockQuestion(
                    QuestionGateType.Q0_GATE,
                    "Bay Entrance Gate (Q0)",
                    prompt = "A 72-year-old arrives with GCS 6, RR 6/min, SpO2 80%, BP 140/60, and HR 60. What must the resuscitation team perform FIRST before sending the patient to CT?",
                    options = listOf("Pre-oxygenate and perform Rapid Sequence Intubation (RSI) to secure the airway", "Send immediately to MRI brain without airway intervention", "Perform a lumbar puncture at the bedside", "Administer IV tPA thrombolysis"),
                    correctIndex = 0,
                    clinicalExplanation = "Airway protection and reversing severe hypoxia/hypercapnia (RR 6, SpO2 80%, GCS 6) take immediate priority before transport to CT."
                ),
                UnlockQuestion(
                    QuestionGateType.Q1_SURVEY,
                    "Bedside Survey Station (Q1)",
                    prompt = "During the Disability & Exposure survey, a fixed dilated left pupil, right hemiplegia, widened pulse pressure, and bradycardia (HR 60) signify what process?",
                    options = listOf("Cushing reflex with left uncal transtentorial herniation", "Right middle cerebral artery ischemic stroke", "Opioid toxidrome", "Wernicke encephalopathy"),
                    correctIndex = 0,
                    clinicalExplanation = "Ipsilateral CN III compression (blown left pupil), contralateral motor weakness, hypertension/widened pulse pressure, and bradycardia indicate Cushing reflex from uncal herniation."
                ),
                UnlockQuestion(
                    QuestionGateType.Q2_LABS,
                    "Point-of-Care Lab Console (Q2)",
                    prompt = "The patient's INR returns critically elevated at 6.8 on Warfarin. What combination of agents is required for both rapid and sustained reversal of Warfarin-associated ICH?",
                    options = listOf("4-Factor Prothrombin Complex Concentrate (PCC) or FFP for immediate factor replacement PLUS IV Vitamin K (10 mg)", "Protamine sulfate monotherapy", "Idarucizumab monotherapy", "Oral aspirin and clopidogrel"),
                    correctIndex = 0,
                    clinicalExplanation = "PCC (or FFP) immediately restores Vitamin K-dependent clotting factors (II, VII, IX, X), while IV Vitamin K1 provides sustained hepatic synthesis."
                ),
                UnlockQuestion(
                    QuestionGateType.Q3_IMAGING,
                    "Mobile Radiology Terminal (Q3)",
                    prompt = "What does the non-contrast Head CT (Case 3 Fig 3.1–3.3) reveal in an acute intracranial haemorrhage?",
                    options = listOf("Acute hyperdense (bright white) intraparenchymal hematoma with mass effect, midline shift, and ventricular compression", "Normal parenchyma with loss of insular ribbon only", "Bilateral ring-enhancing toxoplasmosis lesions", "Diffuse cerebral atrophy without mass effect"),
                    correctIndex = 0,
                    clinicalExplanation = "Acute extravasated blood appears brightly hyperdense on non-contrast Head CT, here showing mass effect and midline shift."
                )
            ),
            dispositionAndPearls = "Emergent Neurosurgery consultation for hematoma evacuation / EVD placement and admission to Neuro-ICU.",
            instructorClues = listOf(
                "Do not let the patient go to CT until the airway is intubated (RR is 6 and SpO2 is 80%).",
                "Check the patient's pockets during Exposure to find the Warfarin medical alert card."
            )
        ),

        // CASE 4
        OralBoardCase(
            caseNumber = 4,
            scenarioCategory = "Chest trauma",
            mainDiagnosis = "Right-sided tension pneumothorax due to penetrating trauma",
            pdfPages = "193–213",
            sourceFiguresCount = 2,
            patientName = "Arjun Singh Sandhu",
            ageDesc = "28-year-old Male",
            gender = "Male",
            weightKg = 78f,
            chiefComplaint = "Single stab wound to the right anterior chest with severe dyspnea and hypotension",
            triageNote = "Assaulted outside a club 20 minutes ago with a pocketknife to the right hemithorax. EMS placed an occlusive 3-sided dressing; patient became increasingly hypotensive.",
            patientSkinToneTag = "CYANOTIC",
            initialVitals = VitalSigns("88/42", 88, 42, 110, 28, 92, 37.9f, 30, "Sinus Tachycardia"),
            stabilizedVitals = VitalSigns("120/76", 120, 76, 86, 18, 99, 37.6f, 37, "Normal Sinus Rhythm"),
            ventilatorPreset = VentilatorSettings(mode = "100% High-Flow NRB Mask", tidalVolumeMlPerKg = 8, respRatePerMin = 18, fio2Percent = 100, isActive = false),
            defibrillatorPreset = DefibrillatorSettings(mode = "Trauma Telemetry", energyJoules = 150, energyPerKgLabel = "Adult Monitor Mode"),
            primarySurvey = PrimarySurveyABCDE(
                airway = "Patent, speaking in 2-word gasps; trachea visibly deviated to the LEFT.",
                breathing = "RR 28/min, absent breath sounds and hyperresonance to percussion over entire RIGHT hemithorax; SpO2 92%.",
                circulation = "BP 88/42 mmHg, HR 110 bpm, prominent jugular venous distension (JVD).",
                disability = "GCS 14 (anxious and agitated from hypoxia), pupils equal.",
                exposure = "2 cm penetrating stab wound in right 4th intercostal space mid-clavicular line; log-roll shows no posterior wounds."
            ),
            physicalExam = PhysicalExamClues(
                generalAppearance = "Diaphoretic, tachypneic young male in obstructive shock.",
                heentAndNeck = "Marked Jugular Venous Distension (JVD) and leftward tracheal deviation.",
                chestAndLungs = "Right chest wall hyper-expanded with absent breath sounds and tympanic hyperresonance.",
                cardiovascular = "Tachycardic at 110 bpm, distant heart sounds shifted leftward.",
                abdomenAndPelvis = "Soft, non-tender, negative abdominal FAST exam.",
                extremitiesAndSkin = "Clammy skin, weak radial pulses.",
                neurologic = "Agitated, moving all four extremities."
            ),
            labReports = listOf(
                LabEntry("CBC & Chemistry", "Hemoglobin", "13.4", "g/dL", "13.5–17.5", LabFlag.LOW),
                LabEntry("Coagulation & Tox", "Type & Crossmatch", "2 Units PRBC Ready", "", "Crossmatched", LabFlag.NORMAL),
                LabEntry("CBC & Chemistry", "Serum Lactate", "3.6", "mmol/L", "0.5–2.0", LabFlag.HIGH)
            ),
            abgVbgReports = listOf(
                LabEntry("ABG / VBG", "Arterial pH", "7.31", "", "7.35–7.45", LabFlag.LOW),
                LabEntry("ABG / VBG", "PaO2", "64", "mmHg", "80–100", LabFlag.LOW)
            ),
            imagingAndEcg = listOf(
                ImagingStudy(
                    id = "c4_fig1",
                    modality = "X-RAY",
                    title = "Portable Supine Chest Radiograph & Post-Tube CXR",
                    figureRef = "Case 4 Fig 4.1–4.2 (2 Source Figures)",
                    pdfPages = "pp. 193–213",
                    findings = "Complete collapse of the right lung with visceral pleural line, hyperlucent right hemithorax devoid of lung markings, flattened right hemidiaphragm, and leftward shift of mediastinum and trachea.",
                    impression = "Right-sided tension pneumothorax; post-intervention CXR confirms right chest tube in good position with lung re-expansion.",
                    visualPattern = ClinicalVisualPattern.CXR_TENSION_PTX
                )
            ),
            criticalActions = listOf(
                CriticalAction("c4_a1", 1, "Apply 100% high-flow supplemental oxygen and establish two large-bore IV lines with crystalloid resuscitation", OralBoardDomain.ACUITY_RECOGNITION, 16, listOf("oxygen", "nrb", "iv access", "normal saline")),
                CriticalAction("c4_a2", 2, "Clinically diagnose right-sided tension pneumothorax during primary survey (absent right breath sounds, JVD, hypotension)", OralBoardDomain.PROBLEM_SOLVING, 17, listOf("auscultat", "steth", "exam")),
                CriticalAction("c4_a3", 3, "Perform immediate right-sided needle decompression (thoracostomy)", OralBoardDomain.PATIENT_MANAGEMENT, 17, listOf("needle decompression", "thoracostomy")),
                CriticalAction("c4_a4", 4, "Insert definitive right-sided tube thoracostomy (chest tube) connected to underwater seal drainage", OralBoardDomain.PATIENT_MANAGEMENT, 17, listOf("chest tube", "thoracostomy")),
                CriticalAction("c4_a5", 5, "Obtain post-procedure portable Chest X-ray to confirm lung re-expansion and tube placement", OralBoardDomain.DIAGNOSTIC_STUDIES, 16, listOf("xray", "cxr")),
                CriticalAction("c4_a6", 6, "Complete full trauma secondary survey (expose & log-roll for additional wounds) and consult Trauma Surgery", OralBoardDomain.DISPOSITION, 17, listOf("surgery", "trauma"))
            ),
            defaultQuestions = listOf(
                UnlockQuestion(
                    QuestionGateType.Q0_GATE,
                    "Bay Entrance Gate (Q0)",
                    prompt = "A 28-year-old stab victim has BP 88/42, HR 110, RR 28, SpO2 92%, JVD, and absent right breath sounds. What is the immediate life-saving bedside intervention?",
                    options = listOf("Immediate right-sided needle decompression followed by tube thoracostomy", "Wait for a CT chest angiogram before any pleural procedure", "Pericardiocentesis via subxiphoid approach", "Esophagogastroduodenoscopy"),
                    correctIndex = 0,
                    clinicalExplanation = "Tension pneumothorax is a clinical diagnosis in an unstable patient requiring immediate pleural decompression without delaying for imaging."
                ),
                UnlockQuestion(
                    QuestionGateType.Q1_SURVEY,
                    "Bedside Survey Station (Q1)",
                    prompt = "Why must the resuscitation team completely undress and log-roll a penetrating chest trauma patient during the primary/secondary survey?",
                    options = listOf("To inspect the back, axillae, and flanks so occult posterior stab wounds are never missed", "To measure lumbar CSF opening pressure", "To check for sacral decubitus ulcers", "To perform an abdominal ultrasound"),
                    correctIndex = 0,
                    clinicalExplanation = "Failing to fully expose and log-roll penetrating trauma patients is a classic oral board pitfall that misses posterior stab wounds."
                ),
                UnlockQuestion(
                    QuestionGateType.Q2_LABS,
                    "Point-of-Care Lab Console (Q2)",
                    prompt = "Which blood bank order is mandatory in any penetrating thoracic trauma patient presenting with hypotension?",
                    options = listOf("Type and Crossmatch for packed red blood cells (PRBCs) alongside hemoglobin and lactate", "Serum iron and TIBC", "Rheumatoid factor and ANA", "Thyroid-stimulating hormone"),
                    correctIndex = 0,
                    clinicalExplanation = "All hypotensive penetrating trauma patients require immediate Type & Crossmatch in case of concomitant hemothorax or vascular injury."
                ),
                UnlockQuestion(
                    QuestionGateType.Q3_IMAGING,
                    "Mobile Radiology Terminal (Q3)",
                    prompt = "After performing needle decompression and placing a right chest tube, why is a portable Chest X-Ray (Case 4 Fig 4.2) mandatory?",
                    options = listOf("To confirm right lung re-expansion, verify tube position, and rule out residual hemopneumothorax", "To diagnose appendicitis", "To measure bone mineral density", "To evaluate for gallstones"),
                    correctIndex = 0,
                    clinicalExplanation = "Post-thoracostomy CXR confirms proper intrapleural placement of all sentinel holes and full re-expansion of the right lung."
                )
            ),
            dispositionAndPearls = "Admit to Trauma Surgery / Surgical ICU for continuous chest tube monitoring and serial hemoglobin checks.",
            instructorClues = listOf(
                "If the team sends the patient to X-ray or CT before needle decompression, BP drops to 60/30!",
                "After needle decompression + chest tube from Drawer 4, vitals normalize to BP 120/76, SpO2 99%."
            )
        ),

        // CASE 5
        OralBoardCase(
            caseNumber = 5,
            scenarioCategory = "Abdominal pain and vomiting",
            mainDiagnosis = "Acute cholecystitis",
            pdfPages = "214–234",
            sourceFiguresCount = 3,
            patientName = "Lakshmi Iyer",
            ageDesc = "44-year-old Female",
            gender = "Female",
            weightKg = 76f,
            chiefComplaint = "Severe constant right upper quadrant pain radiating to the right scapula with fever and bilious vomiting",
            triageNote = "Pain began 10 hours ago after a fatty dinner; unlike previous brief biliary colic episodes, this pain has remained constant and severe with chills.",
            patientSkinToneTag = "FLUSHED",
            initialVitals = VitalSigns("145/85", 145, 85, 93, 16, 99, 38.6f, 38, "Normal Sinus Rhythm"),
            stabilizedVitals = VitalSigns("126/78", 126, 78, 78, 14, 99, 37.5f, 38, "Normal Sinus Rhythm"),
            ventilatorPreset = VentilatorSettings(mode = "Standby (Room Air)", isActive = false),
            defibrillatorPreset = DefibrillatorSettings(mode = "Telemetry Monitor"),
            primarySurvey = PrimarySurveyABCDE(
                airway = "Patent, speaking clearly.",
                breathing = "RR 16/min, SpO2 99% on room air, shallow inspiration due to RUQ pain.",
                circulation = "BP 145/85 mmHg, HR 93 bpm, warm skin.",
                disability = "GCS 15, alert and oriented.",
                exposure = "Febrile at 38.6°C; no jaundice or rash."
            ),
            physicalExam = PhysicalExamClues(
                generalAppearance = "Uncomfortable middle-aged woman lying still on stretcher clutching her right upper abdomen.",
                heentAndNeck = "Anicteric sclerae, moist mucosa.",
                chestAndLungs = "Clear to auscultation bilaterally.",
                cardiovascular = "Regular rate and rhythm, normal S1/S2.",
                abdomenAndPelvis = "Marked Right Upper Quadrant (RUQ) tenderness with positive Murphy's sign (inspiratory arrest on deep RUQ palpation); no rebound in lower quadrants.",
                extremitiesAndSkin = "Warm, well-perfused, no edema.",
                neurologic = "Intact."
            ),
            labReports = listOf(
                LabEntry("CBC & Chemistry", "WBC Count", "15.4", "x10³/µL", "4.5–11.0", LabFlag.HIGH),
                LabEntry("CBC & Chemistry", "Total Bilirubin", "1.1", "mg/dL", "0.2–1.2", LabFlag.NORMAL),
                LabEntry("CBC & Chemistry", "Alkaline Phosphatase", "132", "U/L", "40–120", LabFlag.HIGH),
                LabEntry("CBC & Chemistry", "Lipase", "34", "U/L", "10–60", LabFlag.NORMAL),
                LabEntry("Urinalysis / CSF", "Urine Pregnancy (hCG)", "Negative", "", "Negative", LabFlag.NORMAL)
            ),
            abgVbgReports = listOf(
                LabEntry("ABG / VBG", "Venous pH", "7.41", "", "7.35–7.45", LabFlag.NORMAL),
                LabEntry("ABG / VBG", "Serum Lactate", "1.4", "mmol/L", "0.5–2.0", LabFlag.NORMAL)
            ),
            imagingAndEcg = listOf(
                ImagingStudy(
                    id = "c5_fig1",
                    modality = "USG",
                    title = "Right Upper Quadrant Biliary Ultrasound",
                    figureRef = "Case 5 Fig 5.1–5.3 (3 Source Figures)",
                    pdfPages = "pp. 214–234",
                    findings = "Distended gallbladder containing multiple echogenic gallstones with posterior acoustic shadowing, an impacted calculus in the gallbladder neck, anterior wall thickening (5.5 mm), pericholecystic fluid, and positive sonographic Murphy sign. Common bile duct is normal (4 mm).",
                    impression = "Acute calculous cholecystitis.",
                    visualPattern = ClinicalVisualPattern.RUQ_ULTRASOUND_GALLBLADDER
                )
            ),
            criticalActions = listOf(
                CriticalAction("c5_a1", 1, "Keep patient NPO and administer IV crystalloid hydration", OralBoardDomain.PATIENT_MANAGEMENT, 20, listOf("normal saline", "fluid")),
                CriticalAction("c5_a2", 2, "Administer parenteral analgesia (IV opioid/ketorolac) and antiemetic (ondansetron)", OralBoardDomain.PATIENT_MANAGEMENT, 20, listOf("analgesi", "pain", "morphine", "fentanyl", "ondansetron")),
                CriticalAction("c5_a3", 3, "Obtain Right Upper Quadrant (RUQ) Ultrasound and hepatic/pancreatic lab panel + pregnancy test", OralBoardDomain.DIAGNOSTIC_STUDIES, 20, listOf("ultrasound", "usg", "ruq")),
                CriticalAction("c5_a4", 4, "Administer broad-spectrum parenteral antibiotics covering enteric Gram-negatives and anaerobes", OralBoardDomain.PATIENT_MANAGEMENT, 20, listOf("antibiotic", "ceftriaxone", "piperacillin")),
                CriticalAction("c5_a5", 5, "Consult General Surgery for admission and laparoscopic cholecystectomy", OralBoardDomain.DISPOSITION, 20, listOf("surgery", "cholecystectomy"))
            ),
            defaultQuestions = listOf(
                UnlockQuestion(
                    QuestionGateType.Q0_GATE,
                    "Bay Entrance Gate (Q0)",
                    prompt = "A 44-year-old woman presents with 10 hours of constant RUQ pain radiating to the right scapula, fever 38.6°C, and vomiting. What defines a positive Murphy's sign?",
                    options = listOf("Abrupt inspiratory arrest when the examiner palpates the right subcostal gallbladder fossa", "Right lower quadrant pain upon palpation of the left lower quadrant", "Pain in the right hip upon internal rotation", "Periumbilical ecchymosis"),
                    correctIndex = 0,
                    clinicalExplanation = "Murphy's sign is the sudden cessation of inspiration during deep palpation of the right upper quadrant due to the inflamed gallbladder contacting the examiner's fingers."
                ),
                UnlockQuestion(
                    QuestionGateType.Q1_SURVEY,
                    "Bedside Survey Station (Q1)",
                    prompt = "How does acute cholecystitis clinically differ from uncomplicated biliary colic on history and physical exam?",
                    options = listOf("Pain persists > 6 hours accompanied by fever, leukocytosis, and localized pericholecystic inflammation", "Pain resolves spontaneously within 15 minutes without tenderness", "It presents exclusively with painless jaundice and weight loss", "It only occurs in neonates"),
                    correctIndex = 0,
                    clinicalExplanation = "Unlike transient biliary colic (<6 hours, afebrile), acute cholecystitis causes persistent RUQ pain >6 hours, fever, and leukocytosis."
                ),
                UnlockQuestion(
                    QuestionGateType.Q2_LABS,
                    "Point-of-Care Lab Console (Q2)",
                    prompt = "Why must a serum lipase, liver function panel (bilirubin, alkaline phosphatase, transaminases), and pregnancy test be ordered in a 44-year-old female with RUQ pain?",
                    options = listOf("To evaluate for choledocholithiasis, ascending cholangitis, gallstone pancreatitis, and pregnancy-related emergencies", "To diagnose iron overdose", "To screen for angle-closure glaucoma", "To measure salicylate clearance"),
                    correctIndex = 0,
                    clinicalExplanation = "Evaluating CBD obstruction (bilirubin/alk phos), gallstone pancreatitis (lipase), and pregnancy status is essential in reproductive-age females with RUQ pain."
                ),
                UnlockQuestion(
                    QuestionGateType.Q3_IMAGING,
                    "Mobile Radiology Terminal (Q3)",
                    prompt = "Which quartet of findings on RUQ Ultrasound (Case 5 Fig 5.1–5.3) confirms acute calculous cholecystitis?",
                    options = listOf("Cholelithiasis with posterior acoustic shadowing, gallbladder wall thickening (>3 mm), pericholecystic fluid, and sonographic Murphy's sign", "Normal thin-walled gallbladder without stones", "Target sign of the pylorus", "Pneumoperitoneum under the diaphragm"),
                    correctIndex = 0,
                    clinicalExplanation = "Gallstones with acoustic shadowing, wall thickening (>3–4 mm), pericholecystic fluid, and a sonographic Murphy's sign confirm acute cholecystitis."
                )
            ),
            dispositionAndPearls = "Admit to General Surgery service for IV antibiotics and early laparoscopic cholecystectomy.",
            instructorClues = listOf(
                "Remember to administer analgesia and antiemetics early from Crash Cart Drawer 1.",
                "Start IV antibiotics once acute cholecystitis with fever (38.6°C) and leukocytosis is identified."
            )
        ),

        // CASE 6 (Explicit Ventilator Preset: 10 mL/kg, 30/min, FiO2 100%)
        OralBoardCase(
            caseNumber = 6,
            scenarioCategory = "Weak infant",
            mainDiagnosis = "Septic shock in a young infant",
            pdfPages = "235–255",
            sourceFiguresCount = 1,
            patientName = "Baby of Priya Sharma",
            ageDesc = "3-week-old Male Neonate",
            gender = "Male",
            weightKg = 3.5f,
            chiefComplaint = "Poor feeding, extreme lethargy, high fever (39.2°C), and mottled skin for 8 hours",
            triageNote = "Full-term neonate brought by parents after refusing breastfeeds since midnight; feels burning hot and barely cries when stimulated.",
            patientSkinToneTag = "CYANOTIC",
            isPediatricOrInfant = true,
            initialVitals = VitalSigns("50/20", 50, 20, 205, 58, 94, 39.2f, 29, "Neonatal Sinus Tachycardia"),
            stabilizedVitals = VitalSigns("74/44", 74, 44, 148, 34, 99, 37.8f, 36, "Stabilized Neonatal Sinus"),
            ventilatorPreset = VentilatorSettings(
                mode = "Pediatric Pressure/Volume Control",
                tidalVolumeMlPerKg = 10,
                respRatePerMin = 30,
                fio2Percent = 100,
                peepCmH2O = 5,
                isActive = true,
                sourceCitationNote = "Case 6 PDF Explicit Ventilator Preset: Tidal Volume 10 mL/kg, Rate 30/min, FiO₂ 100%"
            ),
            defibrillatorPreset = DefibrillatorSettings(mode = "Neonatal Telemetry Leads", energyJoules = 14, energyPerKgLabel = "Neonatal Monitor (3.5 kg)"),
            primarySurvey = PrimarySurveyABCDE(
                airway = "Weak, grunting cry; requires airway stabilization and high-flow oxygen / intubation if fluid-refractory.",
                breathing = "Tachypneic at RR 58/min with intercostal retractions and nasal flaring, SpO2 94%.",
                circulation = "Decompensated shock! BP 50/20 mmHg, HR 205 bpm, capillary refill 5 seconds, cold mottled extremities.",
                disability = "Flaccid, lethargic neonate; POC glucose is 38 mg/dL (neonatal hypoglycemia).",
                exposure = "Rectal temperature 39.2°C; cutis marmorata mottling throughout trunk and limbs."
            ),
            physicalExam = PhysicalExamClues(
                generalAppearance = "Toxic-appearing, floppy 3-week-old neonate in decompensated septic shock.",
                heentAndNeck = "Slightly full anterior fontanelle, dry mucous membranes.",
                chestAndLungs = "Tachypneic at 58/min with grunting; clear breath sounds.",
                cardiovascular = "Extreme tachycardia at 205 bpm, thready brachial and femoral pulses.",
                abdomenAndPelvis = "Soft, liver edge 2 cm below right costal margin.",
                extremitiesAndSkin = "Diffuse mottling, delayed capillary refill (5 sec), cool hands and feet.",
                neurologic = "Marked lethargy, weak suck and Moro reflexes."
            ),
            labReports = listOf(
                LabEntry("CBC & Chemistry", "Point-of-Care Glucose", "38", "mg/dL", "60–100", LabFlag.CRITICAL),
                LabEntry("CBC & Chemistry", "WBC Count", "24.6", "x10³/µL", "5.0–19.5", LabFlag.CRITICAL),
                LabEntry("CBC & Chemistry", "Band Neutrophils", "22", "%", "< 10%", LabFlag.CRITICAL),
                LabEntry("Urinalysis / CSF", "CSF Analysis (post-stabilization)", "WBC 1,450 (88% PMN), Glucose 22, Protein 185", "", "Sterile", LabFlag.CRITICAL)
            ),
            abgVbgReports = listOf(
                LabEntry("ABG / VBG", "Arterial pH", "7.18", "", "7.35–7.45", LabFlag.CRITICAL),
                LabEntry("ABG / VBG", "Serum Lactate", "5.8", "mmol/L", "0.5–2.0", LabFlag.CRITICAL),
                LabEntry("ABG / VBG", "HCO3-", "12", "mEq/L", "20–26", LabFlag.CRITICAL)
            ),
            imagingAndEcg = listOf(
                ImagingStudy(
                    id = "c6_fig1",
                    modality = "X-RAY",
                    title = "Neonatal Portable Chest Radiograph",
                    figureRef = "Case 6 Fig 6.1 (1 Source Figure)",
                    pdfPages = "pp. 235–255",
                    findings = "Endotracheal tube tip positioned above carina, clear lung fields without focal lobar consolidation, normal cardiothymic silhouette.",
                    impression = "Neonatal septic shock with confirmed ETT placement (Ventilator set to 10 mL/kg, 30/min, FiO₂ 100%).",
                    visualPattern = ClinicalVisualPattern.INFANT_CXR_SEPSIS
                )
            ),
            criticalActions = listOf(
                CriticalAction("c6_a1", 1, "Support airway/oxygenation (intubate and set ventilator to 10 mL/kg, 30/min, FiO₂ 100%) and establish IV/IO access", OralBoardDomain.ACUITY_RECOGNITION, 25, listOf("intubat", "ventilat", "io", "oxygen")),
                CriticalAction("c6_a2", 2, "Administer aggressive 20 mL/kg isotonic crystalloid (0.9% NS) boluses (up to 60 mL/kg) and correct hypoglycemia with IV Dextrose", OralBoardDomain.PATIENT_MANAGEMENT, 25, listOf("normal saline", "bolus", "dextrose", "glucose")),
                CriticalAction("c6_a3", 3, "Perform full neonatal sepsis evaluation (blood, urine, CSF cultures when stable) and administer immediate empiric IV antibiotics (Ampicillin + Cefotaxime/Gentamicin)", OralBoardDomain.PATIENT_MANAGEMENT, 25, listOf("antibiotic", "culture", "blood")),
                CriticalAction("c6_a4", 4, "Initiate vasopressor support if fluid-refractory and admit to Pediatric/Neonatal ICU", OralBoardDomain.DISPOSITION, 25, listOf("epinephrine", "vasopressor", "icu"))
            ),
            defaultQuestions = listOf(
                UnlockQuestion(
                    QuestionGateType.Q0_GATE,
                    "Bay Entrance Gate (Q0)",
                    prompt = "A 3-week-old neonate arrives with T 39.2°C, HR 205, RR 58, BP 50/20, and lethargy. According to Case 6 guidelines, what are the explicit mechanical ventilator parameters once intubated?",
                    options = listOf("Tidal Volume 10 mL/kg, Respiratory Rate 30/min, FiO₂ 100%", "Tidal Volume 25 mL/kg, Rate 8/min, FiO₂ 21%", "CPAP 20 cmH2O only without rate", "High-frequency oscillation at 900 breaths/min"),
                    correctIndex = 0,
                    clinicalExplanation = "Case 6 specifies mechanical ventilation settings of Tidal Volume 10 mL/kg, Rate 30/min, and FiO₂ 100% for the intubated septic infant."
                ),
                UnlockQuestion(
                    QuestionGateType.Q1_SURVEY,
                    "Bedside Survey Station (Q1)",
                    prompt = "If peripheral IV access cannot be obtained within 60–90 seconds in this hypotensive 3-week-old neonate (BP 50/20), what is the immediate vascular route of choice?",
                    options = listOf("Proximal tibial Intraosseous (IO) needle placement for 20 mL/kg 0.9% NS boluses", "Wait 45 minutes for an interventional radiology PICC line", "Oral rehydration syringe", "Subcutaneous fluid clysis"),
                    correctIndex = 0,
                    clinicalExplanation = "Intraosseous (IO) access in the proximal tibia provides rapid, reliable access for crystalloid boluses, dextrose, antibiotics, and pressors in neonatal shock."
                ),
                UnlockQuestion(
                    QuestionGateType.Q2_LABS,
                    "Point-of-Care Lab Console (Q2)",
                    prompt = "What empiric antimicrobial regimen is standard in a 3-week-old neonate (<28 days old) presenting with septic shock?",
                    options = listOf("IV Ampicillin plus Cefotaxime (or Gentamicin) covering GBS, E. coli, and Listeria monocytogenes (± Acyclovir)", "Oral Azithromycin monotherapy", "Oral Ciprofloxacin monotherapy", "Topical Bacitracin"),
                    correctIndex = 0,
                    clinicalExplanation = "Neonates <28 days require empiric IV Ampicillin (for Listeria and GBS/Enterococcus) plus Cefotaxime or Gentamicin (for Gram-negative rods)."
                ),
                UnlockQuestion(
                    QuestionGateType.Q3_IMAGING,
                    "Mobile Radiology Terminal (Q3)",
                    prompt = "Should empiric IV antibiotics be delayed if the neonate is too hemodynamically unstable (BP 50/20) to safely tolerate a lumbar puncture initially?",
                    options = listOf("No — never delay empiric IV antibiotics for LP in an unstable neonate; give antibiotics immediately after blood/urine cultures", "Yes — hold all antibiotics for 12 hours until LP is performed", "Only give antibiotics if the Chest X-ray shows cavitation", "Perform LP while the infant is apneic"),
                    correctIndex = 0,
                    clinicalExplanation = "In an unstable neonate in septic shock, blood and catheterized urine cultures are drawn and IV antibiotics are given immediately; LP can be deferred until stabilized."
                )
            ),
            dispositionAndPearls = "Admit to Pediatric ICU (PICU) / NICU with mechanical ventilation (10 mL/kg, 30/min, FiO2 100%), fluid/vasopressor resuscitation, and IV antibiotics.",
            instructorClues = listOf(
                "Case 6 Explicit Device Preset active on the 3D Ventilator: Tidal Volume 10 mL/kg, Rate 30/min, FiO₂ 100%.",
                "Don't forget to treat the infant's hypoglycemia (POC glucose 38 mg/dL) with IV Dextrose."
            )
        ),

        // CASE 7
        OralBoardCase(
            caseNumber = 7,
            scenarioCategory = "Chest pain",
            mainDiagnosis = "Boerhaave syndrome (spontaneous oesophageal rupture)",
            pdfPages = "256–275",
            sourceFiguresCount = 3,
            patientName = "Rajesh Kumar Yadav",
            ageDesc = "54-year-old Male",
            gender = "Male",
            weightKg = 86f,
            chiefComplaint = "Excruciating retrosternal chest pain radiating to the back immediately after violent retching",
            triageNote = "Drank heavily last night; had forceful vomiting this morning followed by sudden tearing lower chest pain and shortness of breath.",
            patientSkinToneTag = "PALE",
            initialVitals = VitalSigns("115/65", 115, 65, 100, 18, 99, 37.8f, 36, "Sinus Tachycardia"),
            stabilizedVitals = VitalSigns("122/74", 122, 74, 86, 16, 99, 37.4f, 38, "Normal Sinus Rhythm"),
            ventilatorPreset = VentilatorSettings(mode = "Supplemental O2 Nasal Cannula", isActive = false),
            defibrillatorPreset = DefibrillatorSettings(mode = "12-Lead ECG & Telemetry"),
            primarySurvey = PrimarySurveyABCDE(
                airway = "Patent, voice has a slightly nasal quality; subcutaneous crepitus palpable in suprasternal notch.",
                breathing = "RR 18/min, SpO2 99%, diminished breath sounds at the LEFT lung base.",
                circulation = "BP 115/65 mmHg, HR 100 bpm.",
                disability = "GCS 15, severe pain (10/10).",
                exposure = "Mild low-grade fever 37.8°C; palpable cervical subcutaneous emphysema."
            ),
            physicalExam = PhysicalExamClues(
                generalAppearance = "Diaphoretic middle-aged male sitting upright, terrified to swallow or take a deep breath.",
                heentAndNeck = "Palpable subcutaneous crepitus (emphysema) over the lower neck and supraclavicular fossae.",
                chestAndLungs = "Dullness and decreased breath sounds at the left lung base.",
                cardiovascular = "Hamman's crunch (mediastinal crackling synchronous with the heartbeat) heard over the precordium!",
                abdomenAndPelvis = "Epigastric tenderness without peritonitis.",
                extremitiesAndSkin = "Symmetric radial and femoral pulses.",
                neurologic = "Intact."
            ),
            labReports = listOf(
                LabEntry("CBC & Chemistry", "WBC Count", "17.2", "x10³/µL", "4.5–11.0", LabFlag.HIGH),
                LabEntry("CBC & Chemistry", "Troponin I", "< 0.01", "ng/mL", "< 0.04", LabFlag.NORMAL),
                LabEntry("Urinalysis / CSF", "Pleural Fluid Amylase (if tapped)", "2,850", "U/L (Salivary)", "< 100", LabFlag.CRITICAL)
            ),
            abgVbgReports = listOf(
                LabEntry("ABG / VBG", "Arterial pH", "7.38", "", "7.35–7.45", LabFlag.NORMAL),
                LabEntry("ABG / VBG", "Serum Lactate", "2.6", "mmol/L", "0.5–2.0", LabFlag.HIGH)
            ),
            imagingAndEcg = listOf(
                ImagingStudy(
                    id = "c7_fig1",
                    modality = "X-RAY",
                    title = "Upright Chest X-Ray & Water-Soluble Contrast Esophagram / CT",
                    figureRef = "Case 7 Fig 7.1–7.3 (3 Source Figures)",
                    pdfPages = "pp. 256–275",
                    findings = "Pneumomediastinum (linear air streaks outlining mediastinal structures and V-sign of Naclerio), cervical subcutaneous emphysema, and left pleural effusion. Gastrografin esophagram/CT confirms contrast extravasation from the left posterolateral distal esophagus.",
                    impression = "Boerhaave syndrome (full-thickness spontaneous esophageal perforation).",
                    visualPattern = ClinicalVisualPattern.CXR_BOERHAAVE_PNEUMOMEDIASTINUM
                )
            ),
            criticalActions = listOf(
                CriticalAction("c7_a1", 1, "Obtain 12-lead ECG and upright Chest X-ray (identifying pneumomediastinum and left pleural effusion)", OralBoardDomain.DIAGNOSTIC_STUDIES, 20, listOf("ecg", "xray", "cxr")),
                CriticalAction("c7_a2", 2, "Keep patient strictly NPO and administer IV crystalloid resuscitation, analgesia, and antiemetics", OralBoardDomain.PATIENT_MANAGEMENT, 20, listOf("normal saline", "analgesi", "pain", "morphine", "ondansetron")),
                CriticalAction("c7_a3", 3, "Administer broad-spectrum IV antibiotics (covering oral flora, Gram-negatives, and anaerobes)", OralBoardDomain.PATIENT_MANAGEMENT, 20, listOf("antibiotic", "piperacillin", "vancomycin")),
                CriticalAction("c7_a4", 4, "Confirm esophageal perforation with water-soluble contrast (Gastrografin) esophagram or CT chest with oral contrast", OralBoardDomain.DIAGNOSTIC_STUDIES, 20, listOf("ct", "esophagram", "imaging")),
                CriticalAction("c7_a5", 5, "Obtain emergent Cardiothoracic Surgery consultation for operative esophageal repair and admit to Surgical ICU", OralBoardDomain.DISPOSITION, 20, listOf("surgery", "thoracic", "icu"))
            ),
            defaultQuestions = listOf(
                UnlockQuestion(
                    QuestionGateType.Q0_GATE,
                    "Bay Entrance Gate (Q0)",
                    prompt = "A 54-year-old man develops sudden tearing lower chest pain after violent vomiting. What classic clinical triad defines Mackler's triad in Boerhaave syndrome?",
                    options = listOf("Vomiting, lower thoracic pain, and cervical subcutaneous emphysema", "Fever, jaundice, and right upper quadrant pain", "Hypotension, JVD, and muffled heart sounds", "Headache, neck stiffness, and photophobia"),
                    correctIndex = 0,
                    clinicalExplanation = "Mackler's triad of forceful vomiting, sudden lower chest pain, and subcutaneous emphysema is classic for Boerhaave syndrome."
                ),
                UnlockQuestion(
                    QuestionGateType.Q1_SURVEY,
                    "Bedside Survey Station (Q1)",
                    prompt = "On cardiac auscultation, a crunching, crackling sound synchronous with each heartbeat ('Hamman's sign') indicates what pathology?",
                    options = listOf("Pneumomediastinum (free air in the mediastinum adjacent to the beating pericardium)", "Acute mitral valve prolapse", "Abdominal aortic aneurysm rupture", "Right bundle branch block"),
                    correctIndex = 0,
                    clinicalExplanation = "Hamman's crunch is produced by the heart beating against air-filled mediastinal tissues in pneumomediastinum."
                ),
                UnlockQuestion(
                    QuestionGateType.Q2_LABS,
                    "Point-of-Care Lab Console (Q2)",
                    prompt = "Which oral contrast medium must be used FIRST when performing an esophagram for suspected esophageal perforation to avoid severe chemical mediastinitis?",
                    options = listOf("Water-soluble contrast (Gastrografin)", "Thick high-density Barium sulfate paste first", "Activated charcoal slurry", "Polyethylene glycol"),
                    correctIndex = 0,
                    clinicalExplanation = "Water-soluble contrast (Gastrografin) is used first because extravasated barium causes severe granulomatous mediastinitis."
                ),
                UnlockQuestion(
                    QuestionGateType.Q3_IMAGING,
                    "Mobile Radiology Terminal (Q3)",
                    prompt = "Where does spontaneous esophageal rupture (Case 7 Fig 7.1–7.3) anatomically occur in over 90% of Boerhaave syndrome cases?",
                    options = listOf("The left posterolateral wall of the distal esophagus (2–3 cm above the gastroesophageal junction)", "The anterior cervical cricopharyngeus muscle", "The lesser curvature of the stomach", "The third portion of the duodenum"),
                    correctIndex = 0,
                    clinicalExplanation = "Due to anatomic muscle fiber weakness, >90% of Boerhaave perforations occur in the left posterolateral distal esophagus, leading to a left pleural effusion."
                )
            ),
            dispositionAndPearls = "Emergent Cardiothoracic Surgery consultation for thoracotomy, mediastinal debridement, and primary esophageal repair within 24 hours.",
            instructorClues = listOf(
                "Do not give aspirin or anticoagulants thinking this is an MI before checking the CXR showing pneumomediastinum.",
                "Do not blindly pass an NG tube without surgical/endoscopic guidance as it can extend the esophageal tear."
            )
        ),

        // CASE 8
        OralBoardCase(
            caseNumber = 8,
            scenarioCategory = "Back pain",
            mainDiagnosis = "Spinal epidural abscess, with urinary infection as a possible source",
            pdfPages = "276–294",
            sourceFiguresCount = 2,
            patientName = "Joseph Varghese",
            ageDesc = "58-year-old Male",
            gender = "Male",
            weightKg = 88f,
            chiefComplaint = "Severe mid-to-lower back pain for 4 days with new bilateral leg weakness and difficulty urinating today",
            triageNote = "History of poorly controlled Type 2 diabetes and recent dysuria treated partially at home. Walked with a cane yesterday; today cannot stand unassisted.",
            patientSkinToneTag = "NORMAL",
            initialVitals = VitalSigns("130/80", 130, 80, 110, 20, 100, 36.1f, 37, "Sinus Tachycardia"),
            stabilizedVitals = VitalSigns("124/76", 124, 76, 84, 16, 100, 36.6f, 38, "Normal Sinus Rhythm"),
            ventilatorPreset = VentilatorSettings(mode = "Standby (Room Air)", isActive = false),
            defibrillatorPreset = DefibrillatorSettings(mode = "Telemetry Monitor"),
            primarySurvey = PrimarySurveyABCDE(
                airway = "Patent.",
                breathing = "RR 20/min, SpO2 100% on room air.",
                circulation = "BP 130/80 mmHg, HR 110 bpm.",
                disability = "GCS 15; bilateral lower extremity motor weakness (3/5 strength) and sensory level at T10!",
                exposure = "Afebrile at 36.1°C currently; exquisite midline spinous process tenderness at T9–L1."
            ),
            physicalExam = PhysicalExamClues(
                generalAppearance = "Uncomfortable diabetic male unable to lift legs off stretcher.",
                heentAndNeck = "Supple neck, no meningismus.",
                chestAndLungs = "Clear to auscultation.",
                cardiovascular = "Tachycardic at 110 bpm, strong peripheral pulses.",
                abdomenAndPelvis = "Palpable distended urinary bladder in suprapubic region (post-void residual > 650 mL on Foley catheterization); decreased rectal sphincter tone and perianal saddle hypesthesia.",
                extremitiesAndSkin = "No track marks, warm lower extremities.",
                neurologic = "3/5 motor strength in bilateral hip flexors and ankle dorsiflexion; hyperreflexia at knees with bilateral extensor plantar (Babinski) responses."
            ),
            labReports = listOf(
                LabEntry("CBC & Chemistry", "WBC Count", "18.9", "x10³/µL", "4.5–11.0", LabFlag.HIGH),
                LabEntry("CBC & Chemistry", "ESR / CRP", "ESR 94 mm/hr, CRP 18.2 mg/dL", "", "ESR <20", LabFlag.CRITICAL),
                LabEntry("CBC & Chemistry", "Serum Glucose", "286", "mg/dL", "70–100", LabFlag.HIGH),
                LabEntry("Urinalysis / CSF", "Urinalysis", ">100 WBC/hpf, Positive Nitrites, Many Bacteria", "", "Negative", LabFlag.CRITICAL)
            ),
            abgVbgReports = listOf(
                LabEntry("ABG / VBG", "Venous pH", "7.37", "", "7.35–7.45", LabFlag.NORMAL),
                LabEntry("ABG / VBG", "Serum Lactate", "2.1", "mmol/L", "0.5–2.0", LabFlag.HIGH)
            ),
            imagingAndEcg = listOf(
                ImagingStudy(
                    id = "c8_fig1",
                    modality = "MRI",
                    title = "Gadolinium-Enhanced Thoracic/Lumbar Spine MRI",
                    figureRef = "Case 8 Fig 8.1–8.2 (2 Source Figures)",
                    pdfPages = "pp. 276–294",
                    findings = "Posterior epidural rim-enhancing fluid collection spanning T9 to L1 producing severe thecal sac and spinal cord compression with adjacent vertebral osteomyelitis/discitis.",
                    impression = "Spinal epidural abscess (SEA) with acute spinal cord compression.",
                    visualPattern = ClinicalVisualPattern.SPINE_MRI_EPIDURAL_ABSCESS
                )
            ),
            criticalActions = listOf(
                CriticalAction("c8_a1", 1, "Perform thorough neurologic and rectal exam (identifying lower extremity weakness, decreased rectal tone, and urinary retention)", OralBoardDomain.DATA_ACQUISITION, 20, listOf("exam", "neuro", "foley")),
                CriticalAction("c8_a2", 2, "Place Foley urinary catheter for bladder decompression and obtain urine & blood cultures + ESR/CRP", OralBoardDomain.PATIENT_MANAGEMENT, 20, listOf("foley", "urine", "culture", "blood")),
                CriticalAction("c8_a3", 3, "Order emergent MRI of the Thoracic and Lumbar Spine with contrast", OralBoardDomain.DIAGNOSTIC_STUDIES, 20, listOf("mri", "spine")),
                CriticalAction("c8_a4", 4, "Administer broad-spectrum IV antibiotics covering MRSA and Gram-negative bacilli (Vancomycin + Ceftriaxone/Cefepime)", OralBoardDomain.PATIENT_MANAGEMENT, 20, listOf("antibiotic", "vancomycin", "ceftriaxone")),
                CriticalAction("c8_a5", 5, "Obtain emergent Neurosurgery / Orthopedic Spine Surgery consultation for surgical decompression and laminectomy", OralBoardDomain.DISPOSITION, 20, listOf("neurosurgery", "spine", "surgery"))
            ),
            defaultQuestions = listOf(
                UnlockQuestion(
                    QuestionGateType.Q0_GATE,
                    "Bay Entrance Gate (Q0)",
                    prompt = "A 58-year-old diabetic man with a recent UTI presents with severe back pain, bilateral leg weakness, and urinary retention, though T is 36.1°C. Why must Spinal Epidural Abscess (SEA) remain top of the differential?",
                    options = listOf("The classic triad of fever, back pain, and neurologic deficit is present altogether in <15% of patients initially", "SEA never causes back pain", "Diabetes protects against epidural abscess", "Normal temperature rules out spinal infection 100%"),
                    correctIndex = 0,
                    clinicalExplanation = "Fewer than 15–20% of patients with SEA present with the complete classic triad of fever, back pain, and neurological deficit; waiting for fever leads to permanent paralysis."
                ),
                UnlockQuestion(
                    QuestionGateType.Q1_SURVEY,
                    "Bedside Survey Station (Q1)",
                    prompt = "Which bedside examination step is critical in any patient presenting with acute back pain and bilateral leg weakness?",
                    options = listOf("Digital rectal exam for sphincter tone, perianal saddle sensation, and post-void residual bladder volume", "Weber and Rinne tuning fork test", "Slit-lamp fluorescein exam", "Allen test of the radial artery"),
                    correctIndex = 0,
                    clinicalExplanation = "Assessing rectal sphincter tone, saddle sensation, and urinary retention (post-void residual via Foley/bladder scan) is mandatory to detect spinal cord or cauda equina compression."
                ),
                UnlockQuestion(
                    QuestionGateType.Q2_LABS,
                    "Point-of-Care Lab Console (Q2)",
                    prompt = "Along with blood and urine cultures, which inflammatory markers have >95% sensitivity in screening for spinal epidural abscess?",
                    options = listOf("Erythrocyte Sedimentation Rate (ESR) and C-Reactive Protein (CRP)", "Serum Lipase and Amylase", "Serum Iron and Ferritin", "Troponin and BNP"),
                    correctIndex = 0,
                    clinicalExplanation = "ESR and CRP are elevated in >95% of patients with spinal epidural abscess, and blood/urine cultures identify the causative organism (Staph aureus or Gram-negative rods from UTI)."
                ),
                UnlockQuestion(
                    QuestionGateType.Q3_IMAGING,
                    "Mobile Radiology Terminal (Q3)",
                    prompt = "What is the gold-standard imaging study (Case 8 Fig 8.1–8.2) to diagnose a spinal epidural abscess and define its longitudinal extent?",
                    options = listOf("MRI of the Spine with and without gadolinium contrast", "Plain AP lumbar radiograph only", "Abdominal ultrasound", "Non-contrast Head CT"),
                    correctIndex = 0,
                    clinicalExplanation = "Gadolinium-enhanced Spine MRI is the gold standard (>90% sensitivity/specificity), clearly delineating the epidural collection and cord compression."
                )
            ),
            dispositionAndPearls = "Emergent Neurosurgery consultation for decompressive laminectomy and epidural drainage plus IV Vancomycin + 3rd/4th-generation Cephalosporin.",
            instructorClues = listOf(
                "Perform Foley catheterization from Drawer 6 to relieve 650 mL urinary retention and send urine culture (UTI source).",
                "Plain spine X-rays are insufficient—order the Spine MRI and start IV Vancomycin + Ceftriaxone."
            )
        ),

        // CASE 9
        OralBoardCase(
            caseNumber = 9,
            scenarioCategory = "Leg swelling",
            mainDiagnosis = "Necrotizing fasciitis of the left lower extremity with sepsis and acute kidney injury",
            pdfPages = "295–315",
            sourceFiguresCount = 3,
            patientName = "Suresh Patil",
            ageDesc = "63-year-old Male",
            gender = "Male",
            weightKg = 92f,
            chiefComplaint = "Rapidly worsening severe left leg pain and swelling after minor garden scratch 2 days ago",
            triageNote = "Diabetic patient on a beta-blocker (explaining HR 56 despite sepsis!). Pain in left calf is excruciating (10/10), disproportionate to the skin redness earlier today, now developing dark hemorrhagic blisters.",
            patientSkinToneTag = "PALE",
            initialVitals = VitalSigns("139/103", 139, 103, 56, 18, 98, 36.4f, 34, "Sinus Bradycardia (Beta-Blocked)"),
            stabilizedVitals = VitalSigns("124/78", 124, 78, 72, 16, 99, 36.8f, 38, "Normal Sinus Rhythm"),
            ventilatorPreset = VentilatorSettings(mode = "Standby (Room Air)", isActive = false),
            defibrillatorPreset = DefibrillatorSettings(mode = "Telemetry Monitor"),
            primarySurvey = PrimarySurveyABCDE(
                airway = "Patent.",
                breathing = "RR 18/min, SpO2 98% on room air.",
                circulation = "BP 139/103 mmHg, HR 56 bpm (takes metoprolol daily), delayed capillary refill.",
                disability = "GCS 15, writhing in severe pain out of proportion to outer erythema.",
                exposure = "Left lower extremity swollen from ankle to mid-thigh with dusky violaceous plaques, hemorrhagic bullae, and palpable subcutaneous crepitus!"
            ),
            physicalExam = PhysicalExamClues(
                generalAppearance = "Ill-appearing 63-year-old male in severe distress from left leg pain.",
                heentAndNeck = "Dry oral mucosa.",
                chestAndLungs = "Clear to auscultation bilaterally.",
                cardiovascular = "Bradycardic at 56 bpm (chronic beta-blockade).",
                abdomenAndPelvis = "Soft, non-tender; check perineum to rule out Fournier gangrene extension.",
                extremitiesAndSkin = "Left leg tense, wooden induration extending beyond erythematous borders, violaceous hemorrhagic bullae, dishwater-gray seepage, and palpable subcutaneous crepitus.",
                neurologic = "Cutaneous anesthesia over center of necrotic bullae due to dermal nerve infarction."
            ),
            labReports = listOf(
                LabEntry("CBC & Chemistry", "WBC Count", "26.4", "x10³/µL", "4.5–11.0", LabFlag.CRITICAL),
                LabEntry("CBC & Chemistry", "Serum Sodium (Na+)", "128", "mEq/L", "135–145", LabFlag.LOW),
                LabEntry("CBC & Chemistry", "BUN / Creatinine (AKI)", "BUN 58, Cr 3.4", "mg/dL", "Cr 0.7–1.3", LabFlag.CRITICAL),
                LabEntry("CBC & Chemistry", "Serum Glucose", "340", "mg/dL", "70–100", LabFlag.HIGH),
                LabEntry("CBC & Chemistry", "Creatine Kinase (CK)", "4,820", "U/L", "30–200", LabFlag.CRITICAL)
            ),
            abgVbgReports = listOf(
                LabEntry("ABG / VBG", "Venous pH", "7.26", "", "7.35–7.45", LabFlag.LOW),
                LabEntry("ABG / VBG", "Serum Lactate", "4.9", "mmol/L", "0.5–2.0", LabFlag.CRITICAL)
            ),
            imagingAndEcg = listOf(
                ImagingStudy(
                    id = "c9_fig1",
                    modality = "CT",
                    title = "Left Lower Extremity Radiograph & Non-Contrast CT",
                    figureRef = "Case 9 Fig 9.1–9.3 (3 Source Figures)",
                    pdfPages = "pp. 295–315",
                    findings = "Extensive pockets of subcutaneous and deep intermuscular fascial gas tracking along the left gastrocnemius and tibial planes with marked fascial thickening and fluid collections.",
                    impression = "Necrotizing fasciitis of the left lower extremity with soft-tissue gas.",
                    visualPattern = ClinicalVisualPattern.SOFT_TISSUE_CT_NEC_FASC
                )
            ),
            criticalActions = listOf(
                CriticalAction("c9_a1", 1, "Recognize necrotizing fasciitis clinically (pain out of proportion, hemorrhagic bullae, crepitus) and establish large-bore IV crystalloid resuscitation for sepsis & AKI", OralBoardDomain.ACUITY_RECOGNITION, 20, listOf("normal saline", "fluid", "bolus", "iv access")),
                CriticalAction("c9_a2", 2, "Obtain blood cultures, lab panel (identifying hyponatremia, leukocytosis, and AKI), and extremity X-ray/non-contrast CT showing fascial gas", OralBoardDomain.DIAGNOSTIC_STUDIES, 20, listOf("blood", "culture", "xray", "ct")),
                CriticalAction("c9_a3", 3, "Administer immediate broad-spectrum IV antibiotics including Clindamycin (for antitoxin effect) + Vancomycin + Piperacillin-Tazobactam/Carbapenem", OralBoardDomain.PATIENT_MANAGEMENT, 20, listOf("antibiotic", "clindamycin", "vancomycin", "piperacillin")),
                CriticalAction("c9_a4", 4, "Administer IV opioid analgesia and update tetanus prophylaxis", OralBoardDomain.PATIENT_MANAGEMENT, 20, listOf("analgesi", "pain", "morphine", "fentanyl", "tetanus")),
                CriticalAction("c9_a5", 5, "Obtain emergent General/Orthopedic Surgery consultation for immediate operative surgical fasciotomy and debridement", OralBoardDomain.DISPOSITION, 20, listOf("surgery", "debridement", "or"))
            ),
            defaultQuestions = listOf(
                UnlockQuestion(
                    QuestionGateType.Q0_GATE,
                    "Bay Entrance Gate (Q0)",
                    prompt = "A 63-year-old diabetic man presents with excruciating left leg pain out of proportion to skin exam, hemorrhagic bullae, and crepitus. Why must Clindamycin be added to Vancomycin and Piperacillin-Tazobactam?",
                    options = listOf("Clindamycin inhibits bacterial ribosome toxin synthesis (Eagle effect) in Group A Streptococcal & Clostridial necrotizing fasciitis", "Clindamycin reverses beta-blocker bradycardia", "Clindamycin is a loop diuretic for AKI", "Clindamycin dissolves gallstones"),
                    correctIndex = 0,
                    clinicalExplanation = "Clindamycin suppresses bacterial exotoxin and M-protein production regardless of bacterial growth phase (overcoming the Eagle effect)."
                ),
                UnlockQuestion(
                    QuestionGateType.Q1_SURVEY,
                    "Bedside Survey Station (Q1)",
                    prompt = "Which physical exam features distinguish necrotizing fasciitis from simple superficial cellulitis?",
                    options = listOf("Pain out of proportion to superficial appearance, rapid progression, wooden induration, hemorrhagic/violaceous bullae, crepitus, and cutaneous anesthesia", "Well-demarcated mild erythema that improves with elevation alone", "Bilateral symmetric pitting ankle edema from CHF", "A chronic painless venous stasis ulcer"),
                    correctIndex = 0,
                    clinicalExplanation = "Pain out of proportion, wooden subcutaneous induration, violaceous bullae, crepitus, and later skin anesthesia from nerve destruction hallmark necrotizing fasciitis."
                ),
                UnlockQuestion(
                    QuestionGateType.Q2_LABS,
                    "Point-of-Care Lab Console (Q2)",
                    prompt = "Why should IV iodinated contrast be avoided if a CT scan of the leg is performed in this patient (Cr 3.4 mg/dL)?",
                    options = listOf("The patient has sepsis-induced Acute Kidney Injury (AKI), and non-contrast CT or plain X-ray readily visualizes deep fascial gas", "IV contrast causes acute iron toxicity", "IV contrast masks bone fractures", "IV contrast induces torsades de pointes"),
                    correctIndex = 0,
                    clinicalExplanation = "With acute kidney injury (Cr 3.4 mg/dL), non-contrast CT or bedside plain radiographs detect subcutaneous/fascial gas without nephrotoxic contrast load—and imaging must never delay surgical exploration."
                ),
                UnlockQuestion(
                    QuestionGateType.Q3_IMAGING,
                    "Mobile Radiology Terminal (Q3)",
                    prompt = "What is the single most important definitive life- and limb-saving intervention for necrotizing fasciitis (Case 9 Fig 9.1–9.3)?",
                    options = listOf("Emergent surgical exploration and wide operative debridement in the Operating Room (antibiotics alone cannot penetrate necrotic fascia)", "Outpatient oral cephalexin and warm compresses", "Compression stockings and leg elevation", "Low-molecular-weight heparin anticoagulation"),
                    correctIndex = 0,
                    clinicalExplanation = "Thrombosis of deep perforating vessels prevents antibiotics from reaching necrotic fascia; emergent operative surgical debridement is the only definitive cure."
                )
            ),
            dispositionAndPearls = "Immediate transfer to Operating Room with General/Orthopedic Surgery for wide surgical debridement and post-op Surgical ICU care.",
            instructorClues = listOf(
                "Note that HR is 56 and T is 36.4°C because the patient takes a beta-blocker and NSAIDs—do not be fooled into missing severe sepsis!",
                "Check renal function (Cr 3.4 mg/dL AKI) and consult Surgery immediately for the OR."
            )
        ),

        // CASE 10
        OralBoardCase(
            caseNumber = 10,
            scenarioCategory = "Weakness",
            mainDiagnosis = "Severe hyperkalaemia due to renal insufficiency, possibly contributed to by ACE-inhibitor use",
            pdfPages = "316–334",
            sourceFiguresCount = 2,
            patientName = "Abdul Rasheed",
            ageDesc = "66-year-old Male",
            gender = "Male",
            weightKg = 80f,
            chiefComplaint = "Progressive generalized muscle weakness over 24 hours; unable to stand from sofa this morning",
            triageNote = "History of chronic kidney disease and hypertension; dose of Lisinopril (ACE inhibitor) was doubled 2 weeks ago and patient has been using salt-substitute (potassium chloride) heavily.",
            patientSkinToneTag = "PALE",
            initialVitals = VitalSigns("137/64", 137, 64, 66, 18, 97, 36.7f, 36, "Wide-QRS Sine-Wave Rhythm"),
            stabilizedVitals = VitalSigns("132/74", 132, 74, 76, 16, 99, 36.8f, 38, "Normal Narrow-QRS Sinus Rhythm"),
            ventilatorPreset = VentilatorSettings(mode = "Standby (Supplemental O2 Ready)", isActive = false),
            defibrillatorPreset = DefibrillatorSettings(mode = "Continuous 12-Lead & Pads Attached", energyJoules = 150, energyPerKgLabel = "High-Risk Arrhythmia Watch"),
            primarySurvey = PrimarySurveyABCDE(
                airway = "Patent, speaking clearly.",
                breathing = "RR 18/min, SpO2 97% on room air.",
                circulation = "BP 137/64 mmHg, HR 66 bpm; cardiac monitor shows alarmingly wide QRS complexes with tall peaked T waves!",
                disability = "GCS 15; profound symmetric ascending flaccid weakness (2/5 in lower extremities, 3/5 in upper extremities).",
                exposure = "Left forearm maturing arteriovenous (AV) fistula thrill palpable."
            ),
            physicalExam = PhysicalExamClues(
                generalAppearance = "Alert older male unable to lift legs off the bed.",
                heentAndNeck = "Normocephalic, mucous membranes moist.",
                chestAndLungs = "Bibasilar fine crackles.",
                cardiovascular = "Regular rate at 66 bpm, no rubs.",
                abdomenAndPelvis = "Soft, non-tender.",
                extremitiesAndSkin = "Trace bilateral ankle edema; left upper extremity dialysis access site.",
                neurologic = "Symmetric 2/5 lower extremity motor weakness with diminished deep tendon reflexes; intact cranial nerves and sensation."
            ),
            labReports = listOf(
                LabEntry("CBC & Chemistry", "Serum Potassium (K+)", "8.4", "mEq/L", "3.5–5.0", LabFlag.CRITICAL),
                LabEntry("CBC & Chemistry", "BUN / Creatinine", "BUN 84, Cr 6.2", "mg/dL", "Cr 0.7–1.3", LabFlag.CRITICAL),
                LabEntry("CBC & Chemistry", "Serum Sodium (Na+)", "134", "mEq/L", "135–145", LabFlag.LOW),
                LabEntry("CBC & Chemistry", "Serum Glucose", "112", "mg/dL", "70–100", LabFlag.NORMAL)
            ),
            abgVbgReports = listOf(
                LabEntry("ABG / VBG", "Venous pH", "7.28", "", "7.35–7.45", LabFlag.LOW),
                LabEntry("ABG / VBG", "Serum Bicarbonate", "15", "mEq/L", "22–28", LabFlag.LOW)
            ),
            imagingAndEcg = listOf(
                ImagingStudy(
                    id = "c10_fig1",
                    modality = "ECG",
                    title = "12-Lead ECG Before & After IV Calcium",
                    figureRef = "Case 10 Fig 10.1–10.2 (2 Source Figures)",
                    pdfPages = "pp. 316–334",
                    findings = "Loss of P waves, extreme QRS widening (>200 ms) merging with tall tented/peaked T waves in a pre-arrest 'sine-wave' pattern. Repeat ECG after IV Calcium Gluconate shows narrowing of QRS complex and return of sinus P waves.",
                    impression = "Life-threatening hyperkalaemia with sine-wave conduction toxicity.",
                    visualPattern = ClinicalVisualPattern.ECG_HYPERKALEMIA_SINE_WAVE
                )
            ),
            criticalActions = listOf(
                CriticalAction("c10_a1", 1, "Place patient on continuous cardiac monitor and obtain immediate 12-lead ECG (identifying peaked T waves and wide-QRS sine wave)", OralBoardDomain.DIAGNOSTIC_STUDIES, 20, listOf("ecg", "monitor", "defib")),
                CriticalAction("c10_a2", 2, "Administer immediate IV Calcium Gluconate (or Calcium Chloride) for myocardial membrane stabilization", OralBoardDomain.ACUITY_RECOGNITION, 20, listOf("calcium", "hyperkalaemia")),
                CriticalAction("c10_a3", 3, "Shift potassium intracellularly with IV Regular Insulin (10 units) + IV Dextrose (D50W), plus nebulized Albuterol and IV Sodium Bicarbonate", OralBoardDomain.PATIENT_MANAGEMENT, 20, listOf("insulin", "dextrose", "bicarbonate")),
                CriticalAction("c10_a4", 4, "Promote potassium elimination (loop diuretic / GI cation exchanger) and discontinue offending ACE-inhibitor (Lisinopril) and KCl salt substitute", OralBoardDomain.PATIENT_MANAGEMENT, 20, listOf("lisinopril", "ace", "elimination")),
                CriticalAction("c10_a5", 5, "Consult Nephrology for emergent hemodialysis and admit to Medical ICU", OralBoardDomain.DISPOSITION, 20, listOf("nephrology", "dialysis", "icu"))
            ),
            defaultQuestions = listOf(
                UnlockQuestion(
                    QuestionGateType.Q0_GATE,
                    "Bay Entrance Gate (Q0)",
                    prompt = "A 66-year-old CKD patient on Lisinopril presents with profound weakness and a wide-QRS 'sine-wave' pattern on the monitor. What medication from Crash Cart Drawer 1 must be given FIRST within seconds?",
                    options = listOf("IV Calcium Gluconate 10% (or Calcium Chloride) to stabilize the cardiac myocyte membrane threshold", "Oral sodium polystyrene sulfonate (Kayexalate) alone", "IV potassium chloride bolus", "IV amiodarone 300 mg push"),
                    correctIndex = 0,
                    clinicalExplanation = "IV Calcium immediately antagonizes potassium-induced cardiac membrane excitability within 1–3 minutes, preventing deterioration into VF/asystole."
                ),
                UnlockQuestion(
                    QuestionGateType.Q1_SURVEY,
                    "Bedside Survey Station (Q1)",
                    prompt = "How can severe hyperkalaemia clinically mimic Guillain-Barré syndrome during the neurologic examination?",
                    options = listOf("It causes acute symmetric ascending flaccid muscle paralysis with depressed deep tendon reflexes while sparing cranial nerves and mentation", "It causes unilateral resting pill-rolling tremor", "It causes expressive Broca aphasia", "It causes homonymous hemianopsia"),
                    correctIndex = 0,
                    clinicalExplanation = "Severe hyperkalaemia depolarizes skeletal muscle resting potentials, producing ascending flaccid paralysis that resolves rapidly once serum K+ is lowered."
                ),
                UnlockQuestion(
                    QuestionGateType.Q2_LABS,
                    "Point-of-Care Lab Console (Q2)",
                    prompt = "After stabilizing the myocardium with IV Calcium, which therapy most rapidly and reliably shifts serum potassium (K+ 8.4 mEq/L) into the intracellular space?",
                    options = listOf("IV Regular Insulin (10 units) co-administered with IV Dextrose (D50W 25–50 g), plus high-dose nebulized Albuterol and IV Sodium Bicarbonate", "IV Furosemide alone in an anuric patient", "Subcutaneous heparin", "Oral acetazolamide"),
                    correctIndex = 0,
                    clinicalExplanation = "IV Regular Insulin drives K+ into cells via the Na+/K+-ATPase pump within 15–30 minutes; Dextrose prevents insulin-induced hypoglycemia."
                ),
                UnlockQuestion(
                    QuestionGateType.Q3_IMAGING,
                    "Mobile Radiology Terminal (Q3)",
                    prompt = "What is the classic progression of 12-lead ECG changes (Case 10 Fig 10.1–10.2) as serum potassium rises from 5.5 to >8.0 mEq/L?",
                    options = listOf("Tall peaked/tented T waves → PR prolongation & flattened/absent P waves → progressive QRS widening → Sine-wave pattern → VF/Asystole", "Delta waves with short PR interval", "Osborn J waves", "Alternating electrical alternans with low voltage"),
                    correctIndex = 0,
                    clinicalExplanation = "Hyperkalaemia produces tall peaked T waves first, followed by P-wave flattening, QRS widening, and ultimately a pre-terminal sine-wave fusion of QRS and T waves."
                )
            ),
            dispositionAndPearls = "Emergent Nephrology consultation for hemodialysis and admission to Medical ICU with continuous telemetry.",
            instructorClues = listOf(
                "Do not wait for the chemistry lab result to return K+ 8.4—the 12-lead ECG shows a sine-wave pattern right now! Give IV Calcium from Drawer 1 immediately.",
                "Follow IV Calcium with Regular Insulin + Dextrose and Sodium Bicarbonate from Drawer 1."
            )
        )
    )
}
