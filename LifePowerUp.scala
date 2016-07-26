package game2

import cs2.util.Vec2
import scalafx.scene.image.Image
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color
import scalafx.scene.effect.GaussianBlur


/**
 * @author eherbert
 */
class LifePowerUp(val powerUpImg:Image,
                  val powerUpPos:Vec2,
                  val value:Double,
                  val powerUpTimer:Double) extends PowerUp(powerUpImg,powerUpPos,powerUpTimer){
  
  def copy:LifePowerUp = {
    val temp = new LifePowerUp(img,new Vec2(spritePos.x,spritePos.y),value.toDouble,timer.toDouble)
    temp.internalTimer = internalTimer.toDouble
    temp
  }
  
}