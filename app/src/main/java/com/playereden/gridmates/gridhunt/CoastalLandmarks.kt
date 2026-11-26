package com.playereden.gridmates.gridhunt

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.playereden.gridhunt.core.ExplorationMap

/** Draft native silhouettes. Canonical map points are the only answer anchors. */
internal fun DrawScope.drawCoastalLandmarks(
    map: ExplorationMap, survey: Boolean, project: (Double, Double) -> Offset
) {
    val ink = if (survey) Color(0xFF42686D) else Color(0xFF655E4C)
    val cream = if (survey) Color(0xFFE1EAE4) else Color(0xFFF4E5C5)
    val rust = if (survey) Color(0xFF78999A) else Color(0xFFAF6653)
    val rock = if (survey) Color(0xFF8EA9A5) else Color(0xFF8C9B97)
    val water = if (survey) Color(0xFFA6C8CB) else Color(0xFF83B3B9)
    val wood = if (survey) Color(0xFF718F8D) else Color(0xFF9E7958)
    val unit = (project(map.bounds.west + 1000, map.bounds.north).x -
        project(map.bounds.west, map.bounds.north).x) / 100f
    val s = (unit * 0.58f).coerceIn(0.20.dp.toPx(), 0.60.dp.toPx())
    map.landmarks.forEach { landmark ->
        val anchor = project(landmark.point.easting, landmark.point.northing)
        fun p(x: Float, y: Float) = anchor + Offset(x*s,y*s)
        fun poly(color: Color, vararg coords: Float) {
            val path = Path().apply {
                val start = p(coords[0],coords[1]); moveTo(start.x,start.y)
                for (i in 2 until coords.size step 2) {
                    val a = p(coords[i],coords[i+1]); lineTo(a.x,a.y)
                }
                close()
            }
            drawPath(path,color)
            drawPath(path,ink,style=Stroke(width=(2*s).coerceAtLeast(.65f)))
        }
        fun line(color: Color, x: Float,y: Float,u: Float,v: Float, width: Float = 3f) {
            drawLine(color,p(x,y),p(u,v),strokeWidth=(width*s).coerceAtLeast(.7f))
        }
        fun oval(color: Color,x: Float,y: Float,w: Float,h: Float) {
            drawOval(color,p(x,y),Size(w*s,h*s))
        }
        when (landmark.id) {
            "beacon-point" -> {
                // Tower base is the canonical anchor, not the image center.
                poly(cream,-22f,0f,-14f,-53f,14f,-53f,22f,0f)
                poly(rust,-19f,-16f,-17f,-28f,17f,-28f,19f,-16f)
                poly(cream,-16f,-53f,-16f,-69f,16f,-69f,16f,-53f)
                poly(rust,-22f,-69f,0f,-83f,22f,-69f)
            }
            "driftwood-cove" -> {
                poly(wood,-35f,8f,28f,-15f,34f,-3f,-29f,20f)
                line(wood,-6f,-1f,-15f,-24f,8f)
                line(wood,8f,-6f,23f,-25f,7f)
            }
            "shell-beach" -> {
                val shell = Path().apply {
                    val a=p(0f,29f); moveTo(a.x,a.y)
                    val b=p(-48f,-5f); val c=p(-27f,-27f)
                    quadraticBezierTo(b.x,b.y,c.x,c.y)
                    val d=p(0f,-50f); val e=p(27f,-27f)
                    quadraticBezierTo(d.x,d.y,e.x,e.y)
                    val f=p(48f,-5f); quadraticBezierTo(f.x,f.y,a.x,a.y)
                    close()
                }
                drawPath(shell,cream); drawPath(shell,ink,style=Stroke(2*s))
                for (x in listOf(-20f,-10f,0f,10f,20f)) line(rust,0f,25f,x,-22f,2f)
            }
            "gull-rock" -> {
                poly(rock,-35f,23f,-23f,-3f,2f,-25f,26f,-7f,35f,23f)
                line(ink,-23f,-39f,0f,-30f,4f)
                line(ink,0f,-30f,23f,-39f,4f)
            }
            "tidepool-bend" -> {
                oval(ink,-35f,-24f,70f,48f)
                oval(rock,-32f,-21f,64f,42f)
                oval(water,-24f,-13f,48f,26f)
                line(cream,-17f,0f,17f,0f,2f)
            }
            "old-jetty" -> {
                // Terrain already draws the pier. Mark its seaward tip only.
                drawCircle(ink,3.5f*s,anchor)
            }
        }
    }
}
