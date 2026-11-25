package com.playereden.gridmates.gridhunt

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.imageResource
import com.playereden.gridhunt.core.MapCamera
import com.playereden.gridhunt.core.WorldBounds
import com.playereden.gridhunt.core.WoodlandLandmarks

/** Scene-specific visuals; no selection, progress, or camera gesture ownership. */
internal data class MapScene(
    val backdrop: List<Color>,
    val paper: Color,
    val gridInk: Color,
    val drawTerrain: DrawScope.(WorldBounds, MapCamera, (Double, Double) -> Offset) -> Unit
)

@Composable
internal fun woodlandMapScene(mapLook: MapLook): MapScene {
    val surveyStyle = mapLook == MapLook.SURVEY_TABLE
    val landmarkSprites = if (surveyStyle) emptyMap() else mapOf(
        WoodlandLandmarks.mushrooms.id to woodlandLandmarkSprite(WoodlandLandmarks.mushrooms.id),
        WoodlandLandmarks.flowers.id to woodlandLandmarkSprite(WoodlandLandmarks.flowers.id),
        WoodlandLandmarks.acorn.id to woodlandLandmarkSprite(WoodlandLandmarks.acorn.id),
        WoodlandLandmarks.waystone.id to woodlandLandmarkSprite(WoodlandLandmarks.waystone.id)
    )
    val oakSprite = if (surveyStyle) null else ImageBitmap.imageResource(R.drawable.gh_wood_oak_sm)
    val pineSprite = if (surveyStyle) null else ImageBitmap.imageResource(R.drawable.gh_wood_pine_sm)
    val rocksSprite = if (surveyStyle) null else ImageBitmap.imageResource(R.drawable.gh_env_rocks)
    val fernsSprite = if (surveyStyle) null else ImageBitmap.imageResource(R.drawable.gh_env_ferns)
    val logSprite = if (surveyStyle) null else ImageBitmap.imageResource(R.drawable.gh_env_log)
    val pondSprite = if (surveyStyle) null else ImageBitmap.imageResource(R.drawable.gh_env_pond)
    val mapBackdrop = if (surveyStyle) Color(0xFFB1C7C6) else Color(0xFFD1DFCF)
    val mapPaper = if (surveyStyle) Color(0xFFDCEBE7) else Color(0xFFEDE4CB)
    val treeDark = if (surveyStyle) Color(0xFF68938F) else Color(0xFF88AA84)
    val treeLight = if (surveyStyle) Color(0xFF9CBFBA) else Color(0xFFAAC098)
    val gridInk = if (surveyStyle) Color(0xB74C6F76) else Color(0x88778667)
    val streamInk = if (surveyStyle) Color(0xFF4E8C9E) else Color(0xFF6F9FA9)
    val trailInk = if (surveyStyle) Color(0xFF6D8090) else Color(0xFFA68858)
    return MapScene(
        backdrop = if (surveyStyle) listOf(mapBackdrop, Color(0xFF8EADAE))
            else listOf(Color(0xFFCFDBCB), Color(0xFFB4C9B3)),
        paper = mapPaper,
        gridInk = gridInk,
        drawTerrain = { bounds, cam, p ->
            drawWoodlandTerrain(bounds, cam, surveyStyle, landmarkSprites,
                oakSprite, pineSprite, rocksSprite, fernsSprite, logSprite, pondSprite,
                treeDark, treeLight, streamInk, trailInk, p)
        }
    )
}
