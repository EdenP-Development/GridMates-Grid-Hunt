package com.playereden.gridmates.gridhunt

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.playereden.gridhunt.core.*

/** Decorative, non-interactive overview using the same authored terrain as gameplay.
 * No objective, progress, gesture, saved camera, or new image assets.
 * Unknown maps deliberately have no illustration rather than a misleading fallback.
 */
@Composable
internal fun RegionMapPortrait(map: ExplorationMap, modifier: Modifier = Modifier) {
    val scene = when (map) {
        ExplorationMaps.woodland -> woodlandMapScene(MapLook.FIELD_GUIDE)
        WhisperingCoastDraft.map -> coastalTerrainDraftScene(map,MapLook.FIELD_GUIDE)
        else -> return
    }
    Canvas(modifier.clip(RoundedCornerShape(12.dp)).testTag("regionPortrait_${map.id}")) {
        val w = size.width.toDouble()
        val h = size.height.toDouble()
        if (w <= 0.0 || h <= 0.0) return@Canvas
        val bounds = map.bounds
        val cam = MapCamera.fit(bounds,w,h,padding = 0.0)
        fun project(e: Double,n: Double): Offset {
            val p = cam.worldToScreen(WorldPoint(e,n),w,h)
            return Offset(p.x.toFloat(),p.y.toFloat())
        }
        val a = project(bounds.west,bounds.north)
        val b = project(bounds.east,bounds.south)
        drawRect(Brush.linearGradient(scene.backdrop,start=Offset.Zero,end=Offset(size.width,size.height)))
        drawRect(scene.paper,a,Size(b.x-a.x,b.y-a.y))
        clipRect(a.x,a.y,b.x,b.y) {
            scene.drawTerrain(this,bounds,cam,::project)
        }
    }
}
