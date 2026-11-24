package com.playereden.gridmates.gridhunt

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke

/** Decorative native artwork, never used as evidence of earned qualification. */
@Composable
internal fun ExplorerInsignia(modifier: Modifier = Modifier, medal: Boolean = false) {
    Canvas(modifier) {
        val s = size.minDimension
        val origin = Offset((size.width-s)/2f,(size.height-s)/2f)
        fun p(x: Float,y: Float) = origin + Offset(x*s,y*s)
        val bronze = Color(0xFF97713E)
        val gold = Color(0xFFE6C18A)
        val dark = Color(0xFF345A47)
        val leaf = Color(0xFF78965D)
        val cream = Color(0xFFF6E8C6)
        if (medal) {
            for (side in listOf(-1f,1f)) {
                val ribbon = Path().apply {
                    moveTo(p(.5f+side*.08f,.58f).x,p(.5f,.58f).y)
                    lineTo(p(.5f+side*.31f,.57f).x,p(.5f,.57f).y)
                    lineTo(p(.5f+side*.37f,.94f).x,p(.5f,.94f).y)
                    lineTo(p(.5f+side*.22f,.87f).x,p(.5f,.87f).y)
                    lineTo(p(.5f+side*.10f,.96f).x,p(.5f,.96f).y)
                    close()
                }
                drawPath(ribbon,dark)
                drawPath(ribbon,leaf,style=Stroke(s*.012f))
            }
        } else {
            val shield = Path().apply {
                moveTo(p(.12f,.12f).x,p(.12f,.12f).y)
                quadraticBezierTo(p(.5f,.02f).x,p(.5f,.02f).y,p(.88f,.12f).x,p(.88f,.12f).y)
                lineTo(p(.84f,.61f).x,p(.84f,.61f).y)
                quadraticBezierTo(p(.76f,.82f).x,p(.76f,.82f).y,p(.5f,.95f).x,p(.5f,.95f).y)
                quadraticBezierTo(p(.24f,.82f).x,p(.24f,.82f).y,p(.16f,.61f).x,p(.16f,.61f).y)
                close()
            }
            drawPath(shield,dark)
            drawPath(shield,gold,style=Stroke(s*.025f))
        }
        val center = p(.5f,.42f)
        val r = s*.31f
        drawCircle(bronze,r,center)
        drawCircle(Brush.radialGradient(listOf(cream,gold,bronze),center,r),r*.92f,center)
        drawCircle(cream,r*.73f,center)
        drawCircle(bronze,r*.72f,center,style=Stroke(s*.009f))
        for (i in 0 until 24) {
            val angle = i*Math.PI/12.0
            val v = Offset(kotlin.math.cos(angle).toFloat(),kotlin.math.sin(angle).toFloat())
            drawLine(bronze,center+v*r*.79f,center+v*r*.88f,s*.008f)
        }
        for (i in 0 until 4) {
            val angle = (i-1)*Math.PI/2
            val v = Offset(kotlin.math.cos(angle).toFloat(),kotlin.math.sin(angle).toFloat())
            val cross = Offset(-v.y,v.x)
            val needle = Path().apply {
                val tip = center+v*r*.65f
                val a = center+cross*r*.13f
                val b = center-cross*r*.13f
                moveTo(tip.x,tip.y);lineTo(a.x,a.y);lineTo(b.x,b.y);close()
            }
            drawPath(needle,if(i==0) dark else bronze)
        }
        drawCircle(gold,r*.12f,center)
        drawCircle(dark,r*.055f,center)
    }
}
