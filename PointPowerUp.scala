package game2

import cs2.util.Vec2
import scalafx.scene.image.Image


/**
 * @author eherbert
 */
class PointPowerUp(val powerUpImg:Image,
                  val powerUpPos:Vec2,
                  val powerUpTimer:Double) extends PowerUp(powerUpImg,powerUpPos,powerUpTimer){
  
  def copy:PointPowerUp = {
    val temp = new PointPowerUp(powerUpImg, new Vec2(spritePos.x,spritePos.y),powerUpTimer.toDouble)
    temp.internalTimer = internalTimer.toDouble
    temp
  }
  
}