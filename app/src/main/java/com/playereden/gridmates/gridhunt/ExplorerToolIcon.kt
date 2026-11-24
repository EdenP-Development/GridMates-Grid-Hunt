package com.playereden.gridmates.gridhunt

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

internal enum class ExplorerTool(val label: String) {
    ZOOM_IN("Zoom in"), ZOOM_OUT("Zoom out"), FIT("See all"), GUIDE("Map guide"),
    WEST("Move west"), EAST("Move east"), NORTH("Move north"), SOUTH("Move south")
}

/** One visual meaning per tool; the interactive parent supplies its accessible label. */
@Composable
internal fun ExplorerToolIcon(tool: ExplorerTool, modifier: Modifier = Modifier.size(24.dp)) {
    Canvas(modifier) {
        val s = size.minDimension
        fun p(x: Float,y: Float) = Offset(x*s,y*s)
        fun line(x: Float,y: Float,xx: Float,yy: Float) =
            drawLine(Forest,p(x,y),p(xx,yy),s*.075f,cap=androidx.compose.ui.graphics.StrokeCap.Round)
        when(tool) {
            ExplorerTool.ZOOM_IN, ExplorerTool.ZOOM_OUT -> {
                drawCircle(Forest,s*.27f,p(.39f,.39f),style=Stroke(s*.075f))
                line(.59f,.59f,.86f,.86f)
                line(.24f,.39f,.54f,.39f)
                if(tool==ExplorerTool.ZOOM_IN) line(.39f,.24f,.39f,.54f)
            }
            ExplorerTool.FIT -> {
                for(x in listOf(.16f,.84f)) for(y in listOf(.16f,.84f)) {
                    line(x,y,x+(if(x<.5f) .23f else -.23f),y)
                    line(x,y,x,y+(if(y<.5f) .23f else -.23f))
                }
                drawRect(Forest,p(.36f,.36f),Size(s*.28f,s*.28f),style=Stroke(s*.045f))
            }
            ExplorerTool.GUIDE -> {
                line(.50f,.23f,.50f,.81f)
                line(.50f,.23f,.13f,.15f);line(.13f,.15f,.13f,.73f)
                line(.13f,.73f,.50f,.81f);line(.50f,.23f,.87f,.15f)
                line(.87f,.15f,.87f,.73f);line(.87f,.73f,.50f,.81f)
                line(.23f,.34f,.39f,.38f);line(.61f,.38f,.77f,.34f)
                line(.23f,.51f,.39f,.55f);line(.61f,.55f,.77f,.51f)
            }
            else -> {
                val d=when(tool) {
                    ExplorerTool.WEST -> Offset(-1f,0f)
                    ExplorerTool.EAST -> Offset(1f,0f)
                    ExplorerTool.NORTH -> Offset(0f,-1f)
                    else -> Offset(0f,1f)
                }
                val c=p(.5f,.5f); val tip=c+d*s*.32f
                val back=tip-d*s*.24f; val cross=Offset(-d.y,d.x)*s*.17f
                drawLine(Forest,c-d*s*.32f,tip,s*.075f,cap=androidx.compose.ui.graphics.StrokeCap.Round)
                drawLine(Forest,tip,back+cross,s*.075f,cap=androidx.compose.ui.graphics.StrokeCap.Round)
                drawLine(Forest,tip,back-cross,s*.075f,cap=androidx.compose.ui.graphics.StrokeCap.Round)
            }
        }
    }
}
