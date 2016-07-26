package game2


import scalafx.scene.image.Image
import cs2.util.Vec2
import scala.collection.mutable
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.effect.GaussianBlur
import scalafx.scene.paint.Color



/**
 * @author eherbert
 */
abstract class PowerUp(val img:Image,
            val pos:Vec2,
            val timer:Double) extends Sprite(img,
                                         null,
                                         pos) {
  
  var internalTimer = timer
  def copy:PowerUp
  def display2(gc:GraphicsContext) {
    val increaseValue = 1.2
    val unit = if(img.width.apply > img.height.apply) img.width.apply*increaseValue else img.height.apply*increaseValue
    
    val sX = pos.x + unit
    val tX = pos.x + img.width.apply
    val finalCirclePosX = pos.x - (sX-tX)/2
    
    val sY = pos.y + unit
    val tY = pos.y + img.height.apply
    val finalCirclePosY = pos.y - (sY-tY)/2
    
    gc.fill = Color.CORNFLOWERBLUE
    gc.setEffect(new GaussianBlur(10.0))
    gc.fillOval(finalCirclePosX, finalCirclePosY, unit, unit)
    gc.setEffect(null)
    super.display(gc)
  }
  
}