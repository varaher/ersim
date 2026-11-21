package com.example.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.io.File

class SessionCodecTest {

    private val def: CaseDefinition by lazy {
        val f = listOf("src/main/assets/cases/ersim_stemi_001.json", "app/src/main/assets/cases/ersim_stemi_001.json")
            .map { File(it) }.first { it.exists() }
        CaseParser.parse(f.readText())
    }

    @Test fun encodeDecode_roundTrip_andCompactsClockTicks() {
        val events = listOf(
            SimEvent.Advance(1), SimEvent.Advance(1), SimEvent.Order("ecg_12lead"),
            SimEvent.Advance(1), SimEvent.Ask("hx_onset"), SimEvent.Examine("CARD"), SimEvent.Perform("aspirin")
        )
        val text = SessionCodec.encode(events)
        assertEquals("A:2|O:ecg_12lead|A:1|K:hx_onset|E:CARD|P:aspirin", text)
        assertEquals(SessionCodec.compact(events), SessionCodec.decode(text))
    }

    @Test fun restoredSession_equalsLiveSession() {
        val events = ArrayList<SimEvent>()
        repeat(70) { events += SimEvent.Advance(1) }
        events += SimEvent.Order("ecg_12lead")
        repeat(65) { events += SimEvent.Advance(1) }
        events += SimEvent.Ask("hx_lysis_ci")
        events += SimEvent.Perform("tenecteplase")
        repeat(30) { events += SimEvent.Advance(1) }
        val live = CaseEngine.reduceAll(def, CaseEngine.start(def), events)
        val restored = SessionCodec.restore(def, SessionCodec.decode(SessionCodec.encode(events))!!)
        assertEquals(live, restored)
    }

    @Test fun corruptText_returnsNull() {
        assertNull(SessionCodec.decode("A:x|P:aspirin"))
        assertNull(SessionCodec.decode("Z:1"))
        assertNull(SessionCodec.decode("P:"))
    }
}
