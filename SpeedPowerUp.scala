package game2

import cs2.util.Vec2
import scalafx.scene.image.Image
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.effect.GaussianBlur
import scalafx.scene.paint.Color


/**
 * @author eherbert
 */
class SpeedPowerUp(val powerUpImg:Image,
                  val powerUpPos:Vec2,
                  val value:Double,
                  val powerUpTimer:Double) extends PowerUp(powerUpImg,powerUpPos,powerUpTimer){
  
  def copy:SpeedPowerUp = {
    val temp = new SpeedPowerUp(img,new Vec2(spritePos.x,spritePos.y),value.toDouble,timer.toDouble)
    temp.internalTimer = internalTimer.toDouble
    temp
  }
  
}