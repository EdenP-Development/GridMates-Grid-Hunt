package com.playereden.gridmates.gridhunt

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.playereden.gridhunt.core.ExplorationMap
import com.playereden.gridhunt.core.WhisperingCoastDraft

/** Unpublished terrain and landmark prototype. Coordinates follow the composition study.
 * Final sprite artwork and terrain/anchor reconciliation remain publication gates.
 */
@Composable
internal fun coastalTerrainDraftScene(map: ExplorationMap, look: MapLook): MapScene {
    require(map == WhisperingCoastDraft.map)
    require(map.bounds.west == 42000.0 && map.bounds.south == 51000.0 &&
        map.bounds.east == 48000.0 && map.bounds.north == 57000.0)
    val survey = look == MapLook.SURVEY_TABLE
    val illustrations = if (survey) emptyMap() else mapOf(
        "beacon-point" to painterResource(R.drawable.gh_coast_beacon_point),
        "driftwood-cove" to painterResource(R.drawable.gh_coast_driftwood_cove),
        "shell-beach" to painterResource(R.drawable.gh_coast_shell_beach),
        "gull-rock" to painterResource(R.drawable.gh_coast_gull_rock),
        "tidepool-bend" to painterResource(R.drawable.gh_coast_tidepool_bend)
    )
    return MapScene(
        map = map,
        backdrop = listOf(Color(0xFFB1C7C6), Color(0xFF8EADAE)),
        paper = if (survey) Color(0xFFDCEBE7) else Color(0xFFEDE4CB),
        gridInk = if (survey) Color(0xB74C6F76) else Color(0x88778667),
        drawTerrain = { bounds, _, project ->
            require(bounds == map.bounds)
            drawCoastalTerrainDraft(survey, project)
            drawCoastalScenery(map, survey, project)
            drawCoastalLandmarks(map, survey, project, illustrations)
        }
    )
}

private fun DrawScope.drawCoastalTerrainDraft(
    survey: Boolean, project: (Double, Double) -> Offset
) {
    // Study units: 100 units per kilometer; y increases southward.
    fun point(x: Int, y: Int) = project(42000.0 + x * 10.0, 57000.0 - y * 10.0)
    fun Path.m(x: Int, y: Int) { val a = point(x,y); moveTo(a.x,a.y) }
    fun Path.l(x: Int, y: Int) { val a = point(x,y); lineTo(a.x,a.y) }
    fun Path.q(x: Int,y: Int,u: Int,v: Int) {
        val a = point(x,y); val b = point(u,v)
        quadraticBezierTo(a.x,a.y,b.x,b.y)
    }
    val nw = point(0,0); val se = point(600,600)
    drawRect(if (survey) Color(0xFFA6C8CB) else Color(0xFF98C0C7),
        nw, Size(se.x-nw.x,se.y-nw.y))
    val shore = Path().apply {
        m(0,0); l(480,0); q(520,38,490,82); q(475,110,410,120)
        q(290,115,255,165); q(280,205,370,190); q(440,175,435,225)
        q(445,235,442,250); q(445,270,470,300); q(445,320,505,343); q(523,366,488,400)
        q(460,420,400,430); q(300,445,205,460); q(155,470,180,490)
        q(220,520,335,510); q(395,510,375,550); q(350,580,365,600)
        l(0,600); close()
    }
    val grass = Path().apply {
        m(0,0); l(460,0); q(492,40,462,72); q(440,95,395,98)
        q(270,94,232,159); q(235,232,367,214); q(407,202,383,235)
        q(369,257,400,275); q(430,278,446,294); q(413,335,476,352); q(495,369,465,390)
        q(400,414,295,425); q(190,440,159,469); q(128,511,206,532)
        q(245,540,275,545); q(288,568,278,600); l(0,600); close()
    }
    drawPath(shore, if (survey) Color(0xFFE1DFCE) else Color(0xFFE8D6A6))
    drawPath(grass, if (survey) Color(0xFFB9D0C3) else Color(0xFFB4C39A))
    // Broad sand at Shell Beach and the tidal shelf are shared geography.
    // Subordinate washes add texture without creating answer-bearing features.
    if (!survey) {
        fun wash(x: Int, y: Int, rx: Int, ry: Int, color: Color) {
            val a = point(x-rx,y-ry); val b = point(x+rx,y+ry)
            drawOval(color,a,Size(b.x-a.x,b.y-a.y))
        }
        wash(100,125,68,43,Color(0x247F9D70))
        wash(120,290,82,40,Color(0x26E2D6A2))
        wash(250,370,60,32,Color(0x247F9D70))
        wash(120,535,67,36,Color(0x26E2D6A2))
        wash(324,570,22,17,Color(0x35FFF0C8))
        wash(410,250,24,14,Color(0x30F2E1B7))
    }
    val unit = (se.x-nw.x)/600f
    drawPath(shore, if (survey) Color(0xFF668D90) else Color(0xFFF3E6C6),
        style = Stroke(width = (unit*3f).coerceAtLeast(.5f)))
    // Jetty ends at candidate answer point; no duplicate landmark sprite.
    drawLine(Color(0xFF806447), point(473,359), point(560,370), strokeWidth = unit*17f)
    drawLine(Color(0xFFC29B6E), point(477,359), point(560,370), strokeWidth = unit*10f)
}
