package com.playereden.gridmates.gridhunt

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.playereden.gridhunt.core.ExplorationMap

/** Decorative study geometry only. Never a source of puzzle answers. */
internal fun DrawScope.drawCoastalScenery(
    map: ExplorationMap, survey: Boolean, project: (Double, Double) -> Offset
) {
    fun p(x: Int, y: Int) = project(42000.0 + x * 10.0, 57000.0 - y * 10.0)
    val unit = (p(100,0).x-p(0,0).x)/100f
    val trail = if (survey) Color(0xFF809A94) else Color(0xFFB79B70)
    // Inland trail: deliberately avoids the southern inlet and ocean.
    val path = Path().apply {
        val a=p(100,65); moveTo(a.x,a.y)
        val b=p(185,100); val c=p(135,235); quadraticBezierTo(b.x,b.y,c.x,c.y)
        val d=p(80,325); val e=p(105,410); quadraticBezierTo(d.x,d.y,e.x,e.y)
        val f=p(75,505); val g=p(135,570); quadraticBezierTo(f.x,f.y,g.x,g.y)
    }
    drawPath(path, trail.copy(alpha=.7f), style=Stroke(width=(unit*6f).coerceAtLeast(.6f)))
    if (survey) return
    val vegetation = Color(0xFF789779)
    val highlight = Color(0xFFAABD91)
    for ((x,y) in listOf(65 to 80, 130 to 140, 70 to 300, 85 to 375, 75 to 550, 210 to 310, 270 to 385)) {
        val e=42000.0+x*10.0; val n=57000.0-y*10.0
        if (map.landmarks.any { kotlin.math.abs(it.point.easting-e)<450 && kotlin.math.abs(it.point.northing-n)<450 }) continue
        val a=p(x,y)
        drawOval(vegetation.copy(alpha=.72f), a-Offset(13*unit,8*unit), Size(26*unit,16*unit))
        drawOval(highlight.copy(alpha=.65f), a-Offset(9*unit,7*unit), Size(14*unit,7*unit))
    }
    // Small tufts and wave strokes are subordinate to answer-bearing symbols.
    for ((x,y) in listOf(55 to 190, 175 to 270, 220 to 370, 60 to 455, 200 to 570)) {
        val a=p(x,y)
        for (dx in listOf(-5f,0f,5f)) drawLine(vegetation,a,a+Offset(dx*unit,-9*unit),strokeWidth=(1.5f*unit).coerceAtLeast(.5f))
    }
    for ((x,y) in listOf(550 to 120, 540 to 220, 555 to 300, 490 to 470, 550 to 550)) {
        val a=p(x,y)
        val wave=Path().apply {
            moveTo(a.x-15*unit,a.y)
            quadraticBezierTo(a.x-7*unit,a.y-6*unit,a.x,a.y)
            quadraticBezierTo(a.x+7*unit,a.y+6*unit,a.x+15*unit,a.y)
        }
        drawPath(wave,Color(0x99E4EEE1),style=Stroke(width=(2*unit).coerceAtLeast(.5f)))
    }
}
