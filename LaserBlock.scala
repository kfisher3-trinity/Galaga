package game2

import cs2.util.Vec2
import scalafx.scene.image.Image
import scalafx.scene.canvas.GraphicsContext

/**
 * @author eherbert
 */
class LaserBlock(val img:Image, val initVel:Vec2, val initPos:Vec2) extends Sprite(img,initVel,initPos){
  def copy:LaserBlock = { new LaserBlock(img, new Vec2(spriteVel.x.toDouble,spriteVel.y.toDouble), new Vec2(spritePos.x.toDouble,spritePos.y.toDouble)) }
  def display2(gc:GraphicsContext) = { display(gc) }
  def timeStep(gc:GraphicsContext,delta:Double) {
    move(initVel*delta*200)
  }
}